package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class DuelRequest {
    UUID playerUUID;
    UUID targetUUID;
    DuelRestrictions duelRestrictions;
    boolean isInGame;
    int taskAssignedIDInTheList;
    UUID senderUUID;
    Duels plugin;
    List<UUID> spectators = new ArrayList<>();
    boolean isStartingIn5Seconds;
    Arena arena;
    int taskId = 0;

    public DuelRequest(UUID player, UUID target, DuelRestrictions duelRestrictions, boolean isInGame, boolean isStartingIn5Seconds, Duels plugin, UUID sender) {
        this.playerUUID = player;
        this.targetUUID = target;
        this.isStartingIn5Seconds = isStartingIn5Seconds;
        this.duelRestrictions = duelRestrictions;
        this.isInGame = isInGame;
        this.plugin = plugin;
        this.senderUUID = sender;
    }
    public int getTaskAssignedIDInTheList() {
        return taskAssignedIDInTheList;
    }

    public UUID getPlayer() {
        return playerUUID;
    }
    public UUID getOriginalSender() {
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
    public void setOriginalSender(UUID sender) {
        senderUUID = sender;
    }
    public DuelRestrictions getDuelRestrictions() {
        return duelRestrictions;
    }

    public void setPlayer(UUID player) {
        this.playerUUID = player;
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
    public void storeRequest() {
        Duels.requests.put(playerUUID, this);
        Duels.requests.put(targetUUID, this);
        Duels.SenderToTarget.put(playerUUID, targetUUID);
        Duels.SenderToTarget.put(targetUUID, playerUUID);
    }
    public void removeStoreRequest() {
        Duels.requests.remove(playerUUID);
        Duels.requests.remove(targetUUID);
        Duels.SenderToTarget.remove(playerUUID);
        Duels.SenderToTarget.remove(targetUUID);
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
        Player player = Bukkit.getPlayer(playerUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        DuelRequest secondRequest = Duels.requests.get(targetUUID);

        Location targetLoc = arena.getFirstLocation();
        Location playerLoc = arena.getSecondLocation();
        if (targetLoc == null) {
            plugin.getLogger().log(Level.INFO, "targetLoc is null in the DuelRequest startGame void");
        }
        if (playerLoc == null) {
            plugin.getLogger().log(Level.INFO, "playerLoc is null in the DuelRequest startGame void");
        }
        target.teleport(targetLoc);
        player.teleport(playerLoc);
        setIsInGame(true);
        setIsStartingIn5Seconds(true);
        secondRequest.setIsInGame(true);
        secondRequest.setIsStartingIn5Seconds(true);
        Duels.requests.put(targetUUID, secondRequest);
        Duels.requests.put(playerUUID, this);
        AtomicInteger countdown = new AtomicInteger(6);
        Duels.scheduler.runTaskTimer(plugin, task -> {
            String color = "";
            if (countdown.get() == 5) {
                color = "&4";
            } else if (countdown.get() == 4) {
                color = "&c";
            } else if (countdown.get() == 3) {
                color = "&6";
            } else if (countdown.get() == 2) {
                color = "&2";
            } else if (countdown.get() == 1) {
                color = "&a";
            }
            countdown.getAndDecrement();
            if (countdown.get() == 0) {
                target.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.duel-started")));
                player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.duel-started")));
                setIsStartingIn5Seconds(false);
                secondRequest.setIsStartingIn5Seconds(false);
                task.cancel();
            } else {
                player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.duel-countdown").replace("%color+countdown%", color+countdown)));
                target.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.duel-countdown").replace("%color+countdown%", color+countdown)));
            }
        }, 0, 20);
        Random random = new Random();
        taskAssignedIDInTheList = random.nextInt(999999);
        taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //don't put endgame here because it will cancel this task
            Duels.tasksToCancel.remove(taskAssignedIDInTheList);
            arena.setAvailable(true);
            removeStoreRequest();
            target.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.ran-out-of-time")));
            player.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.ran-out-of-time")));
            Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
        }, 20L * 60 * plugin.getConfig().getInt("max_duel_time_minutes")).getTaskId();
        Duels.tasksToCancel.put(taskAssignedIDInTheList, taskId);
        storeRequest();
        System.out.println(taskAssignedIDInTheList + "."+ taskId);
    }
    public void endGame(UUID winnerUUID) {
        for (UUID spectatorUUID : spectators) {
            Spectating.endSpectatingForEndGame(Bukkit.getPlayer(spectatorUUID), plugin);
        }
        spectators.clear();
        System.out.println(taskAssignedIDInTheList);
        Bukkit.getScheduler().cancelTask(Duels.tasksToCancel.get(taskAssignedIDInTheList));
        Duels.tasksToCancel.remove(taskAssignedIDInTheList);
        arena.setAvailable(true);
        removeStoreRequest();
        Player winner = Bukkit.getPlayer(winnerUUID);
        Player loser = Bukkit.getPlayer(getOpponent(winnerUUID));
        if (winner == null) {
            Bukkit.broadcastMessage("&c&lSOMETHING WENT SUPER WRONG!. CONTACT GREENED ERROR TYPE #3");
        }
        if (loser != null) {
            loser.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.lost-duel")));
        }
        winner.sendMessage(Chat.Color(plugin.languageConfig.getString("duel.won-duel").replace("%number%", plugin.getConfig().getInt("time_to_pick_up_items") + "")));
        Duels.scheduler.runTaskLater(plugin, () -> {
            Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
            winner.teleport(spawnLoc);
        }, 20L * plugin.getConfig().getInt("time_to_pick_up_items"));
    }
    public UUID getOpponent(UUID someone) {
        if (someone == playerUUID) {
            return targetUUID;
        } else {
            return playerUUID;
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
        String finalString = builder.toString();
        if (finalString.isEmpty()) {
            return null;
        }
        String end = finalString.replace(finalString.substring(0, finalString.length()-1), "");
        if (end.equals(",")) {
            return finalString.substring(0, finalString.length()-1)+".";
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
        String finalString = builder.toString();
        if (finalString.isEmpty()) {
            return null;
        }
        String end = finalString.replace(finalString.substring(0, finalString.length()-1), "");
        if (end.equals(",")) {
            return finalString.substring(0, finalString.length()-1)+".";
        } else {
            return null;
        }
    }
    public Arena getArena() {
        return arena;
    }
}
