package net.multylands.duels.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.multylands.duels.gui.DuelInventoryHolder;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUI implements Listener {
    Duels plugin;

    public GUI(Duels plugin) {
        this.plugin = plugin;
    }

    public static List<UUID> acceptedPlayers = new ArrayList<>();

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getLocation() != null) {
            return;
        }
        if (!(inv.getHolder() instanceof DuelInventoryHolder)) {
            return;
        }
        if (acceptedPlayers.contains(event.getPlayer().getUniqueId())) {
            acceptedPlayers.remove(event.getPlayer().getUniqueId());
            return;
        }
        Player player = (Player) event.getPlayer();
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        request.removeStoreRequest();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getInventory();
        if (inv.getLocation() != null) {
            return;
        }
        if (!(inv.getHolder() instanceof DuelInventoryHolder)) {
            return;
        }
        if (item == null) {
            return;
        }
        event.setCancelled(true);
        Player player = Bukkit.getPlayer(event.getView().getPlayer().getUniqueId());
        Player target = Bukkit.getPlayer(Duels.SenderToTarget.get(player.getUniqueId()));
        boolean isBowEnabled = inv.getItem(0).getItemMeta().getDisplayName().contains("Enabled");
        boolean isTotemEnabled = inv.getItem(1).getItemMeta().getDisplayName().contains("Enabled");
        boolean isGPEnabled = inv.getItem(2).getItemMeta().getDisplayName().contains("Enabled");
        boolean isNotchEnabled = inv.getItem(3).getItemMeta().getDisplayName().contains("Enabled");
        boolean isPotionsEnabled = inv.getItem(4).getItemMeta().getDisplayName().contains("Enabled");
        boolean isShieldsEnabled = inv.getItem(5).getItemMeta().getDisplayName().contains("Enabled");
        ItemMeta meta = item.getItemMeta();
        switch (event.getSlot()) {
            case 0:
                isBowEnabled = !isBowEnabled;
                if (isBowEnabled) {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                    item.setItemMeta(meta);
                }
                break;
            case 1:
                isTotemEnabled = !isTotemEnabled;
                if (isTotemEnabled) {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                    item.setItemMeta(meta);
                }
                break;
            case 2:
                isGPEnabled = !isGPEnabled;
                if (isGPEnabled) {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                    item.setItemMeta(meta);
                }
                break;
            case 3:
                isNotchEnabled = !isNotchEnabled;
                if (isNotchEnabled) {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                    item.setItemMeta(meta);
                }
                break;
            case 4:
                isPotionsEnabled = !isPotionsEnabled;
                if (isPotionsEnabled) {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-potions.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-potions.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                    item.setItemMeta(meta);
                }
                break;
            case 5:
                isShieldsEnabled = !isShieldsEnabled;
                if (isShieldsEnabled) {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                    item.setItemMeta(meta);
                }
                break;
            case 26:
                DuelRestrictions restrictions = new DuelRestrictions(isBowEnabled, isNotchEnabled, isPotionsEnabled, isGPEnabled, isShieldsEnabled, isTotemEnabled, true);
                DuelRequest request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, plugin, player.getUniqueId());
                request.storeRequest();
                Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.request-sent").replace("%player%", target.getName()));
                Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.request-received").replace("%player%", player.getName()));
                Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.restrictions"));
                if (request.getEnabled() != null) {
                    Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.enabled-restrictions")+request.getEnabled());
                }
                if (request.getDisabled() != null) {
                    Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.disabled-restrictions") + request.getDisabled());
                }
                Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.click").replace("%player%", target.getName()));
                acceptedPlayers.add(player.getUniqueId());
                player.closeInventory();
                break;
            case 18:
                player.closeInventory();
                break;
        }
    }
}
