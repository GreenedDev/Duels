package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.object.Arena;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {
    public Duels plugin;

    public SpectateCommand(Duels plugin) {
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
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("command-usage").replace("%command%", label) + " player"));
            return false;
        }
        String toSpectateName = args[0];
        Player toSpectate = Bukkit.getPlayer(toSpectateName);
        if (toSpectate == null) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.target-is-offline")));
            return false;
        }
        if (!Duels.requests.containsKey(toSpectate.getUniqueId())) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.spectate-is-not-in-duel").replace("%player%", toSpectateName)));

            return false;
        }
        DuelRequest request = Duels.requests.get(toSpectate.getUniqueId());
        if (!request.getIsInGame()) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.spectate-is-not-in-duel").replace("%player%", toSpectateName)));
            return false;
        }
        if (Duels.spectators.containsKey(player.getUniqueId())) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.already-spectating")));
            return false;
        }
        Player opponent = Bukkit.getPlayer(request.getOpponent(toSpectate.getUniqueId()));
        Spectating.startSpectating(player, toSpectate, plugin);
        player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.spectate-success").replace("%player%", toSpectateName)));
        return false;
    }
}
