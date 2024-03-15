package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgnoreCommand implements CommandExecutor {
    public Duels plugin;
    public IgnoreCommand(Duels plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("command-usage").replace("%command%", label)+" player");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        if (plugin.ignoresConfig.getList("Ignores") == null || !plugin.ignoresConfig.getList("Ignores").contains(player.getUniqueId())) {
            List<String> uuids = new ArrayList<>(Collections.emptyList());
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.ignoring-on-player-enable").replace("%player%", target.getDisplayName()));
            uuids.add(target.getUniqueId().toString());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), uuids);
            plugin.saveIgnoresConfig();
            return false;
        }
        List<String> uuids = plugin.ignoresConfig.getStringList("Ignores." + player.getUniqueId());
        if (uuids.contains(target.getUniqueId().toString())) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.ignoring-on-player-disable").replace("%player%", target.getDisplayName()));
            uuids.remove(target.getUniqueId().toString());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), null);
            plugin.saveIgnoresConfig();
            return false;
        }
        if (!uuids.contains(target.getUniqueId())) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.ignoring-on-player-enable").replace("%player%", target.getDisplayName()));
            uuids.add(target.getUniqueId().toString());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), uuids);
            plugin.saveIgnoresConfig();
            return false;
        }
        return false;
    }
}
