package me.devkevin.bunkers.team.commands.arguments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.team.Team;
import me.devkevin.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 10/05/2021 / 5:46 PM
 * Bunkers / me.redis.bunkers.team.commands.arguments
 */
public class ClearAllVillagersArgument extends CommandArgument {
	public ClearAllVillagersArgument() {
		super("clearallvillagers", null, "clearallvill", "cavillagers", "civillies");
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

			for (Map.Entry<String, Team> current : Bunkers.getPlugin().getTeamManager().getTeams().entrySet()) {
				current.getValue().setAbilityShop(null);
				current.getValue().setBuildShop(null);
				current.getValue().setCombatShop(null);
				current.getValue().setEnchantShop(null);
				current.getValue().setSellShop(null);
			}
		}
		return true;
	}
}
