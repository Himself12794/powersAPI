package com.himself12794.powersapi.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersapi.storage.PowersEntity;

/**
 * Prevents players from using items while a power is being used.
 * 
 * @author Himself12794
 *
 */
public class VisualEvents {
	
	@SubscribeEvent
	public void cancelUseWhenUsingPower(PlayerInteractEvent event) {
		
		PowersEntity pe = PowersEntity.get( event.entityPlayer );
		
		if (pe.isUsingPrimaryPower() || pe.isUsingSecondaryPower()) {
			event.setCanceled( true );
		}
		
	}
	
	@SubscribeEvent
	public void cancelWhenUsingPower2(PlayerUseItemEvent.Start event) {
		
		PowersEntity pe = PowersEntity.get( event.entityPlayer );
		
		if (pe.isUsingSecondaryPower() || pe.isUsingPrimaryPower()) {
			event.setCanceled( true );
		}
		
	}

}
