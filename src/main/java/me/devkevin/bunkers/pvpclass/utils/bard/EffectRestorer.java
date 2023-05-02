package me.devkevin.bunkers.pvpclass.utils.bard;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.pvpclass.Bard;
import me.devkevin.bunkers.pvpclass.utils.events.ArmorClassUnequipEvent;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.UUID;

public class EffectRestorer implements Listener
{
    private Table<UUID, PotionEffectType, PotionEffect> restores;
    
    public EffectRestorer(final Bunkers plugin) {
        this.restores = HashBasedTable.create();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorClassUnequip(final ArmorClassUnequipEvent event) {
        this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }
    
    public void setRestoreEffect(Player player, final PotionEffect effect) {
        boolean shouldCancel = true;
        final Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (final PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) {
                continue;
            }
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            }
            if (effect.getAmplifier() == active.getAmplifier() && effect.getDuration() < active.getDuration()) {
                return;
            }
            this.restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > Bard.HELD_EFFECT_DURATION_TICKS && effect.getDuration() < Bard.DEFAULT_MAX_DURATION) {
            this.restores.remove(player.getUniqueId(), effect.getType());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(final PotionEffectExpireEvent event) {
        final LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            final Player player = (Player)livingEntity;
            final PotionEffect previous = (PotionEffect)this.restores.remove(player.getUniqueId(), event.getEffect().getType());
            if (previous != null) {
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.addPotionEffect(previous, true);
                    }
                }.runTask(Bunkers.getPlugin());
            }
        }
    }
}
