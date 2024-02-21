package net.multylands.duels.utils;

import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.object.DuelRestrictions;
import net.multylands.duels.Duels;
import org.bukkit.Bukkit;
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
    public static Inventory inventory;
    public void openInventory(Player player, Player target) {
        player.closeInventory();
        Inventory inv = Bukkit.createInventory(null, 27, Chat.Color("&8Customize Your Duel Restrictions"));

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

        ItemStack start = new ItemStack(Material.LIME_DYE);
        ItemMeta startMeta = cancel.getItemMeta();
        startMeta.setDisplayName(Chat.Color("&aStart"));
        lore.add(Chat.Color("&7Start duel with &b"+target.getName()+"&7."));
        lore.add("");
        lore.add(Chat.Color("&eClick to start!"));
        startMeta.setLore(lore);
        start.setItemMeta(startMeta);
        lore.clear();

        inv.setItem(0, bow);
        inv.setItem(1, totem);
        inv.setItem(2, gp);
        inv.setItem(3, notch);
        inv.setItem(4, potions);
        inv.setItem(5, shields);
        inv.setItem(26, start);
        inv.setItem(18, cancel);

        player.openInventory(inv);

        DuelRestrictions restrictions = new DuelRestrictions(true, true, true, true, true, true, false);
        DuelRequest request = new DuelRequest(player.getUniqueId(), target.getUniqueId(), restrictions, false, false, plugin, player.getUniqueId());
        DuelRequest secondRequest = new DuelRequest(target.getUniqueId(), player.getUniqueId(), restrictions, false, false, plugin, player.getUniqueId());
        secondRequest.storeRequest();
        request.storeRequest();
        inventory = inv;
    }
    public static Inventory getMenu() {
        return inventory;
    }
}
