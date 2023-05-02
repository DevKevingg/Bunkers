package me.devkevin.bunkers.team.commands.arguments;

import me.devkevin.bunkers.Bunkers;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import me.devkevin.bunkers.game.status.GameStatus;
import me.devkevin.bunkers.team.Team;
import me.devkevin.bunkers.utils.CC;
import me.devkevin.bunkers.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/05/2021 / 12:43 AM
 * Bunkers / me.redis.bunkers.team.commands.arguments
 */
public class RallyArgument extends CommandArgument {
	public RallyArgument() {
		super("rally", null, "rpoint", "r", "rp");
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

			Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);

			LCWaypoint lcWaypoint = new LCWaypoint("Rally Point", player.getLocation(), Color.MAGENTA.hashCode(), true);

			for (UUID u : team.getMembers()) {
				Player currentP = Bukkit.getPlayer(u);
				if (currentP.isOnline()) {
					LunarClientAPI.getInstance().sendWaypoint(currentP, lcWaypoint);
					currentP.sendMessage(CC.chat("&3[Team]&c " + sender.getName() + " has just updated the Rally Point. This will disappear in 5 minutes."));
				}
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					for (UUID u : team.getMembers()) {
						Player currentP = Bukkit.getPlayer(u);
						if (currentP.isOnline()) {
							LunarClientAPI.getInstance().removeWaypoint(currentP, lcWaypoint);
						}
					}
				}
			}.runTaskLater(Bunkers.getPlugin(), 20 * 60 * 5);
		}
		return true;
	}
}

