package me.devkevin.bunkers.abilities.items;


import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.abilities.menus.PocketBardMenu;
import me.devkevin.bunkers.utils.cooldown.Cooldowns;
import me.devkevin.bunkers.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PocketBard extends Ability {



	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns getCooldown() {
		return cd;
	}

	@Override
	public List<String> getLore() {
		return Bunkers.getPlugin().getAbilityYML().gc().getStringList("Abilities.Pocketbard.Lore");
	}

	@Override
	public String displayName() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.Pocketbard.Name");
	}

	@Override
	public String scoreboard() {
		return Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.Pocketbard.Name");
	}

	@Override
	public String name() {
		return "pocketbard";
	}

	@Override
	public int data() {
		return 14;
	}

	@Override
	public Material material() {
		return Material.valueOf(Bunkers.getPlugin().getAbilityYML().gc().getString("Abilities.Pocketbard.Material"));
	}

	@Override
	public boolean glowing() {
		return Bunkers.getPlugin().getAbilityYML().gc().getBoolean("Abilities.Pocketbard.Glowing");
	}

	@Override
	public int cost() {
		return 185;
	}

	@EventHandler
	public void onPocketbard(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!event.getAction().name().contains("RIGHT"))
			return;

		if (event.getItem() == null)
			return;

		if (item().isSimilar(event.getItem())) {
			PocketBardMenu.openPocketBardGUI(player);
		}
	}
}