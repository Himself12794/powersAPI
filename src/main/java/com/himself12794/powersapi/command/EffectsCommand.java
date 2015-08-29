package com.himself12794.powersapi.command;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.DataWrapperP;
import com.himself12794.powersapi.util.UsefulMethods;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EffectsCommand extends BaseCommand {
	
	private final List aliases = Lists.newArrayList( "effects", "eff", "e" );
	private final List<ICommand> subCommands = Lists.newArrayList();
	
	public EffectsCommand() {
		
		subCommands.add( new EffectGet() );
		subCommands.add( new EffectSet() );
	}
	
	@Override
	public List<ICommand> getSubCommands() {
		return subCommands;
	}

	@Override
	public String getName() {
		return "effects";
	}

	@Override
	public List getAliases() {
		return aliases;
	}

}
