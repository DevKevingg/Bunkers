package me.devkevin.bunkers.information.commands;

import me.devkevin.bunkers.information.commands.argments.*;
import me.devkevin.bunkers.utils.command.ExecutableCommand;

public class InformationCommand extends ExecutableCommand {
    public InformationCommand() {
        super("information", null, "info", "bunkers");

        addArgument(new StartGameArgument());
        addArgument(new ShowArgument());
        addArgument(new SetSpawnArgument());
        addArgument(new SetKothAreaArgument());
        addArgument(new SetMinimumPlayersArgument());
        addArgument(new SetServerNameArgument());
        addArgument(new SetAddressArgument());
        addArgument(new SetEventArgument());
        addArgument(new SetEventNameArgument());
        addArgument(new EndGameArgument());
    }
}
