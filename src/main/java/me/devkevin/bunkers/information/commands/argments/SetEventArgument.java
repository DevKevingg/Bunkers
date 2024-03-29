package me.devkevin.bunkers.information.commands.argments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.game.GameManager;
import me.devkevin.bunkers.utils.JavaUtils;
import me.devkevin.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetEventArgument extends CommandArgument {
    public SetEventArgument() {
        super("setgameevent", null, "setevent");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <true/false>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            if (!JavaUtils.isBoolean(args[1])) {
                player.sendMessage(ChatColor.RED + "You must introduce true/false.");
                return true;
            }

            GameManager manager = Bunkers.getPlugin().getGameManager();
            manager.setEvent(Boolean.parseBoolean(args[1]));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThis is now an event match. &7&o(You can check them by /information show)"));
        }
        return true;
    }
}
