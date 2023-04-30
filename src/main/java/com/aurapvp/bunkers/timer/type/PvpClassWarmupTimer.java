package com.aurapvp.bunkers.timer.type;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.pvpclass.utils.ArmorClass;
import com.aurapvp.bunkers.timer.PlayerTimer;
import com.aurapvp.bunkers.timer.TimerCooldown;
import com.aurapvp.bunkers.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Timer that handles {@link ArmorClass} warmups.
 */
public class PvpClassWarmupTimer extends PlayerTimer implements Listener {

	protected Map<UUID, ArmorClass> classWarmups = new HashMap<>();

	private Bunkers plugin;

	public PvpClassWarmupTimer(Bunkers plugin) {
		super("Warmup", 0, false);
		this.plugin = plugin;

		// Re-equip the applicable class for every player during reloads.
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bunkers.getPlugin().getServer().getOnlinePlayers()) {
					attemptEquip(player);
				}
			}
		}.runTaskLater(plugin, 10L);
	}

	@Override
	public String getScoreboardPrefix() {
		return CC.chat("&b&lPvP Warmup");
	}

	@Override
	public TimerCooldown clearCooldown(UUID playerUUID) {
		TimerCooldown runnable = super.clearCooldown(playerUUID);
		if (runnable != null) {
			this.classWarmups.remove(playerUUID);
			return runnable;
		}

		return null;
	}

	@Override
	public void onExpire(UUID userUUID) {
		Player player = Bukkit.getPlayer(userUUID);
		if (player == null)
			return;

		ArmorClass pvpClass = this.classWarmups.remove(userUUID);
		//Preconditions.checkNotNull(pvpClass, "Attempted to equip a class for %s, but nothing was added", player.getName());
		this.plugin.getArmorClassManager().setEquippedClass(player, pvpClass);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerQuitEvent event) {
		this.plugin.getArmorClassManager().setEquippedClass(event.getPlayer(), null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.attemptEquip(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEquipmentSet(EquipmentSetEvent event) {
		HumanEntity humanEntity = event.getHumanEntity();
		if (humanEntity instanceof Player) {
			this.attemptEquip((Player) humanEntity);
		}
	}

	private void attemptEquip(Player player) {
		ArmorClass current = plugin.getArmorClassManager().getEquippedClass(player);
		if (current != null) {
			if (current.isApplicableFor(player)) {
				return;
			}

			this.plugin.getArmorClassManager().setEquippedClass(player, null);
		} else if ((current = classWarmups.get(player.getUniqueId())) != null) {
			if (current.isApplicableFor(player)) {
				return;
			}

			this.clearCooldown(player.getUniqueId());
		}

		Collection<ArmorClass> pvpClasses = plugin.getArmorClassManager().getPvpClasses();
		for (ArmorClass pvpClass : pvpClasses) {
			if (pvpClass.isApplicableFor(player)) {
				this.classWarmups.put(player.getUniqueId(), pvpClass);
				this.setCooldown(player, player.getUniqueId(), pvpClass.getWarmupDelay(), false);
				break;
			}
		}
	}
}
