package com.aurapvp.bunkers.team.commands.arguments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.team.chat.ChatMode;
import com.aurapvp.bunkers.utils.CC;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 04/06/2021 / 2:09 AM
 * Bunkers / com.aurapvp.bunkers.team.commands.arguments
 */
public class ChatArgument extends CommandArgument {
	public ChatArgument() {
		super("chat", null, "c", "cf");
		setRequiresPermission(false);
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

			if (Bunkers.getPlugin().getGameManager().getStatus() != GameStatus.PLAYING) {
				player.sendMessage(ChatColor.RED + "You can't use this command right now.");
				return true;
			}

			Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
			if (profile.getChatMode() == ChatMode.TEAM) {
				profile.setChatMode(ChatMode.PUBLIC);
				player.sendMessage(CC.chat("&eYou are now speaking in public chat."));
			} else {
				profile.setChatMode(ChatMode.TEAM);
				player.sendMessage(CC.chat("&eYou are now speaking in team chat."));
			}

		}
		return true;
	}
}
