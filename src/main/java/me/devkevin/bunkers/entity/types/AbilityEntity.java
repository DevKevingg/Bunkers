package me.devkevin.bunkers.entity.types;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.bunkers.entity.ShopType;
import me.devkevin.bunkers.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 10/05/2021 / 4:54 PM
 * Bunkers / me.redis.bunkers.entity.types
 */
@Getter
public class AbilityEntity {
	private final Team team;
	private final Location spawnLocation;
	@Setter
	private ShopType shopType;

	public AbilityEntity(Team team, Location spawnLocation) {
		this.team = team;
		this.spawnLocation = spawnLocation;

		Villager villager = (Villager) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);
		villager.setFallDistance(0);
		villager.setRemoveWhenFarAway(false);
		villager.setAdult();
		villager.setProfession(Villager.Profession.BLACKSMITH);
		villager.setCanPickupItems(false);
		villager.setMaxHealth(40);
		villager.setHealth(40);
		villager.setCustomName(team.getColor() + "Ability Shop");
		villager.setCustomNameVisible(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, -6, true), true);
		setShopType(ShopType.ABILITY);

		villager.setVelocity(new Vector(0, 0, 0));
	}
}
