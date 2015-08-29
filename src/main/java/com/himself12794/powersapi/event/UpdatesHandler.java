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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

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
		if (event.entityLiving instanceof EntityPlayerMP && wrapper.getLastUpdate() == 15 ) {
			wrapper.setHasSynced( true );
			PowersAPI.network.sendTo( new SyncNBTData( wrapper.getModEntityData() ),
					(EntityPlayerMP) event.entityLiving );
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

	@SubscribeEvent
	public void getPlayerData(PlayerEvent.Clone event) {
		if (event.entity instanceof EntityPlayerMP && event.wasDeath) {
			
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

	/*@SubscribeEvent
	public void loadFromFile(LoadFromFile event) {
		if (event.entityPlayer instanceof EntityPlayerMP) {
			NBTTagCompound data = DataWrapperP.get( event.entityPlayer ).saveHandler.readPlayerData( event.entityPlayer );
			PowersAPI.network.sendTo( new SyncNBTData(data), (EntityPlayerMP) event.entityPlayer );
		}
	}*/
	
	/*@SubscribeEvent
	public void saveOnDeath(LivingDeathEvent event) {
		
		if (event.entityLiving instanceof EntityPlayerMP) {
			
			System.out.println("Saving important player information for respawn");
			
			EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
			DataWrapperP.get( player ).resetForRespawn().saveHandler.writePlayerData( player );
			
		}
		
	}*/

	@SubscribeEvent
	public void getPlayerData(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.entity;
			NBTTagCompound data = DataWrapperP.get( player ).saveHandler.readPlayerData( player );
			PowersAPI.network.sendTo( new SyncNBTData(data), player );
		}
	}

	/*@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {

		if (!PowerEffect.getActiveEffects( event.entityLiving ).hasNoTags()) {
			if (!DataWrapper.get( event.entityLiving ).onAttacked(
					event.source, event.ammount )) event.setCanceled( true );
		}

	}

	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {

		if (!PowerEffect.getActiveEffects( event.entityLiving ).hasNoTags()) {

			float amount = DataWrapper.get( event.entityLiving ).onHurt(
					event.source, event.ammount );

			event.ammount = amount;
		}

		if (event.source.getEntity() instanceof EntityLivingBase) {

			EntityLivingBase attacker = (EntityLivingBase) event.source
					.getEntity();

			if (!PowerEffect.getActiveEffects( attacker ).hasNoTags()) {

				float amount = DataWrapper.get( attacker ).onAttack(
						event.entityLiving, event.source, event.ammount );

			}

		}

	}*/

}
