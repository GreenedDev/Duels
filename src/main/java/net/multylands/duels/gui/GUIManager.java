package net.multylands.duels.gui;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    public Duels plugin;
    public GUIManager(Duels plugin) {
        this.plugin = plugin;
    }
    public List<String> lore = new ArrayList<>();
    public void openInventory(Player player, Player target) {
        player.closeInventory();


        DuelInventoryHolder inventoryHolder = new DuelInventoryHolder(plugin, plugin.duelInventorySize);
        Inventory inventory = inventoryHolder.getInventory();
        ItemStack start = new ItemStack(Material.LIME_DYE);
        ItemMeta startMeta = start.getItemMeta();
        startMeta.setDisplayName(Chat.Color("&aStart"));
        lore.add(Chat.Color("&7Start duel with &b"+target.getName()+"&7."));
        lore.add("");
        lore.add(Chat.Color("&eClick to start!"));
        startMeta.setLore(lore);
        start.setItemMeta(startMeta);
        lore.clear();

        inventory.setItem(26, start);

        player.openInventory(inventory);

        DuelRestrictions restrictions = new DuelRestrictions(true, true, true, true, true, true, false);
        DuelRequest request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, plugin, player.getUniqueId());
        DuelRequest secondRequest = new DuelRequest(target.getUniqueId(), player.getUniqueId(), restrictions, false, false, plugin, player.getUniqueId());
        secondRequest.storeRequest();
        request.storeRequest();
    }
}
