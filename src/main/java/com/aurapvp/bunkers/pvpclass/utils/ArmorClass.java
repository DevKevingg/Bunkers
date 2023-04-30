package com.aurapvp.bunkers.pvpclass.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class ArmorClass
{
    public static long DEFAULT_MAX_DURATION;
    protected Set<PotionEffect> passiveEffects;
    protected String name;
    protected long warmupDelay;
    
    static {
        ArmorClass.DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
    }
    
    public ArmorClass(final String name, final long warmupDelay) {
        this.passiveEffects = new HashSet<>();
        this.name = name;
        this.warmupDelay = warmupDelay;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getWarmupDelay() {
        return this.warmupDelay;
    }
    
    public boolean onEquip(final Player player) {
        for (final PotionEffect effect : this.passiveEffects) {
            player.addPotionEffect(effect, true);
        }
        return true;
    }
    
    public void onUnequip(final Player player) {
        for (final PotionEffect effect : this.passiveEffects) {
            for (final PotionEffect active : player.getActivePotionEffects()) {
                if (active.getDuration() > ArmorClass.DEFAULT_MAX_DURATION && active.getType().equals(effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }
    }
    
    public abstract boolean isApplicableFor(final Player p0);
}
