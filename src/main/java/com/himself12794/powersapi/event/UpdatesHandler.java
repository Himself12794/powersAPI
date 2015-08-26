package com.himself12794.powersapi.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.SyncNBTData;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.DataWrapper;

public class UpdatesHandler {
	
	@SubscribeEvent
	public void updates(LivingUpdateEvent event) {
		DataWrapper.get(event.entityLiving).updateAll();		
	}
	
	@SubscribeEvent
	public void loggedIn(PlayerLoggedInEvent event) {
		if (!event.player.worldObj.isRemote) {
			PowersAPI.proxy.network.sendTo( new SyncNBTData( event.player ), (EntityPlayerMP) event.player );
		}
	}
	
	@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {
		
		if (!PowerEffect.getActiveEffects( event.entityLiving ).hasNoTags()) {
			if (!DataWrapper.get( event.entityLiving ).onAttacked( event.source, event.ammount )) event.setCanceled( true );
		}
		
	}
	
	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {
		
		if (!PowerEffect.getActiveEffects( event.entityLiving ).hasNoTags()) {
			
			float amount = DataWrapper.get( event.entityLiving ).onHurt( event.source, event.ammount );
			
			event.ammount = amount;
		} 
		
		if (event.source.getEntity() instanceof EntityLivingBase) {
			
			EntityLivingBase attacker = (EntityLivingBase) event.source.getEntity();
			
			if (!PowerEffect.getActiveEffects( attacker ).hasNoTags()) {
				
				float amount = DataWrapper.get( attacker ).onAttack( event.entityLiving, event.source, event.ammount );
				
			}
			
		}
		
	}

}
