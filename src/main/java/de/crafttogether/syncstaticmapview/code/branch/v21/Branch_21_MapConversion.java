package de.crafttogether.syncstaticmapview.code.branch.v21;

import org.bukkit.map.MapView;
import de.crafttogether.syncstaticmapview.api.branch.BranchMapColor;
import de.crafttogether.syncstaticmapview.api.branch.BranchMapConversion;
import de.crafttogether.syncstaticmapview.api.data.MapData;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

public final class Branch_21_MapConversion implements BranchMapConversion {
    private final BranchMapColor branchMapColor;
    private final BranchMapConversion branchMapConversion;

    public Branch_21_MapConversion(BranchMapColor branchMapColor, BranchMapConversion branchMapConversion) {
        this.branchMapColor = branchMapColor;
        this.branchMapConversion = branchMapConversion;
    }

    @Override
    public MapData ofBukkit(MapView mapView) {
        try {
            Object worldMap = extractWorldMap(mapView);
            byte[] colors = extractColors(worldMap);
            if (colors != null) {
                return new MapData(branchMapColor, branchMapConversion, colors.clone());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new MapData(branchMapColor, branchMapConversion);
    }

    @Override
    public BufferedImage toImage(MapData mapData) {
        BufferedImage bufferedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                bufferedImage.setRGB(x, y, mapData.getColor(x, y).getRGB());
            }
        }
        return bufferedImage;
    }

    private Object extractWorldMap(MapView mapView) throws ReflectiveOperationException {
        Class<?> craftMapViewClass = mapView.getClass();
        for (String fieldName : new String[]{"worldMap", "worldmap"}) {
            try {
                Field field = craftMapViewClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(mapView);
            } catch (NoSuchFieldException ignored) {
            }
        }
        for (Field field : craftMapViewClass.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(mapView);
            if (value != null && value.getClass().getName().contains("MapItemSavedData")) {
                return value;
            }
        }
        throw new NoSuchFieldException("CraftMapView world map field not found");
    }

    private byte[] extractColors(Object worldMap) throws ReflectiveOperationException {
        for (Field field : worldMap.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(worldMap);
            if (value instanceof byte[] bytes && bytes.length == 128 * 128) {
                return bytes;
            }
        }
        return null;
    }
}
