package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.object.Arena;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopSpectateCommand implements CommandExecutor {
    public Duels plugin;

    public StopSpectateCommand(Duels plugin) {
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
        if (!Duels.spectators.containsKey(player.getUniqueId())) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.not-in-spectator")));
            return false;
        }
        Spectating.endSpectating(player, plugin);
        player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.spectate-end-success")));
        return false;
    }
}
