package com.aurapvp.bunkers.information.commands.argments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.utils.JavaUtils;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMinimumPlayersArgument extends CommandArgument {
    public SetMinimumPlayersArgument() {
        super("setminplayers", null, "minplayers");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <number>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            if (!JavaUtils.isInteger(args[1])) {
                player.sendMessage(ChatColor.RED + "You must introduce a number");
                return true;
            }

            Information information = Bunkers.getPlugin().getInformationManager().getInformation();
            information.setMinPlayers(Integer.parseInt(args[1]));
            information.save();
            if (information.getMinPlayers() == Bukkit.getOnlinePlayers().size()) {
                Bunkers.getPlugin().getGameManager().startCooldown();
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the minimum players to start. &7&o(You can check them by /information show)"));
        }
        return true;
    }
}
