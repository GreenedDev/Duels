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
                    meta.setDisplayName(Chat.Color("&aToggle Bow &a(Enabled)"));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color("&aToggle Bow &c(Disabled)"));
                    item.setItemMeta(meta);
                }
                break;
            case 1:
                isTotemEnabled = !isTotemEnabled;
                if (isTotemEnabled) {
                    meta.setDisplayName(Chat.Color("&aToggle Totem &a(Enabled)"));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color("&aToggle Totem &c(Disabled)"));
                    item.setItemMeta(meta);
                }
                break;
            case 2:
                isGPEnabled = !isGPEnabled;
                if (isGPEnabled) {
                    meta.setDisplayName(Chat.Color("&aToggle Golden Apple &a(Enabled)"));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color("&aToggle Golden Apple &c(Disabled)"));
                    item.setItemMeta(meta);
                }
                break;
            case 3:
                isNotchEnabled = !isNotchEnabled;
                if (isNotchEnabled) {
                    meta.setDisplayName(Chat.Color("&6Toggle Enchanted Golden Apple &a(Enabled)"));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color("&6Toggle Enchanted Golden Apple &c(Disabled)"));
                    item.setItemMeta(meta);
                }
                break;
            case 4:
                isPotionsEnabled = !isPotionsEnabled;
                if (isPotionsEnabled) {
                    meta.setDisplayName(Chat.Color("&bToggle Potions Usage &a(Enabled)"));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color("&bToggle Potions Usage &c(Disabled)"));
                    item.setItemMeta(meta);
                }
                break;
            case 5:
                isShieldsEnabled = !isShieldsEnabled;
                if (isShieldsEnabled) {
                    meta.setDisplayName(Chat.Color("&bToggle Shields Usage &a(Enabled)"));
                    item.setItemMeta(meta);
                } else {
                    meta.setDisplayName(Chat.Color("&bToggle Shields Usage &c(Disabled)"));
                    item.setItemMeta(meta);
                }
                break;
            case 26:
                DuelRestrictions restrictions = new DuelRestrictions(isBowEnabled, isNotchEnabled, isPotionsEnabled, isGPEnabled, isShieldsEnabled, isTotemEnabled, true);
                DuelRequest request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, plugin, player.getUniqueId());
                request.storeRequest();
                player.sendMessage(Chat.Color("&aYou have sent duel request to &b" + target.getName() + "&a!"));
                target.sendMessage(Chat.Color("&b" + player.getName() + " &ais sending you a duel request."));
                target.sendMessage(Chat.Color("&6Restrictions:"));
                if (request.getEnabled() != null) {
                    target.sendMessage(Chat.Color(" &aEnabled: &b"+request.getEnabled()));
                }
                if (request.getDisabled() != null) {
                    target.sendMessage(Chat.Color(" &aDisabled: &b" + request.getDisabled()));
                }
                TextComponent confirm = new TextComponent("ACCEPT");
                confirm.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                confirm.setBold(true);
                confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptduel " + player.getName()));
                confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("").append("Click to accept this request!")
                                .color(ChatColor.GREEN).create()));

                TextComponent deny = new TextComponent("DENY");
                deny.setColor(net.md_5.bungee.api.ChatColor.RED);
                deny.setBold(true);
                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/denyduel " + player.getName()));
                deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("").append("Click to deny this request!")
                                .color(ChatColor.RED).create()));

                ComponentBuilder builder = new ComponentBuilder("");
                builder.append("Click").color(ChatColor.GREEN);
                builder.append(" ");
                builder.append(deny);
                builder.append("/").color(ChatColor.WHITE).bold(false);
                builder.append(confirm);
                BaseComponent[] finalMessage = builder.create();

                target.spigot().sendMessage(finalMessage);
                acceptedPlayers.add(player.getUniqueId());
                player.closeInventory();
                break;
            case 18:
                player.closeInventory();
                break;
        }
    }
}
