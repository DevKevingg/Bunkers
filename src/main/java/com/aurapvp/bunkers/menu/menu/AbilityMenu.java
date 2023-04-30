package com.aurapvp.bunkers.menu.menu;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.abilities.Ability;
import com.aurapvp.bunkers.abilities.items.pocketbards.*;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.menu.Button;
import com.aurapvp.bunkers.utils.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 10/05/2021 / 4:42 PM
 * Bunkers / me.redis.bunkers.menu.menu
 */
public class AbilityMenu extends Menu {
	@Override
	public String getTitle(Player player) {
		return "Ability Purchase";
	}

	@Override
	public int size() {
		return 27;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Map.Entry<String, Ability> current : Bunkers.getPlugin().getAbilityHandler().getAbility().entrySet()) {
			if (current.getValue() instanceof StrengthPocketbard || current.getValue() instanceof JumpPocketbard || current.getValue() instanceof RegenPocketbard || current.getValue() instanceof ResPocketbard || current.getValue() instanceof WitherPocketbard)
				continue;
			buttons.put(i, new Button() {
				@Override
				public ItemStack getItem(Player player) {

					ItemStack stack = current.getValue().item();
					ItemMeta meta = stack.getItemMeta();
					ArrayList<String> newLore = new ArrayList<>();
					newLore.addAll(meta.getLore());

					newLore.add(" ");
					newLore.add("&7Price: &a" + current.getValue().cost());

					meta.setLore(CC.list(newLore));

					stack.setItemMeta(meta);

					return stack;
				}

				@Override
				public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
					Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

					int balance = profile.getBalance();
					int cost = current.getValue().cost();
					int amount = 1;

					if (cost > balance) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
					} else {
						player.getInventory().addItem(current.getValue().item());
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + getItem(player).getItemMeta().getDisplayName() + " &afor &e$" + cost + "&a."));
						profile.setBalance(profile.getBalance() - cost);
					}
				}
			});
			++i;
		}

		return buttons;
	}
}
