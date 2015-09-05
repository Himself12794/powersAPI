package com.himself12794.powersapi.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import com.himself12794.powersapi.network.Network;
import com.himself12794.powersapi.storage.EffectsWrapper;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.storage.PropertiesBase;

public class EventsHandler {

	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		
		PropertiesBase powers = PowersWrapper.get( event.entityLiving );
		if (powers != null) powers.onUpdate();
		
		PropertiesBase effects = EffectsWrapper.get( event.entityLiving );
		if (effects != null) effects.onUpdate();
		
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityPlayerMP) {
		
			PropertiesBase wrapper;
			
			wrapper = PowersWrapper.get( (EntityLivingBase) event.entity );
			Network.client().syncProperties( wrapper, (EntityPlayer) event.entity );
			
			wrapper = EffectsWrapper.get( (EntityLivingBase) event.entity );
			Network.client().syncProperties( wrapper, (EntityPlayer) event.entity );
			
		}
	}
	
	@SubscribeEvent
	public void registerExtendedPropperties(EntityEvent.EntityConstructing event) {
		
		if (event.entity instanceof EntityLivingBase) {
			
			PropertiesBase wrapper;
			
			if (EffectsWrapper.get( (EntityLivingBase) event.entity ) == null)			
				EffectsWrapper.register( (EntityLivingBase) event.entity );
			
			if (PowersWrapper.get( (EntityLivingBase) event.entity ) == null)
				PowersWrapper.register( (EntityLivingBase) event.entity );
							
		}
		
	}
		

	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {
		
		/*if (!event.player.worldObj.isRemote) {		
			NBTTagCompound nbttagcompound = PowersWrapper.get( event.player )
					.getModEntityData();
			PowersAPI.network.sendTo( new SyncNBTDataClient( nbttagcompound ),
					(EntityPlayerMP) event.player );
		}*/
	}
	
	@SubscribeEvent
	public void respawnSync(PlayerRespawnEvent event) {
		
		EntityPlayer player = event.player;
		PropertiesBase wrapper;
		
		wrapper = EffectsWrapper.get( player ).resetForRespawn();
		Network.client().syncProperties( wrapper, player );
		
		wrapper = PowersWrapper.get( player ).resetForRespawn();
		Network.client().syncProperties( wrapper, player );
		
	}

	@SubscribeEvent
	public void getPlayerData(PlayerEvent.Clone event) {
		
		if (event.wasDeath) {
			
			EffectsWrapper.get( event.original ).copyTo( event.entityPlayer );
			PowersWrapper.get( event.original ).copyTo( event.entityPlayer );
			
		}
	}

	@SubscribeEvent
	public void saveToFile(SaveToFile event) {
		//DataWrapperP.get( event.entityPlayer ).saveHandler.writePlayerData( event.entityPlayer );
	}

	@SubscribeEvent
	public void loadFromFile(LoadFromFile event) {
		
			//NBTTagCompound data = PowersAPI.getSaveHandler( event.entityPlayer ).readPlayerData( event.entityPlayer );
			//PowersWrapper.set( event.entityPlayer, data );
			
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
			event.duration = 0;
		}
		
	}

}
