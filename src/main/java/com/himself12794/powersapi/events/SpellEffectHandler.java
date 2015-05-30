package com.himself12794.powersapi.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.spellfx.SpellEffect;

public class SpellEffectHandler {

	@SubscribeEvent
	public void spellEffectsHandler(LivingUpdateEvent event) {
		
		EntityLivingBase target = event.entityLiving;
		NBTTagList activeEffects = SpellEffect.getActiveEffects(target);
		
		if (!activeEffects.hasNoTags()) {
			//PowersAPI.print(activeEffects);
			
			//for ( int i = 0; i < activeEffects.tagCount(); i++)  {
			for (int i = 0; i < activeEffects.tagCount(); ++i) {
				
				NBTTagCompound nbttagcompound = activeEffects.getCompoundTagAt(i);
				
				int timeRemaining = nbttagcompound.getInteger("duration");
				EntityLivingBase caster = (EntityLivingBase) target.worldObj.getEntityByID(nbttagcompound.getInteger("caster"));
				SpellEffect spfx = SpellEffect.getEffectById(nbttagcompound.getShort("id"));
				//System.out.println("Entity: " + target + " has effect: " + spfx);
				
				//UsefulThings.print(spfx);
				if (spfx != null) {
					//if (timeRemaining >= 0) PowersAPI.print("Time remaining: " + timeRemaining);
					
					if (timeRemaining > 0) {
						
						//System.out.println("Deincrementing time");
						spfx.onUpdate(target, timeRemaining, caster);
						spfx.addTo(target, --timeRemaining, caster);
						
					} 
					
					//else if (timeRemaining == 0) spfx.clearFrom(target, caster);
					else if (timeRemaining < 0) spfx.onUpdate(target, timeRemaining, caster);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void preventDeath(LivingDeathEvent event) {
		if (SpellEffect.rapidCellularRegeneration.isEffecting(event.entityLiving)) {
			if (event.source != DamageSource.outOfWorld) {
				event.entityLiving.setHealth(0.5F);
				event.setCanceled(true);
			}
		}
	}
	
}
