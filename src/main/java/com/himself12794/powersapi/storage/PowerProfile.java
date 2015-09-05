package com.himself12794.powersapi.storage;

import java.util.Set;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.Power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Contains statistics and information for a specific power a player has used. 
 * This allows specific powers to store information about their use to players.
 * Additionally, this can be used to store custom information about spell use.
 * 
 * @author Himself12794
 *
 */
public class PowerProfile {
	
	static final String ADDITIONAL_DATA = "additionalData";
	static final String POWER_NAME = "powerName";
	static final String POWER_MODIFIER = "powerModifier";
	static final String COOLDOWN_REMAINING = "cooldownRemaining";
	static final String USES = "uses";
	
	public final EntityLivingBase theEntity;
	public final Power thePower;
	/**Used to store custom information about a power's uses. Think NBT tag for ItemStack*/
	public final NBTTagCompound powerData;
	/**Modifier that is passed when the power is used*/
	public float useModifier = 1.0F;
	public int cooldownRemaining = 0;
	public int uses = 0;
	
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
	
	public void triggerCooldown() {
		cooldownRemaining = thePower.getCooldown();
	}
	
	public int getUses() {
		return uses;
	}
	
	public void addUse() {
		uses++;
	}
	
	public NBTTagCompound getAsNBTTagCompound() {
		
		NBTTagCompound result = new NBTTagCompound();
		
		result.setInteger( POWER_NAME, thePower.getId() );
		result.setFloat( POWER_MODIFIER, useModifier );
		result.setInteger( USES, uses );
		result.setInteger( COOLDOWN_REMAINING, cooldownRemaining );
		result.setTag( ADDITIONAL_DATA, powerData );
		
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
			
			if (thePower != null && theEntity != null) {
				profile = new PowerProfile(theEntity, thePower, powerData);
				profile.cooldownRemaining = cooldownRemaining;
				profile.uses = uses;
				profile.useModifier = useModifier;
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
