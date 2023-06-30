package net.knarcraft.launchpad.listener;

import net.knarcraft.launchpad.Launchpad;
import net.knarcraft.launchpad.config.LaunchpadConfiguration;
import net.knarcraft.launchpad.launchpad.LaunchpadBlock;
import net.knarcraft.launchpad.launchpad.LaunchpadBlockHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A listener for any
 */
public class LaunchpadUseListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPressurePlateUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL || !event.hasBlock() || event.getClickedBlock() == null) {
            return;
        }

        // A registered launchpad ignores material checks
        LaunchpadBlock launchpad = LaunchpadBlockHandler.getLaunchpadBlock(event.getClickedBlock());
        if (launchpad != null) {
            launch(event.getPlayer(), launchpad);
            return;
        }

        // Check if the material is a valid launchpad
        if (Launchpad.getInstance().getConfiguration().isNotLaunchpadMaterial(event.getClickedBlock().getType())) {
            return;
        }
        launch(event.getPlayer(), event.getClickedBlock().getType());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null || isSameLocation(event.getFrom(), event.getTo())) {
            return;
        }

        // If the player is standing on a non-full block, event.getTo will give the correct block, but if not, the 
        // block below has to be checked instead.
        Block block = event.getTo().getBlock();
        if (block.getType().isAir()) {
            block = event.getTo().clone().subtract(0, 0.2, 0).getBlock();
            // Only trigger hit detection for passable blocks if the player is in the block
            if (block.isPassable()) {
                return;
            }
        }

        // Pressure plates are detected in onPressurePlateUse instead
        Material type = block.getType();
        if (Tag.PRESSURE_PLATES.isTagged(type)) {
            return;
        }

        LaunchpadBlock launchpad = LaunchpadBlockHandler.getLaunchpadBlock(block);
        // Ignore material check if the block is a registered launchpad
        if (launchpad == null && Launchpad.getInstance().getConfiguration().isNotLaunchpadMaterial(type)) {
            return;
        }

        Player player = event.getPlayer();

        // Launch the player
        if (launchpad != null) {
            launch(player, launchpad);
        } else {
            launch(player, type);
        }
    }

    /**
     * Launches a player that hit a launchpad
     *
     * @param player   <p>The player to launch</p>
     * @param material <p>The material used for the launchpad</p>
     */
    private void launch(@NotNull Player player, @NotNull Material material) {
        LaunchpadConfiguration configuration = Launchpad.getInstance().getConfiguration();
        launch(player, player.getFacing(), configuration.getHorizontalVelocity(material),
                configuration.getVerticalVelocity(material));
    }

    /**
     * Launches a player that hit a registered launchpad
     *
     * @param player         <p>The player to launch</p>
     * @param launchpadBlock <p>The launchpad the player hit</p>
     */
    private void launch(@NotNull Player player, @NotNull LaunchpadBlock launchpadBlock) {
        BlockFace directionFace = launchpadBlock.getFixedDirection();
        if (directionFace == null) {
            directionFace = player.getFacing();
        }
        launch(player, directionFace, launchpadBlock.getHorizontalVelocity(), launchpadBlock.getVerticalVelocity());
    }

    /**
     * Launches a player that hit a launchpad
     *
     * @param player             <p>The player that hit the launchpad</p>
     * @param directionFace      <p>The direction to launch the player</p>
     * @param horizontalVelocity <p>The horizontal velocity to apply to the player</p>
     * @param verticalVelocity   <p>The vertical velocity to apply to the player</p>
     */
    private void launch(@NotNull Player player, @NotNull BlockFace directionFace, double horizontalVelocity,
                        double verticalVelocity) {
        Vector direction = directionFace.getDirection();
        direction = direction.multiply(horizontalVelocity);
        direction = direction.add(new Vector(0, verticalVelocity, 0));
        player.setVelocity(direction);
    }

    /**
     * Checks if two locations are the same, excluding rotation
     *
     * @param location1 <p>The first location to check</p>
     * @param location2 <p>The second location to check</p>
     * @return <p>True if the locations are the same, excluding rotation</p>
     */
    private boolean isSameLocation(Location location1, Location location2) {
        return location1.getX() == location2.getX() && location1.getY() == location2.getY() &&
                location1.getZ() == location2.getZ();
    }

}
