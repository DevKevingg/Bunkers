package com.aurapvp.bunkers.information.commands.argments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import com.aurapvp.bunkers.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndGameArgument extends CommandArgument {
	public EndGameArgument() {
		super("end", null, "endgame", "forceend");
	}

	private Bunkers bunkers = Bunkers.getPlugin();

	@Override
	public String getUsage(String label) {
		return ChatColor.RED + "/" + label;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			Information information = Bunkers.getPlugin().getInformationManager().getInformation();
			Team team = new Team("White");
			bunkers.getGameManager().setWon(team);
			player.sendMessage(ChatColor.GREEN + "Successfully force ended the game!");

			return true;
		}
		return false;
	}
}
