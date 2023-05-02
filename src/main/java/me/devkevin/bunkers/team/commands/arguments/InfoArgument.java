package me.devkevin.bunkers.team.commands.arguments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.information.Information;
import me.devkevin.bunkers.utils.command.CommandArgument;
import me.devkevin.bunkers.game.status.GameStatus;
import me.devkevin.bunkers.team.Team;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoArgument extends CommandArgument {
    public InfoArgument() {
        super("info", null, "who", "i", "show");
        setRequiresPermission(false);
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <team>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            if (Bunkers.getPlugin().getGameManager().getStatus() != GameStatus.PLAYING) {
                player.sendMessage(ChatColor.RED + "You can't use this command right now.");
                return true;
            }

            Team team = Bunkers.getPlugin().getTeamManager().getByName(args[1]);

            if (team == null) {
                player.sendMessage(ChatColor.RED + "That team does not exist.");
                return true;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eTeam&7: " + team.getColor() + team.getName()));
            if (Information.i == 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eDeaths Until Raidable&7: &f" + team.getDtr1()));
            } else if (Information.i == 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eDeaths Until Raidable&7: &f" + team.getDtr2()));
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eLocation&7: &f" + team.getCenter().getBlockX() + ", " + team.getCenter().getBlockZ()));
            player.sendMessage("");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMembers&7: &f" + StringUtils.join(team.getMembersNames(), ", ")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        }
        return true;
    }
}
