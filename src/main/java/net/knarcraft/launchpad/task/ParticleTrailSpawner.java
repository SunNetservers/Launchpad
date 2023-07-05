package net.knarcraft.launchpad.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A task for spawning trails behind players
 */
public class ParticleTrailSpawner implements Runnable {

    private final Set<UUID> playersWithTrails = new HashSet<>();
    private final Particle particle;

    /**
     * Instantiates a new particle trail spawner
     *
     * @param particle <p>The type of particle used for the trail</p>
     */
    public ParticleTrailSpawner(@NotNull Particle particle) {
        this.particle = particle;
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
            playerWorld.spawnParticle(this.particle, playerLocation, 1);
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
    }

    /**
     * Adds a trail behind the player with the given id
     *
     * @param playerId <p>The id of the player to add the trail to</p>
     */
    public void startTrail(UUID playerId) {
        this.playersWithTrails.add(playerId);
    }

}
