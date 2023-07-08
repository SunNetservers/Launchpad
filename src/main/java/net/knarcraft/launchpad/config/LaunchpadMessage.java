package net.knarcraft.launchpad.config;

import net.knarcraft.knarlib.formatting.TranslatableMessage;

/**
 * A message which ca be displayed to the user
 */
public enum LaunchpadMessage implements TranslatableMessage {

    /* ************** *
     * Error messages *
     * ************** */

    /**
     * The message displayed if the console tries to execute a player-only command
     */
    ERROR_PLAYER_ONLY,

    /**
     * The message displayed if the player tries to modify launchpad property for a block outside the whitelist
     */
    ERROR_NOT_WHITELISTED,

    /**
     * The message displayed if an un-parse-able message is given by a user
     */
    ERROR_MATERIAL_NOT_PARSE_ABLE,

    /* **************** *
     * Success messages *
     * **************** */

    /**
     * The message displayed if the Launchpad plugin is reloaded
     */
    SUCCESS_PLUGIN_RELOADED,

    /**
     * The message to display when a player uses /launchpad clear
     */
    SUCCESS_MODIFICATIONS_CLEARED,

    /**
     * The message to display to prompt the player to click a launchpad
     */
    SUCCESS_CLICK_BLOCK,

    /**
     * The message to display when a launchpad has been added or modified
     */
    SUCCESS_MODIFIED_LAUNCHPAD,
    ;

    @Override
    public TranslatableMessage[] getAllMessages() {
        return LaunchpadMessage.values();
    }

}
