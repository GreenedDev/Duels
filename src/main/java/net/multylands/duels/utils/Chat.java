package net.multylands.duels.utils;

import net.kyori.adventure.text.Component;
import net.multylands.duels.Duels;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chat {
    public static String Color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void sendMessage(Duels plugin, Player player, String message) {
        if (message.startsWith("$")) {
            Component parsed = plugin.miniMessage().deserialize(message.substring(1));
            plugin.adventure().player(player).sendMessage(parsed);
        } else {
            player.sendMessage(Color(message));
        }
    }

    public static void sendMessageSender(Duels plugin, CommandSender sender, String message) {
        if (message.startsWith("$")) {
            Component parsed = plugin.miniMessage().deserialize(message.substring(1));
            plugin.adventure().sender(sender).sendMessage(parsed);
        } else {
            sender.sendMessage(Color(message));
        }
    }

    public static void messagePlayers(Duels plugin, Player player, Player target, String message) {
        Chat.sendMessage(plugin, player, message);
        Chat.sendMessage(plugin, target, message);
    }
}
