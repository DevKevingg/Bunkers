package com.aurapvp.bunkers.menu;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class InventoryUtil {

    public static void changeTitle(Player player, String title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        int windowId = entityPlayer.activeContainer.windowId;
        int type = 0; // Cofre
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}");
        int slots = player.getOpenInventory().getTopInventory().getSize();

        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowId, title, chatTitle, slots);
        PlayerConnection connection = entityPlayer.playerConnection;
        connection.sendPacket(packet);

        player.updateInventory();
    }
}