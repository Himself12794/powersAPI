package com.himself12794.powersAPI.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class DummyHoming extends SpellHoming {
	
	DummyHoming() {
		setPower(2.0F);
		setCoolDown(20);
		setDuration(0);
		setUnlocalizedName("dummyHoming");
	}
	
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {

		if (target.entityHit != null) {
			target.entityHit.attackEntityFrom(DamageSource.magic, getPower());
		}
		return true;
	}
	
	public float getSpellVelocity(){
		return 2.0F;
	}

}
