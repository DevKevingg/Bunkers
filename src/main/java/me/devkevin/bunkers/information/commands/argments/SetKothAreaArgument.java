package me.devkevin.bunkers.information.commands.argments;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.information.Information;
import me.devkevin.bunkers.profiles.Profile;
import me.devkevin.bunkers.utils.LocationUtils;
import me.devkevin.bunkers.utils.command.CommandArgument;
import me.devkevin.bunkers.wand.Wand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetKothAreaArgument extends CommandArgument {
    public SetKothAreaArgument() {
        super("setkotharea", null, "area");
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

            Wand wand = Bunkers.getPlugin().getWandManager().getWand(player);
            Information information = Bunkers.getPlugin().getInformationManager().getInformation();

            if (wand == null || wand.getFirstLocation() == null || wand.getSecondLocation() == null) {
                player.sendMessage(ChatColor.RED + "You must have wand locations.");
                return true;
            }

            information.setKothFirstLocation(LocationUtils.getString(wand.getFirstLocation()));
            information.setKothSecondLocation(LocationUtils.getString(wand.getSecondLocation()));
            information.save();

            Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
            profile.getFirstPillar().removePillar();
            profile.setFirstPillar(null);

            profile.getSecondPillar().removePillar();
            profile.setSecondPillar(null);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the koth coordinates. &7&o(You can check them by /information show)"));
        }
        return true;
    }
}
