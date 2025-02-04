package com.github.K4RUNIO.simpleTimber;


import org.bukkit.plugin.java.JavaPlugin;

public class SimpleTimber extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("[SimpleTimber] §aSimpleTimber plugin loaded successfully!");

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        getServer().getPluginManager().registerEvents(new TreeBreakListener(this), this);

        getCommand("simpletimber").setExecutor(new SimpleTimberCommand(this));
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("[SimpleTimber] §cSimpleTimber plugin disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}