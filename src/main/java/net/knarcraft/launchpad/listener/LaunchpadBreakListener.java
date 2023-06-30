package net.knarcraft.launchpad.listener;

import net.knarcraft.launchpad.launchpad.LaunchpadBlockHandler;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Collection;

/**
 * A listener for broken launchpads
 */
public class LaunchpadBreakListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLaunchpadBreak(BlockBreakEvent event) {
        unregisterLaunchpad(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLaunchpadBurn(BlockBurnEvent event) {
        unregisterLaunchpad(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPistonLaunchpadPush(BlockPistonExtendEvent event) {
        unregisterLaunchpad(event.getBlocks());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPistonLaunchpadPull(BlockPistonRetractEvent event) {
        unregisterLaunchpad(event.getBlocks());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLaunchpadExplode(EntityExplodeEvent event) {
        unregisterLaunchpad(event.blockList());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLaunchpadExplode(BlockExplodeEvent event) {
        unregisterLaunchpad(event.blockList());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLeafLaunchpadDecay(LeavesDecayEvent event) {
        unregisterLaunchpad(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLaunchpadChange(EntityChangeBlockEvent event) {
        if (event.getTo().isAir()) {
            unregisterLaunchpad(event.getBlock());
        }
    }

    /**
     * Unregisters the given blocks if it's a registered launchpad
     *
     * @param blocks <p>The blocks to unregister</p>
     */
    private void unregisterLaunchpad(Collection<Block> blocks) {
        for (Block block : blocks) {
            unregisterLaunchpad(block);
        }
    }

    /**
     * Unregisters the given block if it's a registered launchpad
     *
     * @param block <p>The block to unregister</p>
     */
    private void unregisterLaunchpad(Block block) {
        LaunchpadBlockHandler.unregisterLaunchpadBlock(block);
    }

}
