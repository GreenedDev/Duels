package net.multylands.duels.commands;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.Color("&cThis command can be only executed by a player!"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 0) {
            sender.sendMessage(Chat.Color("&cUsage: /"+label));
            return false;
        }
        if (!Duels.requests.containsKey(player.getUniqueId())) {
            sender.sendMessage(Chat.Color("&cYou haven't created any duel requests."));
            return false;
        }
        sender.sendMessage(Chat.Color("&aYour duel request has been cancelled."));
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        DuelRequest secondRequest = Duels.requests.get(request.getTarget());
        request.removeStoreRequest();
        secondRequest.removeStoreRequest();
        return false;
    }
}
