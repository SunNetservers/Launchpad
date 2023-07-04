package net.knarcraft.launchpad.task;

import net.knarcraft.launchpad.config.LaunchpadParticleConfig;
import net.knarcraft.launchpad.launchpad.LaunchpadBlock;
import net.knarcraft.launchpad.launchpad.LaunchpadBlockHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A runnable tasks that spawns particles at every launchpad
 */
public class ParticleSpawner implements Runnable {

    private Vector[] pyramidVectors;
    private double[][] circleCoordinates;
    private double[][] sphereCoordinates;

    private final LaunchpadParticleConfig particleConfig;
    private final Map<Material, LaunchpadParticleConfig> materialConfigs;
    private LaunchpadBlock processingLaunchpad = null;

    /**
     * Instantiates a new particle spawner
     *
     * @param particleConfig <p>The configuration for the particle to spawn</p>
     */
    public ParticleSpawner(LaunchpadParticleConfig particleConfig, Map<Material,
            LaunchpadParticleConfig> materialConfigs) {
        this.particleConfig = particleConfig;
        this.materialConfigs = materialConfigs;

        this.pyramidVectors = null;
        this.circleCoordinates = null;
        this.sphereCoordinates = null;
    }

    @Override
    public void run() {
        for (LaunchpadBlock launchpad : LaunchpadBlockHandler.getAll()) {
            // Ignore launchpads in unloaded chunks
            if (!launchpad.getBlock().getChunk().isLoaded()) {
                continue;
            }

            Location location = launchpad.getBlock().getLocation().clone();
            World world = location.getWorld();
            if (world == null) {
                continue;
            }

            // Store the currently processed launchpad for height calculation
            processingLaunchpad = launchpad;

            switch (getParticleConfig().getParticleMode()) {
                case SINGLE -> spawnParticle(world, location.clone().add(
                        0.5, getParticleConfig().getHeightOffset(), 0.5));
                case SQUARE -> drawSquare(world, location);
                case CIRCLE -> drawCircle(world, location);
                case PYRAMID -> drawPyramid(world, location);
                case SPHERE -> drawSphere(world, location);
            }
        }
    }

    /**
     * Gets the particle config to use for the current launchpad's material
     *
     * @return <p>The particle config to use</p>
     */
    private LaunchpadParticleConfig getParticleConfig() {
        LaunchpadParticleConfig materialConfig = this.materialConfigs.get(processingLaunchpad.getBlock().getType());
        if (materialConfig != null) {
            return materialConfig;
        }
        return this.particleConfig;
    }

    /**
     * Spawns a sphere of particles at the given location
     *
     * @param world    <p>The world to spawn the particles in</p>
     * @param location <p>The location of the block to spawn the particles at</p>
     */
    private void drawSphere(@NotNull World world, @NotNull Location location) {
        // For spheres, densities below 0.1 has weird bugs such as blinking in and out of existence, and floating point
        // errors when calculating the length of circleCoordinates
        double density = Math.max(1, getParticleConfig().getParticleDensity());
        // Store calculations for improved efficiency
        if (sphereCoordinates == null) {
            int length = (int) Math.ceil((180 / density));
            sphereCoordinates = new double[length * 3][];
            int i = 0;
            for (float x = 0; x < 180; x += density) {
                if (i >= sphereCoordinates.length) {
                    continue;
                }
                sphereCoordinates[i++] = new double[]{(0.5 * Math.sin(x)) + 0.5,
                        getParticleConfig().getHeightOffset() + 0.5, (0.5 * Math.cos(x)) + 0.5};
                sphereCoordinates[i++] = new double[]{(0.5 * Math.sin(x)) + 0.5,
                        getParticleConfig().getHeightOffset() + 0.5 + (0.5 * Math.cos(x)), 0.5};
                sphereCoordinates[i++] = new double[]{0.5,
                        getParticleConfig().getHeightOffset() + 0.5 + (0.5 * Math.sin(x)), (0.5 * Math.cos(x)) + 0.5};
            }
        }

        // Spawn particles on the stored locations, relative to the launchpad
        for (double[] sphereCoordinate : sphereCoordinates) {
            spawnParticle(world, location.clone().add(sphereCoordinate[0], sphereCoordinate[1], sphereCoordinate[2]));
        }
    }

