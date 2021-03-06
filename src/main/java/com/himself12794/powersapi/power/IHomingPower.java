package com.himself12794.powersapi.power;

import com.himself12794.powersapi.entity.EntityPower;

import net.minecraft.entity.Entity;

/**
 * This is implemented by instances of PowerRanged to continuously redirect path to a specified target.
 * The moving target can be an entity or a position. If it is an entity, the projectile will track the
 * target, and if it is only a position, will home in on that location.
 *   
 * @param T the type
 * @author Himself12794
 *
 */
public interface IHomingPower<T extends Entity> {
	
	/**
	 * Gets the target for the spell.
	 * 
	 * @param caster
	 */
	boolean isTargetValid(EntityPower powerEntity, T target);
	
	Class<T> getTargetType();
	
}