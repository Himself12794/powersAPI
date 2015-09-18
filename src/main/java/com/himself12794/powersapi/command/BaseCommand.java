package com.himself12794.powersapi.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;

import com.himself12794.powersapi.ModConfig;
import com.himself12794.powersapi.util.UsefulMethods;

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
		
		for (ICommand com : getSubCommands()) {
			value.append( prefix );
			value.append( com.getCommandUsage(sender) );
			if (i != getSubCommands().size() && !UsefulMethods.nullOrEmptyString(com.getCommandUsage( sender )) ) {
				value.append( "\n" );
			}
			i++;
		}

		return value.toString();
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length == 0) {
			throw new CommandException(
					StatCollector.translateToLocal( "command.invalid" ) );
		} else if (args.length > 0) {
			
			if (ModConfig.modCommandsEnabled) {
				
				ICommand theCommand = getSubCommandByArgs(args);
				
				if (theCommand != null) theCommand.execute( sender, dropFirstString(args) );
				else throw new CommandException("command.invalid");
				
			} else {
				throw new CommandException( "Commands have been disabled in the config." );
			}
			
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
		
		boolean is = args.length > 0;
		if (!is) return false;
		ICommand command = getSubCommandByArgs(args);
		
		is &= command != null ? command.isUsernameIndex( dropFirstString(args), index - 1 ) : false;
		
		
		return is;
	}
	
    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] dropFirstString(String[] input) {
    	
        String[] astring1 = new String[input.length - 1];
        System.arraycopy(input, 1, astring1, 0, input.length - 1);
        return astring1;
    }
    
    private ICommand getSubCommandByArgs(String[] args) {
		ICommand theCommand = null;
		
		labelTop:
		
		for (ICommand command : getSubCommands()) {
			
			if (command.getAliases() == null) continue;
			
			for (Object name : command.getAliases()) {
				if (name.equals( args[0] )) { 
					theCommand = command;
					break labelTop;
				}
			}
			
		}
		
		return theCommand;
    }
	
	public abstract List<ICommand> getSubCommands();
	
	

}
