package com.himself12794.powersapi.powerfx;

import com.himself12794.powersapi.api.powerfx.PowerEffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class RapidCellularRegeneration extends PowerEffect {
	
	public RapidCellularRegeneration() {
		negateable = true;
	}
	
	@Override
	public void onUpdate(EntityLivingBase entity, int timeleft, EntityLivingBase caster ) {
					
		entity.removePotionEffect(Potion.poison.id);
		entity.removePotionEffect(Potion.wither.id);
		
		if (entity.getHealth() < entity.getMaxHealth() && entity.posY > -5.0D) {
			entity.heal(entity.getMaxHealth() / 10);
		}
		
	}

	@Override
	public void onRemoval(EntityLivingBase entity, EntityLivingBase caster) {
		
		entity.addPotionEffect(new PotionEffect(Potion.wither.id, 20 * 60 * 5, 3));
		
	}
	
	
	
}
