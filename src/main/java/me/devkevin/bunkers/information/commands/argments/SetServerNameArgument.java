package me.devkevin.bunkers.information.commands.argments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.information.Information;
import me.devkevin.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetServerNameArgument extends CommandArgument {
    public SetServerNameArgument() {
        super("setservername", null, "sername");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <name>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Information information = Bunkers.getPlugin().getInformationManager().getInformation();
            information.setServerName(args[1]);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the server name. &7&o(You can check them by /information show)"));
            information.save();
        }
        return true;
    }
}
