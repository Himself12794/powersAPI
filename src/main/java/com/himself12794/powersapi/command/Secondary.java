package com.himself12794.powersapi.command;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.client.SetPowerClient;
import com.himself12794.powersapi.network.client.SyncNBTDataClient;
import com.himself12794.powersapi.power.EnumPowerSelection;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.storage.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

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
		
		List al = Lists.newArrayList();
		al.add( "s" );
		al.add( "sec" );
		al.add( "second" );
		
		return al;
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length == 0){
			
			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
				
				Power power = PowersWrapper.get( (EntityLivingBase) sender.getCommandSenderEntity() ).getSecondaryPower();
				
				if (power != null) {
					String name = power.getDisplayName();
					sender.addChatMessage( new ChatComponentText(name) );
				}
			}
			
		} else if (args.length == 1) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				PowersWrapper entity = PowersWrapper.get( (EntityPlayer) sender.getCommandSenderEntity() );
				PowersAPI.logger.info( entity );
				Power commandPower = Power.lookupPower( "power." + args[0] );

				if (commandPower != null) {

					if (entity.knowsPower( commandPower )) {
						entity.setSecondaryPower( commandPower );
						PowersAPI.network.sendTo( new SetPowerClient(commandPower, EnumPowerSelection.SECONDARY), (EntityPlayerMP) entity.theEntity );
						PowersAPI.logger.info( entity );
						//PowersAPI.network.sendTo( new SyncNBTDataClient( entity.getModEntityData() ), (EntityPlayerMP) entity.player );
					} else if (((EntityPlayer)entity.theEntity).capabilities.isCreativeMode) {
						entity.setSecondaryPower( commandPower );
						PowersAPI.network.sendTo( new SetPowerClient(commandPower, EnumPowerSelection.SECONDARY), (EntityPlayerMP) entity.theEntity );
						PowersAPI.logger.info( entity );
						//PowersAPI.network.sendTo( new SyncNBTDataClient( entity.getModEntityData() ), (EntityPlayerMP) entity.player );
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
					DataWrapperP entity = DataWrapperP.get( player );
					Power commandPower = Power.lookupPower( "power." + args[0] );

					if (commandPower != null) {

						if (entity.player.capabilities.isCreativeMode) {
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

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,
			BlockPos pos) {

		// TODO Auto-generated method stub
		return null;
	}

}
