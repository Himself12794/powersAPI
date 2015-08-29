package com.himself12794.powersapi.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import com.google.common.collect.Lists;


public class PowerCommand extends BaseCommand {

	private final List aliases = Lists.newArrayList();
	private final List<ISubCommand> subCommands = Lists.newArrayList();

	public PowerCommand() {

		aliases.add( "p" );
		aliases.add( "pwr" );
		aliases.add( "power" );
		aliases.add( "powers" );
		
		subCommands.add( new Get() );
		subCommands.add( new ListPowers() );
		subCommands.add( new Primary() );
		subCommands.add( new Secondary() );
		subCommands.add( new Teach() );
		subCommands.add( new Effects() );

	}

	@Override
	public String getName() {
		return "powers";
	}

	@Override
	public List getAliases() {
		return aliases;
	}

	@Override
	public List<ISubCommand> getSubCommands() {
		return subCommands;
	}

}
