package net.knarcraft.launchpad.config;

import net.knarcraft.knarlib.particle.ParticleConfig;
import net.knarcraft.knarlib.particle.ParticleSpawner;
import net.knarcraft.knarlib.particle.ParticleTrailSpawner;
import net.knarcraft.knarlib.util.MaterialHelper;
import net.knarcraft.knarlib.util.ParticleHelper;
import net.knarcraft.launchpad.Launchpad;
import net.knarcraft.launchpad.launchpad.LaunchpadBlockHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * The configuration for this plugin
 */
public class LaunchpadConfiguration {

    private @NotNull FileConfiguration fileConfiguration;
    private double horizontalVelocity;
    private double verticalVelocity;
    private boolean particlesEnabled;
    private boolean trailsEnabled;
    private int particleTaskId = -1;
    private int particleTrailTaskId = -1;
    private UUID particleStoredCalculationsId;
    private ParticleTrailSpawner trailSpawner = null;
    private @NotNull Set<Material> launchpadMaterials = new HashSet<>();
    private @NotNull Set<Material> materialWhitelist = new HashSet<>();

    /**
     * Instantiate a new launch pad configuration
     *
     * @param fileConfiguration <p>The file configuration to use</p>
     */
    public LaunchpadConfiguration(@NotNull FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        load(fileConfiguration);
    }

    /**
     * Loads this configuration from the given file configuration
     *
     * @param fileConfiguration <p>The file configuration to load</p>
     */
    public void load(@NotNull FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        ConfigurationSection launchpadSection = fileConfiguration.getConfigurationSection("launchpad");
        if (launchpadSection == null) {
            Launchpad.log(Level.WARNING, "Unable to load launchpad configuration. " +
                    "The \"launchpad\" configuration section is missing.");
            return;
        }

        this.launchpadMaterials = loadMaterials(launchpadSection, "materials");
        this.materialWhitelist = loadMaterials(launchpadSection, "materialWhitelist");

        this.horizontalVelocity = launchpadSection.getDouble("horizontalVelocity");
        this.verticalVelocity = launchpadSection.getDouble("verticalVelocity");

        // Load launchpad blocks
        LaunchpadBlockHandler.loadAll();

        loadParticleConfiguration(launchpadSection);
    }

    /**
     * Checks whether the given material is not used for launch pads
     *
     * @param material <p>The material to check</p>
     * @return <p>True if the given material is not used for launchpads</p>
     */
    public boolean isNotLaunchpadMaterial(@NotNull Material material) {
        return !this.launchpadMaterials.contains(material);
    }

    /**
     * Checks whether the given material is whitelisted
     *
     * @param material <p>The material to check</p>
     * @return <p>True if the material is whitelisted</p>
     */
    public boolean isMaterialWhitelisted(@NotNull Material material) {
        return this.materialWhitelist.isEmpty() || this.materialWhitelist.contains(material);
    }

    /**
     * Gets the default horizontal velocity for launchpads
     *
     * @param material <p>The material of the launchpad</p>
     * @return <p>The default horizontal velocity</p>
     */
    public double getHorizontalVelocity(@NotNull Material material) {
        double materialVelocity = fileConfiguration.getDouble(
                String.format("launchpad.materialVelocities.%s.horizontalVelocity", material.name()), -1);
        if (materialVelocity >= 0) {
            return materialVelocity;
        }
        return Math.max(this.horizontalVelocity, 0);
    }

    /**
     * Gets the default vertical velocity for launchpads
     *
     * @param material <p>The material of the launchpad</p>
     * @return <p>The default vertical velocity</p>
     */
    public double getVerticalVelocity(@NotNull Material material) {
        double materialVelocity = fileConfiguration.getDouble(
                String.format("launchpad.materialVelocities.%s.verticalVelocity", material.name()), -1);
        if (materialVelocity >= 0) {
            return materialVelocity;
        }
        return Math.max(this.verticalVelocity, 0);
    }

    /**
     * Loads a list of materials as a set
     *
     * @param launchpadSection <p>The configuration section to read</p>
     * @param key              <p>The configuration key containing the materials</p>
     * @return <p>The loaded materials as a set</p>
     */
    private Set<Material> loadMaterials(ConfigurationSection launchpadSection, String key) {
        Set<Material> loadedMaterials = new HashSet<>();
        List<?> materialWhitelist = launchpadSection.getList(key);
        if (materialWhitelist != null) {
            loadedMaterials.addAll(MaterialHelper.loadMaterialList(materialWhitelist, "+",
                    Launchpad.getInstance().getLogger()));
        }
        // If a non-block material is specified, simply ignore it
        loadedMaterials.removeIf((item) -> !item.isBlock());

        return loadedMaterials;
    }

    /**
     * Adds a trail behind the player with the given id
     *
     * @param playerId <p>The id of the player to add the trail to</p>
     */
    public void addTrail(UUID playerId) {
        if (trailSpawner != null) {
            trailSpawner.startTrail(playerId);
        }
    }

    /**
     * Removes the trail behind the player with the given id
     *
     * @param playerId <p>The id of the player to remove the trail for</p>
     */
    public void removeTrail(UUID playerId) {
        if (trailSpawner != null) {
            trailSpawner.removeTrail(playerId);
        }
    }

