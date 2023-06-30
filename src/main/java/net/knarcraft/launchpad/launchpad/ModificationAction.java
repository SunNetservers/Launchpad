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
    REMOVE("remove", false),

    /**
     * The action to register a launchpad
     */
    ADD("add", false),

    /**
     * The action to modify the vertical velocity of a launchpad
     */
    VERTICAL_VELOCITY("verticalVelocity", true),

    /**
     * The action to modify the horizontal velocity of a launchpad
     */
    HORIZONTAL_VELOCITY("horizontalVelocity", true),

    /**
     * The action of setting the fixed direction of a launchpad
     */
    FIXED_DIRECTION("fixedDirection", true),

    /**
     * The action of aborting previous actions
     */
    ABORT("abort", false),
    ;

    private final @NotNull String commandName;
    private final boolean needsArgument;

    /**
     * Instantiates a new modification action
     *
     * @param commandName   <p>The name of the command used to specify this action in command input</p>
     * @param needsArgument <p>Whether the modification action requires an argument in order to be valid</p>
     */
    ModificationAction(@NotNull String commandName, boolean needsArgument) {
        this.commandName = commandName;
        this.needsArgument = needsArgument;
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
     * Gets whether this modification action requires an argument
     *
     * @return <p>True if this action requires an argument</p>
     */
    public boolean needsArgument() {
        return this.needsArgument;
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
        if (this == ModificationAction.HORIZONTAL_VELOCITY || this == ModificationAction.VERTICAL_VELOCITY) {
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
