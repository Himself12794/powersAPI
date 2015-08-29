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
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

// TODO option to add effects to entities
public class Effects extends BaseCommand implements ISubCommand {
	

	private final List aliases = Lists.newArrayList( "get", "g" );
	private final List<ISubCommand> subCommands = Lists.newArrayList();
	
	public Effects() {
		
		subCommands.add( new Effects.EffectGet() );
		subCommands.add( new Effects.EffectSet() );
	}

	@Override
	public List getNames() {
		return Lists.newArrayList( "effects", "e" );
	}
	
	@Override
	public String getName() {
		return "effects";
	}
	
	@Override
	public List getAliases() {
		return getNames();
	}
	
	@Override
	public List<ISubCommand> getSubCommands() {
		return subCommands;
	}
	
	public static class EffectSet implements ISubCommand {

		@Override
		public String getUsage() {
			return "<get:set> <effect> (set)<player> [duration]";
		}

		@Override
		public List getNames() {
			return Lists.newArrayList( "s", "set" );
		}



		@Override
		public void execute(ICommandSender sender, String[] args)
				throws CommandException {

			if (args.length == 0) {
				throw new CommandException(StatCollector.translateToLocal( "command.invalid" ));
			} else if (args.length >= 1) {

				if (UsefulMethods.isCreativeModePlayerOrNull( sender.getCommandSenderEntity() )) {
					
					EntityPlayer affector = (EntityPlayer) sender.getCommandSenderEntity();
					EntityPlayer affected = null;
					boolean usesId = false;
					int duration = -1;
					int id = -1;
					String name = "";
					
					try {
						id = Integer.valueOf( args[0] );
						usesId = true;
					} catch (NumberFormatException nfe) {
						name = args[0];
					}
					
					try {
						if (args.length >= 2) {
							duration = Integer.valueOf( args[1] ); 
						}
					} catch (NumberFormatException nfe) {
						throw new CommandException("Duration must be a number");
					}
					
					try {
						if (args.length >= 3) {
							affected = sender.getEntityWorld().getPlayerEntityByUUID( UUID.fromString( args[3] ) );
						} else if (args.length == 2 ) {
							affected = affector;
						}
						
					} catch (IllegalArgumentException iae) {
						affected = sender.getEntityWorld().getPlayerEntityByName( args[3] );
					}
					
					PowerEffect effect = usesId ? PowerEffect.getEffectById( id ) : PowerEffect.getPowerEffect( name );

					if (effect != null) {
						if (!effect.getType().isHidden()) {
							if (affected != null) {
								DataWrapperP wrapper = DataWrapperP.get( affected );
								if (duration == 0) {
									wrapper.removePowerEffectQuietly( effect );
								} else {
									wrapper.addPowerEffect( effect, duration, affector, null );
								}
							} else {
								throw new CommandException( StatCollector.translateToLocal( "commands.generic.player.notFound" ) );
								
							}
						} else {
							throw new CommandException( "That power effect cannot be set" );
						}
					} else {
						throw new CommandException( "That is not a valid power effect" );
					}

				} else {
					throw new CommandException( "commands.generic.permission" );
				}

			} 
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return args.length >= 2 && index == 1;
		}
		
		private String getPowerEffectsAsString(EntityLivingBase entity) {
			DataWrapper wrapper = DataWrapper.get( entity );
			Set<PowerEffect> powers = wrapper.getNonTagEffects();

			StringBuilder value = new StringBuilder(entity.getName() + ": ");
			int iterCount = 1;

			for (PowerEffect power : powers) {

				value.append( power.getUnlocalizedName() );
				if (iterCount != powers.size()) value.append( ", " );
				iterCount++;

			}
			
			return value.toString();
		}
		
	}
	
	public static class EffectGet implements ISubCommand {

		@Override
		public String getUsage() {
			return "";
		}

		@Override
		public List getNames() {
			return Lists.newArrayList( "g", "get" );
		}



		@Override
		public void execute(ICommandSender sender, String[] args)
				throws CommandException {

			if (args.length == 0) {

				if (sender.getCommandSenderEntity() instanceof EntityPlayer) {

					EntityPlayer player = (EntityPlayer) sender
							.getCommandSenderEntity();
					DataWrapperP wrapper = DataWrapperP.get( player );
					Set<PowerEffect> powers = wrapper.getNonTagEffects();

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
					throw new CommandException(StatCollector.translateToLocal( "commands.generic.player.notFound" ));
				}
			}
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return args.length >= 1 && index == 0;
		}
		
		private String getPowerEffectsAsString(EntityLivingBase entity) {
			DataWrapper wrapper = DataWrapper.get( entity );
			Set<PowerEffect> powers = wrapper.getNonTagEffects();

			StringBuilder value = new StringBuilder(entity.getName() + ": ");
			int iterCount = 1;

			for (PowerEffect power : powers) {

				value.append( power.getUnlocalizedName() );
				if (iterCount != powers.size()) value.append( ", " );
				iterCount++;

			}
			
			return value.toString();
		}
		
	}
	
	public String toString() {
		return getName();
	}

	@Override
	public String getUsage() {
		return getCommandUsage(null);
	}

}