    /**
     * Loads configuration values related to launchpad particles
     *
     * @param launchpadSection <p>The configuration section containing launchpad values</p>
     */
    private void loadParticleConfiguration(ConfigurationSection launchpadSection) {
        ConfigurationSection particlesSection = launchpadSection.getConfigurationSection("particles");
        if (particlesSection == null) {
            Launchpad.log(Level.WARNING, "Unable to load particles configuration. " +
                    "The \"particles\" configuration section is missing.");
            return;
        }
        boolean previousEnabled = this.particlesEnabled;
        boolean previousTrailsEnabled = this.trailsEnabled;
        this.particlesEnabled = particlesSection.getBoolean("enabled", false);
        this.trailsEnabled = particlesSection.getBoolean("trailsEnabled", false);

        // Cancel previous particle spawning task if previously enabled
        if (previousEnabled) {
            Bukkit.getScheduler().cancelTask(particleTaskId);
            ParticleHelper.clearStoredCalculations(particleStoredCalculationsId);
        }
        if (previousTrailsEnabled) {
            Bukkit.getScheduler().cancelTask(particleTrailTaskId);
        }

        // Start particle spawning if enabled
        if (this.particlesEnabled) {
            loadLaunchpadParticleConfig(particlesSection);
        }

        if (this.trailsEnabled) {
            loadTrailParticleConfig(particlesSection);
        }
    }

    /**
     * Loads all trail particle-related configuration values
     *
     * @param particlesSection <p>The configuration section containing particle options</p>
     */
    private void loadTrailParticleConfig(ConfigurationSection particlesSection) {
        Particle trailType;
        try {
            trailType = Particle.valueOf(particlesSection.getString("trailType"));
        } catch (IllegalArgumentException | NullPointerException exception) {
            trailType = Particle.EGG_CRACK;
        }
        boolean randomTrailType = particlesSection.getBoolean("randomTrailType", false);
        List<String> randomTrailList = particlesSection.getStringList("randomTrailWhitelist");
        Set<Particle> randomTrailWhitelist = new HashSet<>();
        for (String string : randomTrailList) {
            try {
                randomTrailWhitelist.add(Particle.valueOf(string));
            } catch (IllegalArgumentException exception) {
                Launchpad.log(Level.WARNING, "Unable to parse particle " + string +
                        " from the random trail type whitelist.");
            }
        }
        trailSpawner = new ParticleTrailSpawner(trailType, randomTrailType, new ArrayList<>(randomTrailWhitelist));
        particleTrailTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Launchpad.getInstance(),
                trailSpawner, 20, particlesSection.getInt("trailSpawnDelay", 1));
    }

    /**
     * Loads all launchpad particle-related configuration values
     *
     * @param particlesSection <p>The configuration section containing particle options</p>
     */
    private void loadLaunchpadParticleConfig(ConfigurationSection particlesSection) {
        ConfigurationSection particleSection = particlesSection.getConfigurationSection("particle");
        ParticleConfig particleConfig = null;
        if (particleSection != null) {
            particleConfig = new ParticleConfig(particleSection);
        }

        // Load any per-material configuration options
        Map<Material, ParticleConfig> materialConfigs;
        ConfigurationSection perMaterialSection = particlesSection.getConfigurationSection("materialParticles");
        if (perMaterialSection != null) {
            materialConfigs = loadMaterialParticleConfigs(perMaterialSection);
        } else {
            materialConfigs = new HashMap<>();
        }

        if (particleConfig != null) {
            ParticleSpawner particleSpawner = new ParticleSpawner(particleConfig, materialConfigs, this::getLaunchpadBlocks);
            particleTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Launchpad.getInstance(), particleSpawner,
                    20, particlesSection.getInt("spawnDelay", 20));
            particleStoredCalculationsId = particleSpawner.getStoredCalculationId();
        }
    }

    private Collection<Block> getLaunchpadBlocks() {
        Set<Block> blocks = new HashSet<>();
        LaunchpadBlockHandler.getAll().forEach((item) -> blocks.add(item.getBlock()));
        return blocks;
    }

    /**
     * Loads all per-material particle configuration options
     *
     * @param perMaterialSection <p>The configuration section containing per-material particle options</p>
     * @return <p>The loaded per-material particle configuration options</p>
     */
    private @NotNull Map<Material, ParticleConfig> loadMaterialParticleConfigs(
            @NotNull ConfigurationSection perMaterialSection) {
        Map<Material, ParticleConfig> materialConfigs = new HashMap<>();
        for (String key : perMaterialSection.getKeys(false)) {
            Set<Material> materials = MaterialHelper.loadMaterialString(key, "+", Launchpad.getInstance().getLogger());
            ConfigurationSection materialSection = perMaterialSection.getConfigurationSection(key);
            if (materialSection == null) {
                continue;
            }
            ParticleConfig materialParticleConfig = new ParticleConfig(materialSection);
            for (Material material : materials) {
                materialConfigs.put(material, materialParticleConfig);
            }
        }
        return materialConfigs;
    }

}
