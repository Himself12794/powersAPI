package com.himself12794.powersapi.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;


public interface ISubCommand {
	
	String getUsage();
	
	List getNames();
	
	void execute(ICommandSender sender, String[] args) throws CommandException;
	
}
