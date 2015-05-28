package com.himself12794.powersAPI.spellfx;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SpontaneousRegeneration extends SpellEffect {
	
	SpontaneousRegeneration(int id) {
		super(id);
	}
	
	public void onUpdate(EntityLivingBase entity, int timeRemaining ) {
		
		if (entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(entity.getMaxHealth() / 10);
		}
	}
	
	
	
}
