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
import com.himself12794.powersapi.util.Reference.TagIdentifiers;

/**
 * Utility class used for easier access of information stored in NBT tag data.
 * 
 * @author Himself12794
 *
 */
public class DataWrapper {

	protected EntityLivingBase theEntity;
	protected NBTTagList activePowerEffects;
	protected NBTTagCompound powerCoolDowns;
	protected NBTTagList powerSet;
	/** Unimplemented */
	private NBTTagList learnedAbilitySets;

	public static DataWrapper get(EntityLivingBase entity) {

		return new DataWrapper( entity );
	}

	public static DataWrapper set(EntityLivingBase entity, NBTTagCompound data) {

		NBTTagCompound nbt = entity.getEntityData();

		nbt.setTag( TagIdentifiers.POWER_COOLDOWNS,
				data.getTag( TagIdentifiers.POWER_COOLDOWNS ) );
		
		nbt.setTag( TagIdentifiers.POWER_EFFECTS,
				data.getTag( TagIdentifiers.POWER_EFFECTS ) );
		
		nbt.setString( TagIdentifiers.POWER_CURRENT,
				data.getString( TagIdentifiers.POWER_CURRENT ) );
		
		nbt.setInteger( TagIdentifiers.POWER_CURRENT_USELEFT,
				data.getInteger( TagIdentifiers.POWER_CURRENT_USELEFT ) );
		
		nbt.setString( TagIdentifiers.POWER_PRIMARY,
				data.getString( TagIdentifiers.POWER_PRIMARY ) );
		
		nbt.setString( TagIdentifiers.POWER_SECONDARY,
				data.getString( TagIdentifiers.POWER_SECONDARY ) );
		
		nbt.setTag( TagIdentifiers.POWER_SET,
				data.getTag( TagIdentifiers.POWER_SET ) );
		
		nbt.setBoolean( TagIdentifiers.POWER_SUCCESS,
				data.getBoolean( TagIdentifiers.POWER_SUCCESS ) );

		return new DataWrapper( entity );

	}

	private DataWrapper(EntityLivingBase entity) {

		theEntity = entity;
		activePowerEffects = PowerEffect.getActiveEffects( entity );
		powerCoolDowns = Power.getCooldowns( entity );
		powerSet = Power.getLearnedPowers( theEntity );

	}
	
	/**
	 * Sets the primary power. If the entity does not know the power,
	 * it teaches it to them.
	 * 
	 * @param power
	 */
	public void setPrimaryPower(Power power) {
		
		teachPower(power);
		theEntity.getEntityData().setString( TagIdentifiers.POWER_PRIMARY,
				power.getUnlocalizedName() );
	}
	
	/**
	 * Sets the secondary power. If the entity does not know the power,
	 * it teaches it to them.
	 * 
	 * @param power
	 */
	public void setSecondaryPower(Power power) {

		if (knowsPower( power )) theEntity.getEntityData().setString(
				TagIdentifiers.POWER_SECONDARY, power.getUnlocalizedName() );
	}
	
	/**
	 * Gets the power designated as the primary power.
	 * 
	 * @return
	 */
	public Power getPrimaryPower() {
		
		return Power.lookupPower( theEntity.getEntityData().getString(
				TagIdentifiers.POWER_PRIMARY ) );
	}
	
	/**
	 * Gets the power designated as the secondary power.
	 * 
	 * @return
	 */
	public Power getSecondaryPower() {

		return Power.lookupPower( theEntity.getEntityData().getString(
				TagIdentifiers.POWER_SECONDARY ) );
	}
	
	/**
	 * Teaches the entity the designated power.
	 * 
	 * @param power
	 * @return
	 */
	public DataWrapper teachPower(Power power) {

		power.teachPower( theEntity );
		return this;
	}
	
