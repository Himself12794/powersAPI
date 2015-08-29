package com.himself12794.powersapi.command;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;


public class Primary implements ISubCommand {

	@Override
	public String getUsage() {
		return "p(rimary) <power> [user] ";
	}

	@Override
	public List getNames() {
		
		List al = Lists.newArrayList();
		al.add( "p" );
		al.add( "pr" );
		al.add( "prim" );
		
		return al;
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length == 0){
			
			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
				
				Power power = DataWrapper.get( (EntityLivingBase) sender.getCommandSenderEntity() ).getPrimaryPower();
				
				if (power != null) {
					String name = power.getDisplayName();
					sender.addChatMessage( new ChatComponentText(name) );
				}
			}
			
		} else if (args.length == 1) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				DataWrapperP entity = DataWrapperP
						.get( (EntityPlayer) sender
								.getCommandSenderEntity() );
				Power commandPower = Power.lookupPower( "power." + args[0] );

				if (commandPower != null) {

					if (entity.knowsPower( commandPower )) {
						entity.setPrimaryPower( commandPower );
					} else if (entity.player.capabilities.isCreativeMode) {
						entity.setPrimaryPower( commandPower );
					} else {
						throw new CommandException( StatCollector.translateToLocalFormatted( "command.power.notknown", commandPower.getDisplayName() ) );
					}
					
				} else {
					throw new CommandException(
							StatCollector.translateToLocalFormatted(
									"command.power.notfound", args[1] ) );

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
							entity.setPrimaryPower( commandPower );
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
