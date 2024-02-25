package net.multylands.duels.gui;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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
        ItemStack start = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.start.item")));
        ItemMeta startMeta = start.getItemMeta();
        startMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.start.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.start.lore")) {
            lore.add(Chat.Color(loreLine.replace("%player%", target.getName())));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.start.glowing")) {
            start.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            startMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
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
