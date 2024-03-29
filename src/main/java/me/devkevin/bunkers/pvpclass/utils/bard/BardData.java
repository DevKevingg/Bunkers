package me.devkevin.bunkers.pvpclass.utils.bard;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class BardData
{
    public static double ENERGY_PER_MILLISECOND;
    public static double MIN_ENERGY;
    public static double MAX_ENERGY;
    public long energyStart;
    public long buffCooldown;
    public static long MAX_ENERGY_MILLIS;
    public BukkitTask heldTask;
    
    static {
        BardData.ENERGY_PER_MILLISECOND = 1.0;
        BardData.MIN_ENERGY = 0.0;
        BardData.MAX_ENERGY = 120 + 0.0;
        BardData.MAX_ENERGY_MILLIS = (long)(BardData.MAX_ENERGY * 1000.0);
    }
    
    public BukkitTask getHeldTask() {
        return this.heldTask;
    }
    
    public long getBuffCooldown() {
        return this.buffCooldown;
    }
    
    public void setBuffCooldown(final long millis) {
        this.buffCooldown = System.currentTimeMillis() + millis;
    }
    
    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }
    
    public void startEnergyTracking() {
        this.setEnergy(0.0);
    }
    
    public long getEnergyMillis() {
        if (this.energyStart == 0L) {
            return 0L;
        }
        return Math.min(BardData.MAX_ENERGY_MILLIS, (long)(BardData.ENERGY_PER_MILLISECOND * (System.currentTimeMillis() - this.energyStart)));
    }
    
    public double getEnergy() {
        return Math.round(this.getEnergyMillis() / 100.0) / 10.0;
    }
    
    public void setEnergy(final double energy) {
        Preconditions.checkArgument(energy >= BardData.MIN_ENERGY, ("Energy cannot be less than " + BardData.MIN_ENERGY));
        Preconditions.checkArgument(energy <= BardData.MAX_ENERGY, ("Energy cannot be more than " + BardData.MAX_ENERGY));
        this.energyStart = (long)(System.currentTimeMillis() - 1000.0 * energy);
    }
}
