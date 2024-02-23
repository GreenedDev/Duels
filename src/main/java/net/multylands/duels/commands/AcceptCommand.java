package net.multylands.duels.commands;

import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand implements CommandExecutor {
    public Duels plugin;

    public AcceptCommand(Duels plugin) {
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
        for (Arena arena : Duels.Arenas.values()) {
            if (!arena.isAvailable) {
                if (((Player) sender).getUniqueId().equals(arena.getSenderUUID())) {
                    sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.already-in-duel")));
                    return false;
                }
            }
        }
        if (!Duels.requests.containsKey(target.getUniqueId())) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.target-hasnt-sent-request")));
            return false;
        }
        boolean Available = false;
        Arena availableArena = null;
        for (Arena arena : Duels.Arenas.values()) {
            if (arena.isAvailable) {
                Available = true;
                availableArena = arena;
                break;
            }
        }
        if (!Available) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.no-arenas-available")));
            return false;
        }
        DuelRequest request = Duels.requests.get(target.getUniqueId());
        if (!request.getDuelRestrictions().isComplete()) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.target-hasnt-sent-request")));
            return false;
        }
        sender.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.you-accepted-request").replace("%player%", target.getDisplayName())));
        target.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.request-accepted").replace("%player%", player.getDisplayName())));
        request.startGame(availableArena);
        return false;
    }
}
