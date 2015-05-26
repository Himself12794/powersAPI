package com.himself12794.powersAPI.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersAPI.spellfx.SpellEffect;

public class SpellEffectHandler {

	@SubscribeEvent
	public void spellEffectsHandler(LivingUpdateEvent event) {
		
		EntityLivingBase target = event.entityLiving;
		NBTTagCompound activeEffects = SpellEffect.getActiveEffects(target);
		
		if (activeEffects != null) {
			
			Object[] activeEffectKeys = activeEffects.getKeySet().toArray();
			//UsefulThings.print(activeEffects);
			
			for (Object i : activeEffectKeys ) {
				int id = Integer.parseInt((String)i);
				int timeRemaining = activeEffects.getIntArray((String) i)[0];
				EntityLivingBase caster = (EntityLivingBase) target.worldObj.getEntityByID(activeEffects.getIntArray((String) i)[1]);
				SpellEffect spfx = SpellEffect.getEffectById(id);
				
				//UsefulThings.print(spfx);
				if (spfx != null) {
					//UsefulThings.print(spfx);
					
					if (timeRemaining > 0) {
						
						spfx.onUpdate(target, timeRemaining, caster);
						spfx.addTo(target, --timeRemaining, caster);
						
					} 
					
					else if (timeRemaining == 0) spfx.clearFrom(target);
					else if (timeRemaining < 0) spfx.onUpdate(target, timeRemaining, caster);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void preventDeath(LivingDeathEvent event) {
		
		//System.out.println(SpellEffects.getActiveEffects(event.entityLiving));
		//if (SpellEffects.hasEffect(event.entityLiving, SpellEffects.spontaneousRegeneration.getId())) {
		//	System.out.println("death event by " + event.source.toString());
		//	System.out.println("You're immortal, so you're good");
			event.setCanceled(true);
		//}
	}
	
}
