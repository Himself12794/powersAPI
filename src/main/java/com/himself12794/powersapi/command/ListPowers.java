package com.himself12794.powersapi.command;

import java.util.List;
import java.util.Set;
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
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.DataWrapperP;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

public class ListPowers implements ICommand {

	@Override
	public String getCommandUsage(ICommandSender sender) {

		return "l(ist) [user]";
	}
	
	public String getName() {
		return "list";
	}

	@Override
	public List getAliases() {

		return Lists.newArrayList( "list", "l" );
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		if (args.length == 0) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				EntityPlayer player = (EntityPlayer) sender
						.getCommandSenderEntity();
				PowersWrapper wrapper = PowersWrapper.get( player );
				Set<Power> powers = wrapper.learnedPowers;

				StringBuilder value = new StringBuilder( "You know: " );
				int iterCount = 1;

				for (Power power : powers) {
					value.append( power.getDisplayName() );
					if (iterCount != powers.size()) value.append( ", " );
					iterCount++;

				}

				sender.addChatMessage( new ChatComponentText( value.toString() ) );

			}

		} else if (args.length == 1) {

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
							getPowersAsString( targetPlayer ) ) );
				}
			} else {
				throw new CommandException(StatCollector.translateToLocal( "commands.generic.player.notFound" ));
			}
		}
	}

	private String getPowersAsString(EntityPlayer player) {

		DataWrapperP wrapper = DataWrapperP.get( player );
		Set<Power> powers = wrapper.learnedPowers;

		StringBuilder value = new StringBuilder( player.getName() + ": " );
		int iterCount = 1;

		for (Power power : powers) {

			value.append( power.getSimpleName() );
			if (iterCount != powers.size()) value.append( ", " );
			iterCount++;

		}

		return value.toString();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return args.length >= 1 && index == 0;
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
