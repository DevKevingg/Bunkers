package me.devkevin.bunkers.information.commands.argments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.game.GameManager;
import me.devkevin.bunkers.utils.command.CommandArgument;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetEventNameArgument extends CommandArgument {
    public SetEventNameArgument() {
        super("seteventname");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <name...>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            GameManager manager = Bunkers.getPlugin().getGameManager();
            manager.setEventName(StringUtils.join(args, " ", 1, args.length));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have set the event name. &7&o(You can check them by /information show)"));
        }
        return true;
    }
}
