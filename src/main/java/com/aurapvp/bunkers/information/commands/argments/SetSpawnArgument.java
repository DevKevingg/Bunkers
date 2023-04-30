package com.aurapvp.bunkers.information.commands.argments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.utils.LocationUtils;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnArgument extends CommandArgument {
    public SetSpawnArgument() {
        super("setspawn", null, "setspawnlocation");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName();
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Information information = Bunkers.getPlugin().getInformationManager().getInformation();

            information.setLobbyLocation(LocationUtils.getString(player.getLocation().add(0.5, 0.5, 0.5)));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the spawn coordinates. &7&o(You can check them by /information show)"));
            information.save();
        }
        return true;
    }
}
