package me.devkevin.bunkers.protocol;

import me.devkevin.bunkers.Bunkers;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;

public class ProtocolListener {
    public ProtocolListener() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(Bunkers.getPlugin(), PacketType.Play.Server.CUSTOM_PAYLOAD, PacketType.Play.Client.CUSTOM_PAYLOAD) {
//            @Override public void onPacketReceiving(PacketEvent event) {
//                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a" + event.getPacketType().name() + "&7(Receive):"));
//                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &7- &e" + event.getPacket().getStrings().readSafely(0)));
//                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &7- &e" + event.getPacket().getIntegers().readSafely(0)));
//                Bukkit.broadcastMessage("");
//                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4Byte Arrays:"));
//                int counter = 0;
//                for (byte[] byt : event.getPacket().getByteArrays().getValues()) {
//
//                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &7- &e" + byt[counter]));
//                    counter++;
//                }
//            }
//
//            @Override public void onPacketSending(PacketEvent event) {
//                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a" + event.getPacketType().name() + "&7(Send):"));
//                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &7- &e" + event.getPacket().getStrings().readSafely(0)));
//                Bukkit.broadcastMessage("");
//                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4Byte Arrays:"));
//                int counter = 0;
//                for (byte[] byt : event.getPacket().getByteArrays().getValues()) {
//
//                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', " &7- &e" + byt[counter]));
//                    counter++;
//                }
//            }
        });
    }
}
