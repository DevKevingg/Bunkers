package me.devkevin.bunkers.team;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.team.listeners.TeamListener;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeamManager {
    @Getter public Map<String, Team> teams = new HashMap<>();

    public TeamManager() {
        Team red = new Team("Red");
        red.save();
        teams.put("Red", red);

        Team blue = new Team("Blue");
        blue.save();
        teams.put("Blue", blue);

        Team yellow = new Team("Yellow");
        yellow.save();
        teams.put("Yellow", yellow);

        Team green = new Team("Green");
        green.save();
        teams.put("Green", green);

        Bukkit.getPluginManager().registerEvents(new TeamListener(), Bunkers.getPlugin());

        for (Team team : teams.values()) {
            System.out.println("" + team.getName() + " Information");
            if (team.getBuildShop() != null) {
            }else {
                System.out.println("Error! Build shop location not set");
            }

            if (team.getAbilityShop() != null) {
            }else{
                System.out.println("Error! Ability shop location not set");
            }

            if (team.getSellShop() != null) {
            }else{
                System.out.println("Error! Sell shop location not set");
            }


            if (team.getEnchantShop() != null) {
            }else{
                System.out.println("Error! Enchant shop location not set");
            }

            if (team.getCombatShop() != null) {

            }else{
                System.out.println("Error! Combat shop location not set");
            }
        }
    }

    public Team getByName(String name) {
        return teams.get(name);
    }

    public Team getByPlayer(Player player) {
        for (Team team : teams.values()) {
            if (team.getMembers().contains(player.getUniqueId()))
                return team;
        }

        return null;
    }

    public Team getByColor(ChatColor color) {
        for (Team team : teams.values()) {
            if (team.getColor() == color) return team;
        }

        return null;
    }

    public Team getByNameNotCached(String name) {
        for (Object object : Bunkers.getPlugin().getTeamsCollection().find()) {
            Document document = (Document) object;

            if (document.get("name").equals(name)) {
                return new Team(name);
            }
        }

        return null;
    }

    public boolean canBePlayed() {
        return getByName("Red").isDone() && getByName("Blue").isDone() && getByName("Yellow").isDone() && getByName("Green").isDone();
    }

    public Team getByLocation(Location location) {
        for (Team team : teams.values()) {
            if (team.getCuboid().contains(location.getBlock())) return team;
        }

        return null;
    }
}
