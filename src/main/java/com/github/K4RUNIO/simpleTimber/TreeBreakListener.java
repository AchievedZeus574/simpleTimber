package com.github.K4RUNIO.simpleTimber;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class TreeBreakListener implements Listener {

    private final SimpleTimber plugin;
    private final Set<UUID> activeTimberPlayers = new HashSet<>();
    private Material getSaplingForLog(Material log) {
        switch (log) {
            case OAK_LOG:      return Material.OAK_SAPLING;
            case SPRUCE_LOG:   return Material.SPRUCE_SAPLING;
            case BIRCH_LOG:    return Material.BIRCH_SAPLING;
            case JUNGLE_LOG:   return Material.JUNGLE_SAPLING;
            case ACACIA_LOG:   return Material.ACACIA_SAPLING;
            case DARK_OAK_LOG: return Material.DARK_OAK_SAPLING;
            case CHERRY_LOG:   return Material.CHERRY_SAPLING;
            case MANGROVE_LOG: return Material.MANGROVE_PROPAGULE;
            case CRIMSON_STEM: return Material.CRIMSON_FUNGUS;
            case WARPED_STEM:  return Material.WARPED_FUNGUS;
            case PALE_OAK_LOG: return Material.PALE_OAK_SAPLING;
            default:           return null;
        }
    }
    private void replantSaplings(Set<Block> brokenLogs, Material logType, Player player) {
        Material sapling= getSaplingForLog(logType);
        if (sapling== null) return;

        Inventory inv= player.getInventory();

        int minY= Integer.MAX_VALUE;
        for (Block b : brokenLogs) {
            minY= Math.min(minY, b.getY());
        }

        for (Block b: brokenLogs) {
            if (b.getY() != minY) continue;

            Block spot= b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ());
            Block soil= spot.getRelative(0, -1, 0);

            if (spot.getType() == Material.AIR && soil.getType().isSolid()) {
                if (inv.contains(sapling)) {
                    inv.remove(new ItemStack(sapling, 1));
                    spot.setType(sapling);
                }
            }
        }
    }

    public TreeBreakListener(SimpleTimber plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (shouldTriggerTimber(block, tool, player)) {
            activeTimberPlayers.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!activeTimberPlayers.contains(player.getUniqueId())) return;

        Set<Block> connectedLogs= findConnectedLogs(block);

        Material startLog= block.getType();

        activeTimberPlayers.remove(player.getUniqueId());
        if (plugin.getConfigManager().isFallingAnimationEnabled()) {
            makeTreeFall(block, player);
        } else {
            breakConnectedLogs(block, player);
        }

        if (plugin.getConfigManager().isReplantSaplingsEnabled()) {
            replantSaplings(connectedLogs, startLog, player);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            if (plugin.getConfigManager().getLogTypes().contains(fallingBlock.getBlockData().getMaterial())) {
                event.setCancelled(true);
                fallingBlock.getWorld().dropItemNaturally(
                        fallingBlock.getLocation(),
                        new ItemStack(fallingBlock.getBlockData().getMaterial())
                );
                fallingBlock.remove();
            }
        }
    }

    private boolean shouldTriggerTimber(Block block, ItemStack tool, Player player) {
        return plugin.getConfigManager().getLogTypes().contains(block.getType()) &&
                isAxe(tool.getType()) &&
                // invert sneak-check if enabled in config
                (plugin.getConfigManager().isInvertSneakEnabled()
                   ? player.isSneaking()
                   : !player.isSneaking());
    }

    private void makeTreeFall(Block startBlock, Player player) {
        if (!plugin.getConfigManager().isFallingAnimationEnabled()) {
            breakConnectedLogs(startBlock, player);
            return;
        }

        Set<Block> connectedLogs = findConnectedLogs(startBlock);
        startBlock.breakNaturally();

        for (Block log : connectedLogs) {
            if (log.equals(startBlock)) continue;

            @SuppressWarnings("deprecation")
            FallingBlock fallingBlock = log.getWorld().spawnFallingBlock(
                    log.getLocation().add(0.5, 0, 0.5),
                    log.getBlockData()
            );

            fallingBlock.setVelocity(new Vector(
                    (Math.random() - 0.5) * 0.2,
                    0.1,
                    (Math.random() - 0.5) * 0.2
            ));

            log.setType(Material.AIR);
            player.playSound(log.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
        }
    }

    private void breakConnectedLogs(Block startBlock, Player player) {
        Set<Block> connectedLogs = findConnectedLogs(startBlock);
        ItemStack tool = player.getInventory().getItemInMainHand();

        for (Block log : connectedLogs) {
            log.breakNaturally(tool);
        }
    }

    private Set<Block> findConnectedLogs(Block startBlock) {
        Set<Block> result = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.add(startBlock);

        while (!queue.isEmpty()) {
            Block current = queue.poll();
            if (result.contains(current)) continue;

            if (plugin.getConfigManager().getLogTypes().contains(current.getType())) {
                result.add(current);
                queue.add(current.getRelative(1, 0, 0));
                queue.add(current.getRelative(-1, 0, 0));
                queue.add(current.getRelative(0, 0, 1));
                queue.add(current.getRelative(0, 0, -1));
                queue.add(current.getRelative(0, 1, 0));
            }
        }
        return result;
    }

    private boolean isAxe(Material material) {
        return material.name().endsWith("_AXE");
    }
}