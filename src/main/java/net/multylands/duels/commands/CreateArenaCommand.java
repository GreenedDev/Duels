package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArenaCommand implements CommandExecutor {
    public Duels plugin;

    public CreateArenaCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("only-player-command")));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (!player.hasPermission("duels.createarena")) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("no-perm")));
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(Chat.Color(plugin.languageConfig.getString("command-usage").replace("%command%", label) + " arenaName"));
            return false;
        }
        String arenaName = args[0];
        if (plugin.arenasConfig.contains(arenaName)) {
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.create-arena-already-exists")));
            return false;
        }
        plugin.arenasConfig.set(arenaName + ".pos1", player.getLocation());
        plugin.saveArenasConfig();
        player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.create-arena-success").replace("%arena%", arenaName)));
        return false;
    }
}
