package net.multylands.duels.commands;


import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class DuelCommand implements CommandExecutor {
    public GUIManager guiManager;
    public Duels plugin;

    public DuelCommand(GUIManager guimanager, Duels plugin) {
        this.guiManager = guimanager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.Color("&cYou must be a player to use this command!"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(Chat.Color("&cUsage: /" + label + " <player>"));
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Chat.Color("&cPlayer not found!"));
            return false;
        }
        if (player.equals(target)) {
            player.sendMessage(Chat.Color("&cYou can't duel yourself!"));
            return false;
        }
        if (Duels.requests.containsKey(player.getUniqueId())) {
            DuelRequest request = Duels.requests.get(player.getUniqueId());
            UUID requestsTarget = request.getTarget();
            if (requestsTarget.equals(target.getUniqueId())) {
                player.sendMessage(Chat.Color("&cYou have already sent the duel request to &b"+target.getDisplayName()));
                return false;
            }
        }
        if (plugin.ignoresConfig.contains("Ignores." + target.getUniqueId())) {
            for (String loopUUID : plugin.ignoresConfig.getStringList("Ignores." + target.getUniqueId())) {
                if (Objects.equals(loopUUID, player.getUniqueId().toString())) {
                    player.sendMessage(Chat.Color("&cGiven player is ignoring your duel requests!"));
                    return false;
                }
            }
        }
        guiManager.openInventory(player, target);
        return false;
    }
}
