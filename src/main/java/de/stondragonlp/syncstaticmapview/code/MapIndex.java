package de.stondragonlp.syncstaticmapview.code;


import de.stondragonlp.syncstaticmapview.code.branch.v20.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import de.stondragonlp.syncstaticmapview.api.branch.BranchMapColor;
import de.stondragonlp.syncstaticmapview.api.branch.BranchMapConversion;
import de.stondragonlp.syncstaticmapview.api.branch.BranchMinecraft;
import de.stondragonlp.syncstaticmapview.api.branch.BranchPacket;
import de.stondragonlp.syncstaticmapview.code.command.Command;
import de.stondragonlp.syncstaticmapview.code.command.CommandSuggest;
import de.stondragonlp.syncstaticmapview.code.data.ConfigData;

public final class MapIndex extends JavaPlugin {

    private static Plugin               plugin;
    private static MapServer            mapServer;
    private static ConfigData           configData;
    private static MapDatabase          mapDatabase;
    private static BranchMapConversion  branchMapConversion;
    private static BranchMapColor       branchMapColor;
    private static BranchMinecraft      branchMinecraft;
    private static BranchPacket         branchPacket;

    @Override
    public void onEnable() {
        plugin          = this;

        try {
            String bukkitVersion = Bukkit.getBukkitVersion();
            if (bukkitVersion.matches("^1\\.20\\D.*$")) {
                // 1.19
                branchMapColor      = new Branch_20_MapColor();
                branchMapConversion = new Branch_20_MapConversion(branchMapColor, branchMapConversion);
                branchMinecraft     = new Branch_20_Minecraft();
                branchPacket        = new Branch_20_Packet();
            } else {
                throw new IllegalArgumentException("Unsupported MC version: " + bukkitVersion);
            }

            saveDefaultConfig();

            configData      = new ConfigData(this, getConfig());
            mapDatabase     = new MapDatabase(configData, branchMapConversion, branchMapColor);
            mapServer       = new MapServer(this, configData, mapDatabase, branchMapConversion, branchMapColor, branchMinecraft, branchPacket);

            Bukkit.getPluginManager().registerEvents(new MapEvent(this, mapServer, branchMinecraft), this);

            // 指令
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

    public void onDisable() {
        if (mapServer != null)
            mapServer.close();
        if (configData != null)
            try {
                configData.getDatabaseConnection().disconnect();
            } catch (Exception exception) {
            }
    }
}
