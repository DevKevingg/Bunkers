package me.devkevin.bunkers.wand;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Getter
public class Wand {
    @Setter private Location firstLocation;
    @Setter private Location secondLocation;

    public Wand(Player player) {
        if (Bunkers.getPlugin().getWandManager().getWand(player) != null) {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_HOE).setDisplayName("&4Area wand").create());
        } else {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_HOE).setDisplayName("&4Area wand").create());
            Bunkers.getPlugin().getWandManager().getWands().put(player, this);
        }
    }
}
