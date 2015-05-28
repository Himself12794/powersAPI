package com.himself12794.powersAPI.spell;

import net.minecraft.util.MovingObjectPosition;

import com.himself12794.powersAPI.entity.EntitySpell;

public interface IHomingSpell {
	
	/**
	 * Gets the target for the spell.
	 * 
	 * @param caster
	 */
	MovingObjectPosition getTarget(EntitySpell spell, MovingObjectPosition target);
	
}