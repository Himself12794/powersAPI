package com.himself12794.powersapi.power;

import net.minecraft.util.MovingObjectPosition;

import com.himself12794.powersapi.entity.EntitySpell;

public interface IHomingPower {
	
	/**
	 * Gets the target for the spell.
	 * 
	 * @param caster
	 */
	MovingObjectPosition getTarget(EntitySpell spell, MovingObjectPosition target);
	
}