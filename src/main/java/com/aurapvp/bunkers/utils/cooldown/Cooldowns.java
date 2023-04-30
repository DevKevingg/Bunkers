package com.aurapvp.bunkers.utils.cooldown;

import lombok.Data;
import com.aurapvp.bunkers.utils.cooldown.form.DurationFormatter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Cooldowns {

    private Map<UUID, Long> cooldowns = new HashMap<>();

    public void applyCooldown(Player player, long cooldown) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldown * 1000);
    }
    public boolean onCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && (cooldowns.get(player.getUniqueId()) >= System.currentTimeMillis());
    }
    public void removeCooldown(Player player) {

        cooldowns.remove(player.getUniqueId());
    }
    public String getRemaining(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return DurationFormatter.getRemaining(l, true);

    }

}
