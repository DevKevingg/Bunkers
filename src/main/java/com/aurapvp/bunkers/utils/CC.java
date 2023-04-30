package com.aurapvp.bunkers.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CC {

    public static String BLUE = ChatColor.BLUE.toString();
    public static String AQUA = ChatColor.AQUA.toString();
    public static String YELLOW = ChatColor.YELLOW.toString();
    public static String RED = ChatColor.RED.toString();
    public static String GRAY = ChatColor.GRAY.toString();
    public static String GOLD = ChatColor.GOLD.toString();
    public static String GREEN = ChatColor.GREEN.toString();
    public static String WHITE = ChatColor.WHITE.toString();
    public static String BLACK = ChatColor.BLACK.toString();
    public static String BOLD = ChatColor.BOLD.toString();
    public static String ITALIC = ChatColor.ITALIC.toString();
    public static String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
    public static String RESET = ChatColor.RESET.toString();
    public static String MAGIC = ChatColor.MAGIC.toString();
    public static String OBFUSCATED = MAGIC;
    public static String B = BOLD;
    public static String M = MAGIC;
    public static String O = MAGIC;
    public static String I = ITALIC;
    public static String S = STRIKETHROUGH;
    public static String R = RESET;
    public static String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static String DARK_RED = ChatColor.DARK_RED.toString();
    public static String D_BLUE = DARK_BLUE;
    public static String D_AQUA = DARK_AQUA;
    public static String D_GRAY = DARK_GRAY;
    public static String D_GREEN = DARK_GREEN;
    public static String D_PURPLE = DARK_PURPLE;
    public static String D_RED = DARK_RED;
    public static String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static String L_PURPLE = LIGHT_PURPLE;

    public static String ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
    public static String ARROW_RIGHT = StringEscapeUtils.unescapeJava("\u25B6");
    public static String ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
    public static String ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");

    private CC() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static String strip(String in) {
        return ChatColor.stripColor(translate(in));
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translateLines(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return toReturn;
    }

    public static String chat(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }


    public static List<String> list(List<String> s){
        List<String> strings = new ArrayList<>();
        s.forEach(str -> strings.add(ChatColor.translateAlternateColorCodes('&', str)));
        return strings;
    }

    public static byte getByChatColor(ChatColor color) {
        switch (color) {
            case DARK_RED:
            case RED:
                return (short) 14;
            case BLUE:
            case DARK_BLUE:
                return (short) 11;
            case AQUA:
                return (short) 3;
            case BLACK:
                return (short) 15;
            case DARK_AQUA:
                return (short) 9;
            case DARK_GRAY:
                return (short) 7;
            case DARK_GREEN:
                return (short) 13;
            case DARK_PURPLE:
                return (short) 10;
            case GOLD:
                return (short) 1;
            case GRAY:
                return (short) 8;
            case LIGHT_PURPLE:
                return (short) 6;
            case WHITE:
                return (short) 0;
            case YELLOW:
                return (short) 4;
            case GREEN:
                return (short) 5;
        }

        return (short) 0;
    }

    public static String getNameByChatColor(ChatColor color) {
        switch (color) {
            case DARK_RED:
                return "Dark Red";
            case RED:
                return "Red";
            case BLUE:
                return "Blue";
            case DARK_BLUE:
                return "Dark Blue";
            case AQUA:
                return "Aqua";
            case BLACK:
                return "Black";
            case DARK_AQUA:
                return "Cyan";
            case DARK_GRAY:
                return "Dark Gray";
            case DARK_GREEN:
                return "Dark Green";
            case DARK_PURPLE:
                return "Purple";
            case GOLD:
                return "Orange";
            case GRAY:
                return "Gray";
            case LIGHT_PURPLE:
                return "Pink";
            case WHITE:
                return "White";
            case YELLOW:
                return "Yellow";
            case GREEN:
                return "Green";
        }

        return "White";
    }

}