package net.knarcraft.launchpad.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class for converting colors
 */
public class ColorHelper {

    /**
     * Translates all found color codes to formatting in a string
     *
     * @param message <p>The string to search for color codes</p>
     * @return <p>The message with color codes translated</p>
     */
    public static String translateAllColorCodes(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        Pattern pattern = Pattern.compile("&?(#[a-fA-F0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            message = message.replace(matcher.group(), "" + ChatColor.of(matcher.group(1)));
        }
        return message;
    }

}
