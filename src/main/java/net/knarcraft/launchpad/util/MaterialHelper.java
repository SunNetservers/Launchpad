package net.knarcraft.launchpad.util;

import net.knarcraft.launchpad.Launchpad;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class for dealing with and parsing materials
 */
public final class MaterialHelper {

    private MaterialHelper() {

    }

    /**
     * Loads the materials specified in the block whitelist
     */
    public static @NotNull Set<Material> loadMaterialList(@NotNull List<?> materials) {
        Set<Material> parsedMaterials = new HashSet<>();
        for (Object value : materials) {
            if (!(value instanceof String string)) {
                continue;
            }

            // Try to parse a material tag first
            if (parseMaterialTag(parsedMaterials, string)) {
                continue;
            }

            // Try to parse a material name
            Material matched = Material.matchMaterial(string);
            if (matched != null) {
                parsedMaterials.add(matched);
            } else {
                Launchpad.log(Level.WARNING, "&cUnable to parse material: " + string);
            }
        }
        return parsedMaterials;
    }

    /**
     * Tries to parse the material tag in the specified material name
     *
     * @param targetSet    <p>The set all parsed materials should be added to</p>
     * @param materialName <p>The material name that might be a material tag</p>
     * @return <p>True if a tag was found</p>
     */
    private static boolean parseMaterialTag(@NotNull Set<Material> targetSet, @NotNull String materialName) {
        Pattern pattern = Pattern.compile("^\\+([a-zA-Z_]+)");
        Matcher matcher = pattern.matcher(materialName);
        if (matcher.find()) {
            // The material is a material tag
            Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, NamespacedKey.minecraft(
                    matcher.group(1).toLowerCase()), Material.class);
            if (tag != null) {
                targetSet.addAll(tag.getValues());
            } else {
                Launchpad.log(Level.WARNING, "Unable to parse: " + materialName);
            }
            return true;
        }
        return false;
    }

}
