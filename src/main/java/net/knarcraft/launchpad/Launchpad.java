package net.knarcraft.launchpad;

import net.knarcraft.knarlib.formatting.StringFormatter;
import net.knarcraft.knarlib.formatting.Translator;
import net.knarcraft.knarlib.property.ColorConversion;
import net.knarcraft.launchpad.command.LaunchpadCommand;
import net.knarcraft.launchpad.command.LaunchpadTabCompleter;
import net.knarcraft.launchpad.command.ReloadCommand;
import net.knarcraft.launchpad.config.LaunchpadConfiguration;
import net.knarcraft.launchpad.config.LaunchpadMessage;
import net.knarcraft.launchpad.listener.LaunchpadBreakListener;
import net.knarcraft.launchpad.listener.LaunchpadModifyListener;
import net.knarcraft.launchpad.listener.LaunchpadUseListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * The launchpad plugin's main class
 */
public final class Launchpad extends JavaPlugin {

    private static Launchpad instance;
    private LaunchpadConfiguration launchpadConfiguration;
    private Translator translator;
    private StringFormatter stringFormatter;

    /**
     * Gets an instance of this plugin
     *
     * @return <p>An instance of this plugin</p>
     */
    public static Launchpad getInstance() {
        return instance;
    }

    /**
     * Reloads this plugin
     */
    public void reload() {
        reloadConfig();
        this.getConfiguration().load(this.getConfig());
        this.translator.loadLanguages(this.getDataFolder(), "en", getConfig().getString("language", "en"));
    }

    /**
     * Logs a message
     *
     * @param level   <p>The message level to log at</p>
     * @param message <p>The message to log</p>
     */
    public static void log(Level level, String message) {
        Launchpad.getInstance().getLogger().log(level, message);
    }

    /**
     * Gets this plugin's configuration
     *
     * @return <p>This plugin's configuration</p>
     */
    public LaunchpadConfiguration getConfiguration() {
        return launchpadConfiguration;
    }

    /**
     * Gets the string formatter used to format strings
     *
     * @return <p>The string formatter</p>
     */
    public StringFormatter getStringFormatter() {
        return this.stringFormatter;
    }

    @Override
    public void onEnable() {
        Launchpad.instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        Translator translator = new Translator();
        translator.registerMessageCategory(LaunchpadMessage.ERROR_PLAYER_ONLY);
        translator.setColorConversion(ColorConversion.RGB);
        translator.loadLanguages(this.getDataFolder(), "en", getConfig().getString("language", "en"));
        this.translator = translator;
        this.stringFormatter = new StringFormatter(this.getDescription().getName(), translator);
        this.stringFormatter.setColorConversion(ColorConversion.RGB);
        this.stringFormatter.setNamePrefix("#FFE34C[");
        this.stringFormatter.setNameSuffix("#FFE34C]");


        // Register events
        Bukkit.getPluginManager().registerEvents(new LaunchpadUseListener(), this);
        Bukkit.getPluginManager().registerEvents(new LaunchpadBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new LaunchpadModifyListener(), this);

        // Register commands
        registerCommand("reload", new ReloadCommand(), null);
        registerCommand("launchpad", new LaunchpadCommand(), new LaunchpadTabCompleter());

        this.launchpadConfiguration = new LaunchpadConfiguration(getConfig());
    }

    /**
     * Registers a command
     *
     * @param commandName     <p>The name of the command to register (defined in plugin.yml)</p>
     * @param commandExecutor <p>The executor for the command</p>
     * @param tabCompleter    <p>The tab-completer to use, or null</p>
     */
    private void registerCommand(@NotNull String commandName, @NotNull CommandExecutor commandExecutor,
                                 @Nullable TabCompleter tabCompleter) {
        PluginCommand command = this.getCommand(commandName);
        if (command != null) {
            command.setExecutor(commandExecutor);
            if (tabCompleter != null) {
                command.setTabCompleter(tabCompleter);
            }
        } else {
            log(Level.SEVERE, "Unable to register the command " + commandName);
        }
    }

}
