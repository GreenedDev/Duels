package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class IgnoreDuel implements CommandExecutor {
    public Duels plugin;
    public IgnoreDuel(Duels plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("only-player-command")));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("command-usage").replace("%command%", label)+" player"));
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.target-is-offline")));
            return false;
        }
        if (plugin.ignoresConfig.getList("Ignores") == null || !plugin.ignoresConfig.getList("Ignores").contains(player.getUniqueId())) {
            List<UUID> uuids = new java.util.ArrayList<>(Collections.emptyList());
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.ignoring-on-player-enable").replace("%player%", target.getDisplayName())));
            uuids.add(target.getUniqueId());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), uuids);
            plugin.saveIgnoresConfig();
            return false;
        }
        List<UUID> uuids = (List<UUID>) plugin.ignoresConfig.getList("Ignores." + player.getUniqueId());
        if (uuids.contains(target.getUniqueId())) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.ignoring-on-player-disable").replace("%player%", target.getDisplayName())));
            uuids.remove(target.getUniqueId());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), null);
            plugin.saveIgnoresConfig();
            return false;
        }
        if (!uuids.contains(target.getUniqueId())) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.ignoring-on-player-enable").replace("%player%", target.getDisplayName())));
            uuids.add(target.getUniqueId());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), uuids);
            plugin.saveIgnoresConfig();
            return false;
        }
        return false;
    }
}
