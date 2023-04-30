package com.aurapvp.bunkers.koth;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.tasks.GameTimeTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Koth  {
    @Getter @Setter private int seconds = 600;
    @Getter @Setter private int capSeconds = 600;
    @Getter @Setter Player controller;

    public Koth() {
        new BukkitRunnable() {
            @Override public void run() {
                if (GameTimeTask.getNumOfSeconds() == 600) {
                    setSeconds(300);
                    if (capSeconds > 300) {
                        setCapSeconds(300);
                    }
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&4KingOfTheHill&7] &eThe &9Koth &etime has been decreased. &7(05:00)"));
                }
            }
        }.runTaskTimer(Bunkers.getPlugin(), 0L, 20L);
    }

    public boolean isInsideArea(Location location) {
        return Bunkers.getPlugin().getInformationManager().getInformation().getKothCuboid().contains(location);
    }
}
