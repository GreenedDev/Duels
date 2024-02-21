package net.multylands.duels.listeners;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PvP implements Listener {
    Duels plugin;
    public PvP(Duels plugin) {
        this.plugin = plugin;
    }
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!Duels.requests.containsKey(uuid)) {
            return;
        }
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        if (!request.getIsStartingIn5Seconds()) {
            return;
        }
        event.setCancelled(true);
    }
    //anti leave
    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player playerWhoLeft = event.getPlayer();
        UUID playerWhoLeftUUID = playerWhoLeft.getUniqueId();
        UUID uuid = playerWhoLeft.getUniqueId();
        if (!Duels.requests.containsKey(uuid)) {
            return;
        }
        DuelRequest request = Duels.requests.get(uuid);
        if (!request.getIsInGame()) {
            return;
        }
        UUID winner = request.getOpponent(playerWhoLeftUUID);
        playerWhoLeft.setHealth(0);
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        playerWhoLeft.teleport(spawnLoc);
        request.endGame(winner);
    }
    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!Duels.requests.containsKey(player.getUniqueId())) {
            return;
        }
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        if (!request.getIsInGame()) {
            return;
        }
        player.sendMessage(Chat.Color("&cYou cannot use commands while you are in duel."));
        event.setCancelled(true);
    }
    //handling death
    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        UUID deadUUID = event.getEntity().getUniqueId();
        if (!Duels.requests.containsKey(deadUUID)) {
            return;
        }
        DuelRequest request = Duels.requests.get(deadUUID);
        if (!request.getIsInGame()) {
            return;
        }
        UUID winnerUUID = request.getOpponent(deadUUID);
        request.endGame(winnerUUID);
    }
    //anti teleport
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!Duels.requests.containsKey(player.getUniqueId())) {
            return;
        }
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        if (!request.getIsInGame()) {
            return;
        }
        event.setCancelled(true);
    }

    //anti-bow
    @EventHandler(ignoreCancelled = true)
    public void arrowLaunch(ProjectileLaunchEvent event) {
        Entity projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getEntity().getShooter();
        if (!Duels.requests.containsKey(shooter.getUniqueId())) {
            return;
        }
        DuelRequest request = Duels.requests.get(shooter.getUniqueId());
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isBowAllowed()) {
            return;
        }
        shooter.sendMessage(Chat.Color("&cThe use of arrows is not allowed in this duel."));
        event.setCancelled(true);
    }

    //anti totem
    @EventHandler(ignoreCancelled = true)
    public void onTotemUse(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player playerWhoUsedTotem = (Player) event.getEntity();
        UUID playerWhoUsedTotemUUID = playerWhoUsedTotem.getUniqueId();
        if (!playerWhoUsedTotem.getInventory().contains(Material.TOTEM_OF_UNDYING)) {
            return;
        }
        if (!Duels.requests.containsKey(playerWhoUsedTotem.getUniqueId())) {
            return;
        }
        DuelRequest request = Duels.requests.get(playerWhoUsedTotem.getUniqueId());
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isTotemsAllowed()) {
            return;
        }
        UUID winner = request.getOpponent(playerWhoUsedTotemUUID);
        event.setCancelled(true);
        request.endGame(winner);
    }

    //TODO: other restrictions.
    //anti shields
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Inventory inv = player.getInventory();
        if (inv.getItem(event.getNewSlot()) == null) {
            return;
        }
        if (inv.getItem(event.getNewSlot()).getType() != Material.SHIELD) {
            return;
        }
        if (!Duels.requests.containsKey(player.getUniqueId())) {
            return;
        }
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isShieldsAllowed()) {
            return;
        }
        player.sendMessage(Chat.Color("&cThe use of shields is not allowed in this duel."));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        if (!(event.getMainHandItem().getType() == Material.SHIELD || event.getOffHandItem().getType() == Material.SHIELD)) {
            return;
        }
        Player player = event.getPlayer();
        if (!Duels.requests.containsKey(player.getUniqueId())) {
            return;
        }
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isShieldsAllowed()) {
            return;
        }
        player.sendMessage(Chat.Color("&cThe use of shields is not allowed in this duel."));
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShieldClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (event.getCurrentItem().getType() != Material.SHIELD) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!Duels.requests.containsKey(player.getUniqueId())) {
            return;
        }
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isShieldsAllowed()) {
            return;
        }
        player.sendMessage(Chat.Color("&cThe use of shields is not allowed in this duel."));
        event.setCancelled(true);
    }
//anti potion
    @EventHandler(ignoreCancelled = true)
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!Duels.requests.containsKey(playerUUID)) {
            return;
        }
        DuelRequest request = Duels.requests.get(playerUUID);
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isPotionsAllowed()) {
            return;
        }
        player.sendMessage(Chat.Color("&cUsage of potions is disabled in this duel."));
        event.setCancelled(true);
    }
    //anti gp
    @EventHandler(ignoreCancelled = true)
    public void onGoldenAppleEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!Duels.requests.containsKey(playerUUID)) {
            return;
        }
        DuelRequest request = Duels.requests.get(playerUUID);
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isGoldenAppleAllowed()) {
            return;
        }
        player.sendMessage(Chat.Color("&cUsage of golden apples is disabled in this duel."));
        event.setCancelled(true);
    }
    //anti enchanted gp
    @EventHandler(ignoreCancelled = true)
    public void onEnchantedGoldenAppleEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.ENCHANTED_GOLDEN_APPLE) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!Duels.requests.containsKey(playerUUID)) {
            return;
        }
        DuelRequest request = Duels.requests.get(playerUUID);
        if (!request.getIsInGame()) {
            return;
        }
        if (request.getDuelRestrictions().isNotchAllowed()) {
            return;
        }
        player.sendMessage(Chat.Color("&cUsage of enchanted golden apples is disabled in this duel."));
        event.setCancelled(true);
    }
}
