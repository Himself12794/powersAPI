package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;

import com.himself12794.powersapi.storage.PowerProfile;

/**
 * Indicates that a Power is used to activate/deactivate a power effect
 * 
 * @author Himself12794
 *
 */
public interface IEffectActivator {

	PowerEffect getPowerEffect();

	/**
	 * The max amount of time the effect should last. Cooldown is triggered upon
	 * the effect ending, if applicable.
	 * 
	 * @param profile
	 * @return
	 */
	int getEffectDuration(PowerProfile profile);

	/**
	 * Determines if the effect activator is able to remove the effect, after it
	 * has added it.
	 * 
	 * @param affected
	 * @param caster
	 * @return
	 */
	boolean isRemoveableByCaster(EntityLivingBase affected, EntityLivingBase caster, int timeRemaining);

}
