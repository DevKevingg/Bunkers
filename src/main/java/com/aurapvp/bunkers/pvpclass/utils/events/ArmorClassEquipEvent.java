package com.aurapvp.bunkers.pvpclass.utils.events;

import com.aurapvp.bunkers.pvpclass.utils.ArmorClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ArmorClassEquipEvent extends PlayerEvent
{
    private static HandlerList handlers;
    private ArmorClass pvpClass;
    
    static {
        ArmorClassEquipEvent.handlers = new HandlerList();
    }
    
    public ArmorClassEquipEvent(final Player player, final ArmorClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }
    
    public static HandlerList getHandlerList() {
        return ArmorClassEquipEvent.handlers;
    }
    
    public ArmorClass getPvpClass() {
        return this.pvpClass;
    }
    
    @Override
	public HandlerList getHandlers() {
        return ArmorClassEquipEvent.handlers;
    }
}
