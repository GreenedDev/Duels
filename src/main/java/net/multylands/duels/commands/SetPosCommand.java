package net.multylands.duels.commands;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPosCommand implements CommandExecutor {
    public Duels plugin;

    public SetPosCommand(Duels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = ((Player) sender).getPlayer();
        if (!player.hasPermission("duels.setpos")) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("no-perm"));
            return false;
        }
        if (args.length != 2) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("command-usage").replace("%command%", label) + " arenaName pos1");
            return false;
        }
        String arenaName = args[0];
        String pos = args[1];
        if (!plugin.arenasConfig.contains(arenaName)) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.pos-no-arena"));
            return false;
        }
        if (!pos.equalsIgnoreCase("pos1") && !pos.equalsIgnoreCase("pos2")) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.pos-wrong"));
            return false;
        }
        plugin.arenasConfig.set(arenaName + "." + pos.toLowerCase(), player.getLocation());
        plugin.saveArenasConfig();
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.pos-set-successfully").replace("%pos%", pos));
        return false;
    }
}
