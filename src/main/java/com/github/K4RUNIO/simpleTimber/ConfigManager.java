package com.github.K4RUNIO.simpleTimber;


import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    private final JavaPlugin plugin;

    private boolean pluginEnabled;
    private final Set<Material> logTypes = new HashSet<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        pluginEnabled = config.getBoolean("enabled", true);

        logTypes.clear();
        for (String logName : config.getStringList("log-types")) {
            try {
                Material logMaterial = Material.valueOf(logName.toUpperCase());
                logTypes.add(logMaterial);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid log type in config: " + logName);
            }
        }

        if (logTypes.isEmpty()) {
            logTypes.add(Material.OAK_LOG);
            logTypes.add(Material.SPRUCE_LOG);
            logTypes.add(Material.BIRCH_LOG);
            logTypes.add(Material.JUNGLE_LOG);
            logTypes.add(Material.ACACIA_LOG);
            logTypes.add(Material.DARK_OAK_LOG);
            logTypes.add(Material.MANGROVE_LOG);
            logTypes.add(Material.CHERRY_LOG);
            logTypes.add(Material.PALE_OAK_LOG);
        }
    }

    public boolean isPluginEnabled() {
        return pluginEnabled;
    }

    public Set<Material> getLogTypes() {
        return logTypes;
    }
}