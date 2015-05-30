package com.himself12794.powersapi.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.spellfx.SpellEffect;

public class Slam extends SpellInstant {
	
	Slam() {
		
		setPower(6.0F);
		setCoolDown(4 * 20);
		setUnlocalizedName("slam");
		
	}
	
	@Override
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		

		SpellEffect.levitate.addTo((EntityLivingBase) target.entityHit, 20, caster);
		return true;
		
	}

}
