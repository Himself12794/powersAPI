package com.himself12794.powersapi.storage;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.UsefulMethods;

/**
 * Contains statistics and information for a specific power a player has used. 
 * This allows specific powers to store information about their use to players.
 * This also holds the cooldown timers to allow convenient modification. Each
 * player has a power profile for any power they've used, even if they don't
 * currently know a power.
 * 
 * @author Himself12794
 *
 */
public class PowerProfile {
	
	private static final String ADDITIONAL_DATA = "additionalData";
	private static final String POWER_NAME = "powerName";
	private static final String POWER_MODIFIER = "powerModifier";
	private static final String COOLDOWN_REMAINING = "cooldownRemaining";
	private static final String FUNCTIONAL_STATE = "functionalState";
	private static final String USES = "uses";
	private static final String LEVEL = "level";
	
	public final EntityLivingBase theEntity;
	public final Power thePower;
	/**Used to store custom information about a power's uses. Think NBT tag for ItemStack*/
	public final NBTTagCompound powerData;
	private int functionalState;
	/**Modifier that is passed when the power is used*/
	public float useModifier = 1.0F;
	public int level = 1;
	public int cooldownRemaining;
	public int uses;
	
	PowerProfile(EntityLivingBase player, Power power, NBTTagCompound data) {
		theEntity = player;
		thePower = power;
		
		if (data == null) {
			powerData = new NBTTagCompound();
		} else {
			powerData = data;
		}
		
	}
	
	/**
	 * Resets the data for the NBTTagCompound
	 */
	public void resetPowerData() {
		
		Set removeableKeys = Sets.newConcurrentHashSet( powerData.getKeySet() );
		
		synchronized(powerData) {
			for (Object tag : removeableKeys) {
				powerData.removeTag( (String) tag );
			}
		}
		
	}
	
	/**
	 * Convenience shortcut for getPowerData().getBoolean()
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean( String key ) {
		return powerData.getBoolean( key );
	}
	
	/**
	 * Convenience shortcut for getPowerData().setBoolean()
	 * 
	 * @param key
	 * @return
	 */
	public void setBoolean( String key, boolean value ) {
		powerData.setBoolean( key, value );
	}
	
	public int getState() {
		return functionalState;
	}
	
	public void setState(int value, boolean doOnStateChanged) {
		if (value <= thePower.getMaxFunctionalState(this)) {
			int prevState = functionalState;
			functionalState = value;
			if (doOnStateChanged) thePower.onStateChanged( theEntity.worldObj, theEntity, prevState, functionalState );
		} 
	}
	
	/**
	 * Cycles the power state to the next in line. 
	 * @param doOnStateChange whether to call {@link Power#onStateChanged(net.minecraft.world.World, EntityLivingBase, int, int)}
	 */
	public void cycleState(boolean doOnStateChange) {
		int prevState = functionalState;
		
		if (functionalState < thePower.getMaxFunctionalState(this)) {
			functionalState++;
		} else {
			functionalState = 0;
		}
		
		if (doOnStateChange)
			thePower.onStateChanged( theEntity.worldObj, theEntity, prevState, functionalState );
	}
	
	public void triggerCooldown() {
		
		if (!UsefulMethods.isCreativeModePlayerOrNull( theEntity )) {
			addUse();
			cooldownRemaining = thePower.getCooldown(this);
		} 
	}
	
	public int getUses() {
		return uses;
	}
	
	/**
	 * Only increments if player is not in creative mode.
	 */
	public void addUse() {
		uses++;
		if (thePower.shouldLevelUp( this )) levelUp();
	}
	
	public void levelUp() {
		if (level < thePower.getMaxLevel( this )) {
			level++;
			if (theEntity.worldObj.isRemote) {
				theEntity.addChatMessage( new ChatComponentText( thePower.getDisplayName() + " increased to level " + level + "!" ) );
			}
		}
	}
	
	public void resetUses() {
		uses = 0;
	}
	
	public void update() {
		thePower.onKnowledgeTick( this );
	}
	
	public NBTTagCompound getAsNBTTagCompound() {
		
		NBTTagCompound result = new NBTTagCompound();
		
		result.setInteger( POWER_NAME, thePower.getId() );
		result.setFloat( POWER_MODIFIER, useModifier );
		result.setInteger( USES, uses );
		result.setInteger( LEVEL, level );
		result.setInteger( COOLDOWN_REMAINING, cooldownRemaining );
		result.setTag( ADDITIONAL_DATA, powerData );
		result.setInteger( FUNCTIONAL_STATE, functionalState );
		
		return result;
		
	}
	
	public String getName() {
		return thePower.getDisplayName( this );
	}
	
	public static PowerProfile getFromNBT(EntityLivingBase theEntity, NBTTagCompound compound) {

		PowerProfile profile = null;
		
		if (compound != null) {
			
			Power thePower = Power.lookupPowerById( compound.getInteger( POWER_NAME ) );
			NBTTagCompound powerData = compound.getCompoundTag( ADDITIONAL_DATA );
			float useModifier = compound.getFloat( POWER_MODIFIER );
			int uses = compound.getInteger( USES );
			int level = compound.getInteger( LEVEL );
			int cooldownRemaining = compound.getInteger( COOLDOWN_REMAINING );
			int functionalState = compound.getInteger( FUNCTIONAL_STATE );
			
			if (thePower != null && theEntity != null) {
				profile = new PowerProfile(theEntity, thePower, powerData);
				profile.cooldownRemaining = cooldownRemaining;
				profile.uses = uses;
				profile.level = level == 0 ? 1 : level;
				profile.useModifier = useModifier;
				profile.functionalState = functionalState;
			}
		}
		
		return profile;
	}
	
	@Override
	public String toString() {
		
		String result = "PowerProfile[Power:" + thePower.getSimpleName() +
				",Entity:" + theEntity.getName() + ",UseModifier:" + useModifier +
				",CooldownRemaining:" + cooldownRemaining + ",Uses:" + uses +
				",Level:" + level +	",CustomDataTag:" + powerData;
		
		return result;
		
	}
	
}
