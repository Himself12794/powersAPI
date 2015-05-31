package com.himself12794.powersapi.powerfx;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;

import com.himself12794.powersapi.util.UsefulMethods;

public class Lift extends SpellEffect {
	
	private double liftHeight = 5.0D;
	
	Lift(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpdate(EntityLivingBase entity, int timeLeft,	EntityLivingBase caster) {
		
		if (!(entity instanceof EntityFlying) && entity.getHealth() > 0 ) {
			//System.out.println(entity.posY);
			//entity.motionX = 0.0D;
			//entity.motionZ = 0.0D;
			double groundDistance = UsefulMethods.distanceAboveGround(entity);
			//System.out.println("Height: " + groundDistance);
			if (groundDistance < liftHeight) entity.motionY = 1.0D;
			else entity.motionY = 0.0D;
		}

	}

}
