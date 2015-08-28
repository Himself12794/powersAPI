package com.himself12794.powersapi.util;

import java.util.Set;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.IPersistantEffect;
import com.himself12794.powersapi.power.PowerEffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;


public class DataWrapperP extends DataWrapper {
	
	public final EntityPlayer player;
	public final PlayerSaveHandler saveHandler;

	private DataWrapperP(EntityLivingBase entity) {
		super( entity );
		player = (EntityPlayer) entity;
		saveHandler = new PlayerSaveHandler(player);
	}
	
	public static DataWrapperP get(EntityPlayer player) {
		return new DataWrapperP(player);
	}

	/**
	 * Used to sync entity data client-side. Sets the mod data tag as data, so
	 * be careful when using this.
	 * 
	 * @param entity
	 * @param data
	 * @return
	 */
	public static DataWrapperP set(EntityPlayer entity, NBTTagCompound data) {
		
		NBTTagCompound nbt = entity.getEntityData();

		nbt.setTag( Reference.MODID, data );

		return new DataWrapperP( entity );

	}
	
	public DataWrapperP resetForRespawn() {
		if (getPowerData().hasKey( POWER_COOLDOWNS )) getPowerData().removeTag( POWER_COOLDOWNS );
		
		Set<PowerEffect> toRemove = Sets.newHashSet();
		for (int i = 0; i < getActiveEffects().tagCount(); i++) {
			PowerEffect effect = PowerEffect.getEffectById( getActiveEffects().getCompoundTagAt( i ).getInteger( "id" ) );
			if (!(effect instanceof IPersistantEffect)) {
				toRemove.add( effect );
			}
		}
		
		for (PowerEffect effect : toRemove) {
			removePowerEffectQuietly( effect );
		}
		
		getPowerData().setString( POWER_CURRENT, "" );
		getPowerData().setInteger( POWER_CURRENT_USELEFT, 0 );
		getPowerData().setTag( POWER_PREVIOUS_TARGET, new NBTTagCompound() );
		setHasSynced( false );
		
		return this;
	}

}
