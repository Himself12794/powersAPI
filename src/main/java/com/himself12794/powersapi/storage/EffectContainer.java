package com.himself12794.powersapi.storage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.UsefulMethods;

/**
 * Holds information for an active effect on an entity.
 * 
 * @author Himself12794
 *
 */
public final class EffectContainer {

	private EntityLivingBase affectedEntity;
	public final EntityLivingBase casterEntity;
	public int timeRemaining;
	public final PowerEffect theEffect;
	public final Power initiatedPower;
	private NBTTagCompound dataTag = new NBTTagCompound();

	public EffectContainer(
			final EntityLivingBase caster, final int time,
			final PowerEffect effect) {

		this( caster, time, effect, null );
	}

	public EffectContainer(
			final EntityLivingBase caster, final int time,
			final PowerEffect effect, Power power) {

		casterEntity = caster;
		timeRemaining = time;
		theEffect = effect;
		initiatedPower = power;
	}

	public boolean shouldApplyEffect() {

		return theEffect.shouldApplyEffect( affectedEntity, casterEntity, initiatedPower );
	}

	public NBTTagCompound getDataTag() {

		return this.dataTag;
	}

	public float onDamaged(DamageSource damageSource, float amount, boolean hasChanged) {

		return theEffect.onDamaged( affectedEntity, casterEntity, damageSource, amount, hasChanged );
	}

	public void onApplied() {

		theEffect.onApplied( affectedEntity, casterEntity, this );
	}

	public void onRemoval() {

		theEffect.onRemoval( affectedEntity, casterEntity, initiatedPower );
	}

	public boolean onUpdate() {

		return theEffect.onUpdate( affectedEntity, timeRemaining, casterEntity,
				initiatedPower );
	}

	public NBTTagCompound getAsNBT() {

		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setString( "casterEntity", casterEntity != null ? casterEntity.getPersistentID()
				.toString() : "" );
		nbt.setInteger( "timeRemaining", timeRemaining );
		nbt.setInteger( "theEffect", theEffect != null ? theEffect.getId() : -1 );
		nbt.setInteger( "initiatedPower", initiatedPower != null ? initiatedPower.getId() : -1 );
		nbt.setTag( "dataTags", dataTag );

		return nbt;
	}
	
	void setAffectedEntity(EntityLivingBase entity) {
		affectedEntity = entity;
	}

	public static EffectContainer getFromNBT(NBTTagCompound nbt, EntityLivingBase affectedEntity) {

		EffectContainer result = null;

		if (nbt != null) {

			String entityId = nbt.getString( "casterEntity" );
			EntityLivingBase casterEntity = entityId.equals( affectedEntity.getPersistentID().toString() )
					? affectedEntity
					: (EntityLivingBase) UsefulMethods.getEntityFromPersistentId( affectedEntity.worldObj, entityId,
							EntityLivingBase.class );

			int timeRemaining = nbt.getInteger( "timeRemaining" );
			PowerEffect theEffect = PowerEffect.getEffectById( nbt.getInteger( "theEffect" ) );
			Power initiatedPower = PowersRegistry.lookupPowerById( nbt.getInteger( "initiatedPower" ) );
			NBTTagCompound dataTags = nbt.getCompoundTag( "dataTags" );

			if (theEffect != null) {
				result = new EffectContainer( casterEntity, timeRemaining, theEffect, initiatedPower );
				result.setAffectedEntity( affectedEntity );
				result.dataTag = dataTags;
			}
		}

		return result;
	}

}
