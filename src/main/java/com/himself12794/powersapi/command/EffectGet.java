package com.himself12794.powersapi.command;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.storage.EffectsWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

public class EffectGet implements ICommand {

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		if (args.length == 0) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				EntityPlayer player = (EntityPlayer) sender
						.getCommandSenderEntity();


				sender.addChatMessage( new ChatComponentText( getPowerEffectsAsString(player) ) );

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

	private String getPowerEffectsAsString(EntityPlayer entity) {

		EffectsWrapper wrapper = EffectsWrapper.get( entity );
		Collection powers = wrapper.getNonHiddenEffects();

		StringBuilder value = new StringBuilder( "You have: " );
		int iterCount = 1;

		for (Object power : powers) {

			value.append( ((PowerEffect)power).getUnlocalizedName() );
			if (iterCount != powers.size()) value.append( ", " );
			iterCount++;

		}

		return value.toString();
	}

	@Override
	public int compareTo(Object o) {

		return 0;
	}

	@Override
	public String getName() {

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
