package de.crafttogether.syncstaticmapview.code;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import de.crafttogether.syncstaticmapview.api.branch.BranchMapColor;
import de.crafttogether.syncstaticmapview.api.branch.BranchMapConversion;
import de.crafttogether.syncstaticmapview.api.branch.BranchMinecraft;
import de.crafttogether.syncstaticmapview.api.branch.BranchPacket;
import de.crafttogether.syncstaticmapview.code.branch.v21.Branch_21_MapColor;
import de.crafttogether.syncstaticmapview.code.branch.v21.Branch_21_MapConversion;
import de.crafttogether.syncstaticmapview.code.branch.v21.Branch_21_Minecraft;
import de.crafttogether.syncstaticmapview.code.branch.v21.Branch_21_Packet;
import de.crafttogether.syncstaticmapview.code.command.Command;
import de.crafttogether.syncstaticmapview.code.command.CommandSuggest;
import de.crafttogether.syncstaticmapview.code.data.ConfigData;

public final class MapIndex extends JavaPlugin {

    private static Plugin plugin;
    private static MapServer mapServer;
    private static ConfigData configData;
    private static MapDatabase mapDatabase;
    private static BranchMapConversion branchMapConversion;
    private static BranchMapColor branchMapColor;
    private static BranchMinecraft branchMinecraft;
    private static BranchPacket branchPacket;

    @Override
    public void onEnable() {
        plugin = this;

        try {
            String bukkitVersion = Bukkit.getBukkitVersion();
            if (!bukkitVersion.startsWith("1.21")) {
                throw new IllegalArgumentException("This rebuild targets Paper 1.21.x only. Detected: " + bukkitVersion);
            }

            branchMapColor = new Branch_21_MapColor();
            branchMapConversion = new Branch_21_MapConversion(branchMapColor, null);
            branchMinecraft = new Branch_21_Minecraft();
            branchPacket = new Branch_21_Packet();

            saveDefaultConfig();

            configData = new ConfigData(this, getConfig());
            mapDatabase = new MapDatabase(configData, branchMapConversion, branchMapColor);
            mapServer = new MapServer(this, configData, mapDatabase, branchMapConversion, branchMapColor, branchMinecraft, branchPacket);

            Bukkit.getPluginManager().registerEvents(new MapEvent(this, mapServer, branchMinecraft), this);
            Bukkit.getOnlinePlayers().forEach(player -> {
                mapServer.createCache(player);
                branchMinecraft.injectPlayer(player);
            });

            PluginCommand command = getCommand("mapview");
            if (command != null) {
                command.setExecutor(new Command(plugin, mapDatabase, mapServer, configData, branchMapConversion, branchMapColor, branchMinecraft));
                command.setTabCompleter(new CommandSuggest(mapServer, configData));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new NullPointerException("onEnable error");
        }
    }

    public static MapServer getMapServer() {
        return mapServer;
    }

    public static BranchMapColor getBranchMapColor() {
        return branchMapColor;
    }

    public static BranchMapConversion getBranchMapConversion() {
        return branchMapConversion;
    }

    public static BranchMinecraft getBranchMinecraft() {
        return branchMinecraft;
    }

    public static BranchPacket getBranchPacket() {
        return branchPacket;
    }

    public static MapDatabase getMapDatabase() {
        return mapDatabase;
    }

    public static ConfigData getConfigData() {
        return configData;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        if (mapServer != null) {
            mapServer.close();
        }
        if (configData != null) {
            try {
                configData.getDatabaseConnection().disconnect();
            } catch (Exception ignored) {
            }
        }
    }
}
