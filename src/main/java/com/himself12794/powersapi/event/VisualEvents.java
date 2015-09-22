package com.himself12794.powersapi.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.storage.PowersEntity;

public class VisualEvents {
	
	@SubscribeEvent
	public void cancelUseWhenUsingPower(PlayerInteractEvent event) {
		
		if (PowersEntity.get( event.entityPlayer ).isUsingPrimaryPower()) {
			event.setCanceled( true );
		}
		
	}
	
	@SubscribeEvent
	public void cancelWhenUsingPower2(PlayerUseItemEvent.Start event) {
		
		if (PowersEntity.get( event.entityPlayer ).isUsingPrimaryPower()) {
			event.setCanceled( true );
		}
		
	}

}
