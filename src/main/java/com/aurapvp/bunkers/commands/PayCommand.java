package com.aurapvp.bunkers.commands;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.profiles.Profile;
import com.aurapvp.bunkers.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/05/2021 / 10:12 PM
 * Bunkers / me.redis.bunkers.commands
 */
public class PayCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

		if (commandSender instanceof Player) {
			if (strings.length < 1) {
				commandSender.sendMessage(CC.chat("&c/pay <player> <amount>"));
				return true;
			}
			Player player = Bukkit.getPlayer(strings[0]);
			int toSend = Integer.parseInt(strings[1]);
			Player sender = (Player) commandSender;

			Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(sender);
			Profile profile2 = Bunkers.getPlugin().getProfileManager().getProfile(player);
			if (toSend > profile.getBalance()) {
				sender.sendMessage(CC.chat("&cYou don't have enough money."));
				return true;
			}

			profile2.setBalance(profile2.getBalance() + toSend);
			profile.setBalance(profile.getBalance() - toSend);

		}

		return false;
	}
}
