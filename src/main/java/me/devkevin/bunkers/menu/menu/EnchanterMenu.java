package me.devkevin.bunkers.menu.menu;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.menu.type.ChestMenu;
import me.devkevin.bunkers.profiles.Profile;
import me.devkevin.bunkers.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchanterMenu extends ChestMenu<Bunkers> {
    private Player player;

    public EnchanterMenu(Player player) {
        super(9);
        this.player = player;

        update();
    }

    public void update() {
        inventory.clear();

        inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Sharpness I").setLore("&7&m-------------------", "&7Sharpness I for your sword", "&7&m-------------------", "&ePrice: &a$300").create());
        inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Protection I").setLore("&7&m-------------------", "&7Protection I for your armor", "&7&m-------------------", "&ePrice: &a$750").create());
        inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Feather Falling IV").setLore("&7&m-------------------", "&7Feather Falling IV for your boots", "&7&m-------------------", "&ePrice: &a$200").create());
        inventory.setItem(4, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Unbreaking III").setLore("&7&m-------------------", "&7Unbreaking III for your armor", "&7&m-------------------", "&ePrice: &a$350").create());

        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i) != null) continue;

            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayName(" ").create());
        }
        inventory.setItem(6, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Infinity I").setLore("&7&m-------------------", "&7Infinity I for your bow", "&7&m-------------------", "&ePrice: &a$350").create());
        inventory.setItem(7, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Power III").setLore("&7&m-------------------", "&7Power III for your bow", "&7&m-------------------", "&ePrice: &a$350").create());
        inventory.setItem(8, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Efficiency III").setLore("&7&m-------------------", "&7Efficiency III for your pickaxe", "&7&m-------------------", "&ePrice: &a$200").create());

    }

    @Override
    public String getTitle() {
        return ChatColor.RED + ChatColor.BOLD.toString() + "Enchant Shop";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (clickedInventory == null || topInventory == null || !topInventory.equals(inventory)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() != Material.AIR) {
                if (event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getType() != Material.STAINED_GLASS_PANE) {
                    int balance = profile.getBalance();
                    int cost = Integer.parseInt(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(3).split(" ")[1].replace("$", "")));

                    if (cost > balance) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                    } else {
                        profile.setBalance(profile.getBalance() - cost);
                        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Sharpness")) {
                            int counter = 0;

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && itemStack.getType() == Material.DIAMOND_SWORD && counter == 0) {
                                    itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);

                                    counter++;
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Feather")) {
                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && (itemStack.getType().name().contains("BOOTS")  || itemStack.getType().name().contains("HELMET") || itemStack.getType().name().contains("CHESTPLATE") || itemStack.getType().name().contains("LEGGINGS") )) {
                                    itemStack.addEnchantment(Enchantment.PROTECTION_FALL, 4);
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()){
                                        if (armor != null && (armor.getType().name().contains("BOOTS")  || armor.getType().name().contains("HELMET") || armor.getType().name().contains("CHESTPLATE") || armor.getType().name().contains("LEGGINGS") )) {
                                            armor.addEnchantment(Enchantment.PROTECTION_FALL, 4);
                                        }
                                    }
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Protection")) {

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && (itemStack.getType().name().contains("BOOTS")  || itemStack.getType().name().contains("HELMET") || itemStack.getType().name().contains("CHESTPLATE") || itemStack.getType().name().contains("LEGGINGS") )) {
                                    itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()){
                                        if (armor != null && (armor.getType().name().contains("BOOTS")  || armor.getType().name().contains("HELMET") || armor.getType().name().contains("CHESTPLATE") || armor.getType().name().contains("LEGGINGS") )) {
                                            armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                        }
                                    }
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Unbreaking")) {

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && (itemStack.getType().name().contains("BOOTS")  || itemStack.getType().name().contains("HELMET") || itemStack.getType().name().contains("CHESTPLATE") || itemStack.getType().name().contains("LEGGINGS") )) {
                                    itemStack.addEnchantment(Enchantment.DURABILITY, 3);
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()){
                                        if (armor != null && (armor.getType().name().contains("BOOTS")  || armor.getType().name().contains("HELMET") || armor.getType().name().contains("CHESTPLATE") || armor.getType().name().contains("LEGGINGS") )) {
                                            armor.addEnchantment(Enchantment.DURABILITY, 3);
                                        }
                                    }
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Efficiency")) {

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && (itemStack.getType() == Material.DIAMOND_PICKAXE)) {
                                    itemStack.addEnchantment(Enchantment.DIG_SPEED, 3);
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()){
                                        if (armor != null && (armor.getType() == Material.DIAMOND_PICKAXE)) {
                                            armor.addEnchantment(Enchantment.DIG_SPEED, 3);
                                        }
                                    }
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Infinity")) {

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && (itemStack.getType() == Material.BOW)) {
                                    itemStack.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()){
                                        if (armor != null && (armor.getType() == Material.BOW)) {
                                            armor.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                                        }
                                    }
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Power")) {

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && (itemStack.getType() == Material.BOW)) {
                                    itemStack.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()){
                                        if (armor != null && (armor.getType() == Material.BOW)) {
                                            armor.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (!topInventory.equals(clickedInventory) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.equals(getInventory())) {
            event.setCancelled(true);
        }
    }

    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */){
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
        }
        return sb.toString();
    }
}