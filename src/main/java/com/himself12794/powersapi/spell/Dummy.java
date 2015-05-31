package com.himself12794.powersapi.spell;

import com.himself12794.powersapi.spellfx.SpellEffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Dummy extends SpellRanged {
	
	Dummy() {
		setPower(0.0F);
		setCoolDown(20);
		setDuration(0);
		setUnlocalizedName("dummy");
	}
	
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		
		if (target.entityHit != null) {
			SpellEffect.paralysis.addTo((EntityLivingBase) target.entityHit, 60 * 20, caster);
			//SpellEffect.lift.addTo((EntityLivingBase) target.entityHit, 200, caster);
			target.entityHit.attackEntityFrom(DamageSource.magic, getPower());
		}
		return true;
	}
	
	public float getSpellVelocity(){
		return 2.0F;
	}
}
