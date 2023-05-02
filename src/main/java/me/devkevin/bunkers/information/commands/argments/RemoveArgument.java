package me.devkevin.bunkers.information.commands.argments;

import me.devkevin.bunkers.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 11/05/2021 / 8:08 PM
 * Bunkers / me.redis.bunkers.information.commands.argments
 */
public class RemoveArgument extends CommandArgument {
	public RemoveArgument() {
		super("remove", null, "rem");
	}

	@Override public String getUsage(String label) {
		return ChatColor.RED + "/" + label + " " + getName() + " <player>";
	}

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		if (args.length != 2) {
			player.sendMessage(getUsage(label));
			return true;
		}
		Player target = Bukkit.getPlayer(args[1]);

		return true;
	}
}
