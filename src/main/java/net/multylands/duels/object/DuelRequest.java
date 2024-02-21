package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.ArenaList;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    public void startGame(Arena arena) {
        this.arena = arena;
        arena.setAvailable(false);
        Player player = Bukkit.getPlayer(playerUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        DuelRequest secondRequest = Duels.requests.get(targetUUID);

        Location targetLoc = plugin.arenasConfig.getLocation(arena.getID()+".pos1");
        Location playerLoc = plugin.arenasConfig.getLocation(arena.getID()+".pos2");
        plugin.getLogger().log(Level.INFO, arena.getID());
        plugin.getLogger().log(Level.INFO, "x: " + plugin.arenasConfig.getInt(arena.getID() + ".pos1.x"));
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
                target.sendMessage(Chat.Color("&aDuel started. Get ready your weapons and your luck!"));
                player.sendMessage(Chat.Color("&aDuel started. Get ready your weapons and your luck!"));
                setIsStartingIn5Seconds(false);
                secondRequest.setIsStartingIn5Seconds(false);
                task.cancel();
            } else {
                player.sendMessage(Chat.Color("&aDuel is starting in "+color+countdown+"s&a."));
                target.sendMessage(Chat.Color("&aDuel is starting in "+color+countdown+"s&a."));
            }
        }, 0, 20);
        taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //don't put endgame here because it will cancel this task
            Duels.tasksToCancel.remove(this);
            arena.setAvailable(true);
            removeStoreRequest();
            target.sendMessage(Chat.Color("&cYou ran out of time so you have teleported to the spawn."));
            player.sendMessage(Chat.Color("&cYou ran out of time so you have teleported to the spawn."));
            Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
        }, 20L * 60 * plugin.getConfig().getInt("max_duel_time_minutes")).getTaskId();
        Random random = new Random();
        taskAssignedIDInTheList = random.nextInt(999999);
        Duels.tasksToCancel.put(taskAssignedIDInTheList, taskId);
        storeRequest();
    }
    public void endGame(UUID winnerUUID) {
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
            loser.sendMessage(Chat.Color("&cYou lost the duel. " + winner.getName()));
        }
        winner.sendMessage(Chat.Color("&aYou won the duel! You will be teleported to spawn in 10 seconds. Pick up their items."));
        Duels.scheduler.runTaskLater(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + winner.getName());
        }, 20 * 10);
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
}
