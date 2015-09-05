package com.himself12794.powersapi.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.client.SyncNBTDataClient;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.storage.DataWrapperP;
import com.himself12794.powersapi.storage.EffectsWrapper;
import com.himself12794.powersapi.storage.Reference;

public class UpdatesHandler {

	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		
		PowersWrapper wrapper = PowersWrapper.get( event.entityLiving );
		if (wrapper != null) wrapper.updateAll();
		
	}

	/*@SubscribeEvent
	public void playerLoggedIn(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityPlayer) {
		
			if (!event.entity.worldObj.isRemote) {		
				NBTTagCompound nbttagcompound = PowersAPI.getDataHandler().getData( (EntityPlayer) event.entity );
				PowersAPI.network.sendTo( new SyncNBTData( nbttagcompound ),
						(EntityPlayerMP) event.entity );
			}
		}
	}*/
	
	@SubscribeEvent
	public void registerExtendedPropperties(EntityEvent.EntityConstructing event) {
		
		if (event.entity instanceof EntityLivingBase) {
			
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
		EffectsWrapper.get( player ).resetForRespawn();
		PowersWrapper.get( player ).resetForRespawn();
		
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
