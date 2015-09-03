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
import com.himself12794.powersapi.network.SyncNBTData;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.DataWrapperP;

public class UpdatesHandler {

	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		DataWrapper wrapper = DataWrapper.get( event.entityLiving );
		wrapper.updateAll();
		/*if (event.entityLiving instanceof EntityPlayerMP && wrapper.getLastUpdate() == 15 ) {
				PowersAPI.network.sendTo( new SyncNBTData( wrapper.getModEntityData() ),
						(EntityPlayerMP) event.entityLiving );

		}*/
	}

	@SubscribeEvent
	public void playerLoggedIn(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityPlayer) {
		
			if (!event.entity.worldObj.isRemote) {		
				NBTTagCompound nbttagcompound = PowersAPI.getDataHandler().getData( (EntityPlayer) event.entity );
				PowersAPI.network.sendTo( new SyncNBTData( nbttagcompound ),
						(EntityPlayerMP) event.entity );
			}
		}
	}
		

	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {
		
		if (!event.player.worldObj.isRemote) {		
			NBTTagCompound nbttagcompound = DataWrapper.get( event.player )
					.getModEntityData();
			PowersAPI.network.sendTo( new SyncNBTData( nbttagcompound ),
					(EntityPlayerMP) event.player );
		}
	}
	
	/*@SubscribeEvent
	public void respawnSync(PlayerRespawnEvent event) {
		
		PowersAPI.network.sendTo( new SyncNBTData( DataWrapper.get( event.player ).getModEntityData() ), (EntityPlayerMP) event.player );
		
	}*/

	@SubscribeEvent
	public void getPlayerData(PlayerEvent.Clone event) {
		if (event.wasDeath) {
			
			EntityPlayer player = event.entityPlayer;
			NBTTagCompound tags = DataWrapperP.get( event.original ).getModEntityData();
			NBTTagCompound tags2 = DataWrapperP.set( player, tags ).resetForRespawn().getModEntityData();
			
			PowersAPI.network.sendTo( new SyncNBTData(tags2), (EntityPlayerMP) player );
		}
	}

	@SubscribeEvent
	public void saveToFile(SaveToFile event) {
		DataWrapperP.get( event.entityPlayer ).saveHandler.writePlayerData( event.entityPlayer );
	}

	@SubscribeEvent
	public void loadFromFile(LoadFromFile event) {
		
			NBTTagCompound data = PowersAPI.getSaveHandler( event.entityPlayer ).readPlayerData( event.entityPlayer );
			PowersAPI.getDataHandler().updateEntity( event.entityPlayer, data );
			
	}
	
	@SubscribeEvent
	public void saveOnDeath(LivingDeathEvent event) {
		//DataWrapper.get( event.entityLiving ).resetForRespawn().getModEntityData().setBoolean( "stopUpdate", false );
	}
	
	@SubscribeEvent
	public void cancelUseWhenUsingPower(PlayerInteractEvent event) {
		
		if (DataWrapper.get( event.entityPlayer ).isUsingPower()) {
			event.setCanceled( true );
		}
		
	}
	
	@SubscribeEvent
	public void cancelWhenUsingPower2(PlayerUseItemEvent.Start event) {
		
		if (DataWrapper.get( event.entityPlayer ).isUsingPower()) {
			event.duration = 0;
		}
		
	}

	/*@SubscribeEvent
	public void getPlayerData(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.entity;
			NBTTagCompound data = DataWrapperP.get( player ).saveHandler.readPlayerData( player );
			PowersAPI.network.sendTo( new SyncNBTData(data), player );
		}
	}*/

}
