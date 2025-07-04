package de.stondragonlp.syncstaticmapview.api.branch;

import org.bukkit.entity.Player;
import de.stondragonlp.syncstaticmapview.api.data.MapData;

public interface BranchPacket {
    void sendMapView(Player player, int mapId, MapData mapData);
}
