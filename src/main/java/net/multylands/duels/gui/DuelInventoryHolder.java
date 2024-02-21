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

    private final Inventory inventory;
    public List<String> lore = new ArrayList<>();

    public DuelInventoryHolder(Duels plugin) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.inventory = plugin.getServer().createInventory(this, 9);
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
        bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bowMeta.setDisplayName(Chat.Color("&aToggle Bow &a(Enabled)"));
        lore.add(Chat.Color("&7Toggle bow usage on the duel. by"));
        lore.add(Chat.Color("&7disabling this bot sides will be"));
        lore.add(Chat.Color("&7unable to use items that shot arrows."));
        lore.add(Chat.Color(""));
        lore.add(Chat.Color("&eClick to toggle!"));
        bowMeta.setLore(lore);
        bow.setItemMeta(bowMeta);
        lore.clear();

        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta totemMeta = totem.getItemMeta();
        totemMeta.setDisplayName(Chat.Color("&aToggle Totem &a(Enabled)"));
        lore.add(Chat.Color("&7Toggle totem usage in the duel."));
        lore.add(Chat.Color("&7Disabling this will prevent both"));
        lore.add(Chat.Color("&7players from be unable to use totem."));
        lore.add(Chat.Color(""));
        lore.add(Chat.Color("&eClick to toggle!"));
        totemMeta.setLore(lore);
        totem.setItemMeta(totemMeta);
        lore.clear();

        ItemStack gp = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta gpMeta = gp.getItemMeta();
        gpMeta.setDisplayName(Chat.Color("&aToggle Golden Apple &a(Enabled)"));
        lore.add(Chat.Color("&7Toggle golden apple usage in duel."));
        lore.add(Chat.Color("&7Disabling this will prevent both players"));
        lore.add(Chat.Color("&7from using the golden apple."));
        lore.add("");
        lore.add(Chat.Color("&eClick to toggle!"));
        gpMeta.setLore(lore);
        gp.setItemMeta(gpMeta);
        lore.clear();

        ItemStack notch = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta notchMeta = notch.getItemMeta();
        notchMeta.setDisplayName(Chat.Color("&6Toggle Enchanted Golden Apple &a(Enabled)"));
        lore.add(Chat.Color("&7Toggle enchanted golden apple usage in duel."));
        lore.add(Chat.Color("&7Disabling this will prevent both players"));
        lore.add(Chat.Color("&7from using the enchanted golden apple."));
        lore.add("");
        lore.add(Chat.Color("&eClick to toggle!"));
        notchMeta.setLore(lore);
        notch.setItemMeta(notchMeta);
        lore.clear();

        ItemStack potions = new ItemStack(Material.POTION);
        ItemMeta potionsMeta = potions.getItemMeta();
        potionsMeta.setDisplayName(Chat.Color("&bToggle Potions Usage &a(Enabled)"));
        potionsMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        lore.add(Chat.Color("&7Toggle Potions usage in duel."));
        lore.add(Chat.Color("&7Disabling this will prevent both players"));
        lore.add(Chat.Color("&7from using the Potions."));
        lore.add("");
        lore.add(Chat.Color("&eClick to toggle!"));
        potionsMeta.setLore(lore);
        potions.setItemMeta(potionsMeta);
        lore.clear();

        ItemStack shields = new ItemStack(Material.SHIELD);
        ItemMeta shieldsMeta = shields.getItemMeta();
        shieldsMeta.setDisplayName(Chat.Color("&bToggle Shields Usage &a(Enabled)"));
        lore.add(Chat.Color("&7Toggle Shields usage in duel."));
        lore.add(Chat.Color("&7Disabling this will prevent both players"));
        lore.add(Chat.Color("&7from using the Shields."));
        lore.add("");
        lore.add(Chat.Color("&eClick to toggle!"));
        shieldsMeta.setLore(lore);
        shields.setItemMeta(shieldsMeta);
        lore.clear();

        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(Chat.Color("&cCancel"));
        lore.add(Chat.Color("&7Cancel duel request."));
        lore.add("");
        lore.add(Chat.Color("&eClick to cancel!"));
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