package com.aurapvp.bunkers.menu.menu;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.pvpclass.Bard;
import com.aurapvp.bunkers.utils.ItemBuilder;
import com.aurapvp.bunkers.utils.menu.Button;
import com.aurapvp.bunkers.utils.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 11/05/2021 / 8:51 PM
 * Bunkers / me.redis.bunkers.menu.menu
 */
public class ClassShopMenu extends Menu {
	@Override
	public String getTitle(Player player) {
		return "Class Shop";
	}

	@Override
	public int size() {
		return 27;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;

		for (Material idk : new Bard(Bunkers.getPlugin()).getBardEffects().keySet()) {
			buttons.put(i, new Button() {
				@Override
				public ItemStack getItem(Player player) {
					return new ItemBuilder(idk).setLore("&7&m-------------------", "&71 x " + idk.name(), "&7&m-------------------", "&7Price: &a$25").create();
				}

				@Override
				public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
					Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

					int balance = profile.getBalance();
					int cost = 25;
					int amount = 1;

					if (cost > balance) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
					} else {
						player.getInventory().addItem(new ItemStack(idk, amount));
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + getItem(player).getType().name() + " &afor &e$" + cost + "&a."));
						profile.setBalance(profile.getBalance() - cost);
					}
				}
			});
			++i;

		}
		buttons.put(i + 1, new Button() {
			@Override
			public ItemStack getItem(Player player) {
				return new ItemBuilder(Material.GOLD_SWORD).setLore("&7&m-------------------", "&71 x Gold Sword", "&7&m-------------------", "&7Price: &a$50").create();
			}

			@Override
			public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
				Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

				int balance = profile.getBalance();
				int cost = 50;
				int amount = 1;

				if (cost > balance) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
				} else {
					player.getInventory().addItem(new ItemStack(Material.GOLD_SWORD, amount));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + getItem(player).getType().name() + " &afor &e$" + cost + "&a."));
					profile.setBalance(profile.getBalance() - cost);
				}
			}
		});


		return buttons;
	}

	@Override
	public boolean usePlaceholder() {
		return true;
	}
}
