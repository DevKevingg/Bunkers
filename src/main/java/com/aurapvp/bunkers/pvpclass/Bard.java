package com.aurapvp.bunkers.pvpclass;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.pvpclass.utils.ArmorClass;
import com.aurapvp.bunkers.pvpclass.utils.bard.BardData;
import com.aurapvp.bunkers.pvpclass.utils.bard.EffectData;
import com.aurapvp.bunkers.team.Team;
import com.aurapvp.bunkers.utils.CC;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bard extends ArmorClass implements Listener {
	public static int HELD_EFFECT_DURATION_TICKS;
	private static long BUFF_COOLDOWN_MILLIS;
	private static int TEAMMATE_NEARBY_RADIUS;
	private static long HELD_REAPPLY_TICKS;
	private Map<UUID, BardData> bardDataMap;
	@Getter
	private Map<Material, EffectData> bardEffects;
	private Bunkers plugin;
	private TObjectLongMap<UUID> msgCooldowns;

	static {
		Bard.HELD_EFFECT_DURATION_TICKS = 120;
		Bard.BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(10);
		Bard.TEAMMATE_NEARBY_RADIUS = 20;
		Bard.HELD_REAPPLY_TICKS = 20L;
	}

	public Bard(final Bunkers plugin) {
		super("Bard", TimeUnit.MILLISECONDS.toMillis(1L));
		this.bardDataMap = new HashMap<>();
		this.bardEffects = new EnumMap<>(Material.class);
		this.msgCooldowns = new TObjectLongHashMap();
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

		this.bardEffects.put(Material.SUGAR, new EffectData(30, new PotionEffect(PotionEffectType.SPEED, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.SPEED.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.SPEED.CLICKABLE.AMPLIFIER")), new PotionEffect(PotionEffectType.SPEED, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.SATURATION.HOLDEABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.SPEED.HOLDEABLE.AMPLIFIER"))));

		this.bardEffects.put(Material.BLAZE_POWDER, new EffectData(45, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.STRENGTH.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.STRENGTH.CLICKABLE.AMPLIFIER")), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.STRENGTH.HOLDEABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.STRENGTH.HOLDEABLE.AMPLIFIER"))));

		this.bardEffects.put(Material.IRON_INGOT, new EffectData(40, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.RESISTANCE.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.RESISTANCE.CLICKABLE.AMPLIFIER")), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.RESISTANCE.HOLDEABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.RESISTANCE.HOLDEABLE.AMPLIFIER"))));

		this.bardEffects.put(Material.GHAST_TEAR, new EffectData(25, new PotionEffect(PotionEffectType.REGENERATION, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.REGENERATION.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.REGENERATION.CLICKABLE.AMPLIFIER")), new PotionEffect(PotionEffectType.REGENERATION, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.REGENERATION.HOLDEABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.REGENERATION.HOLDEABLE.AMPLIFIER"))));

		this.bardEffects.put(Material.FEATHER, new EffectData(20, new PotionEffect(PotionEffectType.JUMP, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.JUMP.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.JUMP.CLICKABLE.AMPLIFIER")), new PotionEffect(PotionEffectType.JUMP, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.JUMP.HOLDEABLE.CLICKABLE-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.JUMP.HOLDEABLE.AMPLIFIER"))));

		this.bardEffects.put(Material.SPIDER_EYE, new EffectData(35, new PotionEffect(PotionEffectType.WITHER, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.WITHER.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.WITHER.CLICKABLE.AMPLIFIER")), null));

		this.bardEffects.put(Material.MAGMA_CREAM, new EffectData(25, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.FIRE_RESISTANCE.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.FIRE_RESISTANCE.CLICKABLE.AMPLIFIER")), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.FIRE_RESISTANCE.HOLDEABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.FIRE_RESISTANCE.HOLDEABLE.AMPLIFIER"))));

		this.bardEffects.put(Material.INK_SACK, new EffectData(45, new PotionEffect(PotionEffectType.INVISIBILITY, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.INVISIBILITY.CLICKABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.INVISIBILITY.CLICKABLE.AMPLIFIER")), new PotionEffect(PotionEffectType.INVISIBILITY, Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.INVISIBILITY.HOLDEABLE.AMPLIFIER-DURATION"), Bunkers.getPlugin().getConfig().getInt("BARD.ENERGY.INVISIBILITY.HOLDEABLE.AMPLIFIER"))));

	}

	@Override
	public boolean onEquip(final Player player) {
		if (!super.onEquip(player)) {
			return false;
		}
		final BardData bardData = new BardData();
		this.bardDataMap.put(player.getUniqueId(), bardData);
		bardData.startEnergyTracking();
		bardData.heldTask = new BukkitRunnable() {
			int lastEnergy;

			@Override
			public void run() {
				final ItemStack held = player.getItemInHand();
				if (held != null) {
					final EffectData bardEffect = Bard.this.bardEffects.get(held.getType());
					if (bardEffect == null) {
						return;
					}
					final Team playerFaction = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
					if (playerFaction != null) {
						final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities((double) Bard.TEAMMATE_NEARBY_RADIUS, (double) Bard.TEAMMATE_NEARBY_RADIUS, (double) Bard.TEAMMATE_NEARBY_RADIUS);
						for (final Entity nearby : nearbyEntities) {
							if (nearby instanceof Player && !player.equals(nearby)) {
								final Player target = (Player) nearby;
								if (!playerFaction.getMembers().contains(target.getUniqueId())) {
									continue;
								}
								Bard.this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.heldable);
							}
						}
					}
				}
				final int energy = (int) Bard.this.getEnergy(player);
				if (energy != 0 && energy != this.lastEnergy && (energy % 10 == 0 || this.lastEnergy - energy - 1 > 0 || energy == BardData.MAX_ENERGY)) {
					this.lastEnergy = energy;
					player.sendMessage(CC.translate("&bBard" + " Energy: &a" + energy));
				}
			}
		}.runTaskTimer((Plugin) this.plugin, 0L, Bard.HELD_REAPPLY_TICKS);
		return true;
	}

	@Override
	public void onUnequip(final Player player) {
		super.onUnequip(player);
		this.clearBardData(player.getUniqueId());
	}

	private void clearBardData(final UUID uuid) {
		final BardData bardData = this.bardDataMap.remove(uuid);
		if (bardData != null && bardData.getHeldTask() != null) {
			bardData.getHeldTask().cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		this.clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(final PlayerKickEvent event) {
		this.clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onItemHeld(final PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();
		final ArmorClass equipped = this.plugin.getArmorClassManager().getEquippedClass(player);
		if (equipped == null || !equipped.equals(this)) {
			return;
		}
		final UUID uuid = player.getUniqueId();
		final long lastMessage = this.msgCooldowns.get(uuid);
		final long millis = System.currentTimeMillis();
		if (lastMessage != this.msgCooldowns.getNoEntryValue() && lastMessage - millis > 0L) {
			return;
		}
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		final Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_AIR || (!event.isCancelled() && action == Action.RIGHT_CLICK_BLOCK)) {
			final Player player = event.getPlayer();
			final ItemStack stack = event.getItem();
			final EffectData bardEffect = this.bardEffects.get(stack.getType());
			if (bardEffect == null || bardEffect.clickable == null) {
				return;
			}
			event.setUseItemInHand(Event.Result.DENY);
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			if (bardData != null) {
				if (!this.canUseBardEffect(player, bardData, bardEffect, true)) {
					return;
				}
				if (stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() - 1);
				} else {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				}

				final Team playerFaction = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
				if (playerFaction != null && !bardEffect.clickable.getType().equals(PotionEffectType.SLOW_DIGGING)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(10.0, 10.0, 10.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							if (!playerFaction.getMembers().contains(target.getUniqueId())) {
								continue;
							}
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				} else if (playerFaction != null && bardEffect.clickable.getType().equals(PotionEffectType.SLOW_DIGGING)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(10.0, 10.0, 10.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							if (playerFaction.getMembers().contains(target.getUniqueId())) {
								continue;
							}
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				} else if (bardEffect.clickable.getType().equals(PotionEffectType.SLOW_DIGGING)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(10.0, 10.0, 10.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				}

				if (playerFaction != null && !bardEffect.clickable.getType().equals(PotionEffectType.SLOW)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(15.0, 15.0, 15.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							if (!playerFaction.getMembers().contains(target.getUniqueId())) {
								continue;
							}
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				} else if (playerFaction != null && bardEffect.clickable.getType().equals(PotionEffectType.SLOW)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(15.0, 15.0, 15.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							if (playerFaction.getMembers().contains(target.getUniqueId())) {
								continue;
							}
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				} else if (bardEffect.clickable.getType().equals(PotionEffectType.SLOW)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(15.0, 15.0, 15.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				}
				if (playerFaction != null && !bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(25.0, 25.0, 25.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							if (!playerFaction.getMembers().contains(target.getUniqueId())) {
								continue;
							}
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				} else if (playerFaction != null && bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(25.0, 25.0, 25.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							if (playerFaction.getMembers().contains(target.getUniqueId())) {
								continue;
							}
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				} else if (bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
					final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(25.0, 25.0, 25.0);
					for (final Entity nearby : nearbyEntities) {
						if (nearby instanceof Player && !player.equals(nearby)) {
							final Player target = (Player) nearby;
							this.plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
						}
					}
				}
				this.plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);
				bardData.setBuffCooldown(Bard.BUFF_COOLDOWN_MILLIS);
				final double newEnergy = this.setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);
				player.sendMessage(CC.translate("&cYou have just used a &lBard" + " Buff &cthat cost you " + bardEffect.energyCost + " &cof your Energy."));
			}
		}
	}

	private boolean canUseBardEffect(final Player player, final BardData bardData, final EffectData bardEffect, final boolean sendFeedback) {
		String errorFeedback = null;
		final double currentEnergy = bardData.getEnergy();
		if (bardEffect.energyCost > currentEnergy) {
			errorFeedback = CC.translate("&cYou do not have enough energy for this! You need " + bardEffect.energyCost + " energy, but you only have " + currentEnergy);
		}
		final long remaining = bardData.getRemainingBuffDelay() / 1000L;
		if (remaining > 0L) {
			errorFeedback = ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + remaining + ChatColor.RED + " seconds.";
		}

		if (sendFeedback && errorFeedback != null) {
			player.sendMessage(errorFeedback);
		}
		return errorFeedback == null;
	}

	@Override
	public boolean isApplicableFor(final Player player) {
		final ItemStack helmet = player.getInventory().getHelmet();
		if (helmet == null || helmet.getType() != Material.GOLD_HELMET) {
			return false;
		}
		final ItemStack chestplate = player.getInventory().getChestplate();
		if (chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE) {
			return false;
		}
		final ItemStack leggings = player.getInventory().getLeggings();
		if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) {
			return false;
		}
		final ItemStack boots = player.getInventory().getBoots();
		return boots != null && boots.getType() == Material.GOLD_BOOTS;
	}

	public long getRemainingBuffDelay(final Player player) {
		synchronized (this.bardDataMap) {
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			// monitorexit(this.bardDataMap)
			return (bardData == null) ? 0L : bardData.getRemainingBuffDelay();
		}
	}

	public double getEnergy(final Player player) {
		synchronized (this.bardDataMap) {
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			// monitorexit(this.bardDataMap)
			return (bardData == null) ? 0.0 : bardData.getEnergy();
		}
	}

	public long getEnergyMillis(final Player player) {
		synchronized (this.bardDataMap) {
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			// monitorexit(this.bardDataMap)
			return (bardData == null) ? 0L : bardData.getEnergyMillis();
		}
	}

	public double setEnergy(final Player player, final double energy) {
		final BardData bardData = this.bardDataMap.get(player.getUniqueId());
		if (bardData == null) {
			return 0.0;
		}
		bardData.setEnergy(energy);
		return bardData.getEnergy();
	}
}
