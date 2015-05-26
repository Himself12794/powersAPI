package com.himself12794.powersAPI.spell;

import com.himself12794.powersAPI.entity.EntitySpell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public interface IHomingSpell {
	/**
	 * Gets the target for the spell.
	 * 
	 * @param caster
	 */
	MovingObjectPosition getTarget(EntitySpell spell, MovingObjectPosition target);
	
}