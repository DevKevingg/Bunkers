package com.aurapvp.bunkers.information.commands.argments;

import com.aurapvp.bunkers.Bunkers;
import com.aurapvp.bunkers.game.status.GameStatus;
import com.aurapvp.bunkers.information.Information;
import com.aurapvp.bunkers.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartGameArgument extends CommandArgument {
    public StartGameArgument() {
        super("start", null, "startgame", "forcestart");
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

            if (bunkers.getGameManager().canBePlayed()) {
                if (bunkers.getInformationManager().getInformation().getMinPlayers() <= Bukkit.getOnlinePlayers().size() && bunkers.getGameManager().getStatus() == GameStatus.WAITING) {
                    bunkers.getGameManager().startCooldown();
                    player.sendMessage(ChatColor.GREEN + "Successfully force started the game!");
                }

                return true;
            }else{
                player.sendMessage(ChatColor.RED + "Not enough players to start the game!");
            }
        }
        return false;
    }
}
