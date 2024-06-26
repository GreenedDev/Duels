package net.multylands.duels.utils;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpectatorUtils {
    public static void endSpectating(Player player, Duels plugin) {
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        UUID toSpectateUUID = Duels.spectators.get(player.getUniqueId());
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectateUUID);
        Duels.spectators.remove(player.getUniqueId());
        Player firstPlayer = Bukkit.getPlayer(request.getTarget());
        Player opponent = Bukkit.getPlayer(request.getSender());
        player.teleport(spawnLoc);
        player.setAllowFlight(false);
        if (firstPlayer != null) {
            firstPlayer.showPlayer(plugin, player);
        }
        if (opponent != null) {
            firstPlayer.showPlayer(plugin, player);
        }
        request.getGame().removeSpectator(player.getUniqueId());
        request.storeRequest(false);
    }

    public static void endSpectatingForEndGame(Player player, Duels plugin) {
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        UUID toSpectateUUID = Duels.spectators.get(player.getUniqueId());
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectateUUID);
        Duels.spectators.remove(player.getUniqueId());
        Player firstPlayer = Bukkit.getPlayer(request.getTarget());
        Player opponent = Bukkit.getPlayer(request.getSender());
        player.teleport(spawnLoc);
        player.setAllowFlight(false);
        if (firstPlayer != null) {
            firstPlayer.showPlayer(plugin, player);
        }
        if (opponent != null) {
            firstPlayer.showPlayer(plugin, player);
        }
    }

    public static void startSpectating(Player player, Player toSpectate, Duels plugin) {
        //the teleport needs to be first here
        player.teleport(toSpectate);
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(toSpectate.getUniqueId());
        Player opponent = Bukkit.getPlayer(request.getOpponent(toSpectate.getUniqueId()));
        Duels.spectators.put(player.getUniqueId(), toSpectate.getUniqueId());
        player.setAllowFlight(true);
        toSpectate.hidePlayer(plugin, player);
        opponent.hidePlayer(plugin, player);
        request.getGame().addSpectator(player.getUniqueId());
        request.storeRequest(false);
    }
}
