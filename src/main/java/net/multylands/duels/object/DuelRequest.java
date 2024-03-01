package net.multylands.duels.object;

import net.multylands.duels.Duels;
import net.multylands.duels.listeners.Spectating;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

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
    Duels plugin;
    List<UUID> spectators = new ArrayList<>();
    boolean isStartingIn5Seconds;
    Arena arena;
    int taskId = 0;

    public DuelRequest(UUID player, UUID target, DuelRestrictions duelRestrictions, boolean isInGame, boolean isStartingIn5Seconds, Duels plugin) {
        this.playerUUID = player;
        this.targetUUID = target;
        this.isStartingIn5Seconds = isStartingIn5Seconds;
        this.duelRestrictions = duelRestrictions;
        this.isInGame = isInGame;
        this.plugin = plugin;
    }

    public int getTaskAssignedIDInTheList() {
        return taskAssignedIDInTheList;
    }

    public UUID getPlayer() {
        return playerUUID;
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

    public void storeRequest(boolean justStarted) {
        Duels.requests.put(playerUUID, this);
        if (justStarted) {
            Duels.playerToOpponentInGame.put(playerUUID, targetUUID);
            Duels.playerToOpponentInGame.put(targetUUID, playerUUID);
        }
    }

    public void removeStoreRequest(boolean justEnded) {
        Duels.requests.remove(playerUUID);
        if (justEnded) {
            Duels.playerToOpponentInGame.remove(playerUUID);
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
        Player player = Bukkit.getPlayer(playerUUID);
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
        Duels.requests.put(playerUUID, this);
        disableFlying(player, target);
        removeAppliedEffectsIfDisabled(player, target);
        removeShieldsIfDisabled(player, target);
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

    public void endGame(UUID winnerUUID, boolean ranOutOfTime, boolean endedByServerRestart) {
        arena.setAvailable(true);
        removeStoreRequest(true);
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        for (UUID spectatorUUID : spectators) {
            Spectating.endSpectatingForEndGame(Bukkit.getPlayer(spectatorUUID), plugin);
        }
        spectators.clear();
        if (ranOutOfTime) {
            Player player = Bukkit.getPlayer(playerUUID);
            Player target = Bukkit.getPlayer(targetUUID);
            Chat.messagePlayers(plugin, player, target, plugin.languageConfig.getString("duel.ran-out-of-time"));
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
            return;
        }
        //dont move these two lines below to above because they will cancel the ranoutoftime task and code in it will never be executed
        Bukkit.getScheduler().cancelTask(Duels.tasksToCancel.get(playerUUID));
        Duels.tasksToCancel.remove(playerUUID);
        if (endedByServerRestart) {
            Player player = Bukkit.getPlayer(playerUUID);
            Player target = Bukkit.getPlayer(targetUUID);
            target.teleport(spawnLoc);
            player.teleport(spawnLoc);
            return;
        }
        Player winner = Bukkit.getPlayer(winnerUUID);
        Player loser = Bukkit.getPlayer(getOpponent(winnerUUID));
        if (winner == null) {
            plugin.getLogger().log(Level.INFO, "&c&lDUELS SOMETHING WENT SUPER WRONG!. CONTACT GREENED ERROR TYPE #3");
        }
        if (loser != null) {
            Chat.sendMessage(plugin, loser, plugin.languageConfig.getString("duel.lost-duel"));
        }
        Chat.sendMessage(plugin, winner, plugin.languageConfig.getString("duel.won-duel").replace("%number%", plugin.getConfig().getInt("time_to_pick_up_items") + ""));
        Duels.scheduler.runTaskLater(plugin, () -> {
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
        if (restrictions.isElytraAllowed) {
            builder.append("Elytra,");
        }
        if (restrictions.isEnderPearlAllowed) {
            builder.append("Ender Pearl,");
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
            endGame(playerUUID, true, false);
        }, 20L * 60 * plugin.getConfig().getInt("max_duel_time_minutes")).getTaskId();
        Duels.tasksToCancel.put(playerUUID, taskId);
    }

    public void removeShieldsIfDisabled(Player player, Player target) {
        if (!duelRestrictions.isShieldsAllowed()) {
            ItemStack playerOffHand = player.getInventory().getItemInOffHand();
            ItemStack playerMainHand = player.getInventory().getItemInMainHand();
            ItemStack targetOffHand = target.getInventory().getItemInOffHand();
            ItemStack targetMainHand = target.getInventory().getItemInMainHand();
            if (playerOffHand.getType() == Material.SHIELD) {
                player.getWorld().dropItem(player.getLocation(), playerOffHand);
                player.getInventory().setItemInOffHand(null);
            }
            if (playerMainHand.getType() == Material.SHIELD) {
                player.getWorld().dropItem(player.getLocation(), playerMainHand);
                player.getInventory().remove(playerMainHand);
            }
            if (targetOffHand.getType() == Material.SHIELD) {
                target.getWorld().dropItem(target.getLocation(), targetOffHand);
                target.getInventory().setItemInOffHand(null);
            }
            if (targetMainHand.getType() == Material.SHIELD) {
                target.getWorld().dropItem(target.getLocation(), targetMainHand);
                target.getInventory().remove(targetMainHand);
            }
        }
    }

    public void removeAppliedEffectsIfDisabled(Player player, Player target) {
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
