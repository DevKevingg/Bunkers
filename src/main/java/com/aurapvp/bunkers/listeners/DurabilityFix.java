package com.aurapvp.bunkers.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class DurabilityFix implements Listener {
    @EventHandler
    public void onDurLoss(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        int damage = event.getDamage();

        event.setDamage((int) Math.floor(damage / 3));
    }
}