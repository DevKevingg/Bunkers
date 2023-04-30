package com.aurapvp.bunkers.team.commands.arguments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import com.aurapvp.bunkers.team.Team;
import com.aurapvp.bunkers.utils.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugArgument extends CommandArgument {
    public DebugArgument() {
        super("debug", null, "debugteam");
        setRequiresPermission(true);
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <team>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(getUsage(label));
                return true;
            }

            for (String current : Bunkers.getPlugin().getTeamManager().getTeams().keySet()) {
                Team team = Bunkers.getPlugin().getTeamManager().getByName(current);
                player.sendMessage(CC.chat(team.getColor() + team.getName() + "'s Debugged Info"));
                player.sendMessage(CC.chat("&7Ability Loc: " + LocationUtils.getLocation(team.getAbilityShop()).getBlockX() + ", " + LocationUtils.getLocation(team.getAbilityShop()).getBlockZ()));
                player.sendMessage(CC.chat("&7Build Loc: " + LocationUtils.getLocation(team.getBuildShop()).getBlockX() + ", " + LocationUtils.getLocation(team.getBuildShop()).getBlockZ()));
                player.sendMessage(CC.chat("&7Combat Loc: " + LocationUtils.getLocation(team.getCombatShop()).getBlockX() + ", " + LocationUtils.getLocation(team.getCombatShop()).getBlockZ()));
                player.sendMessage(CC.chat("&7Sell Loc: " + LocationUtils.getLocation(team.getSellShop()).getBlockX() + ", " + LocationUtils.getLocation(team.getSellShop()).getBlockZ()));
                player.sendMessage(CC.chat("&7Enchant Loc: " + LocationUtils.getLocation(team.getEnchantShop()).getBlockX() + ", " + LocationUtils.getLocation(team.getEnchantShop()).getBlockZ()));
                player.sendMessage(CC.chat("&7: " + LocationUtils.getLocation(team.getEnchantShop()).getBlockX() + ", " + LocationUtils.getLocation(team.getEnchantShop()).getBlockZ()));
            }

        }
        return true;
    }
}
