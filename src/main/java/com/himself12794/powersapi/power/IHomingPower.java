package com.himself12794.powersapi.power;

import net.minecraft.util.MovingObjectPosition;

import com.himself12794.powersapi.entity.EntitySpell;

/**
 * This is implemented by instances of PowerRanged to continuously redirect path to a specified target.
 * The moving target can be an entity or a position. If it is an entity, the projectile will track the
 * target, and if it is only a position, will home in on that location.
 *   
 * @author Himself12794
 *
 */
public interface IHomingPower {
	
	/**
	 * Gets the target for the spell.
	 * 
	 * @param caster
	 */
	MovingObjectPosition getTarget(EntitySpell spell, MovingObjectPosition target);
	
}