package net.knarcraft.launchpad.config;

/**
 * The mode used for spawning particles
 */
public enum ParticleMode {

    /**
     * Spawns the set amount of particles on a single point in the world
     */
    SINGLE,

    /**
     * Spawns the set amount of particles in a square around the block
     */
    SQUARE,

    /**
     * Spawns the set amount of particles in a circle around the block
     */
    CIRCLE,

    /**
     * Spawns the set amount of particles in a triangle centered on the block
     */
    PYRAMID,

}
