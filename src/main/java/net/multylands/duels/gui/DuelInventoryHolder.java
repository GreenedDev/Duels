package net.multylands.duels.gui;

import net.multylands.duels.Duels;
import net.multylands.duels.utils.Chat;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DuelInventoryHolder implements InventoryHolder {

    private Inventory inventory;
    public List<String> lore = new ArrayList<>();
    Duels plugin;
    public DuelInventoryHolder(Duels plugin) {
        this.plugin = plugin;
    }

    public DuelInventoryHolder(Duels plugin, int size) {
        this.inventory = plugin.getServer().createInventory(this, size);
        ItemStack bow = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-bow.item")));
        ItemMeta bowMeta = bow.getItemMeta();
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-bow.glowing")) {
            bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        bowMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-bow.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-bow.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        bowMeta.setLore(lore);
        bow.setItemMeta(bowMeta);
        lore.clear();

        ItemStack totem = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-totem.item")));
        ItemMeta totemMeta = totem.getItemMeta();
        totemMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-totem.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-totem.glowing")) {
            totem.addEnchantment(Enchantment.LUCK, 1);
            totemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-totem.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        totemMeta.setLore(lore);
        totem.setItemMeta(totemMeta);
        lore.clear();

        ItemStack gp = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.item")));
        ItemMeta gpMeta = gp.getItemMeta();
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-golden-apple.glowing")) {
            gp.addEnchantment(Enchantment.LUCK, 1);
            gpMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        gpMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-golden-apple.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        gpMeta.setLore(lore);
        gp.setItemMeta(gpMeta);
        lore.clear();

        ItemStack notch = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.item")));
        ItemMeta notchMeta = notch.getItemMeta();
        notchMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-enchanted-golden-apple.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-enchanted-golden-apple.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        notchMeta.setLore(lore);
        notch.setItemMeta(notchMeta);
        lore.clear();

        ItemStack potions = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-potions.item")));
        ItemMeta potionsMeta = potions.getItemMeta();
        potionsMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-potions.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        potionsMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-potions.glowing")) {
            potions.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            potionsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-potions.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        potionsMeta.setLore(lore);
        potions.setItemMeta(potionsMeta);
        lore.clear();

        ItemStack shields = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-shields.item")));
        ItemMeta shieldsMeta = shields.getItemMeta();
        shieldsMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-shields.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-shields.glowing")) {
            shields.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            shieldsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        shieldsMeta.setLore(lore);
        shields.setItemMeta(shieldsMeta);
        lore.clear();

        ItemStack cancel = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.cancel.item")));
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.cancel.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.cancel.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.cancel.glowing")) {
            cancel.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            cancelMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        cancelMeta.setLore(lore);
        cancel.setItemMeta(cancelMeta);
        lore.clear();


        inventory.setItem(0, bow);
        inventory.setItem(1, totem);
        inventory.setItem(2, gp);
        inventory.setItem(3, notch);
        inventory.setItem(4, potions);
        inventory.setItem(5, shields);
        inventory.setItem(18, cancel);

    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}