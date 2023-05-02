package me.devkevin.bunkers.abilities;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.abilities.menus.PocketBardMenu;
import me.devkevin.bunkers.team.Team;
import me.devkevin.bunkers.utils.CC;
import me.devkevin.bunkers.utils.ClassUtils;
import me.devkevin.bunkers.utils.cooldown.Cooldowns;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class AbilityHandler {

	private Bunkers plugin;
	@Getter
	public HashMap<String, Ability> ability;
	@Getter
	private Cooldowns partnerCD;

	public AbilityHandler(Bunkers plugin) {
		this.plugin = plugin;
		partnerCD = new Cooldowns();
		ability = new HashMap<>();
		addAll();
		Bukkit.getPluginManager().registerEvents(new PocketBardMenu(), Bunkers.getPlugin());
	}


	public boolean canHit(Player attacker, Player target) {
		if (attacker == target) {
			return false;
		}

		Team playerFaction = plugin.getTeamManager().getByPlayer(attacker);
		Team targetFaction = plugin.getTeamManager().getByPlayer(target);

		if (targetFaction == null)
			return true;
		if (playerFaction == null)
			return true;

		return targetFaction != playerFaction;

	}

	public void giveTeammatesEffects(Player player, PotionEffectType potType, int time, int power, int radius) {
		Team pf = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
		if (pf == null) {
			player.addPotionEffect(new PotionEffect(potType, time * 20, power - 1));
			return;
		}
		player.addPotionEffect(new PotionEffect(potType, time * 20, power - 1));
		for (Entity near : player.getNearbyEntities(radius, radius, radius)) {
			if (near instanceof Player) {
				Player nearPlayer = (Player) near;
				if (pf.getMembers().contains(nearPlayer.getUniqueId())) {
					nearPlayer.addPotionEffect(new PotionEffect(potType, time * 20, power - 1));
				}

			}
		}
	}


	public void giveEnemyEffects(Player player, PotionEffectType potType, int time, int power, int radius) {
		Team pf = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
		if (pf == null) {
			player.addPotionEffect(new PotionEffect(potType, time * 20, power - 1));
			return;
		}
		player.addPotionEffect(new PotionEffect(potType, time * 20, power - 1));
		for (Entity near : player.getNearbyEntities(radius, radius, radius)) {
			if (near instanceof Player) {
				Player nearPlayer = (Player) near;
				if (pf.getMembers().contains(nearPlayer.getUniqueId())) {
					nearPlayer.addPotionEffect(new PotionEffect(potType, time * 20, power - 1));
				}
			}
		}
	}

	public Ability byName(String name) {
		for (Map.Entry<String, Ability> ab : ability.entrySet()) {
			if (ab.getValue().name().equalsIgnoreCase(name))
				return ab.getValue();
		}
		return null;
	}

	public boolean isEnabled(String ability) {
		return Bunkers.getPlugin().getAbilityYML().gc().getBoolean("showcase." + ability);
	}

	public void addAll() {
		ClassUtils.getClassesInPackage(Bunkers.getPlugin(), "com.aurapvp.bunkers.abilities.items").forEach(clazz -> {
			if (Ability.class.isAssignableFrom(clazz)) {
				try {
					Ability killstreak = (Ability) clazz.newInstance();

					ability.put(killstreak.name(), killstreak);

				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		});
		Bukkit.getConsoleSender().sendMessage(CC.chat("&eLoaded " + ability.keySet().size() + " custom ability items."));
	}

}
