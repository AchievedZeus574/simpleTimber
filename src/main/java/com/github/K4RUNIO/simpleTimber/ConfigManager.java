package com.github.K4RUNIO.simpleTimber;


import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private boolean pluginEnabled;
    private boolean fallingAnimationEnabled;
    private final Set<Material> logTypes = new HashSet<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        pluginEnabled = config.getBoolean("enabled", true);
        fallingAnimationEnabled = config.getBoolean("falling-animation", true);

        logTypes.clear();
        for (String logName : config.getStringList("log-types")) {
            try {
                Material logMaterial = Material.valueOf(logName.toUpperCase());
                logTypes.add(logMaterial);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid log type: " + logName);
            }
        }

        if (logTypes.isEmpty()) {
            addDefaultLogTypes();
        }
    }

    private void addDefaultLogTypes() {
        logTypes.add(Material.OAK_LOG);
        logTypes.add(Material.SPRUCE_LOG);
        logTypes.add(Material.BIRCH_LOG);
        logTypes.add(Material.JUNGLE_LOG);
        logTypes.add(Material.ACACIA_LOG);
        logTypes.add(Material.DARK_OAK_LOG);
        logTypes.add(Material.MANGROVE_LOG);
        logTypes.add(Material.CHERRY_LOG);
        logTypes.add(Material.PALE_OAK_LOG);
        logTypes.add(Material.CRIMSON_STEM);
        logTypes.add(Material.WARPED_STEM);
    }

    public boolean isPluginEnabled() {
        return pluginEnabled;
    }

    public boolean isFallingAnimationEnabled() {
        return fallingAnimationEnabled;
    }

    public Set<Material> getLogTypes() {
        return logTypes;
    }
}