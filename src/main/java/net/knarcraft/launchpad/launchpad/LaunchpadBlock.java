package net.knarcraft.launchpad.launchpad;

import net.knarcraft.launchpad.Launchpad;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A block registered as a launchpad
 */
public class LaunchpadBlock {

    private final @NotNull Block block;
    private double horizontalVelocity;
    private double verticalVelocity;
    private @Nullable BlockFace fixedDirection;

    /**
     * Instantiates a new launchpad block
     *
     * @param block <p>The block to register this launchpad block for</p>
     */
    public LaunchpadBlock(@NotNull Block block) {
        this.block = block;
        this.horizontalVelocity = -1;
        this.verticalVelocity = -1;
        this.fixedDirection = null;
    }

    /**
     * Instantiates a new launchpad block
     *
     * @param block              <p>The block to register this launchpad block for</p>
     * @param horizontalVelocity <p>The horizontal velocity of the launchpad</p>
     * @param verticalVelocity   <p>The vertical velocity of the launchpad</p>
     * @param fixedDirection     <p>The fixed direction of the launchpad</p>
     */
    public LaunchpadBlock(@NotNull Block block, double horizontalVelocity, double verticalVelocity,
                          @Nullable BlockFace fixedDirection) {
        this.block = block;
        setHorizontalVelocity(horizontalVelocity);
        setVerticalVelocity(verticalVelocity);
        setFixedDirection(fixedDirection);
    }

    /**
     * Gets the block that contains this launchpad
     *
     * @return <p>This launchpad's block</p>
     */
    public @NotNull Block getBlock() {
        return this.block;
    }

    /**
     * Gets the horizontal velocity of this launchpad
     *
     * @return <p>The horizontal velocity of this launchpad</p>
     */
    public double getHorizontalVelocity() {
        if (this.horizontalVelocity >= 0) {
            return this.horizontalVelocity;
        } else {
            return Launchpad.getInstance().getConfiguration().getHorizontalVelocity(this.block.getType());
        }
    }

    /**
     * Gets the vertical velocity of this launchpad
     *
     * @return <p>The vertical velocity of this launchpad</p>
     */
    public double getVerticalVelocity() {
        if (this.verticalVelocity >= 0) {
            return this.verticalVelocity;
        } else {
            return Launchpad.getInstance().getConfiguration().getVerticalVelocity(this.block.getType());
        }
    }

    /**
     * Gets the fixed direction of this launchpad block
     *
     * @return <p>The fixed direction, or null if not fixed</p>
     */
    public @Nullable BlockFace getFixedDirection() {
        return this.fixedDirection;
    }

    /**
     * Sets the horizontal velocity of this launchpad
     *
     * @param horizontalVelocity <p>The horizontal velocity to set</p>
     */
    public void setHorizontalVelocity(double horizontalVelocity) {
        this.horizontalVelocity = Math.max(0, horizontalVelocity);
    }

    /**
     * Sets the vertical velocity of this launchpad
     *
     * @param verticalVelocity <p>The vertical velocity to set</p>
     */
    public void setVerticalVelocity(double verticalVelocity) {
        this.verticalVelocity = Math.max(0, verticalVelocity);
    }

    /**
     * Sets the fixed direction of this launchpad
     *
     * @param fixedDirection <p>The fixed direction, or null to unset the fixed direction</p>
     */
    public void setFixedDirection(@Nullable BlockFace fixedDirection) {
        // Make sure the fixed direction is in the correct plane by multiplying it by the normal vector to the 
        // North x West plane.
        if (fixedDirection != null && (fixedDirection.getDirection().dot(BlockFace.WEST.getDirection().crossProduct(
                BlockFace.NORTH.getDirection()))) != 0) {
            return;
        }
        this.fixedDirection = fixedDirection;
    }

}
