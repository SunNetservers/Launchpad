package net.knarcraft.launchpad.launchpad;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A stored representation of a user's request to modify a launchpad
 *
 * @param modificationAction <p>The action the user wants to run</p>
 * @param value              <p>The new value of a property, if applicable</p>
 */
public record ModificationRequest(@NotNull ModificationAction modificationAction, @Nullable String value) {
}
