package com.himself12794.powersapi.util;

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
		
		for (Object tag : powerData.getKeySet()) {
			powerData.removeTag( (String) tag );
		}
		
	}
	
	public NBTTagCompound getPowerData() {
		return powerData;
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
