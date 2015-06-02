package com.himself12794.powersapi.events;

import com.himself12794.powersapi.util.DataWrapper;

import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpdatesHandler {
	
	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		
		//System.out.println("Updating all event");
		DataWrapper.get(event.entityLiving).updateAll();
		
	}

}