    /**
     * Spawns a pyramid of particles at the given location
     *
     * @param world    <p>The world to spawn the particles in</p>
     * @param location <p>The location of the block to spawn the particles at</p>
     */
    private void drawPyramid(@NotNull World world, @NotNull Location location) {
        // Draw the bottom of the pyramid
        drawSquare(world, location);

        // Store calculations for improved efficiency
        if (pyramidVectors == null) {
            // The 0.5 offsets are required for the angle of the pyramid's 4 lines to be correct
            double coordinateMin = -0.5 * getParticleConfig().getHeightOffset();
            double coordinateMax = 1 + (0.5 * getParticleConfig().getHeightOffset());

            pyramidVectors = new Vector[5];
            // The vector from the origin to the top of the pyramid
            pyramidVectors[0] = new Vector(0.5, 1 + getParticleConfig().getHeightOffset(), 0.5);
            // The vectors from the top of the pyramid towards each corner
            pyramidVectors[1] = new Vector(coordinateMin, 0, coordinateMin).subtract(pyramidVectors[0]).normalize();
            pyramidVectors[2] = new Vector(coordinateMax, 0, coordinateMin).subtract(pyramidVectors[0]).normalize();
            pyramidVectors[3] = new Vector(coordinateMin, 0, coordinateMax).subtract(pyramidVectors[0]).normalize();
            pyramidVectors[4] = new Vector(coordinateMax, 0, coordinateMax).subtract(pyramidVectors[0]).normalize();
        }

        Location topLocation = location.clone().add(pyramidVectors[0]);
        for (double x = 0; x <= 1.2; x += getParticleConfig().getParticleDensity()) {
            spawnParticle(world, topLocation.clone().add(pyramidVectors[1].clone().multiply(x)));
            spawnParticle(world, topLocation.clone().add(pyramidVectors[2].clone().multiply(x)));
            spawnParticle(world, topLocation.clone().add(pyramidVectors[3].clone().multiply(x)));
            spawnParticle(world, topLocation.clone().add(pyramidVectors[4].clone().multiply(x)));
        }
    }

    /**
     * Spawns a circle of particles at the given location
     *
     * @param world    <p>The world to spawn the particles in</p>
     * @param location <p>The location of the block to spawn the particles at</p>
     */
    private void drawCircle(@NotNull World world, @NotNull Location location) {
        // For circles, densities below 0.1 has weird bugs such as blinking in and out of existence, and floating point
        // errors when calculating the length of circleCoordinates
        double density = Math.max(1, getParticleConfig().getParticleDensity());
        // Store calculations for improved efficiency
        if (circleCoordinates == null) {
            circleCoordinates = new double[(int) Math.ceil((180 / density))][];
            int i = 0;
            for (float x = 0; x < 180; x += density) {
                if (i >= circleCoordinates.length) {
                    continue;
                }
                circleCoordinates[i++] = new double[]{(0.5 * Math.sin(x)) + 0.5, (0.5 * Math.cos(x)) + 0.5};
            }
        }

        // Spawn particles on the stored locations, relative to the launchpad
        for (double[] circleCoordinate : circleCoordinates) {
            spawnParticle(world, location.clone().add(circleCoordinate[0], getParticleConfig().getHeightOffset(), circleCoordinate[1]));
        }
    }

    /**
     * Spawns a square of particles at the given location
     *
     * @param world    <p>The world to spawn the particles in</p>
     * @param location <p>The location of the block to spawn the particles at</p>
     */
    private void drawSquare(@NotNull World world, @NotNull Location location) {
        for (float x = 0; x <= 1; x += getParticleConfig().getParticleDensity()) {
            spawnParticle(world, location.clone().add(x, getParticleConfig().getHeightOffset(), 0));
            spawnParticle(world, location.clone().add(x, getParticleConfig().getHeightOffset(), 1));
            spawnParticle(world, location.clone().add(0, getParticleConfig().getHeightOffset(), x));
            spawnParticle(world, location.clone().add(1, getParticleConfig().getHeightOffset(), x));
        }
    }

    /**
     * Spawns the specified particle at the given location
     *
     * @param world    <p>The world to spawn the particle in</p>
     * @param location <p>The location to spawn the particle at</p>
     */
    private void spawnParticle(@NotNull World world, @NotNull Location location) {
        world.spawnParticle(getParticleConfig().getParticleType(),
                location.add(0, getBlockHeight(processingLaunchpad), 0), getParticleConfig().getParticleAmount(),
                getParticleConfig().getOffsetX(), getParticleConfig().getOffsetY(), getParticleConfig().getOffsetZ(),
                getParticleConfig().getExtra());
    }

    /**
     * Gets the height of the launchpad block at the given location
     *
     * @param launchpad <p>The launchpad to check</p>
     * @return <p>The height of the block</p>
     */
    private double getBlockHeight(LaunchpadBlock launchpad) {
        double maxY = 0;
        for (BoundingBox boundingBox : launchpad.getBlock().getCollisionShape().getBoundingBoxes()) {
            if (boundingBox.getMaxY() > maxY) {
                maxY = boundingBox.getMaxY();
            }
        }
        return maxY;
    }

}
