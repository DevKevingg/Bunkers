package com.aurapvp.bunkers.timer.type;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.events.TimerExpireEvent;
import com.aurapvp.bunkers.pvpclass.Archer;
import com.aurapvp.bunkers.timer.PlayerTimer;
import com.aurapvp.bunkers.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArcherHandler extends PlayerTimer implements Listener
{
    private Double ARCHER_DAMAGE;
    
    public ArcherHandler(Bunkers plugin) {
        super("Archer Mark", TimeUnit.SECONDS.toMillis(10));
        this.ARCHER_DAMAGE = 0.56;
    }
    
    @EventHandler
    public void onExpire(final TimerExpireEvent event) {
        if (event.getUserUUID().isPresent() && event.getTimer().equals(this)) {
            final UUID userUUID = (UUID)event.getUserUUID().get();
            final Player player = Bukkit.getPlayer(userUUID);
            if (player == null) {
                return;
            }
            Archer.TAGGED.remove(player.getUniqueId());
        }
    }
    
    @EventHandler
    public void onHit(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player entity = (Player)event.getEntity();
            final Entity damager = event.getDamager();
            if (this.getRemaining(entity) > 0L) {
                final Double damage = event.getDamage() * this.ARCHER_DAMAGE;
                event.setDamage(event.getDamage() + damage);
            }
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            if (event.getEntity() instanceof Player) {
                return;
            }
            final Player entity = (Player)event.getEntity();
            final Entity damager = (Entity)((Arrow)event.getDamager()).getShooter();
            if (damager instanceof Player && this.getRemaining(entity) > 0L) {
                if (Archer.TAGGED.get(entity.getUniqueId()).equals(damager.getUniqueId())) {
                    this.setCooldown(entity, entity.getUniqueId());
                }
                final Double damage = event.getDamage() * this.ARCHER_DAMAGE;
                event.setDamage(event.getDamage() + damage);
            }
        }
    }

    @Override
    public String getScoreboardPrefix() {
        return CC.translate("&6&lArcher Mark");
    }
}
