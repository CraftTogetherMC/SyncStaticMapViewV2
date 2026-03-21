package de.crafttogether.syncstaticmapview.code.branch.v21;

import org.bukkit.entity.Player;
import de.crafttogether.syncstaticmapview.api.branch.BranchPacket;
import de.crafttogether.syncstaticmapview.api.data.MapData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class Branch_21_Packet implements BranchPacket {

    @Override
    public void sendMapView(Player player, int mapId, MapData mapData) {
        try {
            Object packet = createMapPacket(mapId, mapData.getPixels());
            if (packet == null) {
                throw new IllegalStateException("Unable to construct ClientboundMapItemDataPacket");
            }
            sendPacket(player, packet);
        } catch (Throwable throwable) {
            throw new RuntimeException("Failed to send map packet", throwable);
        }
    }

    private Object createMapPacket(int mapId, byte[] pixels) throws Exception {
        Class<?> patchClass = Class.forName("net.minecraft.world.level.saveddata.maps.MapItemSavedData$MapPatch");
        Object patch = createPatch(patchClass, pixels);
        Object mapIdObject = createMapId(mapId);

        Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.ClientboundMapItemDataPacket");
        for (Constructor<?> constructor : packetClass.getConstructors()) {
            Object packet = tryConstructPacket(constructor, mapIdObject, patch);
            if (packet != null) {
                return packet;
            }
        }
        return null;
    }

    private Object createMapId(int mapId) throws Exception {
        Class<?> mapIdClass = Class.forName("net.minecraft.world.level.saveddata.maps.MapId");
        for (Constructor<?> constructor : mapIdClass.getConstructors()) {
            Class<?>[] types = constructor.getParameterTypes();
            if (types.length == 1 && isInt(types[0])) {
                return constructor.newInstance(mapId);
            }
        }
        throw new IllegalStateException("Unsupported MapId constructor");
    }

    private Object createPatch(Class<?> patchClass, byte[] pixels) throws Exception {
        for (Constructor<?> constructor : patchClass.getConstructors()) {
            Class<?>[] types = constructor.getParameterTypes();
            if (types.length == 5 && isInt(types[0]) && isInt(types[1]) && isInt(types[2]) && isInt(types[3]) && types[4] == byte[].class) {
                return constructor.newInstance(0, 0, 128, 128, pixels);
            }
        }
        throw new IllegalStateException("Unsupported MapPatch constructor");
    }

    private Object tryConstructPacket(Constructor<?> constructor, Object mapId, Object patch) {
        try {
            Class<?>[] types = constructor.getParameterTypes();
            Object[] args = new Object[types.length];
            boolean assignedMapId = false;
            boolean assignedScale = false;
            boolean assignedBoolean = false;
            boolean assignedPatch = false;
            boolean assignedCollection = false;

            Class<?> mapIdClass = mapId.getClass();

            for (int i = 0; i < types.length; i++) {
                Class<?> type = types[i];

                if (!assignedMapId && (type == mapIdClass || type.isAssignableFrom(mapIdClass))) {
                    args[i] = mapId;
                    assignedMapId = true;
                } else if (!assignedScale && (type == byte.class || type == Byte.class)) {
                    args[i] = (byte) 0;
                    assignedScale = true;
                } else if (!assignedBoolean && (type == boolean.class || type == Boolean.class)) {
                    args[i] = false;
                    assignedBoolean = true;
                } else if (!assignedPatch && type.isInstance(patch)) {
                    args[i] = patch;
                    assignedPatch = true;
                } else if (!assignedPatch && type == Optional.class) {
                    args[i] = Optional.of(patch);
                    assignedPatch = true;
                } else if (!assignedCollection && Collection.class.isAssignableFrom(type)) {
                    args[i] = new ArrayList<>();
                    assignedCollection = true;
                } else if (type == Optional.class) {
                    args[i] = Optional.empty();
                } else if (type == List.class || Collection.class.isAssignableFrom(type)) {
                    args[i] = new ArrayList<>();
                } else {
                    return null;
                }
            }

            if (!assignedMapId || !assignedPatch) {
                return null;
            }
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private void sendPacket(Player player, Object packet) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);

        Object connection = findFieldValue(handle, value -> value != null && value.getClass().getName().contains("ServerGamePacketListenerImpl"));
        if (connection == null) {
            throw new IllegalStateException("Player connection not found");
        }

        Method sendMethod = null;
        for (Method method : connection.getClass().getMethods()) {
            if (method.getName().equals("send") && method.getParameterCount() >= 1) {
                sendMethod = method;
                break;
            }
        }
        if (sendMethod == null) {
            throw new IllegalStateException("Connection#send method not found");
        }

        if (sendMethod.getParameterCount() == 1) {
            sendMethod.invoke(connection, packet);
        } else {
            Object[] args = new Object[sendMethod.getParameterCount()];
            args[0] = packet;
            for (int i = 1; i < args.length; i++) {
                args[i] = null;
            }
            sendMethod.invoke(connection, args);
        }
    }

    private Object findFieldValue(Object source, java.util.function.Predicate<Object> predicate) throws IllegalAccessException {
        for (Field field : source.getClass().getFields()) {
            Object value = field.get(source);
            if (predicate.test(value)) {
                return value;
            }
        }
        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (predicate.test(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean isInt(Class<?> type) {
        return type == int.class || type == Integer.class;
    }
}
