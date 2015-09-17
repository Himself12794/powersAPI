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

public class EventsHandler {

	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		PowersAPI.propertiesManager().runUpdates( event.entityLiving );
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityLivingBase) {
			
			PowersAPI.propertiesManager().runOnJoinWorld( (EntityLivingBase) event.entity, event.world );
			
			if (event.entity instanceof EntityPlayerMP) {
				PowersAPI.propertiesManager().syncPlayerToClient( (EntityPlayerMP) event.entity );
			}
		}
	}
	
	@SubscribeEvent
	public void registerExtendedPropperties(EntityEvent.EntityConstructing event) {
		
		if (event.entity instanceof EntityLivingBase) {
			PowersAPI.propertiesManager().registerPropertiesForEntity( (EntityLivingBase) event.entity );
		}
		
	}
	
	@SubscribeEvent
	public void respawnSync(PlayerRespawnEvent event) {
		
		PowersAPI.propertiesManager().runOnRespawn( event.player );
		PowersAPI.propertiesManager().syncPlayerToClient( (EntityPlayerMP) event.player );
		
	}

	@SubscribeEvent
	public void getPlayerData(PlayerEvent.Clone event) {
		
		if (event.wasDeath) {
			PowersAPI.propertiesManager().copyAllOver( event.original, event.entityPlayer );
		}
	}
	
	@SubscribeEvent
	public void onDamaged(LivingHurtEvent event) {
		
		event.ammount = PowersAPI.propertiesManager().runOnDamaged( event.entityLiving, event.source, event.ammount );
		
	}
	
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
