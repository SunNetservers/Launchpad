package net.knarcraft.launchpad.config;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A configuration describing a launchpad
 */
public class LaunchpadParticleConfig {

    private final ParticleMode particleMode;
    private final Particle particleType;
    private final int particleAmount;
    private final double particleDensity;
    private final double heightOffset;
    private final int spawnDelay;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double extra;

    /**
     * Instantiates a new particle config
     *
     * @param particlesSection <p>The configuration section containing the particle's settings</p>
     */
    public LaunchpadParticleConfig(@NotNull ConfigurationSection particlesSection) {
        @NotNull Particle particleType;
        try {
            particleType = Particle.valueOf(particlesSection.getString("type"));
        } catch (IllegalArgumentException | NullPointerException exception) {
            particleType = Particle.ASH;
        }
        this.particleType = particleType;
        this.particleAmount = particlesSection.getInt("amount", 30);
        this.offsetX = particlesSection.getDouble("offsetX", 0.5);
        this.offsetY = particlesSection.getDouble("offsetY", 1);
        this.offsetZ = particlesSection.getDouble("offsetZ", 0.5);
        this.heightOffset = particlesSection.getDouble("heightOffset", 0.5);
        this.extra = particlesSection.getDouble("extra", 0);
        this.spawnDelay = particlesSection.getInt("spawnDelay", 20);
        ParticleMode particleMode;
        try {
            particleMode = ParticleMode.valueOf(particlesSection.getString("mode"));
        } catch (IllegalArgumentException | NullPointerException exception) {
            particleMode = ParticleMode.SINGLE;
        }
        this.particleMode = particleMode;

        // Make sure particle density is between 1 (inclusive) and 0 (exclusive)
        double particleDensity = particlesSection.getDouble("particleDensity", 0.1);
        if (particleDensity <= 0) {
            particleDensity = 0.1;
        } else if (particleDensity > 360) {
            particleDensity = 360;
        }
        this.particleDensity = particleDensity;
    }

    /**
     * The mode to use when drawing/spawning the particle(s)
     *
     * @return <p>The particle mode</p>
     */
    public ParticleMode getParticleMode() {
        return particleMode;
    }

    /**
     * The type of particle to spawn
     *
     * @return <p>The particle type</p>
     */
    public Particle getParticleType() {
        return particleType;
    }

    /**
     * The amount of particles to spawn
     *
     * @return <p>The amount of particles</p>
     */
    public int getParticleAmount() {
        return particleAmount;
    }

    /**
     * The density of particles to use in shapes closer to 0 causes larger density
     *
     * @return <p>The particle density</p>
     */
    public double getParticleDensity() {
        return particleDensity;
    }

    /**
     * The number of blocks above the launchpad the particle(s) should spawn
     *
     * @return <p>The y-offset</p>
     */
    public double getHeightOffset() {
        return heightOffset;
    }

    /**
     * Gets the delay in ticks to wait before spawning the particle(s) again
     *
     * @return <p>The spawn delay</p>
     */
    public int getSpawnDelay() {
        return spawnDelay;
    }

    /**
     * The offset/spread of particles in the x-direction
     *
     * @return <p>The x-offset</p>
     */
    public double getOffsetX() {
        return offsetX;
    }

    /**
     * The offset/spread of particles in the y-direction
     *
     * @return <p>The y-offset</p>
     */
    public double getOffsetY() {
        return offsetY;
    }

    /**
     * The offset/spread of particles in the z-direction
     *
     * @return <p>The z-offset</p>
     */
    public double getOffsetZ() {
        return offsetZ;
    }

    /**
     * The extra value to set for the particle. Exactly what it does depends on the particle.
     *
     * @return <p>The particle's extra value</p>
     */
    public double getExtra() {
        return extra;
    }

}
