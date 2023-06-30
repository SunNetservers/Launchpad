package net.knarcraft.launchpad.command;

import net.knarcraft.launchpad.launchpad.ModificationAction;
import net.knarcraft.launchpad.launchpad.ModificationRequest;
import net.knarcraft.launchpad.launchpad.ModificationRequestHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LaunchpadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("This command can only be used by a player");
            return false;
        }

        // TODO: Properly allow nulling (unsetting) values

        if (arguments.length < 1) {
            return false;
        }
        ModificationAction action = ModificationAction.getFromCommandName(arguments[0]);
        if (action == null) {
            return false;
        }

        // Make sure 
        if (action.needsArgument() && (arguments.length < 2 || !action.isValidArgument(arguments[1]))) {
            //TODO: Perhaps display the current value instead when missing an argument?
            return false;
        }


        // Register the modification request
        ModificationRequest request = null;
        switch (action) {
            case ADD, REMOVE -> request = new ModificationRequest(action, null);
            case VERTICAL_VELOCITY, HORIZONTAL_VELOCITY, FIXED_DIRECTION ->
                    request = new ModificationRequest(action, arguments[1]);
            case ABORT -> {
                // Retrieving modification requests also removes them
                ModificationRequestHandler.getRequests(player.getUniqueId());
                commandSender.sendMessage("Launchpad modifications cleared");
                return true;
            }
        }
        ModificationRequestHandler.addRequest(player.getUniqueId(), request);
        commandSender.sendMessage("Right-click the block to modify launchpad properties for");
        return true;
    }

}
