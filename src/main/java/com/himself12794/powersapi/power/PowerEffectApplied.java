package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class is for PowerEffects which are targets of IEffectActivator
 * 
 * @author Himself12794
 *
 */
public class PowerEffectApplied extends PowerEffect {
	
	/**
	 * An extra check to make sure both the power and effect are ready
	 * 
	 * @param entityHit
	 * @param caster
	 * @param powerEffectActivatorInstant
	 * @return
	 */
	public boolean shouldApplyEffect(EntityLivingBase entityHit, EntityPlayer caster,
			PowerEffectActivatorInstant powerEffectActivatorInstant) {

		return true;
	}
}
