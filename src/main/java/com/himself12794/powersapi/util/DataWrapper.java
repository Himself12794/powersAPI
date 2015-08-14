package com.himself12794.powersapi.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.power.IPlayerOnly;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;

public final class DataWrapper {
	
	private EntityLivingBase theEntity;
	private NBTTagList activePowerEffects;
	private NBTTagCompound powerCoolDowns;
	/**Unimplemented*/
	private NBTTagList learnedAbilitySets;
	
	public static DataWrapper get(EntityLivingBase entity) {
		return new DataWrapper(entity);
	}
	
	private static DataWrapper set(EntityLivingBase entity, NBTTagCompound data) {

		NBTTagCompound nbt = entity.getEntityData();
		
		nbt.setTag(Reference.TagIdentifiers.POWER_COOLDOWNS, data.getTag(Reference.TagIdentifiers.POWER_COOLDOWNS));
		nbt.setTag(Reference.TagIdentifiers.POWER_EFFECTS, data.getTag(Reference.TagIdentifiers.POWER_EFFECTS));
		
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
					
					boolean shouldNegate = (PowerEffect.negated.isEffecting(theEntity) && spfx.isNegateable());
					
					if (timeRemaining > 0 && !shouldNegate) {
						
						if (spfx instanceof IPlayerOnly && theEntity instanceof EntityPlayer) {
							
							((IPlayerOnly)spfx).onUpdate( (EntityPlayer) theEntity, timeRemaining, caster );
						
						} else spfx.onUpdate(theEntity, timeRemaining, caster);
						
						PowersAPI.proxy.network.sendToAll(new PowerEffectsClient(spfx, theEntity, caster, false, timeRemaining));
						//spfx.addTo(theEntity, --timeRemaining, caster);
						addPowerEffect(spfx, --timeRemaining, caster);
						
					} 
					
					else if (timeRemaining < 0 && !shouldNegate) {
						
						spfx.onUpdate(theEntity, 0, caster);
						PowersAPI.proxy.network.sendToAll(new PowerEffectsClient(spfx, theEntity, caster, false, 0));
						
					}
				}
			}
		}
	}
	
	public boolean onAttacked(DamageSource ds, float amount) {
		
		if (!activePowerEffects.hasNoTags()) {
			
			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {
				
				NBTTagCompound nbttagcompound = activePowerEffects.getCompoundTagAt(i);
				
				int timeRemaining = nbttagcompound.getInteger("duration");
				EntityLivingBase caster = null;
				
				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj.getEntityByID(nbttagcompound.getInteger("caster"));
				
				PowerEffect spfx = PowerEffect.getEffectById(nbttagcompound.getShort("id"));
				
				if (spfx != null) {
					
					boolean shouldNegate = (PowerEffect.negated.isEffecting(theEntity) && spfx.isNegateable());
					
					if (!shouldNegate) {
						
						return spfx.onAttacked( ds, amount );
						
					} 
				}
			}
		}
		return true;
	}
	
	public float onAttack(EntityLivingBase target, DamageSource ds, float amount) {
		
		if (!activePowerEffects.hasNoTags()) {
			
			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {
				
				NBTTagCompound nbttagcompound = activePowerEffects.getCompoundTagAt(i);
				
				EntityLivingBase caster = null;
				
				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj.getEntityByID(nbttagcompound.getInteger("caster"));
				
				PowerEffect spfx = PowerEffect.getEffectById(nbttagcompound.getShort("id"));
				
				System.out.println(spfx.getId());
				
				if (spfx != null) {
					
					boolean shouldNegate = (PowerEffect.negated.isEffecting(theEntity) && spfx.isNegateable());
					
					if (!shouldNegate) {
						
						System.out.println("was not negated!");
						
						return spfx.onAttack( target, ds, amount, caster );
						
					} 
				}
			}
		}
		return amount;
	}
	
	public float onHurt( DamageSource ds, float amount) {
		
		if (!activePowerEffects.hasNoTags()) {
			
			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {
				
				NBTTagCompound nbttagcompound = activePowerEffects.getCompoundTagAt(i);
				
				EntityLivingBase caster = null;
				
				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj.getEntityByID(nbttagcompound.getInteger("caster"));
				
				PowerEffect spfx = PowerEffect.getEffectById(nbttagcompound.getShort("id"));
				
				if (spfx != null) {
					
					boolean shouldNegate = (PowerEffect.negated.isEffecting(theEntity) && spfx.isNegateable());
					
					if (!shouldNegate) {
						
						return spfx.onHurt( ds, amount );
						
					} 
				}
			}
		}
		
		return amount;
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
		
		nbt.setTag(Reference.TagIdentifiers.POWER_COOLDOWNS, powerCoolDowns);
		nbt.setTag(Reference.TagIdentifiers.POWER_EFFECTS, activePowerEffects);
		
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
