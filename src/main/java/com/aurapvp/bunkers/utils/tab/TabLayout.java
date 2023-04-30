package com.aurapvp.bunkers.utils.tab;

import com.aurapvp.bunkers.BunkersAPI;
import com.aurapvp.bunkers.utils.PlayerVersion;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class TabLayout {

    private static Map<String, TabLayout> tabLayouts = new HashMap<>();
    private static List<String> emptyStrings = new ArrayList<>();

    private String[] zeroValue = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
    private String[] zeroValue18 = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };

    @Getter
    private String[] tabNames;

    private int WIDTH = 3;
    private int HEIGHT = 20;
    private int[] tabPings;

    @Getter
    private boolean is18;

    private TabLayout(boolean is18) {
        this(is18, false);
    }

    private TabLayout(boolean is18, boolean fill) {
        this.is18 = is18;
        this.tabNames = (is18 ? zeroValue18.clone() : zeroValue.clone());
        this.tabPings = (is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT]);

        if(fill) {
            for(int i = 0; i < tabNames.length; ++i) {
                tabNames[i] = generateEmpty();
                tabPings[i] = 0;
            }
        }

        Arrays.sort(tabNames);
    }

    public void forceSet(int pos, String name) {
        tabNames[pos] = ChatColor.translateAlternateColorCodes('&',name);
        tabPings[pos] = 0;
    }

    public void forceSet(int x, int y, String name) {
        int pos = is18 ? (y + x * HEIGHT) : (x + y * WIDTH);
        tabNames[pos] = ChatColor.translateAlternateColorCodes('&',name);
        tabPings[pos] = 0;
    }

    public void set(int x, int y, String name, int ping) {
        if(!validate(x, y, true)) {
            return;
        }

        int pos = is18 ? (y + x * HEIGHT) : (x + y * WIDTH);
        tabNames[pos] = ChatColor.translateAlternateColorCodes('&', name);
        tabPings[pos] = ping;
    }

    public void set(int x, int y, String name) {
       set(x, y, name, 0);
    }

    public void set(int x, int y, Player player) {
       set(x, y, player.getName(), ((CraftPlayer) player).getHandle().ping);
    }

    public String getStringAt(int x, int y) {
        validate(x, y);
        int pos = is18 ? (y + x * HEIGHT) : (x + y * WIDTH);
        return tabNames[pos];
    }

    public int getPingAt(int x, int y) {
        validate(x, y);
        int pos = is18 ? (y + x * HEIGHT) : (x + y * WIDTH);
        return tabPings[pos];
    }

    public boolean validate(int x, int y, boolean silent) {
        if(x >= WIDTH) {
            if(!silent) {
                throw new IllegalArgumentException("x >= WIDTH (" + WIDTH + ")");
            }

            return false;
        } else {
            if(y < HEIGHT) {
                return true;
            }

            if(!silent) {
                throw new IllegalArgumentException("y >= HEIGHT (" + HEIGHT + ")");
            }

            return false;
        }
    }

    public boolean validate(int x, int y) {
        return validate(x, y, false);
    }

    public static String generateEmpty() {
        String colorChars = "abcdefghijpqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < 8; ++i) {
            builder.append('§').append(colorChars.charAt(new Random().nextInt(colorChars.length())));
        }

        String toReturn = builder.toString();

        if(emptyStrings.contains(toReturn)) {
            return generateEmpty();
        }

        emptyStrings.add(toReturn);
        return toReturn;
    }

    public void reset() {
        tabNames = (is18 ? zeroValue18.clone() : zeroValue.clone());
        tabPings = (is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT]);
    }

    public static TabLayout create(Player player) {
        if(tabLayouts.containsKey(player.getName())) {
            TabLayout layout = tabLayouts.get(player.getName());
            layout.reset();
            return layout;
        }

        tabLayouts.put(player.getName(), new TabLayout(BunkersAPI.getPlayerVersion(player) == PlayerVersion.v1_8));

        return tabLayouts.get(player.getName());
    }

    public static void remove(Player player) {
        tabLayouts.remove(player.getName());
    }

    public static TabLayout createEmpty(Player player) {
        return BunkersAPI.getPlayerVersion(player) == PlayerVersion.v1_8 ? new TabLayout(true, true) : new TabLayout(false, true);
    }
}
