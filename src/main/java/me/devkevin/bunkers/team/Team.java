package me.devkevin.bunkers.team;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.profiles.Profile;
import me.devkevin.bunkers.profiles.status.PlayerStatus;
import me.devkevin.bunkers.utils.Cuboid;
import me.devkevin.bunkers.utils.LocationUtils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Team {
    private String name;
    @Setter private String firstCorner, secondCorner, spawnLocation;
    private String combatShop, enchantShop, sellShop, buildShop, abilityShop;
    @Setter private int dtr1, dtr2;
    private List<UUID> members;

    public Team(String name) {
        this.name = name;

        members = new ArrayList<>();
        load();
    }

    public void setSellShop(String newLoc) {
        this.sellShop = newLoc;
        save();
    }

    public void setBuildShop(String newLoc) {
        this.buildShop = newLoc;
        save();
    }

    public void setAbilityShop(String newLoc) {
        this.abilityShop = newLoc;
        save();
    }

    public void setEnchantShop(String newLoc) {
        this.enchantShop = newLoc;
        save();
    }

    public void setCombatShop(String newLoc) {
        this.combatShop = newLoc;
        save();
    }

    public void load() {
        Document document = (Document) Bunkers.getPlugin().getTeamsCollection().find(Filters.eq("name", name)).first();

        if (document == null) return;

        name = document.getString("name");
        firstCorner = document.getString("firstCorner");
        secondCorner = document.getString("secondCorner");
        spawnLocation = document.getString("spawnLocation");
        combatShop = document.getString("combatShop");
        enchantShop = document.getString("enchantShop");
        sellShop = document.getString("sellShop");
        buildShop = document.getString("buildShop");
        abilityShop = document.getString("abilityShop");
    }

    public void save() {
        Document document = new Document("_id", name);

        document.put("name", name);
        document.put("firstCorner", firstCorner);
        document.put("secondCorner", secondCorner);
        document.put("spawnLocation", spawnLocation);
        document.put("combatShop", combatShop);
        document.put("enchantShop", enchantShop);
        document.put("sellShop", sellShop);
        document.put("buildShop", buildShop);
        document.put("abilityShop", abilityShop);

        Bson filter = Filters.eq("name", name);
        FindIterable iterable = Bunkers.getPlugin().getTeamsCollection().find(filter);

        if (iterable.first() == null) {
            Bunkers.getPlugin().getTeamsCollection().insertOne(document);
        } else {
            Bunkers.getPlugin().getTeamsCollection().replaceOne(filter, document);
        }
    }

    public ChatColor getColor() {
        return ChatColor.valueOf(name.toUpperCase());
    }

    public Cuboid getCuboid() {
        return new Cuboid(LocationUtils.getLocation(firstCorner), LocationUtils.getLocation(secondCorner));
    }

    public Location getCenter() {
        return getCuboid().getCenter();
    }

    public List<String> getMembersNames() {
        List<String> toReturn = new ArrayList<>();
        for (UUID uniqueId : members) {
            Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(uniqueId);
            Player player = Bukkit.getPlayer(uniqueId);

            if (player == null) {
                toReturn.add(ChatColor.GRAY + profile.getName());
            } else if (profile.getStatus() == PlayerStatus.PLAYING) {
                toReturn.add(getColor() + profile.getName());
            } else if (profile.getStatus() == PlayerStatus.SPECTATOR) {
                toReturn.add(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + profile.getName());
            } else if (profile.getStatus() == PlayerStatus.LOBBY) {
                toReturn.add(getColor() + profile.getName());
            }
        }


        return toReturn;
    }

    public boolean isDone() {
        return firstCorner != null & secondCorner != null && spawnLocation != null;
    }
}
