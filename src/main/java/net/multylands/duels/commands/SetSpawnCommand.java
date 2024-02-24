package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
    public Duels plugin;

    public SetSpawnCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("only-player-command")));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (!player.hasPermission("duels.setspawn")) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("no-perm")));
            return false;
        }
        if (args.length != 0) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("command-usage").replace("%command%", label) + " arenaName pos1"));
            return false;
        }
        plugin.getConfig().set("spawn_location", player.getLocation());
        plugin.saveConfig();
        player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.spawn-set-success")));
        return false;
    }
}
