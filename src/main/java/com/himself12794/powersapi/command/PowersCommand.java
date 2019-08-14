package com.himself12794.powersapi.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.ICommand;


public class PowersCommand extends BaseCommand {

	private final List<String> aliases = Lists.newArrayList();
	private final List<ICommand> subCommands = Lists.newArrayList();

	public PowersCommand() {

		aliases.add( "p" );
		aliases.add( "pwr" );
		aliases.add( "power" );
		aliases.add( "powers" );
		
		subCommands.add( new GetPowers() );
		subCommands.add( new ListPowers() );
		subCommands.add( new Primary() );
		subCommands.add( new Secondary() );
		subCommands.add( new TeachPowers() );

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
	public List<ICommand> getSubCommands() {
		return subCommands;
	}

}
