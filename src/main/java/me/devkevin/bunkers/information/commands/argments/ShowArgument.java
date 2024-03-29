package me.devkevin.bunkers.information.commands.argments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.information.Information;
import me.devkevin.bunkers.utils.LocationUtils;
import me.devkevin.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowArgument extends CommandArgument {
    public ShowArgument() {
        super("show", null, "who", "i", "info");
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
            Location lobby = LocationUtils.getLocation(information.getLobbyLocation());

            if (lobby == null) {
                player.sendMessage(ChatColor.RED + "You must set the spawn location.");
                return true;
            }

            if (information.getKothFirstLocation() == null || information.getKothSecondLocation() == null) {
                player.sendMessage(ChatColor.RED + "You must set the koth location.");
                return true;
            }

            Location koth = information.getKothCenter();

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&nInformation"));
            player.sendMessage("");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eSpawn location&7: &f" + lobby.getBlockX() + ", " + lobby.getBlockZ()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &eKoth location&7: &f" + koth.getBlockX() + ", " + koth.getBlockZ()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));
        }
        return true;
    }
}
