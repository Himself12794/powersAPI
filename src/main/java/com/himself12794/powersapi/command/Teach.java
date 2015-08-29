package com.himself12794.powersapi.command;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;


public class Teach implements ISubCommand {

	@Override
	public String getUsage() {
		return "t(each) <power> [user] ";
	}

	@Override
	public List getNames() {
		return Lists.newArrayList( "teach", "t" );
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length == 1) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				DataWrapperP entity = DataWrapperP
						.get( (EntityPlayer) sender
								.getCommandSenderEntity() );
				Power commandPower = Power.lookupPower( "power." + args[0] );

				if (commandPower != null) {

					if (entity.knowsPower( commandPower )) {
						entity.teachPower( commandPower );
					} else if (entity.player.capabilities.isCreativeMode) {
						entity.teachPower( commandPower );
					} else {
						throw new CommandException( StatCollector.translateToLocal( "commands.generic.permission" ) );
					}
					
				} else {
					throw new CommandException(
							StatCollector.translateToLocalFormatted(
									"command.power.notfound", args[0] ) );

				}
				
			}
			
			
		} else if (args.length == 2) {
				
			if (UsefulMethods.canTeachPower(sender.getCommandSenderEntity())) {
				
				World world = sender.getEntityWorld();
				
				EntityPlayer player = null;
				
				try {
					player = world.getPlayerEntityByUUID( UUID.fromString( args[1] ) );
				} catch (IllegalArgumentException iae) {
					player = world.getPlayerEntityByName( args[1] );
				}
				
				if (player != null) {
					DataWrapperP entity = DataWrapperP.get( player );
					Power commandPower = Power.lookupPower( "power." + args[0] );

					if (commandPower != null) {

						if (entity.player.capabilities.isCreativeMode) {
							entity.teachPower( commandPower );
						} else {
							throw new CommandException( StatCollector.translateToLocalFormatted( "command.power.notknown", commandPower.getDisplayName() ) );
						}
						
					} else {
						throw new CommandException(
								StatCollector.translateToLocalFormatted(
										"command.power.notfound", args[0] ) );

					}
				} else {
					throw new CommandException(StatCollector.translateToLocal( "commands.generic.player.notFound" ));
				}
				
			}
		}
	}

}
