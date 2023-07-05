package net.knarcraft.launchpad.launchpad;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A representation of the types of actions that can be used to modify a launchpad
 */
public enum ModificationAction {

    /**
     * The action to remove a registered launchpad
     */
    REMOVE("remove", 0),

    /**
     * The action to register a launchpad
     */
    ADD("add", 0),

    /**
     * The action to modify the vertical velocity of a launchpad
     */
    VERTICAL_VELOCITY("verticalVelocity", 1),

    /**
     * The action to modify the horizontal velocity of a launchpad
     */
    HORIZONTAL_VELOCITY("horizontalVelocity", 1),

    /**
     * The action of setting the fixed direction of a launchpad
     */
    FIXED_DIRECTION("fixedDirection", 1),

    /**
     * The action of aborting previous actions
     */
    ABORT("abort", 0),

    /**
     * The action of setting both velocities at once
     */
    VELOCITIES("velocities", 2),
    ;

    private final @NotNull String commandName;
    private final int neededArguments;

    /**
     * Instantiates a new modification action
     *
     * @param commandName     <p>The name of the command used to specify this action in command input</p>
     * @param neededArguments <p>The number of arguments required for this modification action</p>
     */
    ModificationAction(@NotNull String commandName, int neededArguments) {
        this.commandName = commandName;
        this.neededArguments = neededArguments;
    }

    /**
     * Gets the command name used to specify this action in command input
     *
     * @return <p>The command name</p>
     */
    public @NotNull String getCommandName() {
        return this.commandName;
    }

    /**
     * The amount of arguments required by this modification action
     *
     * @return <p>The number of arguments required</p>
     */
    public int neededArguments() {
        return this.neededArguments;
    }

    /**
     * Checks whether the given argument is valid for this action
     *
     * @param argument <p>The argument to check</p>
     * @return <p>True if the argument is valid</p>
     */
    public boolean isValidArgument(String argument) {
        if (argument.equalsIgnoreCase("null")) {
            return true;
        }
        if (this == ModificationAction.HORIZONTAL_VELOCITY || this == ModificationAction.VERTICAL_VELOCITY ||
                this == ModificationAction.VELOCITIES) {
            try {
                return Double.parseDouble(argument) >= 0;
            } catch (NumberFormatException exception) {
                return false;
            }
        } else if (this == ModificationAction.FIXED_DIRECTION) {
            try {
                BlockFace.valueOf(argument.toUpperCase());
                return true;
            } catch (IllegalArgumentException exception) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets a modification action given its command name
     *
     * @param commandName <p>A modification action's command name</p>
     * @return <p>The matching modification action, or null if not found</p>
     */
    public static @Nullable ModificationAction getFromCommandName(@NotNull String commandName) {
        for (ModificationAction action : ModificationAction.values()) {
            if (action.getCommandName().equalsIgnoreCase(commandName)) {
                return action;
            }
        }
        return null;
    }

}
