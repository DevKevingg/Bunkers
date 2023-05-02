package me.devkevin.bunkers.tasks;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.game.GameManager;
import me.devkevin.bunkers.koth.Koth;
import me.devkevin.bunkers.spectator.Spectator;
import me.devkevin.bunkers.team.Team;
import me.devkevin.bunkers.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KothTask extends BukkitRunnable {
    private Koth koth = Bunkers.getPlugin().getKoth();
    boolean started = false;

    @Override public void run() {
        if (!started) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[KingOfTheHill] &eThe &9Koth &ecan now be contested. &7(10:00)"));
            started = true;
        }

        if (koth.getCapSeconds() <= 0) {
            Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(koth.getController());
            new GameManager().setWon(team);
            cancel();
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Spectator.spectators.contains(player))
                continue;
            if (koth.isInsideArea(player.getLocation())) {
                if (koth.getController() == null) {
                    koth.setController(player);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[KingOfTheHill] &eSomeone has started controlling the &9Koth&e. &7(" + BukkitUtils.niceTime(koth.getSeconds(), false) + ")"));
                }

                if (koth.getCapSeconds() % 30 == 0 && koth.getCapSeconds() != 600) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[KingOfTheHill] &eSomeone is trying to control the &9Koth&e. &7(" + BukkitUtils.niceTime(koth.getCapSeconds(), false) + ")"));
                }
            } else {
                if (koth.getController() != null) {
                    if (koth.getController() == player) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[KingOfTheHill] &eSomeone has been knocked off the &9Koth&e. &7(" + BukkitUtils.niceTime(koth.getCapSeconds(), false) + ")"));
                        koth.setController(null);
                        koth.setCapSeconds(koth.getSeconds());
                    }
                }
            }
        }

        if (koth.getController() != null) {
            koth.setCapSeconds(koth.getCapSeconds() - 1);
        }
    }
}
