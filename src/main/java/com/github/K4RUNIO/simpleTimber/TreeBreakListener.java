package com.github.K4RUNIO.simpleTimber;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TreeBreakListener implements Listener {

    private final SimpleTimber plugin;

    public TreeBreakListener(SimpleTimber plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfigManager().isPluginEnabled()) {
            return;
        }

        Block block = event.getBlock();
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        if (plugin.getConfigManager().getLogTypes().contains(block.getType()) && isAxe(tool.getType()) && !event.getPlayer().isSneaking()) {
            breakConnectedLogs(block);
        }
    }

    private boolean isAxe(Material material) {
        return material.name().endsWith("_AXE");
    }

    private void breakConnectedLogs(Block block) {
        if (plugin.getConfigManager().getLogTypes().contains(block.getType())) {
            block.breakNaturally();
        }

        checkAndBreak(block.getRelative(1, 0, 0));
        checkAndBreak(block.getRelative(-1, 0, 0));
        checkAndBreak(block.getRelative(0, 0, 1));
        checkAndBreak(block.getRelative(0, 0, -1));
        checkAndBreak(block.getRelative(0, 1, 0));
    }

    private void checkAndBreak(Block block) {
        if (plugin.getConfigManager().getLogTypes().contains(block.getType())) {
            breakConnectedLogs(block);
        }
    }
}