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

public abstract class BaseCommand implements ICommand {

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		
		String prefix = "  " + getName() + " ";
		
		StringBuilder value = new StringBuilder();
		
		int i = 1;
		
		for (ISubCommand com : getSubCommands()) {
			value.append( prefix );
			value.append( com.getUsage() );
			if (i != getSubCommands().size()) value.append( "\n" );
			i++;
		}

		return value.toString();
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {

		if (args.length == 0) {
			throw new CommandException(
					StatCollector.translateToLocal( "command.invalid" ) );
		} else if (args.length > 0) {
			
			ISubCommand theCommand = getSubCommandByArgs(args);
			
			if (theCommand != null) theCommand.execute( sender, dropFirstString(args) );
			else throw new CommandException("command.invalid");
			
		}

	}
	
    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] dropFirstString(String[] input) {
    	
        String[] astring1 = new String[input.length - 1];
        System.arraycopy(input, 1, astring1, 0, input.length - 1);
        return astring1;
    }
    
    private ISubCommand getSubCommandByArgs(String[] args) {
		ISubCommand theCommand = null;
		
		for (ISubCommand command : getSubCommands()) {
			if (command.getNames() == null) continue;
			for (Object name : command.getNames()) {
				if (name.equals( args[0] )) { 
					theCommand = command;
				}
			}
			
			if (theCommand != null) break;
			
		}
		
		return theCommand;
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
		
		boolean is = args.length > 0;
		if (!is) return false;
		ISubCommand command = getSubCommandByArgs(args);
		
		is &= command != null ? command.isUsernameIndex( dropFirstString(args), index - 1 ) : false;
		
		
		return is;
	}
	
	public abstract List<ISubCommand> getSubCommands();
	
	

}
