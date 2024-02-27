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
            bowMeta.addEnchant(Enchantment.LURE, 1,  true);
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
            totemMeta.addEnchant(Enchantment.LURE, 1,  true);
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
            gpMeta.addEnchant(Enchantment.LURE, 1,  true);
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
            potionsMeta.addEnchant(Enchantment.LURE, 1,  true);
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
            shieldsMeta.addEnchant(Enchantment.LURE, 1,  true);
            shieldsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        shieldsMeta.setLore(lore);
        shields.setItemMeta(shieldsMeta);
        lore.clear();

        ItemStack elytra = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-elytra.item")));
        ItemMeta elytraMeta = elytra.getItemMeta();
        elytraMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-elytra.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-elytra.glowing")) {
            elytraMeta.addEnchant(Enchantment.LURE, 1,  true);
            elytraMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        elytraMeta.setLore(lore);
        elytra.setItemMeta(elytraMeta);
        lore.clear();

        ItemStack enderPearl = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.item")));
        ItemMeta enderPearlMeta = enderPearl.getItemMeta();
        enderPearlMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-ender-pearl.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-ender-pearl.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-ender-pearl.glowing")) {
            enderPearlMeta.addEnchant(Enchantment.LURE, 1,  true);
            enderPearlMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        enderPearlMeta.setLore(lore);
        enderPearl.setItemMeta(enderPearlMeta);
        lore.clear();


        ItemStack cancel = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.cancel.item")));
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.cancel.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.cancel.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.cancel.glowing")) {
            cancelMeta.addEnchant(Enchantment.LURE, 1,  true);
            cancelMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        cancelMeta.setLore(lore);
        cancel.setItemMeta(cancelMeta);
        lore.clear();
        int bowSlot = plugin.languageConfig.getInt("duel-GUI.toggle-bow.slot");
        int totemSlot = plugin.languageConfig.getInt("duel-GUI.toggle-totem.slot");
        int GPSlot = plugin.languageConfig.getInt("duel-GUI.toggle-golden-apple.slot");
        int NotchSlot = plugin.languageConfig.getInt("duel-GUI.toggle-enchanted-golden-apple.slot");
        int potionsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-potions.slot");
        int shieldsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-shields.slot");
        int elytraSlot = plugin.languageConfig.getInt("duel-GUI.toggle-elytra.slot");
        int enderpearlSlot = plugin.languageConfig.getInt("duel-GUI.toggle-ender-pearl.slot");
        int cancelSlot = plugin.languageConfig.getInt("duel-GUI.cancel.slot");
        inventory.setItem(bowSlot, bow);
        inventory.setItem(totemSlot, totem);
        inventory.setItem(GPSlot, gp);
        inventory.setItem(NotchSlot, notch);
        inventory.setItem(potionsSlot, potions);
        inventory.setItem(shieldsSlot, shields);
        inventory.setItem(elytraSlot, elytra);
        inventory.setItem(enderpearlSlot, enderPearl);
        inventory.setItem(cancelSlot, cancel);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}