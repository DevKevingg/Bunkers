package me.devkevin.bunkers.utils.tab;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.BunkersAPI;
import me.devkevin.bunkers.utils.PlayerVersion;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TabManager {

    @Getter
    @Setter
    private static LayoutProvider layoutProvider;
    @Getter
    @Setter
    private static int updateInterval = 10;
    @Getter
    private static Map<String, TabManager> tabs = new ConcurrentHashMap<>();

    @Getter
    private Player player;
    @Getter
    private TabLayout initialLayout;
    @Getter
    private String lastHeader, lastFooter;
    @Getter
    private boolean initiatedTab;

    private StringBuilder removeColorCodesBuilder = new StringBuilder();
    private Map<String, GameProfile> cache = new ConcurrentHashMap<>();
    private Map<String, String> previousNames = new HashMap<>();
    private Map<String, Integer> previousPings = new HashMap<>();
    private Set<String> createdTeams = new HashSet<>();

    private String[] skinData = {
            "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=",
            "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="
    };

    public TabManager(LayoutProvider provider, int interval) {
        updateInterval = interval;

        new TabThread().start();

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> TabManager.addPlayer(event.getPlayer()), 10L);
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                TabManager.removePlayer(event.getPlayer());
                TabLayout.remove(event.getPlayer());
            }
        }, Bunkers.getPlugin());

        layoutProvider = provider;
    }

    public TabManager(Player player) {
        this.player = player;
    }

    public static void addPlayer(Player player) {
        tabs.put(player.getName(), new TabManager(player));
    }

    public static void updatePlayer(Player player) {
        if(tabs.containsKey(player.getName())) {
            tabs.get(player.getName()).update();
        }
    }

    public static void removePlayer(Player player) {
        tabs.remove(player.getName());
    }

    private void init() {
        if(!initiatedTab) {
            TabLayout layout = TabLayout.createEmpty(player);

            if (!(BunkersAPI.getPlayerVersion(player) == PlayerVersion.v1_8)) {
                Bukkit.getOnlinePlayers().forEach(player -> updateTabList(player.getName(), ((CraftPlayer) player).getProfile(), EnumPlayerInfoAction.REMOVE_PLAYER));
            }

            // Hide player for all online players
            //Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) this.player).getHandle())));

            Stream.of(layout.getTabNames()).forEach(tabNames -> {
                updateTabList(tabNames, EnumPlayerInfoAction.ADD_PLAYER);

                String teamName = tabNames.replaceAll("§", "");

                if(!createdTeams.contains(teamName)) {
                    createAndAddMember(teamName, tabNames);
                    createdTeams.add(teamName);
                }
            });

            this.initialLayout = layout;
            this.initiatedTab = true;
        }
    }

    protected void update() {
        if(layoutProvider != null) {
            TabLayout layout = layoutProvider.getLayout(player);

            if(layout == null) {
                if(initiatedTab) {
                    reset();
                }

                return;
            }

            init();

            for(int y = 0; y < 20; y++) {
                for(int x = 0; x < 3; x++) {
                    String entry = layout.getStringAt(x, y);
                    int ping = layout.getPingAt(x, y);
                    String entryName = initialLayout.getStringAt(x, y);

                    removeColorCodesBuilder.setLength(0);
                    removeColorCodesBuilder.append(entryName);

                    int j = 0;
                    for(int i = 0; i < removeColorCodesBuilder.length(); i++) {
                        if('§' != removeColorCodesBuilder.charAt(i)) {
                            removeColorCodesBuilder.setCharAt(j++, removeColorCodesBuilder.charAt(i));
                        }
                    }

                    removeColorCodesBuilder.delete(j, removeColorCodesBuilder.length());
                    String teamName = "$" + removeColorCodesBuilder.toString();

                    if(previousNames.containsKey(entryName)) {
                        if(!previousNames.get(entryName).equals(entry)) {
                            update(entryName, teamName, entry, ping);
                        } else if(previousPings.containsKey(entryName) && previousPings.get(entryName) != ping) {
                            updateTabList(entryName, EnumPlayerInfoAction.UPDATE_LATENCY);
                            previousPings.put(entryName, ping);
                        }
                    } else {
                        update(entryName, teamName, entry, ping);
                    }
                }
            }

            setHeaderAndFooter(layoutProvider.getHeader(), layoutProvider.getFooter());
        }
    }

    private void setHeaderAndFooter(String header, String footer) {
        boolean sendHeader = false;
        boolean sendFooter = false;

        header = ChatColor.translateAlternateColorCodes('&', header);
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        if(!header.equals(lastHeader)) {
            sendHeader = true;
        }

        if(!footer.equals(lastFooter)) {
            sendFooter = true;
        }

        if ((BunkersAPI.getPlayerVersion(player) != PlayerVersion.v1_7) && (sendHeader || sendFooter)) {
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

            packet.setA(IChatBaseComponent.ChatSerializer.a("{text: '" + header + "'}"));
            packet.setB(IChatBaseComponent.ChatSerializer.a("{text: '" + footer + "'}"));

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

            lastHeader = header;
            lastFooter = footer;
        }
    }

    public void reset() {
        initiatedTab = false;

        setHeaderAndFooter("", "");

        Stream.of(initialLayout.getTabNames()).forEach(names -> updateTabList(names, EnumPlayerInfoAction.REMOVE_PLAYER));
        updateTabList(player.getPlayerListName(), ((CraftPlayer) player).getHandle().getProfile(), EnumPlayerInfoAction.ADD_PLAYER);

        int[] count = {1};

        Bukkit.getOnlinePlayers().stream().filter(player -> this.player != player).forEach(player -> {
            if(count[0] < initialLayout.getTabNames().length - 1) {
                updateTabList(player.getPlayerListName(), ((CraftPlayer) player).getHandle().getProfile(), EnumPlayerInfoAction.ADD_PLAYER);
                count[0]++;
            }
        });
    }

    private void update(String entryName, String teamName, String entry, int ping) {
        String[] entryStrings = split(entry);

        String prefix = entryStrings[0];
        String suffix = entryStrings[1];

        if(!suffix.isEmpty()) {
            if(prefix.charAt(prefix.length() - 1) == '§') {
                prefix = prefix.substring(0, prefix.length() - 1);
                suffix = '§' + suffix;
            }

            String suffixPrefix = ChatColor.getLastColors(prefix).isEmpty() ? ChatColor.RESET.toString() : ChatColor.getLastColors(prefix);

            suffix = suffix.length() <= 14 ? suffixPrefix + suffix : suffixPrefix + suffix.substring(0, 14);
        }

        updateScore(teamName, prefix, suffix);
        updateTabList(entryName, EnumPlayerInfoAction.UPDATE_LATENCY);

        previousNames.put(entryName, entry);
        previousPings.put(entryName, ping);
    }

    private GameProfile getOrCreateProfile(String name, UUID id) {
        GameProfile player = cache.get(name);

        if(player == null) {
            player = new GameProfile(id, name);
            player.getProperties().put("textures", new Property("textures", skinData[0], skinData[1]));
            cache.put(name, player);
        }

        return player;
    }

    private GameProfile getOrCreateProfile(String name) {
        return getOrCreateProfile(name, new UUID(new Random().nextLong(), new Random().nextLong()));
    }

    private void sendPacketMod(Player player, EnumPlayerInfoAction action, GameProfile profile, String name) {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = minecraftServer.getWorldServer(0);
        PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(action, new EntityPlayer(minecraftServer, worldServer, new GameProfile(profile.getId(), name), playerInteractManager)));
    }

    private void sendTeamPacketMod(String name, String prefix, String suffix, Collection<String> players, int i) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        packet.setA(name);
        packet.setH(i);

        if(i == 0 || i == 2) {
            packet.setB(name);
            packet.setC(prefix);
            packet.setD(suffix);
            packet.setI(1);
        }

        if(i == 0) {
            packet.getG().addAll(players);
        }

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private void createAndAddMember(String name, String member) {
        sendTeamPacketMod("$" + name, "", "", Collections.singletonList(member), 0);
    }

    private void updateScore(String score, String prefix, String suffix) {
        sendTeamPacketMod(score, prefix, suffix, null, 2);
    }

    private void updateTabList(String name, EnumPlayerInfoAction action) {
        updateTabList(name, getOrCreateProfile(name), action);
    }

    private void updateTabList(String name, GameProfile profile, EnumPlayerInfoAction action) {
        sendPacketMod(player, action, profile, name);
    }

    private String[] split(String input) {
        return input.length() <= 16 ? new String[] { input, "" } : new String[] { input.substring(0, 16), input.substring(16, input.length()) };
    }
}
