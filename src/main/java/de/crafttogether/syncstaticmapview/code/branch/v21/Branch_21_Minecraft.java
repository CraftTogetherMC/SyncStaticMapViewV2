package de.crafttogether.syncstaticmapview.code.branch.v21;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import de.crafttogether.syncstaticmapview.api.branch.BranchMinecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Branch_21_Minecraft implements BranchMinecraft {
    private static final Pattern MAP_PATTERN = Pattern.compile("(?:^|[,{])\\s*map\\s*:\\s*(-?\\d+)");
    private static final Pattern LORE_PATTERN = Pattern.compile("\\\"text\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
    private static final NamespacedKey DISABLE_COPY_KEY = new NamespacedKey("syncstaticmapview", "anti_map_copy");


    @Override
    public Entity getEntityFromId(World world, int entityId) {
        for (Entity entity : world.getEntities()) {
            if (entity.getEntityId() == entityId) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean isDisableCopy(ItemStack item) {
        if (item == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Byte value = container.get(DISABLE_COPY_KEY, PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }

    @Override
    public int getMapId(ItemStack item) {
        if (item == null) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof MapMeta mapMeta && mapMeta.hasMapId()) {
            return mapMeta.getMapId();
        }
        return 0;
    }

    @Override
    public List<Player> getTracking(Entity entity) {
        Set<Player> trackedBy = entity.getTrackedBy();
        return new ArrayList<>(trackedBy);
    }

    @Override
    public ItemStack saveItemNBT(ItemStack item, String nbt) throws CommandSyntaxException {
        if (item == null) {
            throw new CommandSyntaxException(null, () -> "item is null");
        }

        ItemStack copy = item.clone();
        ItemMeta meta = copy.getItemMeta();
        if (!(meta instanceof MapMeta mapMeta)) {
            return copy;
        }

        Matcher mapMatcher = MAP_PATTERN.matcher(nbt);
        if (mapMatcher.find()) {
            mapMeta.setMapId(Integer.parseInt(mapMatcher.group(1)));
        }

        if (nbt.contains("AntiMapCopy:true") || nbt.contains("AntiMapCopy:1b") || nbt.contains("AntiMapCopy:1B")) {
            mapMeta.getPersistentDataContainer().set(DISABLE_COPY_KEY, PersistentDataType.BYTE, (byte) 1);
        }

        Matcher loreMatcher = LORE_PATTERN.matcher(nbt);
        if (loreMatcher.find()) {
            mapMeta.setLore(List.of(loreMatcher.group(1)));
        }

        copy.setItemMeta(mapMeta);
        return copy;
    }

    @Override
    public void injectPlayer(Player player) {
        // In the 1.21.11 rebuild we do not inject the network pipeline anymore.
        // Map updates are handled by periodic inventory and entity tracking scans.
    }
}
