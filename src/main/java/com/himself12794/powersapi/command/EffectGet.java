package com.himself12794.powersapi.command;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

public class EffectGet implements ICommand {

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		if (args.length == 0) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				EntityPlayer player = (EntityPlayer) sender
						.getCommandSenderEntity();
				DataWrapperP wrapper = DataWrapperP.get( player );
				Set<PowerEffect> powers = wrapper.powerEffectsData
						.getNonHiddenEffects();

				StringBuilder value = new StringBuilder( "You have: " );
				int iterCount = 1;

				for (PowerEffect power : powers) {

					value.append( power.getUnlocalizedName() );
					if (iterCount != powers.size()) value.append( ", " );
					iterCount++;

				}

				sender.addChatMessage( new ChatComponentText( value.toString() ) );

			}

		} else if (args.length >= 1) {

			World world = sender.getEntityWorld();
			EntityPlayer targetPlayer = null;

			try {
				targetPlayer = world.getPlayerEntityByUUID( UUID
						.fromString( args[0] ) );
			} catch (IllegalArgumentException iae) {
				targetPlayer = world.getPlayerEntityByName( args[0] );
			}

			if (targetPlayer != null) {
				if (sender.getCommandSenderEntity() == targetPlayer
						|| UsefulMethods.canTeachPower( sender
								.getCommandSenderEntity() )) {
					sender.addChatMessage( new ChatComponentText(
							getPowerEffectsAsString( targetPlayer ) ) );
				}
			} else {
				throw new CommandException(
						StatCollector
								.translateToLocal( "commands.generic.player.notFound" ) );
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {

		return args.length >= 1 && index == 0;
	}

	private String getPowerEffectsAsString(EntityLivingBase entity) {

		DataWrapper wrapper = DataWrapper.get( entity );
		Set<PowerEffect> powers = wrapper.powerEffectsData
				.getNonHiddenEffects();

		StringBuilder value = new StringBuilder( entity.getName() + ": " );
		int iterCount = 1;

		for (PowerEffect power : powers) {

			value.append( power.getUnlocalizedName() );
			if (iterCount != powers.size()) value.append( ", " );
			iterCount++;

		}

		return value.toString();
	}

	@Override
	public int compareTo(Object o) {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {

		// TODO Auto-generated method stub
		return "add";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {

		return null;
	}

	@Override
	public List getAliases() {

		return Lists.newArrayList( "get", "g" );
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
