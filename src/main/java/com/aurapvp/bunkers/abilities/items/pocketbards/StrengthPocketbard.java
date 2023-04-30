package com.aurapvp.bunkers.abilities.items.pocketbards;


import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.cooldown.Cooldowns;
import com.aurapvp.bunkers.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class StrengthPocketbard extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns getCooldown() {
		return cd;
	}
	@Override
	public int cost() {
		return 200000;
	}
	@Override
	public List<String> getLore() {
		return Bunkers.getPlugin().getAbilityYML().gc().getStringList("Pocketbard.Strength.Lore");
	}

	@Override
	public String displayName() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Pocketbard.Strength.Name");
	}

	@Override
	public String scoreboard() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Pocketbard.Strength.Name");
	}

	@Override
	public String name() {
		return "strengthpocketbard";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material material() {
		return Material.valueOf(Bunkers.getPlugin().getAbilityYML().gc().getString("Pocketbard.Strength.Material"));
	}

	@Override
	public boolean glowing() {
		return false;
	}

	@EventHandler
	public void onPocketbard(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		YamlConfiguration config = Bunkers.getPlugin().getAbilityYML().gc();
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
			int cooldown2 = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.Partner.Cooldown-Time");
			Bunkers.getPlugin().getAbilityHandler().getPartnerCD().applyCooldown(player, cooldown2);
			int cooldown = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.Pocketbard.Cooldown-Time");
			cd.applyCooldown(player, cooldown);
			event.setCancelled(true);
			int power = config.getInt("Pocketbard.Strength.Effect-Power");
			boolean take = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Abilities.Pocketbard.Take-Item");
			takeItem(player, item(), take);
			int time = config.getInt("Pocketbard.Strength.Effect-Time");
			for (String message : Bunkers.getPlugin().getAbilityYML().gc().getStringList("Messages.Pocketbard.Used-Message")) {
				player.sendMessage(CC.chat(message
						.replaceAll("%heart%", "\u2764")
						.replaceAll("%pocketbard%", "&cStrength")
						.replaceAll("%time%", String.valueOf(time))
						.replaceAll("%effect%", "Strength " + power)));
			}
			PotionEffectType potType = PotionEffectType.INCREASE_DAMAGE;
			int rad = config.getInt("Pocketbard.Strength.Radius");
			Bunkers.getPlugin().getAbilityHandler().giveTeammatesEffects(player, potType, time, power, rad);
		}
	}
}
