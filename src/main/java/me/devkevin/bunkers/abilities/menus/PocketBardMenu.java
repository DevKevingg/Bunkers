package me.devkevin.bunkers.abilities.menus;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.utils.CC;
import me.devkevin.bunkers.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PocketBardMenu implements Listener {


	@EventHandler
	public void onInventoryPocketBard(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		String title = Bunkers.getPlugin().getInventoriesYML().gc().getString("POCKETBARD.SETTINGS.TITLE");
		if (event.getInventory().getTitle().equalsIgnoreCase(CC.chat(title))) {
			for (String section : Bunkers.getPlugin().getInventoriesYML().gc().getConfigurationSection("POCKETBARD.ITEMS").getKeys(false)) {

				int data = Bunkers.getPlugin().getInventoriesYML().gc().getInt("POCKETBARD.ITEMS." + section + ".Data");
				String material = Bunkers.getPlugin().getInventoriesYML().gc().getString("POCKETBARD.ITEMS." + section + ".Material");
				String name = Bunkers.getPlugin().getInventoriesYML().gc().getString("POCKETBARD.ITEMS." + section + ".Name");
				List<String> lore = Bunkers.getPlugin().getInventoriesYML().gc().getStringList("POCKETBARD.ITEMS." + section + ".Lore");
				String cmd = Bunkers.getPlugin().getInventoriesYML().gc().getString("POCKETBARD.ITEMS." + section + ".Command");
				ItemStack builtItem = new ItemBuilder(Material.valueOf(material)).setDisplayName(CC.chat(name)).setData((short) data).setLore(CC.list(lore)).create();

				if (event.getCurrentItem() == null)
					return;

				if (builtItem.isSimilar(event.getCurrentItem())) {
					try {
						if (player.getItemInHand().isSimilar(Bunkers.getPlugin().getAbilityHandler().byName("pocketbard").item())) {
							takeItem(player, player.getItemInHand(), true);

							if (event.getCurrentItem().getType() == Material.BLAZE_POWDER) {
								player.getInventory().addItem(Bunkers.getPlugin().getAbilityHandler().byName("strengthpocketbard").item());
							} else if (event.getCurrentItem().getType() == Material.IRON_INGOT) {
								player.getInventory().addItem(Bunkers.getPlugin().getAbilityHandler().byName("respocketbard").item());
							} else if (event.getCurrentItem().getType() == Material.SPIDER_EYE) {
								player.getInventory().addItem(Bunkers.getPlugin().getAbilityHandler().byName("witherpocketbard").item());
							} else if (event.getCurrentItem().getType() == Material.FEATHER) {
								player.getInventory().addItem(Bunkers.getPlugin().getAbilityHandler().byName("jumppocketbard").item());
							} else if (event.getCurrentItem().getType() == Material.GHAST_TEAR) {
								player.getInventory().addItem(Bunkers.getPlugin().getAbilityHandler().byName("regenpocketbard").item());
							}

							player.closeInventory();
							event.setCancelled(true);
						} else {
							player.closeInventory();
							event.setCancelled(true);
							player.sendMessage(CC.chat("&cYou cannot claim a pocketbard when you don't have it in your hand!"));
							return;
						}
					} catch (Exception ignored) {
						player.closeInventory();
						player.sendMessage(CC.chat("&cYou cannot claim a pocketbard when you don't have it in your hand!"));
						return;
					}
				} else {
					event.setCancelled(true);
				}

			}
		}
	}
	public void takeItem(Player player, ItemStack item, boolean shouldTake) {
		if (shouldTake) {
			if (player.getItemInHand().getAmount() > 1) {
				player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
			} else {
				player.setItemInHand(null);
			}
		}
	}
	public static void openPocketBardGUI(Player player) {

		int size = Bunkers.getPlugin().getInventoriesYML().gc().getInt("POCKETBARD.SETTINGS.SIZE");
		String title = Bunkers.getPlugin().getInventoriesYML().gc().getString("POCKETBARD.SETTINGS.TITLE");

		Inventory inv = Bukkit.createInventory(null, size, CC.chat(title));


		for (String section : Bunkers.getPlugin().getInventoriesYML().gc().getConfigurationSection("POCKETBARD.ITEMS").getKeys(false)) {

			int slot = Bunkers.getPlugin().getInventoriesYML().gc().getInt("POCKETBARD.ITEMS." + section + ".Slot");
			int data = Bunkers.getPlugin().getInventoriesYML().gc().getInt("POCKETBARD.ITEMS." + section + ".Data");
			String material = Bunkers.getPlugin().getInventoriesYML().gc().getString("POCKETBARD.ITEMS." + section + ".Material");
			String name = Bunkers.getPlugin().getInventoriesYML().gc().getString("POCKETBARD.ITEMS." + section + ".Name");
			List<String> lore = Bunkers.getPlugin().getInventoriesYML().gc().getStringList("POCKETBARD.ITEMS." + section + ".Lore");

			ItemStack builtItem = new ItemBuilder(Material.valueOf(material)).setDisplayName(CC.chat(name)).setData((short) data).setLore(CC.list(lore)).create();

			inv.setItem(slot - 1, builtItem);

		}

		player.openInventory(inv);

	}
}
