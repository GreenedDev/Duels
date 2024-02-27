package net.multylands.duels.commands;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DenyCommand implements CommandExecutor {
    Duels plugin;
    public DenyCommand(Duels plugin) {
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
        if (!Duels.requests.containsKey(target.getUniqueId())) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-hasnt-sent-request"));
            return false;
        }
        DuelRequest request = Duels.requests.get(target.getUniqueId());
        //request.getPlayer is always sender
        if (request.getPlayer() == player.getUniqueId()) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("command-usage").replace("%command%", label));
            return false;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.you-denied-request").replace("%player%", target.getDisplayName()));
        Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.someone-denied-your-request").replace("%player%", player.getDisplayName()));
        request.removeStoreRequest(false);
        return false;
    }
}
