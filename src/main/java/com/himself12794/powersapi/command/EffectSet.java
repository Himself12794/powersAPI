package com.himself12794.powersapi.command;

import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.storage.EffectsWrapper;
import com.himself12794.powersapi.util.UsefulMethods;


public class EffectSet implements ICommand {

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "<get:set> <effect> (set)<player> [duration]";
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		if (args.length == 0) {
			throw new CommandException(StatCollector.translateToLocal( "command.invalid" ));
		} else if (args.length >= 1) {

			if(UsefulMethods.isCreativeModePlayerOrNull( sender.getCommandSenderEntity() )) {
			
				EntityPlayer affector = (EntityPlayer) sender.getCommandSenderEntity();
				EntityPlayer affected = null;
				boolean usesId = false;
				int duration = -1;
				int id = -1;
				String name = "";
				
				try {
					id = Integer.valueOf( args[0] );
					usesId = true;
				} catch (NumberFormatException nfe) {
					name = args[0];
				}
				
				try {
					if (args.length >= 2) {
						duration = Integer.valueOf( args[1] ); 
					}
				} catch (NumberFormatException nfe) {
					throw new CommandException("Duration must be a number");
				}
				
				try {
					if (args.length >= 3) {
						affected = sender.getEntityWorld().getPlayerEntityByUUID( UUID.fromString( args[2] ) );
					} else if (args.length == 2 ) {
						affected = affector;
					}
					
				} catch (IllegalArgumentException iae) {
					affected = sender.getEntityWorld().getPlayerEntityByName( args[2] );
				}
				
				PowerEffect effect = usesId ? PowerEffect.getEffectById( id ) : PowerEffect.getPowerEffect( name );
	
				if (effect != null) {
					if (!effect.getType().isHidden()) {
						if (affected != null) {
							EffectsWrapper wrapper = EffectsWrapper.get( affected );
							if (duration == 0) {
								wrapper.removePowerEffectQuietly( effect );
							} else {
								wrapper.addPowerEffect( effect, duration, affector, null );
							}
						} else {
							throw new CommandException( StatCollector.translateToLocal( "commands.generic.player.notFound" ) );
							
						}
					} else {
						throw new CommandException( "That power effect cannot be set" );
					}
				} else {
					throw new CommandException( "That is not a valid power effect" );
				}
			
			}

		} 
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return args.length >= 2 && index == 1;
	}

	@Override
	public int compareTo(Object o) {

		return 0;
	}

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public List getAliases() {
		return Lists.newArrayList( "set", "s" );
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender,
			String[] args, BlockPos pos) {
		return null;
	}
	
}