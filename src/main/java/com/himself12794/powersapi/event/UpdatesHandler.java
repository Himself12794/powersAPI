package com.himself12794.powersapi.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.DataWrapper;

public class UpdatesHandler {
	
	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		
		//System.out.println("Updating all event");
		DataWrapper.get(event.entityLiving).updateAll();
		
	}
	
	@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {
		
		if (!PowerEffect.getActiveEffects( event.entityLiving ).hasNoTags()) {

			if (!DataWrapper.get( event.entityLiving ).onAttacked( event.source, event.ammount )) event.setCanceled( true );
			
		}
		
		/*if (event.source.getEntity() instanceof EntityLivingBase) {
			
			EntityLivingBase attacker = (EntityLivingBase) event.source.getEntity();
			
			if (!PowerEffect.getActiveEffects( attacker ).hasNoTags()) 
				DataWrapper.get( attacker ).onAttack( event.entityLiving, event.source, event.ammount );
			
		}*/
		
	}
	
	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {
		
		if (!PowerEffect.getActiveEffects( event.entityLiving ).hasNoTags()) {
			
			float amount = DataWrapper.get( event.entityLiving ).onHurt( event.source, event.ammount );
			
			event.ammount = amount;
		} 
		
		if (event.source.getEntity() instanceof EntityLivingBase) {
			
			EntityLivingBase attacker = (EntityLivingBase) event.source.getEntity();
			
			if (!PowerEffect.getActiveEffects( attacker ).hasNoTags()) {
				
				float amount = DataWrapper.get( attacker ).onAttack( event.entityLiving, event.source, event.ammount );
				
			}
			
		}
		
	}

}
