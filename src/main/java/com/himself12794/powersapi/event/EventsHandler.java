package com.himself12794.powersapi.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.storage.EffectsEntity;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.storage.PropertiesBase;
import com.himself12794.powersapi.storage.PropertiesManager;

public class EventsHandler {

	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		PropertiesManager.runUpdates( event.entityLiving );
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityLivingBase) {
			
			PropertiesManager.runOnJoinWorld( (EntityLivingBase) event.entity, event.world );
			
			if (event.entity instanceof EntityPlayerMP) {
				System.out.println("Synchronizing");
				PropertiesManager.syncPlayerToClient( (EntityPlayerMP) event.entity );
			}
		}
	}
	
	@SubscribeEvent
	public void registerExtendedPropperties(EntityEvent.EntityConstructing event) {
		
		if (event.entity instanceof EntityLivingBase) {
			PropertiesManager.registerPropertiesForEntity( (EntityLivingBase) event.entity );					
		}
		
	}
	
	@SubscribeEvent
	public void respawnSync(PlayerRespawnEvent event) {
		
		PropertiesManager.runOnRespawn( event.player );
		PropertiesManager.syncPlayerToClient( (EntityPlayerMP) event.player );
		
	}

	@SubscribeEvent
	public void getPlayerData(PlayerEvent.Clone event) {
		
		if (event.wasDeath) {
			PropertiesManager.copyAllOver( event.original, event.entityPlayer );			
		}
	}
	
	@SubscribeEvent
	public void cancelUseWhenUsingPower(PlayerInteractEvent event) {
		
		if (PowersEntity.get( event.entityPlayer ).isUsingPower()) {
			event.setCanceled( true );
		}
		
	}
	
	@SubscribeEvent
	public void cancelWhenUsingPower2(PlayerUseItemEvent.Start event) {
		
		if (PowersEntity.get( event.entityPlayer ).isUsingPower()) {
			event.setCanceled( true );
		}
		
	}

}
