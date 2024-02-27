package net.multylands.duels.listeners;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
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
        UUID playerUUID = player.getUniqueId();
        
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player);
        
        if (!RequestUtils.isInGame(request)) {
            return;
        }
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
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerWhoLeft);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        UUID winner = request.getOpponent(playerWhoLeftUUID);
        Location spawnLoc = plugin.getConfig().getLocation("spawn_location");
        playerWhoLeft.teleport(spawnLoc);
        request.endGame(winner);
        playerWhoLeft.setHealth(0);
    }

    //anti command
    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.no-commands-in-duel"));
        event.setCancelled(true);
    }

    //handling death
    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity().getPlayer();
        UUID deadUUID = dead.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(dead);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        UUID winnerUUID = request.getOpponent(deadUUID);
        request.endGame(winnerUUID);
    }

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
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(damagedPlayer);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getOpponent(damagedPlayer.getUniqueId()) == damager.getUniqueId()) {
            return;
        }
        Chat.sendMessage(plugin, damager, plugin.languageConfig.getString("duel.cannot-damage-in-duel"));
        event.setCancelled(true);
    }

    //anti teleport
    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player);
        if (!RequestUtils.isInGame(request)) {
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
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(shooter);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isBowAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, shooter, (plugin.languageConfig.getString("duel.arrows-deny-message")));
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
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerWhoUsedTotem);
        if (!RequestUtils.isInGame(request)) {
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
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isShieldsAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.shields-deny-message"));
        event.setCancelled(true);
    }

    //antishield
    @EventHandler(ignoreCancelled = true)
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        if (!(event.getMainHandItem().getType() == Material.SHIELD || event.getOffHandItem().getType() == Material.SHIELD)) {
            return;
        }
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isShieldsAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.shields-deny-message"));
        event.setCancelled(true);
    }

    //antielytra
    @EventHandler(ignoreCancelled = true)
    public void onGliding(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = ((Player) entity).getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isElytraAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.elytra-deny-message"));
        event.setCancelled(true);
    }
    //antipearl
    @EventHandler(ignoreCancelled = true)
    public void enderPearlLaunch(ProjectileLaunchEvent event) {
        Entity projectile = event.getEntity();
        if (!(projectile instanceof EnderPearl)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getEntity().getShooter();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(shooter);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isEnderPearlAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, shooter, (plugin.languageConfig.getString("duel.ender-pearl-deny-message")));
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
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isShieldsAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.shields-deny-message"));
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
        DuelRequest request = Duels.requests.get(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isPotionsAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.potions-deny-message"));
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
        DuelRequest request = Duels.requests.get(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isGoldenAppleAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.golden-apples-deny-message"));
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
        DuelRequest request = Duels.requests.get(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getDuelRestrictions().isNotchAllowed()) {
            return;
        }
        Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.enchanted-golden-apples-deny-message"));
        event.setCancelled(true);
    }
    
}
