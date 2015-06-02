package com.himself12794.powersapi.util;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.powerfx.PowerEffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class DataWrapper {
	
	private EntityLivingBase theEntity;
	private NBTTagList activePowerEffects;
	private NBTTagCompound powerCoolDowns;
	/**Unimplemented*/
	private NBTTagList learnedAbilitySets;
	
	public static DataWrapper get(EntityLivingBase entity) {
		return new DataWrapper(entity);
	}
	
	public static DataWrapper set(EntityLivingBase entity, NBTTagCompound data) {

		NBTTagCompound nbt = entity.getEntityData();
		
		nbt.setTag(Reference.TagIdentifiers.powerCooldowns, data.getTag(Reference.TagIdentifiers.powerCooldowns));
		nbt.setTag(Reference.TagIdentifiers.powerEffects, data.getTag(Reference.TagIdentifiers.powerEffects));
		
		return new DataWrapper(entity);
		
	}
	
	private DataWrapper(EntityLivingBase entity) {
		
		theEntity = entity;
		activePowerEffects = PowerEffect.getActiveEffects(entity);
		powerCoolDowns = Power.getCooldowns(entity);
		
	}
	
	public void updateCooldowns() {
		
		for (Object name : this.powerCoolDowns.getKeySet()) {
			
			String powerName = (String)name;
			int remaining = this.powerCoolDowns.getInteger(powerName);
			Power power = Power.lookupPower(powerName);
			
			if (remaining > 0 && power != null) {
					
				power.setCoolDown(theEntity, --remaining);
						
			}
			
		}
		
	}
	
	public void updatePowerEffects() {
		
		if (!activePowerEffects.hasNoTags()) {
			
			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {
				
				NBTTagCompound nbttagcompound = activePowerEffects.getCompoundTagAt(i);
				
				int timeRemaining = nbttagcompound.getInteger("duration");
				EntityLivingBase caster = null;
				
				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj.getEntityByID(nbttagcompound.getInteger("caster"));
				
				PowerEffect spfx = PowerEffect.getEffectById(nbttagcompound.getShort("id"));
				
				if (spfx != null) {
					
					if (timeRemaining > 0) {
						
						spfx.onUpdate(theEntity, timeRemaining, caster);
						PowersAPI.proxy.network.sendToAll(new PowerEffectsClient(spfx, theEntity, caster, false, timeRemaining));
						//spfx.addTo(theEntity, --timeRemaining, caster);
						addPowerEffect(spfx, --timeRemaining, caster);
						
					} 
					
					else if (timeRemaining < 0) {
						
						spfx.onUpdate(theEntity, 0, caster);
						PowersAPI.proxy.network.sendToAll(new PowerEffectsClient(spfx, theEntity, caster, false, 0));
						
					}
				}
			}
		}
		
	}

	public void updateAll() {
		
		updatePowerEffects();
		updateCooldowns();
		
	}
	
	public NBTTagCompound toNBT() {
		
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setTag("activeEffects", activePowerEffects);
		nbt.setTag("powerCooldowns", powerCoolDowns);
		
		return nbt;
		
	}
	
	public void copyTo(EntityLivingBase entity) {
		
		NBTTagCompound nbt = entity.getEntityData();
		
		nbt.setTag(Reference.TagIdentifiers.powerCooldowns, powerCoolDowns);
		nbt.setTag(Reference.TagIdentifiers.powerEffects, activePowerEffects);
		
	}
	
	public void addPowerEffect(PowerEffect effect, int duration, EntityLivingBase caster) {
		
		effect.addTo(theEntity, duration, caster);
		
	}
	
	public void removePowerEffect(PowerEffect effect) {

		effect.clearFrom(theEntity, null);
		
	}
	
	public EntityLivingBase getEntity() {
		return theEntity;
	}
	
}
