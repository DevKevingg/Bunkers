package com.aurapvp.bunkers.pvpclass.utils.archer;

import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;

public class ArcherMark implements Comparable<ArcherMark>
{
    public BukkitTask decrementTask;
    public int currentLevel;
    
    public void reset() {
        if (this.decrementTask != null) {
            this.decrementTask.cancel();
            this.decrementTask = null;
        }
        this.currentLevel = 0;
    }
    
    public int incrementMark() {
        return ++this.currentLevel;
    }
    
    public int decrementMark() {
        return --this.currentLevel;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArcherMark)) {
            return false;
        }
        final ArcherMark that = (ArcherMark)o;
        return this.currentLevel == that.currentLevel && ((this.decrementTask != null) ? this.decrementTask.equals(that.decrementTask) : (that.decrementTask == null));
    }
    
    @Override
    public int hashCode() {
        int result = (this.decrementTask != null) ? this.decrementTask.hashCode() : 0;
        result = 31 * result + this.currentLevel;
        return result;
    }
    
    @Override
    public int compareTo(@Nonnull final ArcherMark archerMark) {
        return Integer.compare(this.currentLevel, archerMark.currentLevel);
    }
}
