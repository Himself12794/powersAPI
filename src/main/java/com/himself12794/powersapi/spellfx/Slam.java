package com.himself12794.powersapi.spellfx;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;

public class Slam extends Lift {

	Slam(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onRemoval(EntityLivingBase entity, EntityLivingBase caster) {
		
		if (!(entity instanceof EntityFlying)) {
			entity.motionY = -4.0D;
			//entity.fall(10.0F, 1.0F);
			entity.fallDistance = 9.0F;
		}
		

	}

}
