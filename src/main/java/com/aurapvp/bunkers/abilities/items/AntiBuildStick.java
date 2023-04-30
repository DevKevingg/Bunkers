package com.aurapvp.bunkers.abilities.items;


import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.cooldown.Cooldowns;
import com.aurapvp.bunkers.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AntiBuildStick extends Ability {

	@Override
	public int cost() {
		return 120;
	}
	public HashMap<UUID, Integer> hits = new HashMap<>();

	public Cooldowns cd = new Cooldowns();
	public Cooldowns boned = new Cooldowns();

	@Override
	public Cooldowns getCooldown() {
		return cd;
	}

	@Override
	public List<String> getLore() {
		return Bunkers.getPlugin().getAbilityYML().gc().getStringList("Abilities.AntiBuildStick.Lore");
	}

	@Override
	public String displayName() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.AntiBuildStick.Name");
	}

	@Override
	public String scoreboard() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.AntiBuildStick.Name");
	}

	@Override
	public String name() {
		return "antibuildstick";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material material() {
		return Material.STICK;
	}

	@Override
	public boolean glowing() {
		return Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Abilities.AntiBuildStick.Glowing");
	}

	@EventHandler
	public void onExoticBoneHit(EntityDamageByEntityEvent event) {
		YamlConfiguration config = Bunkers.getPlugin().getAbilityYML().gc();
		if (event.getDamager() instanceof Player) {
			if (event.getEntity() instanceof Player) {
				Player damaged = (Player) event.getEntity();
				Player damager = (Player) event.getDamager();
				if (Bunkers.getPlugin().getAbilityHandler().canHit(damager, damaged)) {
					if (item().isSimilar(damager.getItemInHand())) {
						if (checkCooldown(damager) != null) {
							sendMessage(damager);
							return;
						}
						int cooldown = config.getInt("Ability-Settings.AntiBuildStick.Cooldown-Time");
						int cooldown2 = config.getInt("Ability-Settings.AntiBuildStick.Anti-Build-Time");
						int maxhits = config.getInt("Ability-Settings.AntiBuildStick.Max-Hits");
						hits.putIfAbsent(damaged.getUniqueId(), 1);
						if (hits.get(damaged.getUniqueId()) >= maxhits) {
							boolean enabled = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Messages.AntiBuildStick.Hit-Someone-Message.Enabled");
							boolean enabled2 = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Messages.AntiBuildStick.Been-Hit-Message.Enabled");
							if (enabled) {
								for (String message : config.getStringList("Messages.AntiBuildStick.Hit-Someone-Message.Message")) {
									damager.sendMessage(CC.chat(message.replaceAll("%heart%", "\u2764")).replaceAll("%player%", damaged.getName()));
								}
							}
							if (enabled2) {
								for (String message : config.getStringList("Messages.AntiBuildStick.Been-Hit-Message.Message")) {
									damaged.sendMessage(CC.chat(message.replaceAll("%heart%", "\u2764")).replaceAll("%player%", damager.getName()));
								}
							}
							boolean take = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Abilities.AntiBuildStick.Take-Item");
							takeItem(damager, item(), take);
							int cool = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.AntiBuildStick.Cooldown-Time");
							int cool2 = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.Partner.Cooldown-Time");
							Bunkers.getPlugin().getAbilityHandler().getPartnerCD().applyCooldown(damager, cool2);
							cd.applyCooldown(damager, cool);
							boned.applyCooldown(damaged, cooldown2);
							hits.remove(damaged.getUniqueId());
							return;
						}
						hits.put(damaged.getUniqueId(), hits.get(damaged.getUniqueId()) + 1);
					}

				}

			}
		}
	}

	@EventHandler
	public void onInteractWhilstBoned(PlayerInteractEvent event) {
		YamlConfiguration config = Bunkers.getPlugin().getAbilityYML().gc();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (boned.onCooldown(event.getPlayer())) {
			if (event.getClickedBlock() == null)
				return;

			if (event.getClickedBlock().getType() == Material.FENCE_GATE
					|| event.getClickedBlock().getType() == Material.WOOD_DOOR
					|| event.getClickedBlock().getType() == Material.WOODEN_DOOR
					|| event.getClickedBlock().getType() == Material.TRAP_DOOR
					|| event.getClickedBlock().getType() == Material.TRAPPED_CHEST
					|| event.getClickedBlock().getType() == Material.CHEST) {
				boolean enabled2 = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Messages.AntiBuildStick.Cant-Interact-Message.Enabled");
				String remaining = boned.getRemaining(event.getPlayer());
				if (enabled2) {
					for (String message : config.getStringList("Messages.AntiBuildStick.Cant-Interact-Message.Message")) {
						event.getPlayer().sendMessage(CC.chat(message).replaceAll("%time%", remaining));
					}
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlaceWhilstBoned(BlockPlaceEvent event) {

		YamlConfiguration config = Bunkers.getPlugin().getAbilityYML().gc();
		if (event.getBlockPlaced() == null)
			return;

		if (boned.onCooldown(event.getPlayer())) {
			boolean enabled2 = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Messages.AntiBuildStick.Cant-Interact-Message.Enabled");
			String remaining = boned.getRemaining(event.getPlayer());
			if (enabled2) {
				for (String message : config.getStringList("Messages.AntiBuildStick.Cant-Interact-Message.Message")) {
					event.getPlayer().sendMessage(CC.chat(message).replaceAll("%time%", remaining));
				}
			}
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onPlaceWhilstBoned(BlockBreakEvent event) {

		YamlConfiguration config = Bunkers.getPlugin().getAbilityYML().gc();
		if (event.getBlock() == null)
			return;

		if (boned.onCooldown(event.getPlayer())) {
			boolean enabled2 = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Messages.AntiBuildStick.Cant-Interact-Message.Enabled");
			String remaining = boned.getRemaining(event.getPlayer());
			if (enabled2) {
				for (String message : config.getStringList("Messages.AntiBuildStick.Cant-Interact-Message.Message")) {
					event.getPlayer().sendMessage(CC.chat(message).replaceAll("%time%", remaining));
				}
			}
			event.setCancelled(true);
		}

	}
}
