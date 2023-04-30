package com.aurapvp.bunkers.scoreboard.sidebars;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.profiles.status.PlayerStatus;
import com.aurapvp.bunkers.pvpclass.Bard;
import com.aurapvp.bunkers.scoreboard.scoreboard.Board;
import com.aurapvp.bunkers.scoreboard.scoreboard.BoardAdapter;
import com.aurapvp.bunkers.scoreboard.scoreboard.cooldown.BoardCooldown;
import com.aurapvp.bunkers.tasks.GameTimeTask;
import com.aurapvp.bunkers.team.Team;
import com.aurapvp.bunkers.utils.Animation;
import com.aurapvp.bunkers.utils.BukkitUtils;
import com.aurapvp.bunkers.utils.DurationFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.spigot.utils.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BunkersSidebar implements BoardAdapter, Listener {
    public BunkersSidebar(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getTitle(Player player) {
        return "&6&lBunkers";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> strings = new ArrayList<>();
        Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------&r"));
        if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.WAITING || Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.STARTING) {
            strings.add("&e&lOnline&7: &c" + Bukkit.getOnlinePlayers().size());
            strings.add("&6&lTeam&7: &c" + (team == null ? "None" : team.getColor() + team.getName()));

            if (Bunkers.getPlugin().getGameManager().getStarted()) {
                strings.add("&eStarting in &l" + (Bunkers.getPlugin().getGameManager().getCooldown()[0] + 1) + "s");
            }
        } else if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.PLAYING && profile.getStatus() == PlayerStatus.PLAYING) {
            strings.add("&6&lGame Time&7: &c" + BukkitUtils.niceTime(GameTimeTask.getNumOfSeconds(), false));
            if (Bunkers.getPlugin().getTimerManager().getProtectionTimer().getRemaining(player) > 0) {
                strings.add("&a&lImmunity&7: &c" + DurationFormatter.getRemaining(Bunkers.getPlugin().getTimerManager().getProtectionTimer().getRemaining(player), true));
            }
            /*if (LastChance.enderPearlCD.onCooldown(player)) {
                strings.add("&6&lEnderpearl&7: &c" + LastChance.enderPearlCD.getRemaining(player));
            }*/

            if (new Bard(Bunkers.getPlugin()).isApplicableFor(player)) {
                strings.add("&b&lEnergy&7: &c" + new Bard(Bunkers.getPlugin()).getEnergy(player));
            }
            strings.add("&9&lKoth&7: &f" + (GameTimeTask.getNumOfSeconds() < 300 ? "Starts in " + BukkitUtils.niceTime(300 - GameTimeTask.getNumOfSeconds(), false) : BukkitUtils.niceTime(Bunkers.getPlugin().getKoth().getCapSeconds(), false)));
            strings.add("&e&lTeam&7: &f" + team.getName());

            if (Information.i == 1) {
                strings.add("&6&lDTR&7: &f" + team.getDtr1() + ".0");
            } else if (Information.i == 2) {
                strings.add("&6&lDTR&7: &f" + team.getDtr2() + ".0");
            }
            strings.add("&a&lBalance&7: &f$" + profile.getBalance());
            if (Bunkers.getPlugin().getTimerManager().getTeleportTimer().getRemaining(player) > 0) {
                strings.add("&9&lHome&7: &f" + DurationFormatter.getRemaining(Bunkers.getPlugin().getTimerManager().getTeleportTimer().getRemaining(player), true));
            }
        } else if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.PLAYING && profile.getStatus() == PlayerStatus.SPECTATOR) {
            strings.add("&6&lGame Time&7: &f" + BukkitUtils.niceTime(GameTimeTask.getNumOfSeconds(), false));
            strings.add("&9&lKoth&7: &f" + (GameTimeTask.getNumOfSeconds() < 300 ? "Starts in " + BukkitUtils.niceTime(300 - GameTimeTask.getNumOfSeconds(), false) : BukkitUtils.niceTime(Bunkers.getPlugin().getKoth().getCapSeconds(), false)));

        } else if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.ENDING) {
            strings.add("&6&lWinner Team&7: " + Bunkers.getPlugin().getGameManager().getWinnerTeam().getColor() + Bunkers.getPlugin().getGameManager().getWinnerTeam().getName());
        }

        if (Bunkers.getPlugin().getGameManager().isEvent()) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------&l"));
            if (Bunkers.getPlugin().getGameManager().getScoreboard() == 0) {
                strings.add("&e" + Bunkers.getPlugin().getGameManager().getEventName());
            } else {
                strings.add("&eAddress&7: &f" + Bunkers.getPlugin().getInformationManager().getInformation().getAddress());
            }
        }

        strings.add("");

        Animation animation = Animation.getAnimation(player.getUniqueId(), "footer");
        if (animation != null) {
            strings.add(CC.translate(animation.getLine()));
        } else  {
            strings.add(CC.GRAY + "udrop.club");
        }

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));

        return strings;
    }
}
