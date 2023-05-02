package me.devkevin.bunkers.pvpclass.utils.events;

import me.devkevin.bunkers.pvpclass.utils.ArmorClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ArmorClassUnequipEvent extends PlayerEvent
{
    private static HandlerList handlers;
    private ArmorClass pvpClass;
    
    static {
        ArmorClassUnequipEvent.handlers = new HandlerList();
    }
    
    public ArmorClassUnequipEvent(final Player player, final ArmorClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }
    
    public static HandlerList getHandlerList() {
        return ArmorClassUnequipEvent.handlers;
    }
    
    public ArmorClass getPvpClass() {
        return this.pvpClass;
    }
    
    @Override
    public HandlerList getHandlers() {
        return ArmorClassUnequipEvent.handlers;
    }
}
