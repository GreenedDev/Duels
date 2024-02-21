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
            sender.sendMessage(Chat.Color("&cThis command can be only executed by a player!"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            sender.sendMessage(Chat.Color("&cUsage: /"+ label +" player"));
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Chat.Color("&cGiven player is offline."));
            return false;
        }
        if (!plugin.ignoresConfig.getList("Ignores").contains(player.getUniqueId())) {
            List<UUID> uuids = Collections.emptyList();
            sender.sendMessage(Chat.Color("&aYou are now ignoring duel requests of &b" + target.getDisplayName()));
            uuids.add(target.getUniqueId());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), uuids);
            plugin.saveIgnoresConfig();
            return false;
        }
        List<UUID> uuids = (List<UUID>) plugin.ignoresConfig.getList("Ignores." + player.getUniqueId());
        if (uuids.contains(target.getUniqueId())) {
            sender.sendMessage(Chat.Color("&aYou are no longer ignoring duel requests of &b" + target.getDisplayName()));
            uuids.remove(target.getUniqueId());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), null);
            plugin.saveIgnoresConfig();
            return false;
        }
        if (!uuids.contains(target.getUniqueId())) {
            sender.sendMessage(Chat.Color("&aYou are now ignoring duel requests of &b" + target.getDisplayName()));
            uuids.add(target.getUniqueId());
            plugin.ignoresConfig.set("Ignores." + player.getUniqueId(), uuids);
            plugin.saveIgnoresConfig();
            return false;
        }
        return false;
    }
}
