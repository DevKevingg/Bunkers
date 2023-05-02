package me.devkevin.bunkers.information;

import me.devkevin.bunkers.Bunkers;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.bunkers.game.status.GameStatus;
import me.devkevin.bunkers.utils.Cuboid;
import me.devkevin.bunkers.utils.LocationUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@Getter
public class Information {
    @Setter private String lobbyLocation, kothFirstLocation, kothSecondLocation, motd1, motd2;
    @Setter private String serverName;
    private GameStatus status1 = Bunkers.getPlugin().getGameManager().getStatus(), status2 = Bunkers.getPlugin().getGameManager().getStatus();
    private int redDTR1 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.RED).getDtr1(), redDTR2 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.RED).getDtr2();
    private int yellowDTR1 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.YELLOW).getDtr1(), yellowDTR2 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.YELLOW).getDtr2();
    private int blueDTR1 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.BLUE).getDtr1(), blueDTR2 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.BLUE).getDtr2();
    private int greenDTR1 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.GREEN).getDtr1(), greenDTR2 = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.GREEN).getDtr2();
    private int online1 = 0, online2 = 0;
    @Setter private int minPlayers = 5;
    @Setter private String address = "none";
    public static int i = Bunkers.getPlugin().getConfig().getInt("NUMBER");

    public Information() {
        load();
    }


    public void load() {
        Document document = (Document) Bunkers.getPlugin().getInformationCollection().find(Filters.eq("_id", "Information")).first();

        if (document == null) return;

        lobbyLocation = document.getString("lobbyLocation");
        serverName = document.getString("serverName");
        kothFirstLocation = document.getString("kothFirstLocation");
        kothSecondLocation = document.getString("kothSecondLocation");
        minPlayers = document.getInteger("minPlayers");
        address = document.getString("address");

    }

    public void save() {
        Document document = new Document("_id", "Information");

        document.put("lobbyLocation", lobbyLocation);
        document.put("serverName", serverName);
        document.put("kothFirstLocation", kothFirstLocation);
        document.put("kothSecondLocation", kothSecondLocation);
        document.put("minPlayers", minPlayers);
        document.put("address", address);
        if (i == 1) {
            document.put("motd1", motd1);
            document.put("status1", status1.toString());
            document.put("greenDTR1", greenDTR1);
            document.put("redDTR1", redDTR1);
            document.put("yellowDTR1", yellowDTR1);
            document.put("blueDTR1", blueDTR1);
            document.put("online1", online1);

        } else if (i == 2) {
            document.put("motd2", motd2);
            document.put("status2", status2.toString());
            document.put("greenDTR2", greenDTR2);
            document.put("redDTR2", redDTR2);
            document.put("yellowDTR2", yellowDTR2);
            document.put("blueDTR2", blueDTR2);
            document.put("online2", online2);
        }
        Bson filter = Filters.eq("_id", "Information");
        FindIterable iterable = Bunkers.getPlugin().getInformationCollection().find(filter);

        if (iterable.first() == null) {
            Bunkers.getPlugin().getInformationCollection().insertOne(document);
        } else {
            Bunkers.getPlugin().getInformationCollection().replaceOne(filter, document);
        }

        Bunkers.getPlugin().getInformationManager().setInformation(this);
    }

    public Cuboid getKothCuboid() {
        return new Cuboid(LocationUtils.getLocation(kothFirstLocation), LocationUtils.getLocation(kothSecondLocation));
    }

    public Location getKothCenter() {
        return getKothCuboid().getCenter();
    }

    public void setMotd1(String newMotd) {
        this.motd1 = newMotd;
        save();
    }
    public void setMotd2(String newMotd) {
        this.motd2 = newMotd;
        save();
    }

    public void setBlueDTR1(int newValue) {
        this.blueDTR1 = newValue;
        save();
    }
    public void setStatus1(GameStatus newValue) {
        this.status1 = newValue;
        save();
    }
    public void setStatus2(GameStatus newValue) {
        this.status2 = newValue;
        save();
    }
    public void setRedDTR1(int newValue) {
        this.redDTR1 = newValue;
        save();
    }
    public void setGreenDTR1(int newValue) {
        this.greenDTR1 = newValue;
        save();
    }

    public void setOnline1(int newValue) {
        this.online1 = newValue;
        save();
    }
    public void setOnline2(int newValue) {
        this.online2 = newValue;
        save();
    }

    public void setYellowDTR1(int newValue) {
        this.yellowDTR1 = newValue;
        save();
    }

    public void setBlueDTR2(int newValue) {
        this.blueDTR2 = newValue;
        save();
    }
    public void setRedDTR2(int newValue) {
        this.redDTR2 = newValue;
        save();
    }
    public void setGreenDTR2(int newValue) {
        this.greenDTR2 = newValue;
        save();
    }
    public void setYellowDTR2(int newValue) {
        this.yellowDTR2 = newValue;
        save();
    }
}
