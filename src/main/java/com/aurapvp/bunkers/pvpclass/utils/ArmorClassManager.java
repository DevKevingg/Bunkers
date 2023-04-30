package com.aurapvp.bunkers.pvpclass.utils;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.pvpclass.Archer;
import com.aurapvp.bunkers.pvpclass.Bard;
import com.aurapvp.bunkers.pvpclass.Rogue;
import com.aurapvp.bunkers.pvpclass.utils.events.ArmorClassEquipEvent;
import com.aurapvp.bunkers.pvpclass.utils.events.ArmorClassUnequipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ArmorClassManager implements Listener
{
    private Map<UUID, ArmorClass> equippedClassMap;
    private List<ArmorClass> pvpClasses;
    
    public ArmorClassManager(Bunkers plugin) {
        this.equippedClassMap = new HashMap<>();
        this.pvpClasses = new ArrayList<>();
        this.pvpClasses.add(new Archer(plugin));
        this.pvpClasses.add(new Bard(plugin));
        this.pvpClasses.add(new Rogue(plugin));
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        for (final ArmorClass pvpClass : this.pvpClasses) {
            if (pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener)pvpClass, (Plugin)plugin);
            }
        }
    }
    
    public void onDisable() {
        for (final Map.Entry<UUID, ArmorClass> entry : new HashMap<>(this.equippedClassMap).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer((UUID)entry.getKey()), null);
        }
        this.pvpClasses.clear();
        this.equippedClassMap.clear();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.setEquippedClass(event.getEntity(), null);
    }
    
    public Collection<ArmorClass> getPvpClasses() {
        return this.pvpClasses;
    }
    
    public ArmorClass getEquippedClass(final Player player) {
        synchronized (this.equippedClassMap) {
            // monitorexit(this.equippedClassMap)
            return this.equippedClassMap.get(player.getUniqueId());
        }
    }
    
    public boolean hasClassEquipped(final Player player, final ArmorClass pvpClass) {
        return this.getEquippedClass(player) == pvpClass;
    }
    
    public void setEquippedClass(final Player player, final ArmorClass pvpClass) {
        if (pvpClass == null) {
            final ArmorClass equipped = this.equippedClassMap.remove(player.getUniqueId());
            if (equipped != null) {
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent((Event)new ArmorClassUnequipEvent(player, equipped));
            }
        }
        else if (pvpClass.onEquip(player) && pvpClass != this.getEquippedClass(player)) {
            this.equippedClassMap.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent((Event)new ArmorClassEquipEvent(player, pvpClass));
        }
    }
}
