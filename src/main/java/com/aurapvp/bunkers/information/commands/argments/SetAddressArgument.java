package com.aurapvp.bunkers.information.commands.argments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAddressArgument extends CommandArgument {
    public SetAddressArgument() {
        super("setaddress", null, "setip");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <address>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Information information = Bunkers.getPlugin().getInformationManager().getInformation();
            information.setAddress(args[1]);
            information.save();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have the server's address. &7&o(You can check it by /information show)"));
        }
        return true;
    }
}
