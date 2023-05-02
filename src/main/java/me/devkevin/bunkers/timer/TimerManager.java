package me.devkevin.bunkers.timer;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.timer.type.ArcherHandler;
import me.devkevin.bunkers.timer.type.ProtectionTimer;
import me.devkevin.bunkers.timer.type.PvpClassWarmupTimer;
import me.devkevin.bunkers.timer.type.TeleportTimer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

public class TimerManager {
    @Getter private final Bunkers plugin;

    @Getter private ProtectionTimer protectionTimer;
    @Getter private TeleportTimer teleportTimer;
    @Getter private PvpClassWarmupTimer pvpClassWarmupTimer;
    @Getter private ArcherHandler archerHandler;

    @Getter private final Set<Timer> timers = new LinkedHashSet<>();

    public TimerManager(Bunkers plugin) {
        this.plugin = plugin;

        registerTimer(teleportTimer = new TeleportTimer(plugin));
        registerTimer(protectionTimer = new ProtectionTimer());
        registerTimer(archerHandler = new ArcherHandler(plugin));
        registerTimer(pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin));
    }

    public void registerTimer(Timer timer) {
        timers.add(timer);
        if (timer instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) timer, plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        timers.remove(timer);
    }
}