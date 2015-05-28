package com.himself12794.powersAPI.spellfx;

import net.minecraft.entity.EntityLivingBase;

public class RapidRegeneration extends SpellEffect {
	
	RapidRegeneration(int id) {
		super(id);
	}
	
	@Override
	public void onUpdate(EntityLivingBase entity, int timeleft, EntityLivingBase caster ) {
		
		if (entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(entity.getMaxHealth() / 10);
		}
	}

	@Override
	public void onRemoval(EntityLivingBase entity) {}
	
	
	
}
