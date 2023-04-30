package com.aurapvp.bunkers.team.commands.arguments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class HomeArgument extends CommandArgument {
    public HomeArgument() {
        super("home", null, "hq", "hh", "base");
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

            if (Bunkers.getPlugin().getTeamManager().getByLocation(player.getLocation()) != null) {
                Bunkers.getPlugin().getTimerManager().getTeleportTimer().setCooldown(player, player.getUniqueId(), TimeUnit.SECONDS.toMillis(20), true);
            } else {
                Bunkers.getPlugin().getTimerManager().getTeleportTimer().setCooldown(player, player.getUniqueId());
            }
        }
        return true;
    }
}
