package com.himself12794.powersapi.command;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class PowerCommand implements ICommand {

	private final List aliases = Lists.newArrayList();
	private final List<ISubCommand> subCommands = Lists.newArrayList();

	public PowerCommand() {

		aliases.add( "p" );
		aliases.add( "pwr" );
		aliases.add( "power" );
		aliases.add( "powers" );
		
		subCommands.add( new Get() );
		subCommands.add( new ListPowers() );
		subCommands.add( new Primary() );
		subCommands.add( new Secondary() );
		subCommands.add( new Teach() );

	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getName() {
		return "powers";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		
		String prefix = "  powers ";
		
		StringBuilder value = new StringBuilder();
		
		int i = 1;
		
		for (ISubCommand com : subCommands) {
			value.append( prefix );
			value.append( com.getUsage() );
			if (i != subCommands.size()) value.append( "\n" );
			i++;
		}

		return value.toString();
	}

	@Override
	public List getAliases() {
		return aliases;
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		if (args.length == 0) {
			throw new CommandException(
					StatCollector.translateToLocal( "command.invalid" ) );
		} else if (args.length > 0) {
			
			ISubCommand theCommand = null;
			
			for (ISubCommand command : subCommands) {
				if (command.getNames() == null) continue;
				for (Object name : command.getNames()) {
					if (name.equals( args[0] )) { 
						theCommand = command;
					}
				}
				
				if (theCommand != null) break;
				
			}
			
			if (theCommand != null) theCommand.execute( sender, dropFirstString(args) );
			else throw new CommandException(getCommandUsage(sender));
			
		}

	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {

		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,
			BlockPos pos) {

		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return args.length >= 2 && (index == 2 || (((new ListPowers()).getNames().contains( args[0])) && index == 1));
	}
	


    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] dropFirstString(String[] input) {
    	
        String[] astring1 = new String[input.length - 1];
        System.arraycopy(input, 1, astring1, 0, input.length - 1);
        return astring1;
    }
	
	

}
