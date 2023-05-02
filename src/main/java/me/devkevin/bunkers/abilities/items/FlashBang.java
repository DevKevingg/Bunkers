package me.devkevin.bunkers.abilities.items;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.abilities.Ability;
import me.devkevin.bunkers.utils.CC;
import me.devkevin.bunkers.utils.cooldown.Cooldowns;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/05/2021 / 9:21 PM
 * SparkHCTeams / net.frozenorb.foxtrot.extras.abilities.items
 */
public class FlashBang extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns getCooldown() {
		return cd;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
				"",
				"&7Shoot this near players for whoever in a 10x10 radius of",
				"&7the egg it blinds and slows them for 10 seconds.",
				""
		);
	}
	@Override
	public int cost() {
		return 150;
	}
	@Override
	public String displayName() {
		return CC.chat("&4&lFlashBang");
	}

	@Override
	public String scoreboard() {
		return CC.chat("&4&lFlashBang");
	}

	@Override
	public String name() {
		return "flashbang";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material material() {
		return Material.EGG;
	}

	@Override
	public boolean glowing() {
		return true;
	}

	@EventHandler
	public void onBelchBombInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!event.getAction().name().contains("RIGHT"))
			return;
		if (item().isSimilar(player.getItemInHand())) {
			if (checkCooldown(player) != null) {
				sendMessage(player);
				event.setCancelled(true);
				return;
			}

			if (!canInteractWithAbility(player)) {
				event.setCancelled(true);
				return;
			}
			event.setCancelled(true);
			int cooldown = 90;
			takeItem(player, item(), true);
			int cooldown23 = 10;
			Bunkers.getPlugin().getAbilityHandler().getPartnerCD().applyCooldown(player, cooldown23);
			cd.applyCooldown(player, cooldown);
			Egg egg = player.launchProjectile(Egg.class);
			egg.setMetadata("flashbang", new FixedMetadataValue(Bunkers.getPlugin(), true));
		}
	}

	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		if (event.getEntity().hasMetadata("flashbang")) {
			ArrayList<String> affected = new ArrayList<>();
			((Player) event.getEntity().getShooter()).sendMessage(CC.chat("&4&lAffected Players:"));
			for (Entity currentMob : event.getEntity().getNearbyEntities(10, 10, 10)) {
				if (currentMob instanceof Player) {
					if (((Player) currentMob).getName().equalsIgnoreCase(((Player) event.getEntity().getShooter()).getName()))
						continue;
					if (!Bunkers.getPlugin().getAbilityHandler().canHit(((Player) event.getEntity().getShooter()), ((Player)currentMob)))
						continue;
					((Player) currentMob).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 4, 6));
					((Player) currentMob).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 4, 3));
					affected.add(((Player) currentMob).getName());
				}
			}
			for (String currentName : affected) {
				((Player) event.getEntity().getShooter()).sendMessage(CC.chat("&f» &e" + currentName));
			}
		}
	}

}
