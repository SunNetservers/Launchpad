package net.knarcraft.launchpad.command;

import net.knarcraft.launchpad.Launchpad;
import net.knarcraft.launchpad.config.LaunchpadMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The command used for reloading this plugin
 */
public class ReloadCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Launchpad.getInstance().reload();
        Launchpad.getInstance().getStringFormatter().displaySuccessMessage(commandSender,
                LaunchpadMessage.SUCCESS_PLUGIN_RELOADED);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                                      @NotNull String[] strings) {
        return new ArrayList<>();
    }

}
