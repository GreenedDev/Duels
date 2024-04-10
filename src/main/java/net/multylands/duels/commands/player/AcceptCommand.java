package net.multylands.duels.commands.player;

import net.multylands.duels.Duels;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
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
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " player");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        for (Arena arena : Duels.Arenas.values()) {
            if (arena.isAvailable()) {
                continue;
            }
            if (player.getUniqueId().equals(arena.getSenderUUID()) || player.getUniqueId().equals(arena.getTargetUUID())) {
                Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.already-in-duel"));
                return false;
            }
        }
        DuelRequest request = RequestUtils.getRequestForCommands(player.getUniqueId(), target.getUniqueId());

        if (request == null) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-hasnt-sent-request"));
            return false;
        }
        boolean Available = false;
        Arena availableArena = null;
        for (Arena arena : Duels.Arenas.values()) {
            if (!arena.isAvailable()) {
                continue;
            }
            Available = true;
            availableArena = arena;
            break;
        }
        if (!Available) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.no-arenas-available"));
            return false;
        }
        if (!request.getRestrictions().isComplete()) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-hasnt-sent-request"));
            return false;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.you-accepted-request").replace("%player%", target.getDisplayName()));
        Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.request-accepted").replace("%player%", player.getDisplayName()));
        request.startGame(availableArena);
        return false;
    }
}
