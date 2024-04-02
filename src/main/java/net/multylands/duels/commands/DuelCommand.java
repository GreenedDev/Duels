package net.multylands.duels.commands;


import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.gui.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

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
            Chat.sendMessageSender(plugin, sender, plugin.languageConfig.getString("only-player-command"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("command-usage").replace("%command%", label)+ " player");
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-is-offline"));
            return false;
        }
        if (player.equals(target)) {
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.cant-duel-yourself"));
            return false;
        }
        //already sent check
        if (Duels.requestsReceiverToSenders.containsKey(target.getUniqueId())) {
            for (DuelRequest request : Duels.requestsReceiverToSenders.get(target.getUniqueId())) {
                if (request.getSender() != player.getUniqueId()) {
                    continue;
                }
                if (request.getIsAboutToTeleportedToSpawn()) {
                    Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-already-in-duel").replace("%player%", target.getDisplayName()));
                    return false;
                }
                Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.request-already-sent").replace("%player%", target.getDisplayName()));
                return false;
            }
        }
        //target already in duel check
        if (Duels.requestsReceiverToSenders.containsKey(target.getUniqueId())) {
            for (DuelRequest request : Duels.requestsReceiverToSenders.get(target.getUniqueId())) {
                if (!request.getIsInGame()) {
                    continue;
                }
                Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.target-already-in-duel").replace("%player%", target.getDisplayName()));
                return false;
            }
        }
        //ignore check
        if (plugin.ignoresConfig.contains("Ignores." + target.getUniqueId())) {
            for (String loopUUID : plugin.ignoresConfig.getStringList("Ignores." + target.getUniqueId())) {
                if (!Objects.equals(loopUUID, player.getUniqueId().toString())) {
                    continue;
                }
                Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.player-is-ignoring-requests"));
                return false;
            }
        }
        guiManager.openInventory(player, target);
        return false;
    }
}
