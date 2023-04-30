package com.aurapvp.bunkers.team.commands;

import com.aurapvp.bunkers.team.commands.arguments.*;
import com.aurapvp.bunkers.utils.command.ExecutableCommand;

public class TeamCommand extends ExecutableCommand {
    public TeamCommand() {
        super("team", null, "t", "f", "faction", "factions", "sq", "squad");

        addArgument(new InfoArgument());
        addArgument(new SetAreaArgument());
        addArgument(new SetSpawnLocationArgument());
        addArgument(new ChatArgument());
        addArgument(new HomeArgument());
        addArgument(new DebugArgument());
        addArgument(new RallyArgument());
        addArgument(new SetVillagerArgument());
        addArgument(new ClearAllVillagersArgument());
    }
}
