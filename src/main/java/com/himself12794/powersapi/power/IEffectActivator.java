package com.himself12794.powersapi.power;

import com.himself12794.powersapi.storage.PowerProfile;

import net.minecraft.entity.EntityLivingBase;

/**
 * Indicates that a PowerBuff is used to activate/deactivate a power effect
 * 
 * @author Himself12794
 *
 */
public interface IEffectActivator {
	
	PowerEffect getPowerEffect();
	
	int getEffectDuration(PowerProfile profile);
	
	/**
	 * Determines if the effect activator is able to remove the effect, after it has added it.
	 * 
	 * @param affected
	 * @param caster
	 * @return
	 */
	boolean isRemoveableByCaster(EntityLivingBase affected, EntityLivingBase caster, int timeRemaining);

}
