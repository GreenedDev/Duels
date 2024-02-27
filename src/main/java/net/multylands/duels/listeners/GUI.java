package net.multylands.duels.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.multylands.duels.gui.DuelInventoryHolder;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
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

    public static List<UUID> PlayersWhoSentRequest = new ArrayList<>();

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getLocation() != null) {
            return;
        }
        if (!(inv.getHolder() instanceof DuelInventoryHolder)) {
            return;
        }
        if (PlayersWhoSentRequest.contains(event.getPlayer().getUniqueId())) {
            PlayersWhoSentRequest.remove(event.getPlayer().getUniqueId());
            return;
        }
        Player player = (Player) event.getPlayer();
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        request.removeStoreRequest(false);
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
        //always!!! get this request from the GUI clicker. or if someone does /duel onthesameplayer then this will break
        DuelRequest request = Duels.requests.get(player.getUniqueId());
        Player target = Bukkit.getPlayer(request.getOpponent(player.getUniqueId()));

        DuelRestrictions restrictions = request.getDuelRestrictions();
        boolean isBowEnabled = restrictions.isBowAllowed();
        boolean isTotemEnabled = restrictions.isTotemsAllowed();
        boolean isGPEnabled = restrictions.isGoldenAppleAllowed();
        boolean isNotchEnabled = restrictions.isNotchAllowed();
        boolean isPotionsEnabled = restrictions.isPotionsAllowed();
        boolean isShieldsEnabled = restrictions.isShieldsAllowed();
        boolean isElytraEnabled = restrictions.isElytraAllowed();
        boolean isEnderPearlEnabled = restrictions.isEnderPearlAllowed();
        int bowSlot = plugin.languageConfig.getInt("duel-GUI.toggle-bow.slot");
        int totemSlot = plugin.languageConfig.getInt("duel-GUI.toggle-totem.slot");
        int GPSlot = plugin.languageConfig.getInt("duel-GUI.toggle-golden-apple.slot");
        int NotchSlot = plugin.languageConfig.getInt("duel-GUI.toggle-enchanted-golden-apple.slot");
        int potionsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-potions.slot");
        int shieldsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-shields.slot");
        int elytraSlot = plugin.languageConfig.getInt("duel-GUI.toggle-elytra.slot");
        int enderpearlSlot = plugin.languageConfig.getInt("duel-GUI.toggle-ender-pearl.slot");
        int startSlot = plugin.languageConfig.getInt("duel-GUI.start.slot");
        int cancelSlot = plugin.languageConfig.getInt("duel-GUI.cancel.slot");
        ItemMeta meta = item.getItemMeta();
        int slot = event.getSlot();
        //not using switch because it does not support using not very reliable int getter from the config
        if (slot == bowSlot) {
            isBowEnabled = !isBowEnabled;
            restrictions.setBowAllowed(isBowEnabled);
            saveRestriction(request, restrictions);
            if (isBowEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == totemSlot) {
            isTotemEnabled = !isTotemEnabled;
            restrictions.setTotemsAllowed(isTotemEnabled);
            saveRestriction(request, restrictions);
            if (isTotemEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == GPSlot) {
            isGPEnabled = !isGPEnabled;
            restrictions.setGoldenAppleAllowed(isGPEnabled);
            saveRestriction(request, restrictions);
            if (isGPEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == NotchSlot) {
            isNotchEnabled = !isNotchEnabled;
            restrictions.setNotchAllowed(isNotchEnabled);
            saveRestriction(request, restrictions);
            if (isNotchEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == potionsSlot) {
            isPotionsEnabled = !isPotionsEnabled;
            restrictions.setPotionsAllowed(isPotionsEnabled);
            saveRestriction(request, restrictions);
            if (isPotionsEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-potions.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-potions.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == shieldsSlot) {
            isShieldsEnabled = !isShieldsEnabled;
            restrictions.setShieldsAllowed(isShieldsEnabled);
            saveRestriction(request, restrictions);
            if (isShieldsEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == elytraSlot) {
            isElytraEnabled = !isElytraEnabled;
            restrictions.setElytraAllowed(isElytraEnabled);
            saveRestriction(request, restrictions);
            if (isElytraEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == enderpearlSlot) {
            isEnderPearlEnabled = !isEnderPearlEnabled;
            restrictions.setEnderPearlAllowed(isEnderPearlEnabled);
            saveRestriction(request, restrictions);
            if (isEnderPearlEnabled) {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
                item.setItemMeta(meta);
            } else {
                meta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
                item.setItemMeta(meta);
            }
        }
        if (slot == startSlot) {
            restrictions = new DuelRestrictions(isBowEnabled, isNotchEnabled, isPotionsEnabled, isGPEnabled, isShieldsEnabled, isTotemEnabled, isElytraEnabled, isEnderPearlEnabled, true);
            //dont change position of player and target below
            request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, plugin);


            request.storeRequest(false);
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.request-sent").replace("%player%", target.getName()));
            Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.request-received").replace("%player%", player.getName()));
            Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.restrictions"));
            if (request.getEnabled() != null) {
                Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.enabled-restrictions")+request.getEnabled());
            }
            if (request.getDisabled() != null) {
                Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.disabled-restrictions") + request.getDisabled());
            }
            Chat.sendMessage(plugin, target, plugin.languageConfig.getString("duel.click").replace("%player%", player.getName()));
            PlayersWhoSentRequest.add(player.getUniqueId());
            player.closeInventory();
        }
        if (slot == cancelSlot) {
            player.closeInventory();
            request.removeStoreRequest(false);
            Chat.sendMessage(plugin, player, plugin.languageConfig.getString("duel.request-cancelled"));
        }
    }
    public void saveRestriction(DuelRequest request, DuelRestrictions restrictions) {
        request.setDuelRestrictions(restrictions);
        request.storeRequest(false);
    }
}
