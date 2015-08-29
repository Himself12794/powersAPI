package com.himself12794.powersapi.command;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

public class ListPowers implements ISubCommand {

	@Override
	public String getUsage() {

		return "l(ist) [user]";
	}

	@Override
	public List getNames() {

		return Lists.newArrayList( "list", "l" );
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		if (args.length == 0) {

			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

				EntityPlayer player = (EntityPlayer) sender
						.getCommandSenderEntity();
				DataWrapperP wrapper = DataWrapperP.get( player );
				Set<Power> powers = wrapper.getPowersAsSet();

				StringBuilder value = new StringBuilder( "You know: " );
				int iterCount = 1;

				for (Power power : powers) {

					value.append( power.getSimpleName() );
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
		Set<Power> powers = wrapper.getPowersAsSet();

		StringBuilder value = new StringBuilder( player.getName() + ": " );
		int iterCount = 1;

		for (Power power : powers) {

			value.append( power.getSimpleName() );
			if (iterCount != powers.size()) value.append( ", " );
			iterCount++;

		}

		return value.toString();
	}

}
