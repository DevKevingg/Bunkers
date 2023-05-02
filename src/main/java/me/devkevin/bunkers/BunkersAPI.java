package me.devkevin.bunkers;

import me.devkevin.bunkers.utils.EntityUtils;
import me.devkevin.bunkers.utils.PlayerVersion;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import us.myles.ViaVersion.api.Via;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 29/04/2023 @ 18:45
 * BunkersAPI / com.aurapvp.bunkers / Bunkers
 */
public class BunkersAPI {
    public static PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(Via.getAPI().getPlayerVersion(player.getUniqueId()));
    }

    private static Field SPAWN_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;

    public static void playDeathAnimation(Player player) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);
            int radius = MinecraftServer.getServer().getPlayerList().d();
            Set<Player> sentTo = new HashSet<>();

            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof Player) {
                    Player watcher = (Player) entity;
                    if (!watcher.getUniqueId().equals(player.getUniqueId())) {
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
                        ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);
                        sentTo.add(watcher);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> {
                for (Player watcher : sentTo) {
                    ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                }
            }, 40L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reset(Player player) {
        reset(player, true);
    }

    public static void reset(Player player, boolean resetHeldSlot) {
        if (!player.hasMetadata("frozen")) {
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
        }

        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.getInventory().setHeldItemSlot(4);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.spigot().setCollidesWithEntities(true);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0); // removes players arrows on body

        if (resetHeldSlot) {
            player.getInventory().setHeldItemSlot(4);
        }

        player.updateInventory();
    }

    public static PacketContainer createLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128); // entity id of 128
        lightningPacket.getIntegers().write(4, 1); // type of lightning (1)
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0D)); // x
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0D)); // y
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0D)); // z

        return lightningPacket;
    }

    public static void sendLightningPacket(Player target, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
        } catch (InvocationTargetException ignored) {
            // will never happen, ProtocolWrapper (the lib this code was from)
            // ignores this exception as well
        }
    }
}
