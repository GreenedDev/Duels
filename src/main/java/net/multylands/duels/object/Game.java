package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import net.multylands.duels.utils.SpectatorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class Game {
    UUID senderUUID;
    UUID targetUUID;
    UUID winnerUUID;
    DuelRestrictions duelRestrictions;
    boolean isInGame;
    int taskAssignedIDInTheList;
    boolean isAboutToBeTeleportedToSpawn = false;
    DuelRequest request;
    Duels plugin;
    List<UUID> spectators = new ArrayList<>();
    boolean isStartingIn5Seconds;
    Arena arena;
    int taskId = 0;
    Instant runOutOfTime;

    public Game(UUID sender, UUID target, DuelRequest request, DuelRestrictions duelRestrictions, boolean isInGame, boolean isStartingIn5Seconds, Duels plugin) {
        this.senderUUID = sender;
        this.targetUUID = target;
        this.request = request;
        this.isStartingIn5Seconds = isStartingIn5Seconds;
        this.duelRestrictions = duelRestrictions;
        this.isInGame = isInGame;
        this.plugin = plugin;
    }

    public UUID getSender() {
        return senderUUID;
    }

    public UUID getTarget() {
        return targetUUID;
    }

    public boolean getIsInGame() {
        return isInGame;
    }

    public boolean getIsStartingIn5Seconds() {
        return isStartingIn5Seconds;
    }

    public void setIsAboutToTeleportedToSpawn(boolean value) {
        isAboutToBeTeleportedToSpawn = value;
    }

    public boolean getIsAboutToTeleportedToSpawn() {
        return isAboutToBeTeleportedToSpawn;
    }

    public UUID getWinnerUUID() {
        return winnerUUID;
    }

    public void setWinnerUUID(UUID winner) {
        winnerUUID = winner;
    }

    public DuelRestrictions getRestrictions() {
        return duelRestrictions;
    }

    public void setSender(UUID player) {
        this.senderUUID = player;
    }

    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    public void setTarget(UUID target) {
        this.targetUUID = target;
    }

    public void setIsStartingIn5Seconds(boolean YesOrNot) {
        this.isStartingIn5Seconds = YesOrNot;
    }

    public void setDuelRestrictions(DuelRestrictions duelRestrictions) {
        this.duelRestrictions = duelRestrictions;
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
    }

    public void startGame(Arena arena) {
        this.arena = arena;

        arena.setAvailable(false);
        arena.setSenderUUID(senderUUID);
        arena.setTargetUUID(targetUUID);
        Player player = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);

        Location targetLoc = arena.getFirstLocation();
        Location playerLoc = arena.getSecondLocation();
        if (targetLoc == null) {
            plugin.getLogger().log(Level.INFO, "DUELS targetLoc is null in the DuelRequest startGame void");
        }
        if (playerLoc == null) {
            plugin.getLogger().log(Level.INFO, "DUELS playerLoc is null in the DuelRequest startGame void");
        }
        target.teleport(targetLoc);
        player.teleport(playerLoc);
        setIsInGame(true);
        setIsStartingIn5Seconds(true);

        Set<DuelRequest> requestsThatWereAlreadyThere = RequestUtils.getRequestsReceiverToSenders(targetUUID, senderUUID);
        requestsThatWereAlreadyThere.add(request);
        Duels.requestsReceiverToSenders.put(senderUUID, requestsThatWereAlreadyThere);

        disableFlying(player, target);
        removeEffectsIfDisabled(player, target);
        disableShieldsIfDisabled(player, target);
        AtomicInteger countdown = new AtomicInteger(6);
        Duels.scheduler.runTaskTimer(plugin, task -> {
            String color = Chat.getColorForNumber(countdown);
            countdown.getAndDecrement();
            if (countdown.get() == 0) {
                setIsStartingIn5Seconds(false);
                task.cancel();
                Chat.messagePlayers(player, target, plugin.languageConfig.getString("duel.game.duel-started"));
            } else {
                Chat.messagePlayers(player, target, plugin.languageConfig.getString("duel.game.duel-countdown").replace("%color+countdown%", color + countdown));
            }
        }, 0, 20);
        saveAndRunRanOutOfTimeTask();
        request.storeRequest(true);
    }

    public void endGame(UUID winnerUUIDFromMethod, boolean ranOutOfTime, boolean endedByServerRestart) {
        setIsInGame(false);
        request.storeRequest(false);
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        for (UUID spectatorUUID : spectators) {
            SpectatorUtils.endSpectatingForEndGame(Bukkit.getPlayer(spectatorUUID), plugin);
        }
        spectators.clear();
        Player player = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        if (target != null) {
            resetShieldsDelay(player);
        }
        if (player != null) {
            resetShieldsDelay(target);
        }
        if (ranOutOfTime) {
            Chat.messagePlayers(player, target, plugin.languageConfig.getString("duel.game.ran-out-of-time"));
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
            return;
        }
        //don't move these two lines below to above because they will cancel the ranOutOfTime task and code in it will never be executed
        Bukkit.getScheduler().cancelTask(Duels.tasksToCancel.get(senderUUID));
        Duels.tasksToCancel.remove(senderUUID);
        if (endedByServerRestart) {
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
            return;
        }
        setIsAboutToTeleportedToSpawn(true);
        request.storeRequest(false);
        setWinnerUUID(winnerUUIDFromMethod);
        Player winner = Bukkit.getPlayer(winnerUUID);
        Player loser = Bukkit.getPlayer(getOpponent(winnerUUID));
        if (winner == null) {
            plugin.getLogger().log(Level.INFO, "&c&lDUELS SOMETHING WENT SUPER WRONG. CONTACT GREENED ERROR TYPE #3");
        }
        if (loser != null) {
            Chat.sendMessage(loser, plugin.languageConfig.getString("duel.game.lost-duel"));
        }
        Chat.sendMessage(winner, plugin.languageConfig.getString("duel.game.won-duel").replace("%number%", plugin.getConfig().getInt("time_to_pick_up_items") + ""));
        Duels.scheduler.runTaskLater(plugin, () -> {
            winner.teleport(spawnLoc);
            arena.setAvailable(true);
            isAboutToBeTeleportedToSpawn = false;
            request.removeStoreRequest(true);
        }, 20L * plugin.getConfig().getInt("time_to_pick_up_items"));
    }

    public UUID getOpponent(UUID someone) {
        if (someone == senderUUID) {
            return targetUUID;
        } else if (someone == targetUUID) {
            return senderUUID;
        } else {
            System.out.println("Plugin tried to get opponent of the player that's " +
                    "not in that duel object. please report this to the author immediately");
            return null;
        }
    }

    public Instant getRunOutOfTimeInstant() {
        return runOutOfTime;
    }

    public void setRunOutOfTimeInstant(Instant newValue) {
        runOutOfTime = newValue;
    }

    public int getNumberOfSpectators() {
        return spectators.size();
    }

    public Arena getArena() {
        return arena;
    }

    public void saveAndRunRanOutOfTimeTask() {
        Random random = new Random();
        int max_duel_time_minutes = plugin.getConfig().getInt("max_duel_time_minutes");
        taskAssignedIDInTheList = random.nextInt(999999);
        taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //just provide random uuid here it doesn't really matter.
            endGame(senderUUID, true, false);
        }, 20L * 60 * max_duel_time_minutes).getTaskId();
        Duels.tasksToCancel.put(senderUUID, taskId);
        Instant timeWhenDuelRunsOutOfTime = Instant.now().plus(max_duel_time_minutes, ChronoUnit.MINUTES);
        setRunOutOfTimeInstant(timeWhenDuelRunsOutOfTime);
    }

    public void disableShieldsIfDisabled(Player player, Player target) {
        if (!duelRestrictions.isShieldsAllowed()) {
            int maxDuelTimeInTicks = plugin.getConfig().getInt("max_duel_time_minutes") * 60 * 20;
            player.setShieldBlockingDelay(maxDuelTimeInTicks);
            target.setShieldBlockingDelay(maxDuelTimeInTicks);
        }
    }

    public void resetShieldsDelay(Player player) {
        if (!duelRestrictions.isShieldsAllowed()) {
            player.setShieldBlockingDelay(plugin.getConfig().getInt("default-shield-blocking-delay"));
        }
    }

    public void removeEffectsIfDisabled(Player player, Player target) {
        if (!duelRestrictions.isPotionsAllowed()) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            for (PotionEffect effect : target.getActivePotionEffects()) {
                target.removePotionEffect(effect.getType());
            }
        }
    }

    public void disableFlying(Player player, Player target) {
        player.setFlying(false);
        player.setAllowFlight(false);
        target.setFlying(false);
        target.setAllowFlight(false);
    }
}
