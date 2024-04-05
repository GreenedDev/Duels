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
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("command-usage").replace("%command%", label));
            return false;
        }
        if (!sender.hasPermission("duels.reload")) {
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        plugin.reloadArenaConfig();
        plugin.reloadConfig();
        plugin.reloadLanguageConfig();
        Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("all-config-reloaded"));
        return false;
    }
}
