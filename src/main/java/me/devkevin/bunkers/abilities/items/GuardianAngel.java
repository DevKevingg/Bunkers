package me.devkevin.bunkers.abilities.items;


import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.utils.CC;
import me.devkevin.bunkers.utils.cooldown.Cooldowns;
import me.devkevin.bunkers.abilities.Ability;
import me.devkevin.bunkers.abilities.AbilityHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class GuardianAngel extends Ability {
	@Override
	public int cost() {
		return 165;
	}
	public HashSet<UUID> gAngel = new HashSet<>();

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns getCooldown() {
		return cd;
	}

	@Override
	public List<String> getLore() {
		return Bunkers.getPlugin().getAbilityYML().gc().getStringList("Abilities.GuardianAngel.Lore");
	}

	@Override
	public String displayName() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.GuardianAngel.Name");
	}

	@Override
	public String scoreboard() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.GuardianAngel.Name");
	}

	@Override
	public String name() {
		return "guardianangel";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material material() {
		return Material.valueOf(Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.GuardianAngel.Material"));
	}

	@Override
	public boolean glowing() {
		return Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Abilities.GuardianAngel.Glowing");
	}

	@EventHandler
	public void onGuardianAngel(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		AbilityHandler abilityManager = Bunkers.getPlugin().getAbilityHandler();
		if (!event.getAction().name().contains("RIGHT"))
			return;

		if (event.getItem() == null)
			return;

		if (item().isSimilar(event.getItem())) {
			if (checkCooldown(player) != null) {
				sendMessage(player);
				return;
			}
			if (!canInteractWithAbility(player))
				return;
			if (gAngel.contains(player.getUniqueId())) {
				return;
			}
			gAngel.add(player.getUniqueId());
			int cool = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.GuardianAngel.Cooldown-Time");
			int cool2 = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.Partner.Cooldown-Time");
			Bunkers.getPlugin().getAbilityHandler().getPartnerCD().applyCooldown(player, cool2);
			cd.applyCooldown(player, cool);
			event.setCancelled(true);
			boolean take = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Abilities.GuardianAngel.Take-Item");
			takeItem(player, item(), take);
			for (String message : Bunkers.getPlugin().getAbilityYML().gc().getStringList("Messages.GuardianAngel.Used-Message")) {
				player.sendMessage(CC.chat(message.replaceAll("%heart%", "\u2764")));
			}
			activateGuardianAngel(player);
		}
	}

	@EventHandler
	public void onPlayerDmg(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			if (gAngel.contains(damaged.getUniqueId())) {
				double health = damaged.getHealth();
				int heal = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.GuardianAngel.Revive-Hearts");
				if (health <= heal) {
					event.setCancelled(true);
					damaged.damage(0);
					deactivateGuardianAngel(damaged, "hit");
					for (String msg : Bunkers.getPlugin().getAbilityYML().gc().getStringList("Messages.GuardianAngel.Activated-Message")) {
						damaged.sendMessage(CC.chat(msg.replaceAll("%heart%", "\u2764")));
					}
				}
			}
		}
	}

	private void activateGuardianAngel(Player player) {
		gAngel.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (gAngel.contains(player.getUniqueId())) {
					deactivateGuardianAngel(player, "wornoff");
				}
			}
		}.runTaskLater(Bunkers.getPlugin(), 20 * 30);
	}

	private void deactivateGuardianAngel(Player player, String reason) {
		if (reason.equalsIgnoreCase("hit")) {
			player.setHealth(player.getMaxHealth());
			gAngel.remove(player.getUniqueId());
		} else {
			gAngel.remove(player.getUniqueId());
			player.sendMessage(CC.chat("&cYour guardian angel has worn off and will no longer work!"));
		}
	}
}
