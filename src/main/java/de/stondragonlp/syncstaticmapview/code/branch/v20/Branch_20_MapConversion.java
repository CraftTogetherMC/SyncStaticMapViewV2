package de.stondragonlp.syncstaticmapview.code.branch.v20;

import de.stondragonlp.syncstaticmapview.api.branch.BranchMapColor;
import de.stondragonlp.syncstaticmapview.api.branch.BranchMapConversion;
import de.stondragonlp.syncstaticmapview.api.data.MapData;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

public final class Branch_20_MapConversion implements BranchMapConversion {
    private final BranchMapColor branchMapColor;
    private final BranchMapConversion branchMapConversion;
    private final Field field_CraftMapView_WorldMap;

    public Branch_20_MapConversion(BranchMapColor branchMapColor, BranchMapConversion branchMapConversion) throws NoSuchFieldException {
        this.branchMapColor = branchMapColor;
        this.branchMapConversion = branchMapConversion;

        this.field_CraftMapView_WorldMap = CraftMapView.class.getDeclaredField("worldMap");
        this.field_CraftMapView_WorldMap.setAccessible(true);
    }

    @Override
    public MapData ofBukkit(MapView mapView) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BufferedImage toImage(MapData mapData) {
        throw new UnsupportedOperationException();
    }
}
