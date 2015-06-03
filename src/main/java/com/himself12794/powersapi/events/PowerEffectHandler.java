package com.himself12794.powersapi.events;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersapi.api.powerfx.PowerEffect;

public class PowerEffectHandler {
	
	@SubscribeEvent
	public void preventDeath(LivingDeathEvent event) {
		if (PowerEffect.rapidCellularRegeneration.isEffecting(event.entityLiving)) {
			if (event.source != DamageSource.outOfWorld) {
				event.entityLiving.setHealth(0.5F);
				PowerEffect.paralysis.addTo(event.entityLiving, 20 * 10, event.entityLiving);
				event.setCanceled(true);
			}
		}
	}
	
}
