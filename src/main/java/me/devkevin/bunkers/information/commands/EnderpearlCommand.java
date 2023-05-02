package me.devkevin.bunkers.information.commands;

import me.devkevin.bunkers.information.commands.argments.RemoveArgument;
import me.devkevin.bunkers.utils.command.ExecutableCommand;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 11/05/2021 / 8:07 PM
 * Bunkers / me.redis.bunkers.information.commands
 */
public class EnderpearlCommand extends ExecutableCommand {
	public EnderpearlCommand() {
		super("enderpearl", null, "ep", "epearl");

		addArgument(new RemoveArgument());
	}
}
