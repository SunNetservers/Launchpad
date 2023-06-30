package net.knarcraft.launchpad.listener;

import net.knarcraft.launchpad.launchpad.LaunchpadBlock;
import net.knarcraft.launchpad.launchpad.LaunchpadBlockHandler;
import net.knarcraft.launchpad.launchpad.ModificationRequest;
import net.knarcraft.launchpad.launchpad.ModificationRequestHandler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A listener for application of launchpad requests
 */
public class LaunchpadModifyListener implements Listener {

    @EventHandler
    public void onLaunchpadClick(PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || clicked == null) {
            return;
        }

        Set<ModificationRequest> requests = ModificationRequestHandler.getRequests(event.getPlayer().getUniqueId());
        if (requests == null || requests.isEmpty()) {
            return;
        }

        for (ModificationRequest request : requests) {
            handleRequest(request, clicked);
        }

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        event.getPlayer().sendMessage("Modified launchpad at " + clicked.getLocation());
    }

    /**
     * Performs the tasks required by the given launchpad modification request
     *
     * @param request <p>The modification request to handle</p>
     * @param block   <p>The block to perform the request on</p>
     */
    private void handleRequest(@NotNull ModificationRequest request, @NotNull Block block) {
        LaunchpadBlock existingLaunchpad = LaunchpadBlockHandler.getLaunchpadBlock(block);
        boolean isLaunchpad = existingLaunchpad != null;

        if (!isLaunchpad) {
            existingLaunchpad = new LaunchpadBlock(block);
        }

        switch (request.modificationAction()) {
            case REMOVE -> {
                LaunchpadBlockHandler.unregisterLaunchpadBlock(block);
                return;
            }
            case VERTICAL_VELOCITY -> {
                if (request.value() != null) {
                    existingLaunchpad.setVerticalVelocity(Double.parseDouble(request.value()));
                }
            }
            case HORIZONTAL_VELOCITY -> {
                if (request.value() != null) {
                    existingLaunchpad.setHorizontalVelocity(Double.parseDouble(request.value()));
                }
            }
            case FIXED_DIRECTION -> {
                if (request.value() != null) {
                    existingLaunchpad.setFixedDirection(BlockFace.valueOf(request.value().toUpperCase()));
                }
            }
        }

        if (!isLaunchpad) {
            // If not already registered as a launchpad, register it. This completely handles the ADD case
            LaunchpadBlockHandler.registerLaunchpadBlock(existingLaunchpad);
        } else {
            // Save if the existing launchpad was modified
            LaunchpadBlockHandler.saveAll();
        }
    }

}
