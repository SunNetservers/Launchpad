package net.knarcraft.launchpad.task;

import net.knarcraft.launchpad.config.ParticleMode;
import net.knarcraft.launchpad.launchpad.LaunchpadBlock;
import net.knarcraft.launchpad.launchpad.LaunchpadBlockHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A runnable tasks that spawns particles at every launchpad
 */
public class ParticleSpawner implements Runnable {

    private final ParticleMode particleMode;
    private final Particle particleType;
    private final int particleAmount;
    private final double particleDensity;
    private final double heightOffset;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double extra;

    /**
     * Instantiates a new particle spawner
     *
     * @param particleMode    <p>The mode used when spawning particles</p>
     * @param particleType    <p>The type of particle to spawn</p>
     * @param particleAmount  <p>The amount of particles to spawn at once</p>
     * @param particleDensity <p>The density of particles for particle shapes. Lower = more particles.</p>
     * @param heightOffset    <p>The height above the launchpad the particle should spawn</p>
     * @param offsetX         <p>The offset of the particle in the X direction</p>
     * @param offsetY         <p>The offset of the particle in the Y direction</p>
     * @param offsetZ         <p>The offset of the particle in the Z direction</p>
     * @param extra           <p>Extra data for the particle</p>
     */
    public ParticleSpawner(@NotNull ParticleMode particleMode, @NotNull Particle particleType, int particleAmount,
                           double particleDensity, double heightOffset, double offsetX, double offsetY, double offsetZ,
                           double extra) {
        this.particleMode = particleMode;
        this.particleType = particleType;
        this.particleAmount = particleAmount;
        this.particleDensity = particleDensity;
        this.heightOffset = heightOffset;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.extra = extra;
    }

    @Override
    public void run() {
        for (LaunchpadBlock launchpad : LaunchpadBlockHandler.getAll()) {
            if (!launchpad.getBlock().getChunk().isLoaded()) {
                continue;
            }

            Location location = launchpad.getBlock().getLocation().clone();
            World world = location.getWorld();
            if (world == null) {
                continue;
            }

            switch (particleMode) {
                case SINGLE -> spawnParticle(world, location.clone().add(0.5, heightOffset, 0.5));
                case SQUARE -> drawSquare(world, location);
                case CIRCLE -> drawCircle(world, location);
                case PYRAMID -> drawPyramid(world, location);
            }
        }
    }

    /**
     * Spawns a pyramid of particles at the given location
     *
     * @param world    <p>The world to spawn the particles in</p>
     * @param location <p>The location of the block to spawn the particles at</p>
     */
    private void drawPyramid(@NotNull World world, @NotNull Location location) {
        // Draw the bottom of the 
        drawSquare(world, location);
        // Top: 0.5, 1, 0.5. Corner 1: 0, 0, 0. Corner 2: 1, 0, 0. Corner 3: 0, 0, 1, Corner 4: 1, 0, 1
        double triangleHeight = 1;
        //TODO: This works fine for heightOffset = 1, but changing the height offset changes things. 0 offset causes
        // the pyramid to become too wide. 2 offset causes the pyramid to become too narrow.
        Vector topVector = new Vector(0.5, triangleHeight + heightOffset, 0.5);
        Location topLocation = location.clone().add(topVector);
        Vector line1Direction = new Vector(-0.5, 0, -0.5).subtract(topVector).normalize();
        Vector line2Direction = new Vector(1.5, 0, -0.5).subtract(topVector).normalize();
        Vector line3Direction = new Vector(-0.5, 0, 1.5).subtract(topVector).normalize();
        Vector line4Direction = new Vector(1.5, 0, 1.5).subtract(topVector).normalize();

        for (double x = 0; x <= triangleHeight; x += particleDensity) {
            spawnParticle(world, topLocation.clone().add(line1Direction.clone().multiply(x)));
            spawnParticle(world, topLocation.clone().add(line2Direction.clone().multiply(x)));
            spawnParticle(world, topLocation.clone().add(line3Direction.clone().multiply(x)));
            spawnParticle(world, topLocation.clone().add(line4Direction.clone().multiply(x)));
        }
    }

    /**
     * Spawns a circle of particles at the given location
     *
     * @param world    <p>The world to spawn the particles in</p>
     * @param location <p>The location of the block to spawn the particles at</p>
     */
    private void drawCircle(@NotNull World world, @NotNull Location location) {
        for (float x = 0; x < 180; x += particleDensity) {
            spawnParticle(world, location.clone().add((0.5 * Math.sin(x)) + 0.5, heightOffset,
                    (0.5 * Math.cos(x)) + 0.5));
        }
    }

    /**
     * Spawns a square of particles at the given location
     *
     * @param world    <p>The world to spawn the particles in</p>
     * @param location <p>The location of the block to spawn the particles at</p>
     */
    private void drawSquare(@NotNull World world, @NotNull Location location) {
        for (float x = 0; x < 1; x += particleDensity) {
            spawnParticle(world, location.clone().add(x, heightOffset, 0));
            spawnParticle(world, location.clone().add(x, heightOffset, 1));
            spawnParticle(world, location.clone().add(0, heightOffset, x));
            spawnParticle(world, location.clone().add(1, heightOffset, x));
        }
    }

    /**
     * Spawns the specified particle at the given location
     *
     * @param world    <p>The world to spawn the particle in</p>
     * @param location <p>The location to spawn the particle at</p>
     */
    private void spawnParticle(@NotNull World world, @NotNull Location location) {
        world.spawnParticle(particleType, location, particleAmount, offsetX, offsetY, offsetZ, extra);
    }

}
