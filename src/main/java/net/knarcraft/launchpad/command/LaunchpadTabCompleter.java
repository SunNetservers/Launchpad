package net.knarcraft.launchpad.command;

import net.knarcraft.launchpad.launchpad.ModificationAction;
import net.knarcraft.launchpad.util.TabCompleteHelper;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A tab-completer for the launchpad command
 */
public class LaunchpadTabCompleter implements TabCompleter {

    private List<String> modificationActions = null;
    private List<String> blockFaces = null;
    private List<String> doubles = null;

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                                      @NotNull String[] arguments) {
        if (arguments.length == 1) {
            // Display available sub-commands
            return TabCompleteHelper.filterMatchingContains(getModificationActions(), arguments[0]);
        } else {
            // If given a valid modification action, and an argument is expected, display possible values
            ModificationAction action = ModificationAction.getFromCommandName(arguments[0]);
            if (action == null || action.neededArguments() + 1 < arguments.length) {
                return new ArrayList<>();
            }
            if (arguments.length == 2) {
                return TabCompleteHelper.filterMatchingContains(getTabCompletions(action), arguments[1]);
            } else if (arguments.length == 3) {
                return TabCompleteHelper.filterMatchingContains(getTabCompletions(action), arguments[2]);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Gets tab-completions for the given modification action
     *
     * @param action <p>The action to get tab-completions for</p>
     * @return <p>Tab-completion values</p>
     */
    private @NotNull List<String> getTabCompletions(@NotNull ModificationAction action) {
        return switch (action) {
            case FIXED_DIRECTION -> getBlockFaces();
            case HORIZONTAL_VELOCITY, VERTICAL_VELOCITY, VELOCITIES -> getPositiveDoubles();
            default -> new ArrayList<>();
        };
    }

    /**
     * Gets tab-completions for positive double values
     *
     * @return <p>Tab-completion values</p>
     */
    private List<String> getPositiveDoubles() {
        if (this.doubles == null) {
            this.doubles = new ArrayList<>();
            this.doubles.add("0.1");
            this.doubles.add("0.5");
            this.doubles.add("1");
            this.doubles.add("1.5");
            this.doubles.add("2");
        }
        return this.doubles;
    }

    /**
     * Gets tab-completions for block faces
     *
     * @return <p>Tab-completion values</p>
     */
    private List<String> getBlockFaces() {
        if (this.blockFaces == null) {
            this.blockFaces = new ArrayList<>();
            for (BlockFace face : BlockFace.values()) {
                // Only add block faces on the X Z plane
                if (face.getDirection().dot(BlockFace.WEST.getDirection().crossProduct(
                        BlockFace.NORTH.getDirection())) == 0 && face != BlockFace.SELF) {
                    blockFaces.add(face.name());
                }
            }
        }
        return this.blockFaces;
    }

    /**
     * Gets a list of every modification action's command
     *
     * @return <p>All modification action commands</p>
     */
    private List<String> getModificationActions() {
        if (this.modificationActions == null) {
            this.modificationActions = new ArrayList<>();
            for (ModificationAction action : ModificationAction.values()) {
                modificationActions.add(action.getCommandName());
            }
        }
        return this.modificationActions;
    }

}
