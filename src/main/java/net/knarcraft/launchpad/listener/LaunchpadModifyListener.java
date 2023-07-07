package net.knarcraft.launchpad.listener;

import net.knarcraft.launchpad.Launchpad;
import net.knarcraft.launchpad.config.Message;
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
import java.util.UUID;

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

        UUID playerId = event.getPlayer().getUniqueId();
        Set<ModificationRequest> requests = ModificationRequestHandler.getRequests(playerId);
        if (requests == null || requests.isEmpty()) {
            return;
        }

        boolean completeSuccess = true;
        for (ModificationRequest request : requests) {
            boolean success = handleRequest(request, clicked);
            if (!success) {
                completeSuccess = false;
                // Re-schedule the request to allow the player to click a valid block
                ModificationRequestHandler.addRequest(playerId, request);
            }
        }

        if (completeSuccess) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);

            event.getPlayer().sendMessage(Message.SUCCESS_MODIFIED_LAUNCHPAD.getMessage());
        } else {
            event.getPlayer().sendMessage(Message.ERROR_NOT_WHITELISTED.getMessage());
        }
    }

    /**
     * Performs the tasks required by the given launchpad modification request
     *
     * @param request <p>The modification request to handle</p>
     * @param block   <p>The block to perform the request on</p>
     * @return <p>True if the request was successfully handled</p>
     */
    private boolean handleRequest(@NotNull ModificationRequest request, @NotNull Block block) {
        LaunchpadBlock existingLaunchpad = LaunchpadBlockHandler.getLaunchpadBlock(block);
        boolean isLaunchpad = existingLaunchpad != null;

        if (!isLaunchpad) {
            // Only allow modification of whitelisted launchpad materials
            if (!Launchpad.getInstance().getConfiguration().isMaterialWhitelisted(block.getType())) {
                return false;
            }

            existingLaunchpad = new LaunchpadBlock(block);
        }

        switch (request.modificationAction()) {
            case REMOVE -> {
                if (isLaunchpad) {
                    LaunchpadBlockHandler.unregisterLaunchpadBlock(block);
                    return true;
                }
            }
            case VERTICAL_VELOCITY -> {
                if (request.value() != null) {
                    existingLaunchpad.setVerticalVelocity(Double.parseDouble(request.value()));
                } else {
                    existingLaunchpad.setVerticalVelocity(-1);
                }
            }
            case HORIZONTAL_VELOCITY -> {
                if (request.value() != null) {
                    existingLaunchpad.setHorizontalVelocity(Double.parseDouble(request.value()));
                } else {
                    existingLaunchpad.setHorizontalVelocity(-1);
                }
            }
            case FIXED_DIRECTION -> {
                if (request.value() != null) {
                    existingLaunchpad.setFixedDirection(BlockFace.valueOf(request.value().toUpperCase()));
                } else {
                    existingLaunchpad.setFixedDirection(null);
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

        return true;
    }

}
