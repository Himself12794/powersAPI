package com.himself12794.powersapi.util;

import java.util.Set;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.Power;

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
	

	public final EntityPlayer thePlayer;
	public final Power thePower;
	private final NBTTagCompound powerData;
	
	PowerProfile(EntityPlayer player, Power power, NBTTagCompound data) {
		thePlayer = player;
		thePower = power;
		
		if (data == null) {
			powerData = new NBTTagCompound();
		} else {
			powerData = data;
		}
		
	}
	
	public void resetPowerData() {
		
		Set removeableKeys = Sets.newConcurrentHashSet( powerData.getKeySet() );
		
		synchronized(powerData) {
			for (Object tag : removeableKeys) {
				powerData.removeTag( (String) tag );
			}
		}
		
	}
	
	public NBTTagCompound getPowerData() {
		return powerData;
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
	
	public static PowerProfile getFromNBT(EntityPlayer player, NBTTagCompound data) {
		
		if (data != null) {
		
			NBTTagCompound powerData;
			Power power;
			
			if (data.hasKey( ADDITIONAL_DATA, 10 )) {
				powerData = data.getCompoundTag( ADDITIONAL_DATA );
			} else {
				powerData = new NBTTagCompound();
				data.setTag( ADDITIONAL_DATA, powerData );
			}
			
			if (data.hasKey( POWER_NAME, 8 )) {
				power = Power.lookupPower( data.getString( POWER_NAME ) );
			} else {
				power = null;
			}
			
			return new PowerProfile(player, power, powerData);
			
		} else {
			return null;
		}
		
	}
	
}
