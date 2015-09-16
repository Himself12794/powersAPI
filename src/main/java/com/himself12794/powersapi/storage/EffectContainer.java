package com.himself12794.powersapi.storage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.UsefulMethods;

public class EffectContainer {

	public final EntityLivingBase affectedEntity;
	public final EntityLivingBase casterEntity;
	public int timeRemaining;
	public final PowerEffect theEffect;
	public final Power initiatedPower;
	private NBTTagCompound dataTag = new NBTTagCompound();

	public EffectContainer(final EntityLivingBase affected,
			final EntityLivingBase caster, final int time,
			final PowerEffect effect) {

		this( affected, caster, time, effect, null );
	}

	public EffectContainer(final EntityLivingBase affected,
			final EntityLivingBase caster, final int time,
			final PowerEffect effect, Power power) {

		affectedEntity = affected;
		casterEntity = caster;
		timeRemaining = time;
		theEffect = effect;
		initiatedPower = power;
	}
	
	public boolean shouldApplyEffect() {
		System.out.println(theEffect);
		return theEffect.shouldApplyEffect( affectedEntity, casterEntity, initiatedPower );
	}
	
	public NBTTagCompound getDataTag() {
		return this.dataTag;
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
		nbt.setInteger( "initiatedPower", initiatedPower != null ? initiatedPower.getId() : -1);
		nbt.setTag( "dataTags", dataTag );

		return nbt;
	}

	public static EffectContainer getFromNBT(NBTTagCompound nbt,
			EntityLivingBase affectedEntity) {
		EffectContainer result = null;
		
		if (nbt != null) {

			EntityLivingBase casterEntity = (EntityLivingBase) UsefulMethods.getEntityFromPersistentId( affectedEntity.worldObj, nbt.getString( "casterEntity" ), EntityLivingBase.class );
			int timeRemaining = nbt.getInteger( "timeRemaining" );
			PowerEffect theEffect = PowerEffect.getEffectById( nbt.getInteger( "theEffect" ) );
			Power initiatedPower = PowersRegistry.lookupPowerById( nbt.getInteger( "initiatedPower" ) );
			NBTTagCompound dataTags = nbt.getCompoundTag( "dataTags" );
			
			if (theEffect != null) {
				result = new EffectContainer(affectedEntity, casterEntity, timeRemaining, theEffect, initiatedPower);
				result.dataTag = dataTags;
			}
		} 
		
		return result;
	}

}
