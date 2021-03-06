package com.himself12794.powersapi.command;

import java.util.List;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.power.Power;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;


public class GetPowers implements ICommand {

	private final List<String> aliases = Lists.newArrayList( "get", "g" );
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "get <power> ";
	}

	@Override
	public String getName() {
		return "get";
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length == 0) {

			StringBuilder powers = new StringBuilder("Available powers: ");

			int powerCount = PowersRegistry.getPowerCount();
			int count = 1;

			for (Power power : PowersRegistry.getPowers().values()) {
				powers.append( power.getSimpleName() );
				if (powerCount != count) powers.append( ", " );
				count++;
			}

			sender.addChatMessage( new ChatComponentText( powers.toString() ) );

		} else if (args.length == 1) {

			if (PowersRegistry.lookupPower( args[0] ) != null) {
				sender.addChatMessage( new ChatComponentText( PowersRegistry
						.lookupPower( args[0] ).getDescription() ) );
			} else {
				sender.addChatMessage( new ChatComponentTranslation(
						"command.power.notfound", args[0] ) );
			}
			
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public List getAliases() {
		return aliases;
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,
			BlockPos pos) {
		return null;
	}

}
