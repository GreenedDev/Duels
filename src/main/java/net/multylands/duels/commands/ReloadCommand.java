package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    Duels plugin;
    public ReloadCommand(Duels duels) {
        plugin = duels;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(Chat.Color("&cUsage: /"+label));
            return false;
        }
        if (!sender.hasPermission("duels.reload")) {
            sender.sendMessage(Chat.Color("&cYou don't have a permission to execute this command!"));
        }
        plugin.reloadArenaConfig();
        plugin.reloadConfig();
        sender.sendMessage(Chat.Color("&aArenas config has been reloaded!"));
        return false;
    }
}
