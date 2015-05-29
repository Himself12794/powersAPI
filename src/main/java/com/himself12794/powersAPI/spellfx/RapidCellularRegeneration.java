package com.himself12794.powersAPI.spellfx;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class RapidCellularRegeneration extends SpellEffect {
	
	RapidCellularRegeneration(int id) {
		super(id);
	}
	
	@Override
	public void onUpdate(EntityLivingBase entity, int timeleft, EntityLivingBase caster ) {
		
		entity.removePotionEffect(Potion.poison.id);
		entity.removePotionEffect(Potion.wither.id);
		
		if (entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(entity.getMaxHealth() / 10);
		}
	}

	@Override
	public void onRemoval(EntityLivingBase entity) {
		
		entity.addPotionEffect(new PotionEffect(Potion.wither.id, 20 * 60 * 5, 3));
		
	}
	
	
	
}
