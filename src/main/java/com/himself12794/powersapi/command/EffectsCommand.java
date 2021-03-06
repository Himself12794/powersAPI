package com.himself12794.powersapi.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.ICommand;

public class EffectsCommand extends BaseCommand {
	
	private final List<String> aliases = Lists.newArrayList( "effects", "eff", "e" );
	private final List<ICommand> subCommands = Lists.newArrayList();
	
	public EffectsCommand() {
		
		subCommands.add( new EffectGet() );
		subCommands.add( new EffectSet() );
	}
	
	@Override
	public List<ICommand> getSubCommands() {
		return subCommands;
	}

	@Override
	public String getName() {
		return "effects";
	}

	@Override
	public List getAliases() {
		return aliases;
	}

}
