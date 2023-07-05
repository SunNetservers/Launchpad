package net.knarcraft.launchpad.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * A task for spawning trails behind players
 */
public class ParticleTrailSpawner implements Runnable {

    private final Set<UUID> playersWithTrails = new HashSet<>();
    private final Map<UUID, Particle> playerParticles = new HashMap<>();
    private final Random random = new Random();
    private final Particle particle;
    private final boolean randomTrailType;
    private final List<Particle> randomTrailTypes;

    /**
     * Instantiates a new particle trail spawner
     *
     * @param particle         <p>The type of particle used for the trail</p>
     * @param randomTrailType  <p>Whether to use a random trail type each time a player is launched</p>
     * @param randomTrailTypes <p>The types of particles to use for random trails</p>
     */
    public ParticleTrailSpawner(@NotNull Particle particle, boolean randomTrailType,
                                @NotNull List<Particle> randomTrailTypes) {
        this.particle = particle;
        this.randomTrailType = randomTrailType;
        this.randomTrailTypes = randomTrailTypes;
    }

    @Override
    public void run() {
        Set<UUID> offlinePlayers = new HashSet<>();
        for (UUID playerId : playersWithTrails) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
                offlinePlayers.add(playerId);
                continue;
            }

            Location playerLocation = player.getLocation();
            World playerWorld = playerLocation.getWorld();
            if (playerWorld == null) {
                continue;
            }

            Particle spawnParticle;
            if (randomTrailType) {
                spawnParticle = playerParticles.get(playerId);
                if (spawnParticle == null) {
                    spawnParticle = this.particle;
                }
            } else {
                spawnParticle = this.particle;
            }
            playerWorld.spawnParticle(spawnParticle, playerLocation, 0, 0, 0, 0, 0);
        }
        playersWithTrails.removeAll(offlinePlayers);

    }

    /**
     * Removes the trail behind the player with the given id
     *
     * @param playerId <p>The id of the player to remove the trail for</p>
     */
    public void removeTrail(UUID playerId) {
        this.playersWithTrails.remove(playerId);
        this.playerParticles.remove(playerId);
    }

    /**
     * Adds a trail behind the player with the given id
     *
     * @param playerId <p>The id of the player to add the trail to</p>
     */
    public void startTrail(UUID playerId) {
        this.playerParticles.put(playerId, randomParticle());
        this.playersWithTrails.add(playerId);
    }

    /**
     * Gets a random particle
     *
     * @return <p>A random particle</p>
     */
    private Particle randomParticle() {
        Particle spawnParticle = null;
        while (spawnParticle == null || spawnParticle.getDataType() != Void.class) {
            spawnParticle = randomTrailTypes.get(random.nextInt(randomTrailTypes.size()));
        }
        return spawnParticle;
    }

}
