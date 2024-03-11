package net.multylands.duels.gui;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.utils.Chat;
import org.bukkit.ChatColor;
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

    int bowSlot;
    int totemSlot;
    int GPSlot;
    int NotchSlot;
    int potionsSlot;
    int elytraSlot;
    int enderPearlSlot;
    int keepInventorySlot;
    int cancelSlot;
    private Inventory inventory;
    public List<String> lore = new ArrayList<>();
    public DuelRequest request;
    Duels plugin;

    public DuelInventoryHolder(Duels plugin, int size, DuelRequest request) {
        this.plugin = plugin;
        bowSlot = plugin.languageConfig.getInt("duel-GUI.toggle-bow.slot");
        totemSlot = plugin.languageConfig.getInt("duel-GUI.toggle-totem.slot");
        GPSlot = plugin.languageConfig.getInt("duel-GUI.toggle-golden-apple.slot");
        NotchSlot = plugin.languageConfig.getInt("duel-GUI.toggle-enchanted-golden-apple.slot");
        potionsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-potions.slot");
        elytraSlot = plugin.languageConfig.getInt("duel-GUI.toggle-elytra.slot");
        enderPearlSlot = plugin.languageConfig.getInt("duel-GUI.toggle-ender-pearl.slot");
        keepInventorySlot = plugin.languageConfig.getInt("duel-GUI.toggle-keep-inventory.slot");
        cancelSlot = plugin.languageConfig.getInt("duel-GUI.cancel.slot");
        this.request = request;
        this.inventory = plugin.getServer().createInventory(this, size, Chat.Color(plugin.languageConfig.getString("duel-GUI.title")));
        ItemStack bow = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-bow.item")));
        ItemMeta bowMeta = bow.getItemMeta();
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-bow.glowing")) {
            bowMeta.addEnchant(Enchantment.LURE, 1, true);
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
            totemMeta.addEnchant(Enchantment.LURE, 1, true);
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
            gpMeta.addEnchant(Enchantment.LURE, 1, true);
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
            potionsMeta.addEnchant(Enchantment.LURE, 1, true);
            potionsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-potions.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        potionsMeta.setLore(lore);
        potions.setItemMeta(potionsMeta);
        lore.clear();
        if (plugin.isServerPaper) {
            ItemStack shields = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-shields.item")));
            ItemMeta shieldsMeta = shields.getItemMeta();
            shieldsMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-shields.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
            for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-shields.lore")) {
                lore.add(Chat.Color(loreLine));
            }
            if (plugin.languageConfig.getBoolean("duel-GUI.toggle-shields.glowing")) {
                shieldsMeta.addEnchant(Enchantment.LURE, 1, true);
                shieldsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            shieldsMeta.setLore(lore);
            shields.setItemMeta(shieldsMeta);
            lore.clear();
            int shieldsSlot = plugin.languageConfig.getInt("duel-GUI.toggle-shields.slot");
            inventory.setItem(shieldsSlot, shields);
        }

        ItemStack elytra = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-elytra.item")));
        ItemMeta elytraMeta = elytra.getItemMeta();
        elytraMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-elytra.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-enabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-elytra.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-elytra.glowing")) {
            elytraMeta.addEnchant(Enchantment.LURE, 1, true);
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
            enderPearlMeta.addEnchant(Enchantment.LURE, 1, true);
            enderPearlMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        enderPearlMeta.setLore(lore);
        enderPearl.setItemMeta(enderPearlMeta);
        lore.clear();

        ItemStack keepInventory = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.item")));
        ItemMeta keepInventoryMeta = keepInventory.getItemMeta();
        keepInventoryMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.toggle-keep-inventory.display-name").replace("%toggled%", plugin.languageConfig.getString("duel-GUI.restriction-disabled"))));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.toggle-keep-inventory.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.toggle-keep-inventory.glowing")) {
            keepInventoryMeta.addEnchant(Enchantment.LURE, 1, true);
            keepInventoryMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        keepInventoryMeta.setLore(lore);
        keepInventory.setItemMeta(keepInventoryMeta);
        lore.clear();


        ItemStack cancel = new ItemStack(Material.getMaterial(plugin.languageConfig.getString("duel-GUI.cancel.item")));
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(Chat.Color(plugin.languageConfig.getString("duel-GUI.cancel.display-name")));
        for (String loreLine : plugin.languageConfig.getStringList("duel-GUI.cancel.lore")) {
            lore.add(Chat.Color(loreLine));
        }
        if (plugin.languageConfig.getBoolean("duel-GUI.cancel.glowing")) {
            cancelMeta.addEnchant(Enchantment.LURE, 1, true);
            cancelMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        cancelMeta.setLore(lore);
        cancel.setItemMeta(cancelMeta);
        lore.clear();
        inventory.setItem(bowSlot, bow);
        inventory.setItem(totemSlot, totem);
        inventory.setItem(GPSlot, gp);
        inventory.setItem(NotchSlot, notch);
        inventory.setItem(potionsSlot, potions);
        inventory.setItem(elytraSlot, elytra);
        inventory.setItem(enderPearlSlot, enderPearl);
        inventory.setItem(keepInventorySlot, keepInventory);
        inventory.setItem(cancelSlot, cancel);
    }

    public void setRequest(DuelRequest request) {
        this.request = request;
    }

    public DuelRequest getRequest() {
        return this.request;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}