package me.devkevin.bunkers.utils;

import me.devkevin.bunkers.Bunkers;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.io.File;

@UtilityClass
public final class WorldEditUtils {

    private static EditSession editSession;
    private static com.sk89q.worldedit.world.World worldEditWorld;

    public static void primeWorldEditApi() {
        if (editSession != null) {
            return;
        }

        EditSessionFactory esFactory = WorldEdit.getInstance().getEditSessionFactory();

        worldEditWorld = new BukkitWorld(Bukkit.getWorld("world"));
        editSession = esFactory.getEditSession(worldEditWorld, Integer.MAX_VALUE);
    }
    public static CuboidClipboard paste(Vector pasteAt) throws Exception {
        primeWorldEditApi();

        CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(getSchematicFile());

        // systems like the ArenaGrid assume that pastes will 'begin' directly at the Vector
        // provided. to ensure we can do this, we manually clear any offset (distance from
        // corner of schematic to player) to ensure our pastes aren't dependant on the
        // location of the player when copied
        clipboard.paste(editSession, pasteAt, true);

        return clipboard;
    }

    public File getSchematicFile() {
        return new File(Bunkers.getPlugin().getDataFolder(), "default" + ".schematic");
    }

}