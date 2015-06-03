package com.himself12794.powersapi.powerfx;

import com.himself12794.powersapi.api.powerfx.PowerEffect;

import net.minecraft.entity.EntityLivingBase;

public class Levitate extends PowerEffect {

	public Levitate() {}

	@Override
	public void onUpdate(EntityLivingBase entity, int timeLeft, EntityLivingBase caster) {
		
		//entity.motionY = 0.0D;
		entity.jumpMovementFactor = 0.0F;
		
	}

}
