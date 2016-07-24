package com.himself12794.powersapi.command;

import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.network.server.S02SetPower.Selection;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.UsefulMethods;


public class Secondary implements ICommand {

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "s(econdary) <power> [user] ";
	}
	
	public String getName() {
		return "secondary";
	}
	
	@Override
	public List getAliases() {
		
		List<String> al = Lists.newArrayList();
		al.add( "s" );
		al.add( "sec" );
		al.add( "second" );
		
		return al;
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length == 0){
			
			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
				
				Power power = PowersEntity.get( (EntityLivingBase) sender.getCommandSenderEntity() ).getSecondaryPower();
				
				if (power != null) {
					String name = power.getDisplayName();
					sender.addChatMessage( new ChatComponentText(name) );
				}
			}
			
		} else if (args.length == 1) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				PowersEntity entity = PowersEntity.get( (EntityPlayer) sender.getCommandSenderEntity() );
				Power commandPower = PowersRegistry.lookupPower( args[0] );

				if (commandPower != null) {

					if (entity.knowsPower( commandPower )) {
						entity.setSecondaryPower( commandPower );
						PowersNetwork.client().setPower( commandPower, Selection.SECONDARY, (EntityPlayerMP) entity.theEntity );
					} else if (((EntityPlayer)entity.theEntity).capabilities.isCreativeMode) {
						entity.setSecondaryPower( commandPower );
						PowersNetwork.client().setPower( commandPower, Selection.SECONDARY, (EntityPlayerMP) entity.theEntity );
					} else {
						throw new CommandException( StatCollector.translateToLocalFormatted( "command.power.notknown", commandPower.getDisplayName() ) );
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
					PowersEntity entity = PowersEntity.get( player );
					Power commandPower = PowersRegistry.lookupPower( "power." + args[0] );

					if (commandPower != null) {

						if (((EntityPlayer)entity.theEntity).capabilities.isCreativeMode) {
							entity.setSecondaryPower( commandPower );
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

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return args.length >= 2 && index == 1;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
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
