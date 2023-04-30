package com.aurapvp.bunkers.team.commands.arguments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.utils.LocationUtils;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import com.aurapvp.bunkers.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnLocationArgument extends CommandArgument {
    public SetSpawnLocationArgument() {
        super("setspawn", null, "setspawnlocation");
        setRequiresPermission(true);
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

            Team team = Bunkers.getPlugin().getTeamManager().getByName(args[1]);

            if (team == null) {
                player.sendMessage(ChatColor.RED + "That team does not exist.");
                return true;
            }

            team.setSpawnLocation(LocationUtils.getString(player.getLocation().add(0, .5, 0)));
            team.save();

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the " + team.getColor() + team.getName() + " &eteam."));
        }
        return true;
    }
}
