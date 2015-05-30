package com.himself12794.powersapi.spellfx;

import net.minecraft.entity.EntityLivingBase;

import com.himself12794.powersapi.PowersAPI;

public class Levitate extends SpellEffect {

	Levitate(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpdate(EntityLivingBase entity, int timeLeft,	EntityLivingBase caster) {
		
		if (PowersAPI.proxy.getSide().isClient()) entity.setVelocity(0.0D, 1.0D, 0.0D);
		else {
			entity.motionX = 0.0D;
			entity.motionY = 1.0D;
			entity.motionZ = 0.0D;
		}

	}

	@Override
	public void onRemoval(EntityLivingBase entity, EntityLivingBase caster) {
		//System.out.println("Now removing levitate");
		//if (PowersAPI.proxy.getSide().isClient()) entity.setVelocity(0.0D, 0.0D, 0.0D);
		SpellEffect.ground.addTo(entity, 40, caster);

	}

}
