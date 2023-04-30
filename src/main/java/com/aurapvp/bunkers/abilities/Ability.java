package com.aurapvp.bunkers.abilities;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.ItemBuilder;
import com.aurapvp.bunkers.utils.cooldown.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Ability implements Listener {

	public Ability() {
		Bukkit.getPluginManager().registerEvents(this, Bunkers.getPlugin());
	}

	public ItemStack item() {
		ItemStack stack = new ItemBuilder(material()).setDisplayName(CC.chat(displayName())).setData((short) data()).setLore(CC.list(getLore())).create();

		stack.addUnsafeEnchantment(
				Enchantment.getByName(Bunkers.getPlugin().getAbilityYML().gc().getString("Ability-Settings.Glowing.Enchant")),
				Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.Glowing.Level"));
		return stack;
	}

	public abstract Cooldowns getCooldown();
	public abstract List<String> getLore();
	public abstract String displayName();
	public abstract String scoreboard();
	public abstract String name();
	public abstract int data();
	public abstract Material material();
	public abstract boolean glowing();

	public boolean canInteractWithAbility(Player p) {

		return true;
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

	public Cooldowns checkCooldown(Player p) {
		if (Bunkers.getPlugin().getAbilityHandler().getPartnerCD().onCooldown(p))
			return Bunkers.getPlugin().getAbilityHandler().getPartnerCD();
		if (getCooldown().onCooldown(p))
			return getCooldown();
		return null;
	}

	public void sendMessage(Player player) {
		if (Bunkers.getPlugin().getAbilityHandler().getPartnerCD().onCooldown(player)) {
			for (String message : Bunkers.getPlugin().getAbilityYML().gc().getStringList("Messages.Cooldown-Message")) {
				String display = CC.chat(Bunkers.getPlugin().getAbilityYML().gc().getString("Ability-Settings.Partner.Display"));
				String remaining = Bunkers.getPlugin().getAbilityHandler().getPartnerCD().getRemaining(player);
				player.sendMessage(CC.chat(message)
						.replaceAll("%time%", remaining)
						.replaceAll("%ability%", display));
			}
			return;
		}
		if (getCooldown().onCooldown(player)) {
			for (String message : Bunkers.getPlugin().getAbilityYML().gc().getStringList("Messages.Cooldown-Message")) {
				String display = CC.chat(displayName());
				String remaining = getCooldown().getRemaining(player);
				player.sendMessage(CC.chat(message)
						.replaceAll("%time%", remaining)
						.replaceAll("%ability%", display));
			}
		}
	}

	public abstract int cost();

	public boolean checkClicks(PlayerInteractEvent event, Action type) {
		return event.getAction() == type;
	}

	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {

		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (event.getItem() == null)
			return;

		if (item().isSimilar(event.getItem())) {
			if (getCooldown().onCooldown(event.getPlayer())) {
				for (String message : Bunkers.getPlugin().getAbilityYML().gc().getStringList("Messages.Cooldown-Message")) {
					String display = CC.chat(displayName());
					event.getPlayer().sendMessage(CC.chat(message)
							.replaceAll("%time%", getCooldown().getRemaining(event.getPlayer()))
							.replaceAll("%ability%", display));
				}
			}
		}
	}

}
