package com.github.K4RUNIO.simpleTimber;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class SimpleTimberCommand implements CommandExecutor {

    private final SimpleTimber plugin;

    public SimpleTimberCommand(SimpleTimber plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player && !sender.hasPermission("simpletimber.reload")) {
                sender.sendMessage(Component.text("You don't have permission!", NamedTextColor.RED));
                return true;
            }

            plugin.getConfigManager().loadConfig();
            sender.sendMessage(Component.text("SimpleTimber config reloaded!", NamedTextColor.GREEN));
            return true;
        }
        sender.sendMessage(Component.text("/simpletimber reload").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        return true;
    }
}