package net.knarcraft.launchpad.config;

import net.knarcraft.launchpad.Launchpad;
import net.knarcraft.launchpad.launchpad.LaunchpadBlockHandler;
import net.knarcraft.launchpad.task.ParticleSpawner;
import net.knarcraft.launchpad.util.MaterialHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * The configuration for this plugin
 */
public class LaunchpadConfiguration {

    private @NotNull FileConfiguration fileConfiguration;
    private double horizontalVelocity;
    private double verticalVelocity;
    private boolean particlesEnabled;
    private int particleTaskId = -1;
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
            loadedMaterials.addAll(MaterialHelper.loadMaterialList(materialWhitelist));
        }
        // If a non-block material is specified, simply ignore it
        loadedMaterials.removeIf((item) -> !item.isBlock());

        return loadedMaterials;
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
        this.particlesEnabled = particlesSection.getBoolean("enabled", false);
        @NotNull Particle particleType;
        try {
            particleType = Particle.valueOf(particlesSection.getString("type"));
        } catch (IllegalArgumentException exception) {
            particleType = Particle.ASH;
        }
        int particleAmount = particlesSection.getInt("amount", 30);
        double offsetX = particlesSection.getDouble("offsetX", 0.5);
        double offsetY = particlesSection.getDouble("offsetY", 1);
        double offsetZ = particlesSection.getDouble("offsetZ", 0.5);
        double heightOffset = particlesSection.getDouble("heightOffset", 0.5);
        double particleDensity = particlesSection.getDouble("particleDensity", 0.1);
        double extra = particlesSection.getDouble("extra", 0);
        ParticleMode particleMode;
        try {
            particleMode = ParticleMode.valueOf(particlesSection.getString("mode"));
        } catch (IllegalArgumentException exception) {
            particleMode = ParticleMode.SINGLE;
        }

        // Make sure particle density is between 1 (inclusive) and 0 (exclusive)
        if (particleDensity <= 0) {
            particleDensity = 0.1;
        } else if (particleDensity > 360) {
            particleDensity = 360;
        }

        if (previousEnabled) {
            // Cancel previous particle spawning task
            Bukkit.getScheduler().cancelTask(particleTaskId);
        }

        if (this.particlesEnabled) {
            // Start particle spawning
            particleTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Launchpad.getInstance(),
                    new ParticleSpawner(particleMode, particleType, particleAmount, particleDensity, heightOffset,
                            offsetX, offsetY, offsetZ, extra), 20, 20);
        }
    }

}
