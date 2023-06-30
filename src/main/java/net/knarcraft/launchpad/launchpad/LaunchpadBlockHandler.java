package net.knarcraft.launchpad.launchpad;

import net.knarcraft.launchpad.Launchpad;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * A handler class to keep track of launchpad blocks
 */
public final class LaunchpadBlockHandler {

    private static final File launchpadsFile = new File(Launchpad.getInstance().getDataFolder(), "data.yml");
    private static @NotNull Map<Block, LaunchpadBlock> launchpadBlocks = new HashMap<>();

    private LaunchpadBlockHandler() {

    }

    /**
     * Loads all current launchpad blocks from disk
     */
    public static void loadAll() {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(launchpadsFile);
        ConfigurationSection launchpadsSection = yamlConfiguration.getConfigurationSection("launchpads");
        launchpadBlocks = new HashMap<>();
        if (launchpadsSection == null) {
            Launchpad.log(Level.WARNING, "Launchpads section not found in data.yml. Ignore this warning if" +
                    " you have no saved launchpads.");
            return;
        }

        for (String key : launchpadsSection.getKeys(false)) {
            try {
                loadLaunchpad(launchpadsSection, key);
            } catch (InvalidConfigurationException exception) {
                Launchpad.log(Level.SEVERE, "Unable to load launchpad " + key + ": " + exception.getMessage());
            }
        }
    }

    /**
     * Registers a new launchpad block
     *
     * @param launchpadBlock <p>The launchpad block to register</p>
     */
    public static void registerLaunchpadBlock(@NotNull LaunchpadBlock launchpadBlock) {
        launchpadBlocks.put(launchpadBlock.getBlock(), launchpadBlock);
        saveAll();
    }

    /**
     * Unregisters a launchpad block
     *
     * @param block <p>The block containing the launchpad block to unregister</p>
     */
    public static void unregisterLaunchpadBlock(@NotNull Block block) {
        if (launchpadBlocks.containsKey(block)) {
            launchpadBlocks.remove(block);
            saveAll();
        }
    }

    /**
     * Gets the launchpad block for the given block
     *
     * @param block <p>The block containing the launchpad</p>
     * @return <p>The launchpad block, or null if not a launchpad</p>
     */
    public static @Nullable LaunchpadBlock getLaunchpadBlock(@NotNull Block block) {
        return launchpadBlocks.get(block);
    }

    /**
     * Saves all current launchpad blocks to disk
     */
    public static void saveAll() {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(launchpadsFile);
        ConfigurationSection launchpadsSection = yamlConfiguration.createSection("launchpads");
        for (Block block : launchpadBlocks.keySet()) {
            String locationString = Objects.requireNonNull(block.getWorld()).getUID() + "," +
                    block.getX() + "," + block.getY() + "," + block.getZ();
            ConfigurationSection launchpadSection = launchpadsSection.createSection(locationString);
            LaunchpadBlock launchpadBlock = launchpadBlocks.get(block);
            launchpadSection.set("verticalVelocity", launchpadBlock.getVerticalVelocityRaw());
            launchpadSection.set("horizontalVelocity", launchpadBlock.getHorizontalVelocityRaw());
            launchpadSection.set("fixedDirection", launchpadBlock.getFixedDirection() != null ?
                    launchpadBlock.getFixedDirection().name() : null);
        }
        try {
            yamlConfiguration.save(launchpadsFile);
        } catch (IOException e) {
            Launchpad.log(Level.SEVERE, "Unable to save launchpads. Data loss will occur! Please report " +
                    "details about this problem to the developer.");
        }
    }

    /**
     * Loads a single launchpad
     *
     * @param configurationSection <p>The configuration section to read</p>
     * @param key                  <p>The key containing tha launchpad's info</p>
     * @throws InvalidConfigurationException <p>If unable to parse the launchpad's location</p>
     */
    private static void loadLaunchpad(@NotNull ConfigurationSection configurationSection,
                                      @NotNull String key) throws InvalidConfigurationException {
        String[] locationParts = key.split(",");
        Block launchpadBlock;
        try {
            launchpadBlock = new Location(Bukkit.getWorld(UUID.fromString(locationParts[0])),
                    Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2]),
                    Double.parseDouble(locationParts[3])).getBlock();
        } catch (NumberFormatException exception) {
            throw new InvalidConfigurationException("Invalid launchpad location");
        }

        // If the launchpad's block has been removed, ignore it
        if (launchpadBlock.getType().isAir()) {
            return;
        }

        double horizontalVelocity = configurationSection.getDouble(key + ".horizontalVelocity", -1);
        double verticalVelocity = configurationSection.getDouble(key + ".verticalVelocity", -1);
        String fixedDirectionString = configurationSection.getString(key + ".fixedDirection");
        BlockFace fixedDirection = null;
        if (fixedDirectionString != null) {
            fixedDirection = BlockFace.valueOf(configurationSection.getString(key + ".fixedDirection"));
        }
        launchpadBlocks.put(launchpadBlock, new LaunchpadBlock(launchpadBlock, horizontalVelocity, verticalVelocity,
                fixedDirection));
    }

}
