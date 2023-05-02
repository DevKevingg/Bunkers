package me.devkevin.bunkers.utils.command;

import me.devkevin.bunkers.Bunkers;
import me.devkevin.bunkers.information.commands.EnderpearlCommand;
import me.devkevin.bunkers.information.commands.InformationCommand;
import me.devkevin.bunkers.commands.PayCommand;
import me.devkevin.bunkers.team.commands.TeamCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CommandRegistrer {
    private CommandMap commandMap;

    public CommandRegistrer() {
        Bunkers.getPlugin().getCommand("pay").setExecutor(new PayCommand());
        registerCommand(new TeamCommand(), Bunkers.getPlugin(), false);
        registerCommand(new InformationCommand(), Bunkers.getPlugin(), true);
        registerCommand(new EnderpearlCommand(), Bunkers.getPlugin(), true);
    }

    public CommandMap getCommandMap() {
        if (commandMap != null) {
            return commandMap;
        }

        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);

            commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return commandMap;
    }

    public PluginCommand getCommand(String name, Plugin owner) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            command = constructor.newInstance(name, owner);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return command;
    }

    public void registerCommand(ExecutableCommand executableCommand, Plugin owner, boolean requiresPermission) {
        PluginCommand command = getCommand(executableCommand.getName(), owner);

        command.setPermissionMessage(ChatColor.RED + "You do not have permission to execute this command.");

        if (requiresPermission) {
            command.setPermission((owner.getName() + ".command." + executableCommand.getName()).toLowerCase());
        }

        if (executableCommand.getDescription() != null) {
            command.setDescription(executableCommand.getDescription());
        }

        command.setAliases(Arrays.asList(executableCommand.getAliases()));

        command.setExecutor(executableCommand);
        command.setTabCompleter(executableCommand);

        if (!getCommandMap().register(executableCommand.getName(), command)) {
            command.unregister(getCommandMap());
            getCommandMap().register(executableCommand.getName(), command);
        }
    }
}
