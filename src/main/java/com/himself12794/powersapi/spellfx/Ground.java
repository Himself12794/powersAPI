package com.himself12794.powersapi.spellfx;

import net.minecraft.entity.EntityLivingBase;

import com.himself12794.powersapi.PowersAPI;

public class Ground extends SpellEffect {

	Ground(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpdate(EntityLivingBase entity, int timeLeft,	EntityLivingBase caster) {
		//SpellEffect.slam.addTo(entity, 2, caster);
		if (PowersAPI.proxy.getSide().isClient()) entity.setVelocity(entity.motionX, -3.0D, entity.motionZ);
		else entity.motionY = -3.0D;

	}

	@Override
	public void onRemoval(EntityLivingBase entity, EntityLivingBase caster) {
		// TODO Auto-generated method stub

	}

}
