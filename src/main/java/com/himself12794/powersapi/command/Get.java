package com.himself12794.powersapi.command;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;


public class Get implements ISubCommand {

	@Override
	public String getUsage() {
		return "g(et) <power> ";
	}

	@Override
	public List getNames() {
		return Lists.newArrayList( "get", "g" );
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		
		System.out.println(Arrays.toString( args ));
		if (args.length == 0) {

			StringBuilder powers = new StringBuilder();

			int powerCount = Power.getPowerCount();
			int count = 1;

			for (Power power : Power.getPowers().values()) {
				powers.append( power.getSimpleName() );
				if (powerCount != count) powers.append( ", " );
			}

			sender.addChatMessage( new ChatComponentText( powers.toString() ) );

		} else if (args.length == 1) {

			if (Power.lookupPower( "power." + args[0] ) != null) {
				sender.addChatMessage( new ChatComponentText( Power
						.lookupPower( "power." + args[0] ).getInfo( null ) ) );
			} else {
				sender.addChatMessage( new ChatComponentTranslation(
						"command.power.notfound", args[0] ) );
			}
			
		}
	}

}
