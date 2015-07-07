package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Just a convenience interface. Denotes a PowerEffect that can only
 * affect players.
 * 
 * @author Himself12794
 *
 */
public interface IPlayerOnly {
	
	/**
	 * Works the same as {@link PowerEffect#onUpdate(EntityLivingBase, int, EntityLivingBase)}, except 
	 * entity is automatically cast to EntityPlayer
	 * 
	 * @param entity
	 * @param timeLeft
	 * @param caster
	 */
	void onUpdate(EntityPlayer entity, int timeLeft, EntityLivingBase caster);
	
}
