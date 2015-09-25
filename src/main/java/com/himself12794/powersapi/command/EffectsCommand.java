package com.himself12794.powersapi.command;

import java.util.List;

import net.minecraft.command.ICommand;

import com.google.common.collect.Lists;

public class EffectsCommand extends BaseCommand {
	
	private final List aliases = Lists.newArrayList( "effects", "eff", "e" );
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
