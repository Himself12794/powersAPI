package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;

import com.himself12794.powersapi.entity.EntitySpell;

/**
 * Used when it is desired that a ranged spell remain in the world after striking a target.
 * 
 * @author Himself12794
 *
 */
public interface IPersistantPower {
	
	/**
	 * Called every tick after the projectile has finished moving.
	 * 
	 * @param projectile the EntitySpell projectile
	 * @param caster the caster of the power
	 * @param power the power cast
	 * @param timeLeft time left until projectile death
	 * @return whether or not the entity projectile should die next tick
	 */
	boolean onUpdate(EntitySpell projectile, EntityLivingBase caster, PowerRanged power, int timeLeft);

	int getLingerTime();
	
}
