package de.crafttogether.syncstaticmapview.code.data;

import de.crafttogether.syncstaticmapview.api.data.MapData;

public final class MapDataCache {
    public final long vitality;
    public final MapData data;

    public MapDataCache(MapData data, long vitality) {
        this.data = data;
        this.vitality = vitality;
    }
}
