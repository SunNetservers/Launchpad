package net.knarcraft.launchpad.config;

import net.knarcraft.launchpad.Launchpad;
import net.knarcraft.launchpad.util.ColorHelper;
import org.jetbrains.annotations.NotNull;

/**
 * A message which ca be displayed to the user
 */
public enum Message {

    /* ************** *
     * Error messages *
     * ************** */

    /**
     * The message displayed if the console tries to execute a player-only command
     */
    ERROR_PLAYER_ONLY("&cThis command must be used by a player"),

    /**
     * The message displayed if the player tries to modify launchpad property for a block outside the whitelist
     */
    ERROR_NOT_WHITELISTED("&cThe block could not be modified, as it's not whitelisted. If you want to " +
            "abort changing a launchpad, use \"/launchpad abort\""),

    /**
     * The message displayed if an un-parse-able message is given by a user
     */
    ERROR_MATERIAL_NOT_PARSE_ABLE("&cUnable to parse material: {material}"),

    /* **************** *
     * Success messages *
     * **************** */

    /**
     * The message displayed if the Launchpad plugin is reloaded
     */
    SUCCESS_PLUGIN_RELOADED("&aPlugin reloaded!"),

    /**
     * The message to display when a player uses /launchpad clear
     */
    SUCCESS_MODIFICATIONS_CLEARED("&aCleared your launchpad modification queue"),

    /**
     * The message to display to prompt the player to click a launchpad
     */
    SUCCESS_CLICK_BLOCK("&aClick the launchpad you want to create or modify"),

    /**
     * The message to display when a launchpad has been added or modified
     */
    SUCCESS_MODIFIED_LAUNCHPAD("&aThe clicked block's launchpad properties have been modified"),
    ;

    private final @NotNull String defaultMessage;

    /**
     * Instantiates a new message
     *
     * @param defaultMessage <p>The default value of the message</p>
     */
    Message(@NotNull String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    /**
     * Gets the message this enum represents
     *
     * @return <p>The formatted message</p>
     */
    public @NotNull String getMessage() {
        return formatMessage(this.defaultMessage);
    }

    /**
     * Gets the message this enum represents
     *
     * @param placeholder <p>The placeholder to replace</p>
     * @param replacement <p>The replacement to use</p>
     * @return <p>The formatted message</p>
     */
    public @NotNull String getMessage(@NotNull String placeholder, @NotNull String replacement) {
        return formatMessage(this.defaultMessage.replace(placeholder, replacement));
    }

    /**
     * Gets the formatted version of the given message
     *
     * @param message <p>The message to format</p>
     * @return <p>The formatted message</p>
     */
    private @NotNull String formatMessage(@NotNull String message) {
        String prefix = Launchpad.getInstance().getDescription().getPrefix();
        return ColorHelper.translateAllColorCodes("#FFE34C[&r&l" + prefix + "#FFE34C]&r " + message);
    }

}
