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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.Color("&cThis command can be only executed by a player!"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            sender.sendMessage(Chat.Color("&cUsage: /"+label+" player"));
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Chat.Color("&cGiven player is offline."));
            return false;
        }
        if (!Duels.requests.containsKey(target.getUniqueId())) {
            sender.sendMessage(Chat.Color("&cGiven player haven't sent you any request."));
            return false;
        }
        DuelRequest request = Duels.requests.get(target.getUniqueId());
        if (request.getOriginalSender() == player.getUniqueId()) {
            sender.sendMessage(Chat.Color("&cYou are using wrong command. use /cancelduel"));
            return false;
        }
        sender.sendMessage(Chat.Color("&aYou have denied duel request of &b" + target.getDisplayName()));
        target.sendMessage(Chat.Color("&b" + player.getDisplayName() + " &chas denied your duel request."));
        DuelRequest secondRequest = Duels.requests.get(request.getTarget());
        secondRequest.removeStoreRequest();
        request.removeStoreRequest();
        return false;
    }
}
