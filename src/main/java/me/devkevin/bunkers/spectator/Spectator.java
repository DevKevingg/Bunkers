package me.devkevin.bunkers.spectator;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.BunkersAPI;
import lombok.Getter;
import me.devkevin.bunkers.profiles.Profile;
import me.devkevin.bunkers.profiles.status.PlayerStatus;
import me.devkevin.bunkers.utils.CC;
import me.devkevin.bunkers.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Spectator {
    @Getter public static List<Player> spectators = new ArrayList<>();

    public void setSpectator(Player player) {
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        BunkersAPI.reset(player);

        profile.setStatus(PlayerStatus.SPECTATOR);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setSaturation(20);
        player.setHealth(20);
        player.spigot().setCollidesWithEntities(false);
        spectators.add(player);

        player.getInventory().setItem(1, new ItemBuilder(Material.COMPASS).setDisplayName("&e&lTeleporter").addLore(CC.chat("&7&oClick to show a list of the online active players.")).create());
        player.getInventory().setItem(7, new ItemBuilder(Material.REDSTONE).setDisplayName("&cLeave Match").addLore(CC.chat("&7&oClick to leave the match.")).create());

        Bukkit.getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
        });
    }
}
