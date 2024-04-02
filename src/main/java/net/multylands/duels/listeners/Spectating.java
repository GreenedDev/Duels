package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class Spectating implements Listener {
    Duels plugin;

    public Spectating(Duels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player playerWhoMoved = event.getPlayer();
        UUID uuid = playerWhoMoved.getUniqueId();
        if (!Duels.spectators.containsKey(uuid)) {
            return;
        }
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(Duels.spectators.get(uuid));
        Location loc1 = request.getArena().getFirstLocation();
        if (playerWhoMoved.getLocation().distance(loc1) < 50) {
            return;
        }
        //this is slightly more optimized than getting both loc at the same time
        Location loc2 = request.getArena().getSecondLocation();
        if (playerWhoMoved.getLocation().distance(loc2) < 50) {
            return;
        }
        playerWhoMoved.teleport(loc1);
        event.setCancelled(true);
    }
    //antidamage for spectators
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player damagedPlayer = ((Player) entity).getPlayer();
        Entity damagerEntity = event.getDamager();
        if (!(damagerEntity instanceof Player)) {
            return;
        }
        Player damager = ((Player) damagerEntity).getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(damagedPlayer.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getOpponent(damagedPlayer.getUniqueId()) == damager.getUniqueId()) {
            return;
        }
        Chat.sendMessage(plugin, damager, plugin.languageConfig.getString("duel.cannot-damage-in-duel"));
        event.setCancelled(true);
    }

    //quit spectating & tp player to spawn after he quits
    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player playerWhoLeft = event.getPlayer();
        UUID playerWhoLeftUUID = playerWhoLeft.getUniqueId();
        if (!Duels.spectators.containsKey(playerWhoLeftUUID)) {
            return;
        }
        endSpectating(playerWhoLeft, plugin);
    }

    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player commandSender = event.getPlayer();
        UUID commandSenderUUID = commandSender.getUniqueId();
        if (!Duels.spectators.containsKey(commandSenderUUID)) {
            return;
        }
        String command = event.getMessage();
        boolean ifMatchesAny = false;
        for (String whitelisted_or_blacklisted_Command : plugin.getConfig().getStringList("commands_blacklisted_or_whitelisted_in_spectator")) {
            if (command.equalsIgnoreCase(whitelisted_or_blacklisted_Command)) {
                ifMatchesAny = true;
                break;
            }
        }
        if (plugin.getConfig().getBoolean("spectator_whitelist_mode")) {
            //then this command is whitelisted
            if (ifMatchesAny) {
                return;
            }
            Chat.sendMessage(plugin, commandSender, plugin.languageConfig.getString("duel.cant-use-that-command-in-spectator"));
            event.setCancelled(true);
            return;
        }
        //then this command is not in the blacklist
        if (!ifMatchesAny) {
            return;
        }
        Chat.sendMessage(plugin, commandSender, plugin.languageConfig.getString("duel.cant-use-that-command-in-spectator"));
        event.setCancelled(true);
    }

    //preventing death
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        UUID damagedUUID = event.getEntity().getUniqueId();
        if (!Duels.spectators.containsKey(damagedUUID)) {
            return;
        }
        event.setCancelled(true);
    }

    //anti teleport
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!Duels.spectators.containsKey(playerUUID)) {
            return;
        }
        endSpectating(player, plugin);
    }
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
        request.removeSpectator(player.getUniqueId());
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
        request.addSpectator(player.getUniqueId());
        request.storeRequest(false);
    }
}
