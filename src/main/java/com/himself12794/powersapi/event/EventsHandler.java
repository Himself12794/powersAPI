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
import com.himself12794.powersapi.storage.EffectsWrapper;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.storage.PropertiesBase;
import com.himself12794.powersapi.storage.PropertiesManager;

public class EventsHandler {

	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		
		//PropertiesBase powers = PowersWrapper.get( event.entityLiving );
		//if (powers != null) powers.onUpdate();
		
		//PropertiesBase effects = EffectsWrapper.get( event.entityLiving );
		//if (effects != null) effects.onUpdate();
		
		/*for (Object identifier : PropertiesBase.getIdentifiers()) {
			
			String name = (String)identifier;
			Class clazz = PropertiesBase.getClassFor( name );
			IExtendedEntityProperties wrapper = event.entityLiving.getExtendedProperties( name );
	
			if (wrapper.getClass().isAssignableFrom( clazz ) && wrapper.getClass().isAssignableFrom( PropertiesBase.class )) {
				((PropertiesBase)wrapper).onUpdate();
			}
			
		}*/
		PropertiesManager.runUpdates( event.entityLiving );
		
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityLivingBase) {
			
			PropertiesManager.runOnJoinWorld( (EntityLivingBase) event.entity, event.world );
			
			if (event.entity instanceof EntityPlayerMP) {
				
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
		
		if (PowersWrapper.get( event.entityPlayer ).isUsingPower()) {
			event.setCanceled( true );
		}
		
	}
	
	@SubscribeEvent
	public void cancelWhenUsingPower2(PlayerUseItemEvent.Start event) {
		
		if (PowersWrapper.get( event.entityPlayer ).isUsingPower()) {
			event.setCanceled( true );
		}
		
	}

}
