package com.aurapvp.bunkers.pvpclass.utils.bard;

import org.bukkit.potion.PotionEffect;

public class EffectData
{
    public PotionEffect clickable;
    public PotionEffect heldable;
    public int energyCost;
    
    public EffectData(final int energyCost, final PotionEffect clickable, final PotionEffect heldable) {
        this.energyCost = energyCost;
        this.clickable = clickable;
        this.heldable = heldable;
    }
}
