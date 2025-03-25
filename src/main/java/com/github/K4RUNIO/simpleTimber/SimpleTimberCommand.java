package com.github.K4RUNIO.simpleTimber;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SimpleTimberCommand implements CommandExecutor {

    private final SimpleTimber plugin;

    public SimpleTimberCommand(SimpleTimber plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player && !sender.hasPermission("simpletimber.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }

            plugin.getConfigManager().loadConfig();
            sender.sendMessage(ChatColor.GREEN + "SimpleTimber config reloaded!");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "§l/simpletimber reload");
        return true;
    }
}