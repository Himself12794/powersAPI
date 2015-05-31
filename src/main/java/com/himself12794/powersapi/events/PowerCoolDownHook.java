package com.himself12794.powersapi.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersapi.power.Power;

public class PowerCoolDownHook {
	
	@SubscribeEvent
	public void handlePowerCoolDown(LivingUpdateEvent event) {
		
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			
			ItemStack[] itemStacks = player.inventory.mainInventory;
			
			for (ItemStack stack : itemStacks) {
				
				if (stack != null && Power.hasPower(stack) ) {
					
					Power power = Power.getPower(stack);
					
					if (power != null) {
						
						int remaining = 0;
						
						if (power.getCoolDown() > 0) {
														
							remaining  = power.getCoolDownRemaining(player);
							
							if (remaining > 0) { 
								--remaining;
								//event.setCanceled(true);
							}
							
							power.setCoolDown(player, remaining);
							
						}
					}
				}	
			}
		}
	}
}
