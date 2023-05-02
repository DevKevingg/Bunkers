package me.devkevin.bunkers.information.commands.argments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.information.Information;
import me.devkevin.bunkers.utils.command.CommandArgument;
import me.devkevin.bunkers.team.Team;
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
