package com.aurapvp.bunkers;

import com.aurapvp.bunkers.abilities.AbilityHandler;
import com.aurapvp.bunkers.entity.EntityManager;
import com.aurapvp.bunkers.game.GameManager;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.information.InformationManager;
import com.aurapvp.bunkers.koth.Koth;
import com.aurapvp.bunkers.listeners.DeathMessageListener;
import com.aurapvp.bunkers.listeners.PlayerListeners;
import com.aurapvp.bunkers.listeners.StrengthFixListener;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.profiles.ProfileManager;
import com.aurapvp.bunkers.profiles.status.PlayerStatus;
import com.aurapvp.bunkers.protocol.ProtocolListener;
import com.aurapvp.bunkers.pvpclass.utils.ArmorClassManager;
import com.aurapvp.bunkers.pvpclass.utils.bard.EffectRestorer;
import com.aurapvp.bunkers.scoreboard.Aether;
import com.aurapvp.bunkers.scoreboard.sidebars.BunkersSidebar;
import com.aurapvp.bunkers.spectator.Spectator;
import com.aurapvp.bunkers.team.Team;
import com.aurapvp.bunkers.team.TeamManager;
import com.aurapvp.bunkers.timer.TimerManager;
import com.aurapvp.bunkers.utils.WorldEditUtils;
import com.aurapvp.bunkers.utils.YamlDoc;
import com.aurapvp.bunkers.utils.command.CommandRegistrer;
import com.aurapvp.bunkers.utils.menu.MenuListener;
import com.aurapvp.bunkers.wand.WandManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sk89q.worldedit.Vector;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Bunkers extends JavaPlugin {
    @Getter private static Bunkers plugin;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoDatabase mongoDatabase2;

    private MongoCollection informationCollection;
    private InformationManager informationManager;

    private MongoCollection teamsCollection;
    private TeamManager teamManager;

    private MongoCollection profilesCollection;
    private ProfileManager profileManager;

    private WandManager wandManager;
    private GameManager gameManager;
    private Koth koth;
    private TimerManager timerManager;
    private Spectator spectatorManager;
    private EntityManager entityManager;

    private YamlDoc abilityYML;
    private YamlDoc inventoriesYML;
    private AbilityHandler abilityHandler;
    private ArmorClassManager armorClassManager;
    private EffectRestorer effectRestorer;

    @SneakyThrows
    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.VILLAGER) {
                    entity.remove();
                }
            }

            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDayLightCycle", "false");
            world.setTime(0);
        }
        int i = getConfig().getInt("NUMBER");
        final String databaseId = getConfig().getString("DATABASE.NAME");
        String uri = "mongodb://localhost";
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient mongoClient = new MongoClient(clientURI);

        /*VoiceChatAPI.getInstance().createVoice("PublicVC");
        VoiceChatAPI.getInstance().createVoice("Red");
        VoiceChatAPI.getInstance().createVoice("Green");
        VoiceChatAPI.getInstance().createVoice("Blue");
        VoiceChatAPI.getInstance().createVoice("Yellow");*/

        mongoDatabase = mongoClient.getDatabase(databaseId);

        informationCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.INFORMATION"));
        teamsCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.TEAMS"));
        profilesCollection = mongoDatabase.getCollection(getConfig().getString("DATABASE.COLLECTIONS.PROFILES"));

        effectRestorer = new EffectRestorer(this);

        teamManager = new TeamManager();
        profileManager = new ProfileManager();
        wandManager = new WandManager();
        gameManager = new GameManager();
        gameManager.setStatus(GameStatus.WAITING);
        armorClassManager = new ArmorClassManager(this);
        informationManager = new InformationManager();

        koth = new Koth();
        timerManager = new TimerManager(this);
        spectatorManager = new Spectator();
        entityManager = new EntityManager();

        new CommandRegistrer();
        new ProtocolListener();

        inventoriesYML = new YamlDoc(getDataFolder(), "inventories.yml");
        inventoriesYML.init();
        abilityYML = new YamlDoc(getDataFolder(), "ability.yml");
        abilityYML.init();

        abilityHandler = new AbilityHandler(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new DeathMessageListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new StrengthFixListener(), this);
//        Bukkit.getPluginManager().registerEvents(new DurabilityFix(), this);

        new Aether(this, new BunkersSidebar(this));

        WorldEditUtils.paste(new Vector(0.5, 54, 0.5));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            int players = getServer().getOnlinePlayers().size();

            for (Player p : getServer().getOnlinePlayers()) {
                Profile profile = getProfileManager().getProfile(p);
                if (profile.getStatus() == PlayerStatus.SPECTATOR) {
                    players = players -1;
                }
            }

            if (getGameManager().getStatus() == GameStatus.PLAYING) {
                if (getTeamManager().getByColor(ChatColor.RED).getMembers().isEmpty()) {
                    if (getTeamManager().getByColor(ChatColor.GREEN).getMembers().isEmpty()) {
                        if (getTeamManager().getByColor(ChatColor.YELLOW).getMembers().isEmpty()) {
                            if (getTeamManager().getByColor(ChatColor.BLUE).getMembers().isEmpty()) {
                                getGameManager().startEndTimer();
                            }
                        }
                    }
                }
            }

            if (i == 1) {
                int redTeam = getTeamManager().getByColor(ChatColor.RED).getDtr1();
                int yellowTeam = getTeamManager().getByColor(ChatColor.YELLOW).getDtr1();
                int greenTeam = getTeamManager().getByColor(ChatColor.GREEN).getDtr1();
                int blueTeam = getTeamManager().getByColor(ChatColor.BLUE).getDtr1();
                getInformationManager().getInformation().setOnline1(players);

                getInformationManager().getInformation().setStatus1(getGameManager().getStatus());

                getInformationManager().getInformation().setBlueDTR1(blueTeam);
                getInformationManager().getInformation().setGreenDTR1(greenTeam);
                getInformationManager().getInformation().setYellowDTR1(yellowTeam);
                getInformationManager().getInformation().setRedDTR1(redTeam);

                getInformationManager().getInformation().save();
            } else if (i == 2) {
                int redTeam = getTeamManager().getByColor(ChatColor.RED).getDtr2();
                int yellowTeam = getTeamManager().getByColor(ChatColor.YELLOW).getDtr2();
                int greenTeam = getTeamManager().getByColor(ChatColor.GREEN).getDtr2();
                int blueTeam = getTeamManager().getByColor(ChatColor.BLUE).getDtr2();

                getInformationManager().getInformation().setStatus2(getGameManager().getStatus());

                getInformationManager().getInformation().setOnline2(players);

                getInformationManager().getInformation().setBlueDTR2(blueTeam);
                getInformationManager().getInformation().setGreenDTR2(greenTeam);
                getInformationManager().getInformation().setYellowDTR2(yellowTeam);
                getInformationManager().getInformation().setRedDTR2(redTeam);
                getInformationManager().getInformation().save();
            }

        }, 20, 20);

    }

    @Override
    public void onDisable() {
        teamManager.getTeams().values().forEach(Team::save);
        Bukkit.getServer().getWorld("world").getEntities().forEach(Entity::remove);
    }
}
