package com.himself12794.powersapi.storage;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.Power;

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
	
	static final String ADDITIONAL_DATA = "additionalData";
	static final String POWER_NAME = "powerName";
	static final String POWER_MODIFIER = "powerModifier";
	static final String COOLDOWN_REMAINING = "cooldownRemaining";
	static final String FUNCTIONAL_STATE = "functionalState";
	static final String USES = "uses";
	
	public final EntityLivingBase theEntity;
	public final Power thePower;
	/**Used to store custom information about a power's uses. Think NBT tag for ItemStack*/
	public final NBTTagCompound powerData;
	/**Modifier that is passed when the power is used*/
	public float useModifier = 1.0F;
	public int level = 1;
	public int functionalState;
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
	
	public void setState(int value) {
		if (value <= thePower.getMaxFunctionalState(this)) {
			int prevState = functionalState;
			functionalState = value;
			thePower.onStateChanged( theEntity.worldObj, theEntity, prevState, functionalState );
		} 
	}
	
	/**
	 * Cycles the power state. 
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
		
		if (theEntity instanceof EntityPlayer) {
			if (!((EntityPlayer)theEntity).capabilities.isCreativeMode) 
				cooldownRemaining = thePower.getCooldown(this);
		} else {
			cooldownRemaining = thePower.getCooldown(this);
		}
	}
	
	public int getUses() {
		return uses;
	}
	
	/**
	 * Only increments if 
	 */
	public void addUse() {
		
		if( theEntity instanceof EntityPlayer && ((EntityPlayer) theEntity).capabilities.isCreativeMode)
			return;
		
		uses++;
	}
	
	public void resetUses() {
		uses = 0;
	}
	
	public NBTTagCompound getAsNBTTagCompound() {
		
		NBTTagCompound result = new NBTTagCompound();
		
		result.setInteger( POWER_NAME, thePower.getId() );
		result.setFloat( POWER_MODIFIER, useModifier );
		result.setInteger( USES, uses );
		result.setInteger( COOLDOWN_REMAINING, cooldownRemaining );
		result.setTag( ADDITIONAL_DATA, powerData );
		result.setInteger( FUNCTIONAL_STATE, functionalState );
		
		return result;
		
	}
	
	public static PowerProfile getFromNBT(EntityLivingBase theEntity, NBTTagCompound compound) {

		PowerProfile profile = null;
		
		if (compound != null) {
			
			Power thePower = Power.lookupPowerById( compound.getInteger( POWER_NAME ) );
			NBTTagCompound powerData = compound.getCompoundTag( ADDITIONAL_DATA );
			float useModifier = compound.getFloat( POWER_MODIFIER );
			int uses = compound.getInteger( USES );
			int cooldownRemaining = compound.getInteger( COOLDOWN_REMAINING );
			int functionalState = compound.getInteger( FUNCTIONAL_STATE );
			
			if (thePower != null && theEntity != null) {
				profile = new PowerProfile(theEntity, thePower, powerData);
				profile.cooldownRemaining = cooldownRemaining;
				profile.uses = uses;
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
				",CustomDataTag:" + powerData;
		
		return result;
		
	}
	
}
