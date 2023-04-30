package com.aurapvp.bunkers.information.commands;

import com.aurapvp.bunkers.information.commands.argments.*;
import com.aurapvp.bunkers.utils.command.ExecutableCommand;

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
