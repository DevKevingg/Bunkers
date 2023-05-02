package me.devkevin.bunkers;

import me.devkevin.bunkers.abilities.AbilityHandler;
import me.devkevin.bunkers.entity.EntityManager;
import me.devkevin.bunkers.game.GameManager;
import me.devkevin.bunkers.game.status.GameStatus;
import me.devkevin.bunkers.information.InformationManager;
import me.devkevin.bunkers.koth.Koth;
import me.devkevin.bunkers.listeners.DeathMessageListener;
import me.devkevin.bunkers.listeners.PlayerListeners;
import me.devkevin.bunkers.listeners.StrengthFixListener;
import me.devkevin.bunkers.profiles.Profile;
import me.devkevin.bunkers.profiles.ProfileManager;
import me.devkevin.bunkers.profiles.status.PlayerStatus;
import me.devkevin.bunkers.protocol.ProtocolListener;
import me.devkevin.bunkers.pvpclass.utils.ArmorClassManager;
import me.devkevin.bunkers.pvpclass.utils.bard.EffectRestorer;
import me.devkevin.bunkers.scoreboard.Aether;
import me.devkevin.bunkers.scoreboard.sidebars.BunkersSidebar;
import me.devkevin.bunkers.spectator.Spectator;
import me.devkevin.bunkers.team.Team;
import me.devkevin.bunkers.team.TeamManager;
import me.devkevin.bunkers.timer.TimerManager;
import me.devkevin.bunkers.utils.WorldEditUtils;
import me.devkevin.bunkers.utils.YamlDoc;
import me.devkevin.bunkers.utils.command.CommandRegistrer;
import me.devkevin.bunkers.utils.menu.MenuListener;
import me.devkevin.bunkers.wand.WandManager;
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
