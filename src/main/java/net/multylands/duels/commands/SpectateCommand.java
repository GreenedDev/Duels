package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
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
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (args.length != 1) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " player");
            return false;
        }
        String toSpectateName = args[0];
        Player toSpectate = Bukkit.getPlayer(toSpectateName);
        if (toSpectate == null) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectate.getUniqueId());
        //will return null if player is not in game because in the getRequestOfTheDuelPlayerIsIn method we are checking if toSpectate player is in the list of players that are in game.
        if (!RequestUtils.isInGame(request)) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.spectate-is-not-in-duel").replace("%player%", toSpectateName));
            return false;
        }
        if (Duels.spectators.containsKey(player.getUniqueId())) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.already-spectating"));
            return false;
        }
        Player toSpectateOpponent = Bukkit.getPlayer(request.getOpponent(toSpectate.getUniqueId()));
        Spectating.startSpectating(player, toSpectate, plugin);
        Chat.sendMessage(plugin, toSpectate, plugin.languageConfig.getString("duel.is-spectating").replace("%player%", player.getName()));
        Chat.sendMessage(plugin, toSpectateOpponent, plugin.languageConfig.getString("duel.is-spectating").replace("%player%", player.getName()));
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.spectate-success").replace("%player%", toSpectateName));
        return false;
    }
}
