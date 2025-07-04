package de.stondragonlp.syncstaticmapview.code.branch.v20;

import de.stondragonlp.syncstaticmapview.api.branch.packet.PacketSpawnEntityEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Branch_20_ProxyPlayerConnection {

    public static boolean read(Player player, Packet<?> packet) {
        return true; // no-op, keep simple
    }

    public static boolean write(Player player, Packet<?> packet) {
        try {
            if (packet instanceof ClientboundAddEntityPacket entityPacket) {
                // In 1.20.4 → nutze den Getter:
                int entityId = entityPacket.getEntityId();

                PacketSpawnEntityEvent event = new PacketSpawnEntityEvent(player, entityId);
                Bukkit.getPluginManager().callEvent(event);
                return !event.isCancelled();
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }
}
