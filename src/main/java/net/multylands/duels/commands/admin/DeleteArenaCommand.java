package net.multylands.duels.commands.admin;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteArenaCommand implements CommandExecutor {
    public Duels plugin;

    public DeleteArenaCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (!player.hasPermission("duels.admin.deletearena")) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        if (args.length != 1) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " deletearena arenaName");
            return false;
        }
        String arenaName = args[0];
        if (!plugin.arenasConfig.contains(arenaName)) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.delete-arena-doesnt-exists"));
            return false;
        }
        plugin.arenasConfig.set(arenaName, null);
        plugin.saveArenasConfig();
        plugin.reloadArenaConfig();
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.delete-arena-success").replace("%arena%", arenaName));
        return false;
    }
}