	/**
	 * Detects if the player knows the specfified power.
	 * 
	 * @param power
	 * @return
	 */
	public boolean knowsPower(Power power) {

		for (int i = 0; i < powerSet.tagCount(); i++) {
			if (powerSet.getStringTagAt( i )
					.equals( power.getUnlocalizedName() )) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Causes the player to use the designated power.
	 * 
	 * @param power
	 */
	public void usePower(Power power) {
		
		if (theEntity instanceof EntityPlayer) {
	    	if (power != null && power.canUsePower( theEntity )) {
	    		
	    		if (power.onPreparePower(null, theEntity.worldObj, (EntityPlayer) theEntity)) {
	    			
	    			if ( power.isConcentrationPower() ) {
	    				
	    				if (power.cast(theEntity.worldObj, theEntity, null, 1)) {
	    					setPowerInUse( power );
	    				}
	        			
	    			} else if (power.cast(theEntity.worldObj, theEntity, null, 1)) {
	
	    				if (power.onFinishedCasting(null, theEntity.worldObj, (EntityPlayer) theEntity)) power.triggerCooldown(theEntity);
	
	    			}
	    		}
	    	}
    	}
		
	}
	
	/**
	 * Uses the power designated as primary.
	 */
	public void usePrimaryPower() {
		usePower(getPrimaryPower());
	}
	
	/**
	 * Uses the power designated secondary.
	 */
	public void useSecondaryPower() {
		usePower(getSecondaryPower());
	}
	
	/**
	 * Makes the player stop using the power.
	 */
	public void stopUsingPower() {
		Power power = getPowerInUse();
		
		if (theEntity instanceof EntityPlayer && power != null) {
			if (power.onFinishedCastingEarly( null, theEntity.worldObj,
					(EntityPlayer) theEntity, getPowerUseTimeLeft() )) {
				power.triggerCooldown( theEntity );
			}
			theEntity.getEntityData().setString( TagIdentifiers.POWER_CURRENT, "" );
		}
	}
	
	/**
	 * Sets the current power in use.
	 * 
	 * @param power
	 */
	public void setPowerInUse(Power power) {
		NBTTagCompound nbt = theEntity.getEntityData();
		if (power != null) {
			nbt.setString( TagIdentifiers.POWER_CURRENT, power.getUnlocalizedName() );
			nbt.setInteger( TagIdentifiers.POWER_CURRENT_USELEFT, power.getMaxConcentrationTime() );
		} else {
			nbt.setString( TagIdentifiers.POWER_CURRENT, "" );
			nbt.setInteger( TagIdentifiers.POWER_CURRENT_USELEFT, -1 );
		}
	}

	public Power getPowerInUse() {

		return Power.lookupPower( theEntity.getEntityData().getString(
				TagIdentifiers.POWER_CURRENT ) );
	}

	public int getPowerUseTimeLeft() {

		return theEntity.getEntityData().getInteger(
				TagIdentifiers.POWER_CURRENT_USELEFT );
	}
	
	public void setPowerUseTimeLeft(int x) {
		theEntity.getEntityData().setInteger( TagIdentifiers.POWER_CURRENT_USELEFT, x );
	}

	public boolean isUsingPower() {

		return Power.lookupPower( theEntity.getEntityData()
				.getString( TagIdentifiers.POWER_CURRENT ) ) != null;
	}
	
	public void updateUsingPowers() {
		
		if (theEntity instanceof EntityPlayer) {
			Power power = getPowerInUse();
			int useTime = getPowerUseTimeLeft();
			
			if (power != null) {
				
				if (useTime > 0) {
					
					if ( useTime % 4  == 0 ) {
						
						power.cast(theEntity.worldObj, theEntity, null, 1);
						
					}
					setPowerUseTimeLeft(useTime - 1);
					
				} else if (useTime <= 0) {
	
		    		if( power.onFinishedCasting(null, theEntity.worldObj, (EntityPlayer) theEntity)) power.triggerCooldown(theEntity);
		    		setPowerUseTimeLeft(0);
		    		setPowerInUse(null);
				}
				
			}
			
		}
		
	}

	public void updateCooldowns() {

		for (Object name : this.powerCoolDowns.getKeySet()) {

			String powerName = (String) name;
			int remaining = this.powerCoolDowns.getInteger( powerName );
			Power power = Power.lookupPower( powerName );

			if (remaining > 0 && power != null) {

				power.setCoolDown( theEntity, --remaining );

			}

		}

	}

	public void updatePowerEffects() {

		if (!activePowerEffects.hasNoTags()) {

			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {

				NBTTagCompound nbttagcompound = activePowerEffects
						.getCompoundTagAt( i );

				int timeRemaining = nbttagcompound.getInteger( "duration" );
				EntityLivingBase caster = null;

				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj
						.getEntityByID( nbttagcompound.getInteger( "caster" ) );

				PowerEffect spfx = PowerEffect.getEffectById( nbttagcompound
						.getShort( "id" ) );

				if (spfx != null) {

					boolean shouldNegate = (PowerEffect.negated
							.isEffecting( theEntity ) && spfx.isNegateable());

					if (timeRemaining > 0 && !shouldNegate) {

						if (spfx instanceof IPlayerOnly
								&& theEntity instanceof EntityPlayer) {

							((IPlayerOnly) spfx).onUpdate(
									(EntityPlayer) theEntity, timeRemaining,
									caster );

						} else spfx.onUpdate( theEntity, timeRemaining, caster );

						addPowerEffect( spfx, --timeRemaining, caster );

					}

					else if (timeRemaining < 0 && !shouldNegate) {

						/*if (!theEntity.worldObj.isRemote) {
							PowersAPI.proxy.network
									.sendToAll( new PowerEffectsClient( spfx,
											theEntity, caster, false, 0 ) );
						} 
						}*/
						spfx.onUpdate( theEntity, 0, caster );

					}
				}
			}
		}
	}

	public void updateAll() {
		
		updatePowerEffects();
		updateCooldowns();
		updateUsingPowers();

	}

	public boolean onAttacked(DamageSource ds, float amount) {

		if (!activePowerEffects.hasNoTags()) {

			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {

				NBTTagCompound nbttagcompound = activePowerEffects
						.getCompoundTagAt( i );

				int timeRemaining = nbttagcompound.getInteger( "duration" );
				EntityLivingBase caster = null;

				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj
						.getEntityByID( nbttagcompound.getInteger( "caster" ) );

				PowerEffect spfx = PowerEffect.getEffectById( nbttagcompound
						.getShort( "id" ) );

				if (spfx != null) {

					boolean shouldNegate = (PowerEffect.negated
							.isEffecting( theEntity ) && spfx.isNegateable());

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

				NBTTagCompound nbttagcompound = activePowerEffects
						.getCompoundTagAt( i );

				EntityLivingBase caster = null;

				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj
						.getEntityByID( nbttagcompound.getInteger( "caster" ) );

				PowerEffect spfx = PowerEffect.getEffectById( nbttagcompound
						.getShort( "id" ) );

				if (spfx != null) {

					boolean shouldNegate = (PowerEffect.negated
							.isEffecting( theEntity ) && spfx.isNegateable());

					if (!shouldNegate) {
						return spfx.onAttack( target, ds, amount, caster );
					}
				}
			}
		}
		return amount;
	}

	public float onHurt(DamageSource ds, float amount) {

		if (!activePowerEffects.hasNoTags()) {

			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {

				NBTTagCompound nbttagcompound = activePowerEffects
						.getCompoundTagAt( i );

				EntityLivingBase caster = null;

				if (caster instanceof EntityLivingBase) caster = (EntityLivingBase) theEntity.worldObj
						.getEntityByID( nbttagcompound.getInteger( "caster" ) );

				PowerEffect spfx = PowerEffect.getEffectById( nbttagcompound
						.getShort( "id" ) );

				if (spfx != null) {

					boolean shouldNegate = (PowerEffect.negated
							.isEffecting( theEntity ) && spfx.isNegateable());

					if (!shouldNegate) {

						return spfx.onHurt( ds, amount );

					}
				}
			}
		}

		return amount;
	}

	public NBTTagCompound toNBT() {

		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setTag( "activeEffects", activePowerEffects );
		nbt.setTag( "powerCooldowns", powerCoolDowns );

		return nbt;

	}

	public void copyTo(EntityLivingBase entity) {

		NBTTagCompound nbt = entity.getEntityData();

		nbt.setTag( TagIdentifiers.POWER_COOLDOWNS, powerCoolDowns );
		nbt.setTag( TagIdentifiers.POWER_EFFECTS, activePowerEffects );

	}

	public void addPowerEffect(PowerEffect effect, int duration,
			EntityLivingBase caster) {

		effect.addTo( theEntity, duration, caster );

	}

	public void removePowerEffect(PowerEffect effect) {

		effect.clearFrom( theEntity, null );

	}

	public EntityLivingBase getEntity() {

		return theEntity;
	}

}
