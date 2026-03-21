package de.crafttogether.syncstaticmapview.api.branch;

import org.bukkit.entity.Player;
import de.crafttogether.syncstaticmapview.api.data.MapData;

public interface BranchPacket {
    void sendMapView(Player player, int mapId, MapData mapData);
}
