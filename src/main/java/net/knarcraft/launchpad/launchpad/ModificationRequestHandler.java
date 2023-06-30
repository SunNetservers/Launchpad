package net.knarcraft.launchpad.launchpad;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A handler to keep track of launchpad modification requests
 */
public final class ModificationRequestHandler {

    private static final Map<UUID, Set<ModificationRequest>> modificationRequests = new HashMap<>();

    private ModificationRequestHandler() {

    }

    /**
     * Adds a new modification request to this handler
     *
     * @param playerId <p>The id of the player that created the request</p>
     * @param request  <p>The player's modification request</p>
     */
    public static void addRequest(@NotNull UUID playerId, @NotNull ModificationRequest request) {
        Set<ModificationRequest> requests = modificationRequests.get(playerId);
        if (requests == null) {
            modificationRequests.put(playerId, new HashSet<>());
            requests = modificationRequests.get(playerId);
        }

        requests.add(request);
    }

    /**
     * Gets and removes all modification requests registered for the given player
     *
     * @param playerId <p>The player to get modification requests for</p>
     */
    public static @Nullable Set<ModificationRequest> getRequests(@NotNull UUID playerId) {
        return modificationRequests.remove(playerId);
    }

}
