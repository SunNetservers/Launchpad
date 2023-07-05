package net.knarcraft.launchpad.command;

import net.knarcraft.launchpad.launchpad.ModificationAction;
import net.knarcraft.launchpad.launchpad.ModificationRequest;
import net.knarcraft.launchpad.launchpad.ModificationRequestHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The command used to modify launchpads
 */
public class LaunchpadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("This command can only be used by a player");
            return false;
        }

        if (arguments.length < 1) {
            return false;
        }
        ModificationAction action = ModificationAction.getFromCommandName(arguments[0]);
        if (action == null) {
            return false;
        }

        // Make sure arguments are correct
        if (arguments.length <= action.neededArguments() || !hasValidArguments(action, arguments)) {
            //TODO: Perhaps display the current value instead when missing an argument?
            return false;
        }

        // Register the modification request
        switch (action) {
            case ADD, REMOVE ->
                    ModificationRequestHandler.addRequest(player.getUniqueId(), new ModificationRequest(action, null));
            case VERTICAL_VELOCITY, HORIZONTAL_VELOCITY, FIXED_DIRECTION -> ModificationRequestHandler.addRequest(
                    player.getUniqueId(), new ModificationRequest(action, nullArgument(arguments[1])));
            case ABORT -> {
                // Retrieving modification requests also removes them
                ModificationRequestHandler.getRequests(player.getUniqueId());
                commandSender.sendMessage("Launchpad modifications cleared");
                return true;
            }
            case VELOCITIES -> {
                ModificationRequestHandler.addRequest(player.getUniqueId(), new ModificationRequest(
                        ModificationAction.HORIZONTAL_VELOCITY, nullArgument(arguments[1])));
                ModificationRequestHandler.addRequest(player.getUniqueId(), new ModificationRequest(
                        ModificationAction.VERTICAL_VELOCITY, nullArgument(arguments[2])));
            }
        }

        commandSender.sendMessage("Right-click the block to modify launchpad properties for");
        return true;
    }

    /**
     * Checks whether the given action has valid arguments
     *
     * @param action    <p>The action to check</p>
     * @param arguments <p>The arguments provided</p>
     * @return <p>True if the arguments are valid for the action</p>
     */
    private boolean hasValidArguments(@NotNull ModificationAction action, @NotNull String[] arguments) {
        for (int i = 0; i < action.neededArguments(); i++) {
            if (!action.isValidArgument(arguments[i + 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts the string "null" to a real null
     *
     * @param argument <p>The argument to null</p>
     * @return <p>The nullified argument</p>
     */
    private @Nullable String nullArgument(@NotNull String argument) {
        return argument.equalsIgnoreCase("null") ? null : argument;
    }

}
