package net.knarcraft.launchpad.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper-class for common tab-completions
 */
public final class TabCompleteHelper {

    private TabCompleteHelper() {

    }

    /**
     * Finds tab complete values that contain the typed text
     *
     * @param values    <p>The values to filter</p>
     * @param typedText <p>The text the player has started typing</p>
     * @return <p>The given string values that contain the player's typed text</p>
     */
    public static List<String> filterMatchingContains(@NotNull List<String> values, @NotNull String typedText) {
        List<String> configValues = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase().contains(typedText.toLowerCase())) {
                configValues.add(value);
            }
        }
        return configValues;
    }

}
