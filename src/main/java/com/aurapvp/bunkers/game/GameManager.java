package com.aurapvp.bunkers.game;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.entity.types.*;
import com.aurapvp.bunkers.events.GameStatusChangeEvent;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.profiles.status.PlayerStatus;
import com.aurapvp.bunkers.tasks.GameTimeTask;
import com.aurapvp.bunkers.tasks.KothTask;
import com.aurapvp.bunkers.team.Team;
import com.aurapvp.bunkers.team.listeners.TeamListener;
import com.aurapvp.bunkers.timer.PvPCooldown;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.LocationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getServer;

public class GameManager {
    @Getter
    private Bunkers bunkers = Bunkers.getPlugin();
    @Getter
    private GameStatus status;
    @Getter
    @Setter
    private Boolean started = false;
    @Getter
    @Setter
    private long gameTime;
    @Getter
    @Setter
    private boolean event = false;
    @Getter
    private int scoreboard = 0;
    @Getter
    @Setter
    private String eventName = "Custom Event";
    @Getter
    @Setter
    private Team winnerTeam;
    @Getter
    @Setter
    public ArrayList<Team> teamsLeft = new ArrayList<>();
    private World world;


    public GameManager() {
        Bukkit.getScheduler().runTaskTimer(Bunkers.getPlugin(), () -> {
            if (event) {
                if (scoreboard == 0) {
                    scoreboard = 1;
                } else if (scoreboard == 1) {
                    scoreboard = 0;
                }
            }
        }, 20L, 20 * 3L);

    }

    public void setStatus(GameStatus status) {
        Bukkit.getPluginManager().callEvent(new GameStatusChangeEvent(this.status, status));
        this.status = status;
    }

    public boolean canBePlayed() {
        return bunkers.getTeamManager().canBePlayed();
    }

    @Getter private final int[] cooldown = {15};
    @Getter private final int[] endtimer = {15};

    public void startCooldown() {
        setStatus(GameStatus.STARTING);
        setStarted(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldown[0] <= 0) {
                    startGame();

                    cancel();
                } else {
                    if (cooldown[0] % 5 == 0) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe match will start in &9" + cooldown[0] + "&e seconds&e."));
                    }
                }

                cooldown[0]--;
            }
        }.runTaskTimer(Bunkers.getPlugin(), 20L, 20L);
    }

    public void startGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
                Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
                Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);

                player.teleport(LocationUtils.getLocation(team.getSpawnLocation()));
                player.getInventory().clear();
                player.getInventory().setItem(0, new ItemStack(Material.STONE_PICKAXE));
                player.getInventory().setItem(1, new ItemStack(Material.STONE_AXE));
                setStatus(GameStatus.PLAYING);
                profile.setStatus(PlayerStatus.PLAYING);
                profile.setGamesPlayed(profile.getGamesPlayed() + 1);
                profile.setBalance(250);
                profile.save();
                if (Information.i == 1) {

                    if (team.getDtr1() == 0 && team.getMembers().size() == 0) {
                        teamsLeft.remove(team);
                    }

                    team.setDtr1(team.getMembers().size() + 1);
                } else if (Information.i == 2) {

                    if (team.getDtr2() == 0 && team.getMembers().size() == 0) {
                        teamsLeft.remove(team);
                    }

                    team.setDtr2(team.getMembers().size() + 1);
                }
                teamsLeft.add(team);


                for (Entity t : Bukkit.getWorld("world").getEntitiesByClass(Item.class)) {
                    t.getLocation().getChunk().load(true);
                    t.remove();
                }

                for (Entity t : Bukkit.getWorld("world").getEntitiesByClass(Villager.class)) {
                    t.getLocation().getChunk().load(true);
                    Bukkit.getLogger().info("[Bunkers] Successfully despawned a villager at " + t.getLocation());
                    t.remove();
                }

                for (Map.Entry<String, Team> c : Bunkers.getPlugin().getTeamManager().getTeams().entrySet()) {
                    Team current = c.getValue();

                    LocationUtils.getLocation(current.getAbilityShop()).getChunk().load(true);
                    LocationUtils.getLocation(current.getBuildShop()).getChunk().load(true);
                    LocationUtils.getLocation(current.getCombatShop()).getChunk().load(true);
                    LocationUtils.getLocation(current.getEnchantShop()).getChunk().load(true);
                    LocationUtils.getLocation(current.getSellShop()).getChunk().load(true);

                    new AbilityEntity(current, LocationUtils.getLocation(current.getAbilityShop()));
                    new BuildEntity(current, LocationUtils.getLocation(current.getBuildShop()));
                    new CombatEntity(current, LocationUtils.getLocation(current.getCombatShop()));
                    new EnchantEntity(current, LocationUtils.getLocation(current.getEnchantShop()));
                    new SellEntity(current, LocationUtils.getLocation(current.getSellShop()));
                }

                player.sendMessage(ChatColor.GREEN + "The match has started...");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PvPCooldown.isGrace = false;
                        Bukkit.broadcastMessage(CC.chat("&cGrace period is now over."));
                    }
                }.runTaskLater(Bunkers.getPlugin(), 20 * 60 * 2);
            } else {
                player.kickPlayer(ChatColor.RED + "You must have a team to play.");
            }
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bunkers.getPlugin(), () -> {
            getServer().getOnlinePlayers().forEach(player -> {
                Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

                if (profile.getStatus() == PlayerStatus.PLAYING) {
                    profile.setBalance(profile.getBalance() + 3);
                }
            });
        }, 0L, 20 * 3L);

        new GameTimeTask().runTaskTimer(Bunkers.getPlugin(), 20L, 20L);
        Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> new KothTask().runTaskTimer(Bunkers.getPlugin(), 20L, 20L), 20 * 60 * 10);

    }

    public void setWon(Team team) {
        Bunkers.getPlugin().getGameManager().setWinnerTeam(team);

        for (Location l : TeamListener.locs) {
            l.getChunk().load(true);
            Bukkit.getLogger().info("[Bunkers] Successfully removed a block at " + l);
            l.getBlock().setType(Material.AIR);
        }

        for (Entity current : Bukkit.getWorld("world").getEntitiesByClass(Villager.class)) {
            current.getLocation().getChunk().load(true);
            Bukkit.getLogger().info("[Bunkers] Successfully despawned a villager at " + current.getLocation());
            current.remove();
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe " + team.getColor() + team.getName() + " Team &ehas won the &9Bunkers&e!"));

        Bunkers.getPlugin().getGameManager().getWinnerTeam().getMembers().forEach(player -> {
            Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
            profile.setGamesWon(profile.getGamesWon() + 1);
        });

        startEndTimer();
    }
    public void startEndTimer() {
        Bunkers.getPlugin().getGameManager().setStatus(GameStatus.ENDING);
        new BukkitRunnable() {
            @SneakyThrows
            @Override
            public void run() {
                if (endtimer[0] <= 0) {
                    Bukkit.dispatchCommand(getConsoleSender(), "stop");
                    cancel();
                } else {
                    if (endtimer[0] % 5 == 0) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe server will close in &9" + endtimer[0] + " seconds&e."));
                    }
                }

                endtimer[0]--;
            }
        }.runTaskTimerAsynchronously(Bunkers.getPlugin(), 20L, 20L);
    }
}