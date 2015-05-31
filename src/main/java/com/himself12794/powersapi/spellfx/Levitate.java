package com.himself12794.powersapi.spellfx;

import net.minecraft.entity.EntityLivingBase;

public class Levitate extends SpellEffect {

	Levitate(int id) {
		super(id);
	}

	@Override
	public void onUpdate(EntityLivingBase entity, int timeLeft, EntityLivingBase caster) {
		
		entity.motionY = 0.0D;
		
	}

}
