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

public class JumpPocketbard extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public int cost() {
		return 200000;
	}

	@Override
	public Cooldowns getCooldown() {
		return cd;
	}

	@Override
	public List<String> getLore() {
		return Bunkers.getPlugin().getAbilityYML().gc().getStringList("Pocketbard.Jump.Lore");
	}

	@Override
	public String displayName() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Pocketbard.Jump.Name");
	}

	@Override
	public String scoreboard() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Pocketbard.Jump.Name");
	}

	@Override
	public String name() {
		return "jumpboostpocketbard";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material material() {
		return Material.valueOf(Bunkers.getPlugin().getAbilityYML().gc().getString("Pocketbard.Jump.Material"));
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

			int partner = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.Partner.Cooldown-Time");
			Bunkers.getPlugin().getAbilityHandler().getPartnerCD().applyCooldown(player, partner);
			int cooldown = Bunkers.getPlugin().getAbilityYML().gc().getInt("Ability-Settings.Pocketbard.Cooldown-Time");
			cd.applyCooldown(player, cooldown);
			event.setCancelled(true);
			int power = config.getInt("Pocketbard.Jump.Effect-Power");
			boolean take = Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Abilities.Pocketbard.Take-Item");
			takeItem(player, item(), take);
			int time = config.getInt("Pocketbard.Jump.Effect-Time");
			for (String message : Bunkers.getPlugin().getAbilityYML().gc().getStringList("Messages.Pocketbard.Used-Message")) {
				player.sendMessage(CC.chat(message
						.replaceAll("%heart%", "\u2764")
						.replaceAll("%pocketbard%", "&aJumpBoost")
						.replaceAll("%time%", String.valueOf(time))
						.replaceAll("%effect%", "JumpBoost " + power)));
			}
			PotionEffectType potType = PotionEffectType.JUMP;
			int rad = config.getInt("Pocketbard.Jump.Radius");
			Bunkers.getPlugin().getAbilityHandler().giveTeammatesEffects(player, potType, time, power, rad);
		}
	}
}
