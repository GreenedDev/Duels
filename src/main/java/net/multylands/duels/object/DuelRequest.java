package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class DuelRequest {
    UUID senderUUID;
    UUID targetUUID;
    UUID winnerUUID;
    DuelRestrictions duelRestrictions;
    boolean isInGame;
    int taskAssignedIDInTheList;
    boolean isAboutToBeTeleportedToSpawn = false;
    Duels plugin;
    List<UUID> spectators = new ArrayList<>();
    boolean isStartingIn5Seconds;
    Arena arena;
    int taskId = 0;
    ArrayList<Integer> taskIdForRequestTimeout = new ArrayList<>();

    public DuelRequest(UUID sender, UUID target, DuelRestrictions duelRestrictions, boolean isInGame, boolean isStartingIn5Seconds, Duels plugin) {
        this.senderUUID = sender;
        this.targetUUID = target;
        this.isStartingIn5Seconds = isStartingIn5Seconds;
        this.duelRestrictions = duelRestrictions;
        this.isInGame = isInGame;
        this.plugin = plugin;
    }

    public int getTaskAssignedIDInTheList() {
        return taskAssignedIDInTheList;
    }

    public UUID getSender() {
        return senderUUID;
    }

    public ArrayList<Integer> getTaskIdsForRequestTimeout() {
        return taskIdForRequestTimeout;
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

    public DuelRestrictions getDuelRestrictions() {
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

    public void storeRequest(boolean justStarted) {
        Set<DuelRequest> requestsThatWereAlreadyThere = getRequestsThatWereAlreadyThereInReceiveToSendersWithAlreadyRemovedThisRequest(targetUUID);

        //second map
        Set<DuelRequest> requestsThatWereAlreadyThereSenderToReceiver = getRequestsThatWereAlreadyThereInSenderToReceiversWithAlreadyRemovedThisRequest(senderUUID);

        if (justStarted) {
            Duels.playerToOpponentInGame.put(senderUUID, targetUUID);
            Duels.playerToOpponentInGame.put(targetUUID, senderUUID);
        }
        int taskIDOfTheTimeout = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isInGame) {
                return;
            }
            removeStoreRequest(false);
        }, plugin.getConfig().getInt("request-timeout") * 20).getTaskId();
        taskIdForRequestTimeout.add(taskIDOfTheTimeout);

        //do not move the below code up because the taskid will not be saved then
        requestsThatWereAlreadyThere.add(this);
        requestsThatWereAlreadyThereSenderToReceiver.add(this);
        Duels.requestsReceiverToSenders.put(targetUUID, requestsThatWereAlreadyThere);
        Duels.requestsSenderToReceivers.put(senderUUID, requestsThatWereAlreadyThereSenderToReceiver);
    }

    public void removeStoreRequest(boolean justEnded) {
        Iterator<Integer> iterator = taskIdForRequestTimeout.iterator();
        iterator.forEachRemaining(taskIDOfTheTimeout -> {
            Bukkit.getScheduler().cancelTask(taskIDOfTheTimeout);
        });
        taskIdForRequestTimeout.clear();
        Set<DuelRequest> requestsThatWereAlreadyThere = getRequestsThatWereAlreadyThereInReceiveToSendersWithAlreadyRemovedThisRequest(targetUUID);

        Set<DuelRequest> requestsThatWereAlreadyThereSenderToReceiver = getRequestsThatWereAlreadyThereInSenderToReceiversWithAlreadyRemovedThisRequest(senderUUID);
        if (requestsThatWereAlreadyThereSenderToReceiver.isEmpty()) {
            Duels.requestsSenderToReceivers.remove(senderUUID);
        } else {
            Duels.requestsSenderToReceivers.put(senderUUID, requestsThatWereAlreadyThereSenderToReceiver);
        }
        if (requestsThatWereAlreadyThere.isEmpty()) {
            Duels.requestsReceiverToSenders.remove(targetUUID);
        } else {
            Duels.requestsReceiverToSenders.put(targetUUID, requestsThatWereAlreadyThere);
        }
        if (justEnded) {
            Duels.playerToOpponentInGame.remove(senderUUID);
            Duels.playerToOpponentInGame.remove(targetUUID);
        }
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
        Set<DuelRequest> requestsThatWereAlreadyThere = getRequestsThatWereAlreadyThereInReceiveToSendersWithAlreadyRemovedThisRequest(targetUUID);
        requestsThatWereAlreadyThere.add(this);
        Duels.requestsReceiverToSenders.put(senderUUID, requestsThatWereAlreadyThere);
        disableFlying(player, target);
        handleEffects(player, target);
        handleShields(player, target);
        AtomicInteger countdown = new AtomicInteger(6);
        Duels.scheduler.runTaskTimer(plugin, task -> {
            String color = getColorForNumber(countdown);
            countdown.getAndDecrement();
            if (countdown.get() == 0) {
                Chat.messagePlayers(plugin, player, target, plugin.languageConfig.getString("duel.duel-started"));
                setIsStartingIn5Seconds(false);
                task.cancel();
            } else {
                Chat.messagePlayers(plugin, player, target, plugin.languageConfig.getString("duel.duel-countdown").replace("%color+countdown%", color + countdown));
            }
        }, 0, 20);
        saveRanOutOfTimeTask();
        storeRequest(true);
    }

    public void endGame(UUID winnerUUIDFromMethod, boolean ranOutOfTime, boolean endedByServerRestart) {
        setIsInGame(false);
        storeRequest(false);
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        for (UUID spectatorUUID : spectators) {
            Spectating.endSpectatingForEndGame(Bukkit.getPlayer(spectatorUUID), plugin);
        }
        spectators.clear();
        Player player = Bukkit.getPlayer(senderUUID);
        Player target = Bukkit.getPlayer(getOpponent(targetUUID));
        if (target != null) {
            undoShields(player);
        }
        if (player != null) {
            undoShields(target);
        }
        if (ranOutOfTime) {
            Chat.messagePlayers(plugin, player, target, plugin.languageConfig.getString("duel.ran-out-of-time"));
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
            return;
        }
        //dont move these two lines below to above because they will cancel the ranoutoftime task and code in it will never be executed
        Bukkit.getScheduler().cancelTask(Duels.tasksToCancel.get(senderUUID));
        Duels.tasksToCancel.remove(senderUUID);
        if (endedByServerRestart) {
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
            return;
        }
        setIsAboutToTeleportedToSpawn(true);
        storeRequest(false);
        setWinnerUUID(winnerUUIDFromMethod);
        Player winner = Bukkit.getPlayer(winnerUUID);
        Player loser = Bukkit.getPlayer(getOpponent(winnerUUID));
        if (winner == null) {
            plugin.getLogger().log(Level.INFO, "&c&lDUELS SOMETHING WENT SUPER WRONG. CONTACT GREENED ERROR TYPE #3");
        }
        if (loser != null) {
            Chat.sendMessage(plugin, loser, plugin.languageConfig.getString("duel.lost-duel"));
        }
        Chat.sendMessage(plugin, winner, plugin.languageConfig.getString("duel.won-duel").replace("%number%", plugin.getConfig().getInt("time_to_pick_up_items") + ""));
        Duels.scheduler.runTaskLater(plugin, () -> {
            winner.teleport(spawnLoc);
            arena.setAvailable(true);
            isAboutToBeTeleportedToSpawn = false;
            removeStoreRequest(true);
        }, 20L * plugin.getConfig().getInt("time_to_pick_up_items"));
    }

    public UUID getOpponent(UUID someone) {
        if (someone == senderUUID) {
            return targetUUID;
        } else {
            return senderUUID;
        }
    }

    public String getEnabled() {
        StringBuilder builder = new StringBuilder();
        DuelRestrictions restrictions = getDuelRestrictions();
        if (restrictions.isNotchAllowed) {
            builder.append("Notch,");
        }
        if (restrictions.isPotionsAllowed) {
            builder.append("Potions,");
        }
        if (restrictions.isGoldenAppleAllowed) {
            builder.append("Golden apple,");
        }
        if (restrictions.isShieldsAllowed) {
            builder.append("Shields,");
        }
        if (restrictions.isTotemsAllowed) {
            builder.append("Totems,");
        }
        if (restrictions.isBowAllowed) {
            builder.append("Bow,");
        }
        if (restrictions.isElytraAllowed) {
            builder.append("Elytra,");
        }
        if (restrictions.isEnderPearlAllowed) {
            builder.append("Ender Pearl,");
        }
        if (restrictions.isKeepInventoryEnabled) {
            builder.append("Keep Inventory,");
        }
        String finalString = builder.toString();
        if (finalString.isEmpty()) {
            return null;
        }
        String end = finalString.replace(finalString.substring(0, finalString.length() - 1), "");
        if (end.equals(",")) {
            return finalString.substring(0, finalString.length() - 1) + ".";
        } else {
            return null;
        }
    }

    public String getDisabled() {
        StringBuilder builder = new StringBuilder();
        DuelRestrictions restrictions = getDuelRestrictions();
        if (!restrictions.isNotchAllowed) {
            builder.append("Notch,");
        }
        if (!restrictions.isPotionsAllowed) {
            builder.append("Potions,");
        }
        if (!restrictions.isGoldenAppleAllowed) {
            builder.append("Golden apple,");
        }
        if (!restrictions.isShieldsAllowed) {
            builder.append("Shields,");
        }
        if (!restrictions.isTotemsAllowed) {
            builder.append("Totems,");
        }
        if (!restrictions.isBowAllowed) {
            builder.append("Bow,");
        }
        if (!restrictions.isElytraAllowed) {
            builder.append("Elytra,");
        }
        if (!restrictions.isEnderPearlAllowed) {
            builder.append("Ender Pearl,");
        }
        if (!restrictions.isKeepInventoryEnabled) {
            builder.append("Keep Inventory,");
        }
        String finalString = builder.toString();
        if (finalString.isEmpty()) {
            return null;
        }
        String end = finalString.replace(finalString.substring(0, finalString.length() - 1), "");
        if (end.equals(",")) {
            return finalString.substring(0, finalString.length() - 1) + ".";
        } else {
            return null;
        }
    }

    public String getColorForNumber(AtomicInteger countdown) {
        if (countdown.get() == 5) {
            return "&4";
        } else if (countdown.get() == 4) {
            return "&c";
        } else if (countdown.get() == 3) {
            return "&6";
        } else if (countdown.get() == 2) {
            return "&2";
        } else if (countdown.get() == 1) {
            return "&a";
        }
        return "";
    }

    public Arena getArena() {
        return arena;
    }

    public void saveRanOutOfTimeTask() {
        Random random = new Random();
        taskAssignedIDInTheList = random.nextInt(999999);
        taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //just provide random uuid here it doesn't really matter.
            endGame(senderUUID, true, false);
        }, 20L * 60 * plugin.getConfig().getInt("max_duel_time_minutes")).getTaskId();
        Duels.tasksToCancel.put(senderUUID, taskId);
    }

    public void handleShields(Player player, Player target) {
        if (!duelRestrictions.isShieldsAllowed()) {
            int maxDuelTimeInTicks = plugin.getConfig().getInt("max_duel_time_minutes") * 60 * 20;
            player.setShieldBlockingDelay(maxDuelTimeInTicks);
            target.setShieldBlockingDelay(maxDuelTimeInTicks);
        }
    }

    public void undoShields(Player player) {
        if (!duelRestrictions.isShieldsAllowed()) {
            player.setShieldBlockingDelay(plugin.getConfig().getInt("default-shield-blocking-delay"));
        }
    }

    public void handleEffects(Player player, Player target) {
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

    public Set<DuelRequest> getRequestsThatWereAlreadyThereInReceiveToSendersWithAlreadyRemovedThisRequest(UUID targetUUID) {
        Set<DuelRequest> requestsThatWereAlreadyThere = Duels.requestsReceiverToSenders.get(targetUUID);
        //checking if there was no value set for that key preventing requestsThatWereAlreadyThere to be null
        if (Duels.requestsReceiverToSenders.get(targetUUID) == null) {
            requestsThatWereAlreadyThere = new HashSet<>();
        } else {
            //removing the old request that was in map
            Iterator<DuelRequest> iterator = requestsThatWereAlreadyThere.iterator();
            while (iterator.hasNext()) {
                DuelRequest request = iterator.next();
                if (request.getSender() == senderUUID && request.getTarget() == targetUUID) {
                    iterator.remove();
                    break;
                }
            }
        }
        return requestsThatWereAlreadyThere;
    }

    public Set<DuelRequest> getRequestsThatWereAlreadyThereInSenderToReceiversWithAlreadyRemovedThisRequest(UUID senderUUID) {
        Set<DuelRequest> requestsThatWereAlreadyThereSenderToReceiver = Duels.requestsSenderToReceivers.get(senderUUID);
        if (Duels.requestsSenderToReceivers.get(senderUUID) == null) {
            requestsThatWereAlreadyThereSenderToReceiver = new HashSet<>();
        } else {
            //removing the old request that was in map
            Iterator<DuelRequest> iterator = requestsThatWereAlreadyThereSenderToReceiver.iterator();
            while (iterator.hasNext()) {
                DuelRequest request = iterator.next();
                if (request.getSender() == senderUUID && request.getTarget() == targetUUID) {
                    iterator.remove();
                    break;
                }
            }
        }
        return requestsThatWereAlreadyThereSenderToReceiver;
    }
}
