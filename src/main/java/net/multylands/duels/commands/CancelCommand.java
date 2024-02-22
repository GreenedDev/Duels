package net.multylands.duels.commands;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelCommand implements CommandExecutor {
    Duels plugin;
    public CancelCommand(Duels plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("only-player-command")));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 0) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("command-usage").replace("%command%", label)));
            return false;
        }
        if (!Duels.requests.containsKey(player.getUniqueId())) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.no-request-sent")));
            return false;
        }
        sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.request-cancelled")));
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        DuelRequest secondRequest = Duels.requests.get(request.getTarget());
        request.removeStoreRequest();
        secondRequest.removeStoreRequest();
        return false;
    }
}
