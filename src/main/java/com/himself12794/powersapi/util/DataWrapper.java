package com.himself12794.powersapi.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;

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
	
	private static final String BUTTON_DELAY = Reference.MODID + ".buttonDelay";
	protected EntityLivingBase theEntity;
	protected NBTTagList activePowerEffects;
	protected NBTTagCompound powerCoolDowns;
	protected NBTTagList powerSet;
	/** Unimplemented */
	private NBTTagList learnedAbilitySets;

	public static DataWrapper get(EntityLivingBase entity) {

		return new DataWrapper( entity );
	}
	
	/**
	 * Used to sync entity data client-side 
	 * @param entity
	 * @param data
	 * @return
	 */
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
		
		nbt.setTag( TagIdentifiers.POWER_PREVIOUS_TARGET,
				data.getCompoundTag( TagIdentifiers.POWER_PREVIOUS_TARGET ) );

		return new DataWrapper( entity );

	}

	private DataWrapper(EntityLivingBase entity) {

		theEntity = entity;
		activePowerEffects = PowerEffect.getActiveEffects( entity );
		powerCoolDowns = Power.getCooldowns( entity );
		powerSet = Power.getLearnedPowers( theEntity );

	}

	/**
	 * Sets the primary power. If the entity does not know the power, it teaches
	 * it to them.
	 * 
	 * @param power
	 */
	public void setPrimaryPower(Power power) {

		teachPower( power );
		theEntity.getEntityData().setString( TagIdentifiers.POWER_PRIMARY,
				power.getUnlocalizedName() );
	}

	/**
	 * Sets the secondary power. If the entity does not know the power, it
	 * teaches it to them.
	 * 
	 * @param power
	 */
	public void setSecondaryPower(Power power) {

		teachPower( power );
		theEntity.getEntityData().setString(
				TagIdentifiers.POWER_SECONDARY, power.getUnlocalizedName() );

		System.out.println( "Set secondary power as " + power );
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

	public int getPrimaryPowerCooldownLeft() {

		return getPrimaryPower() != null ? powerCoolDowns
				.getInteger( getPrimaryPower().getUnlocalizedName() ) : 0;
	}

	public int getSecondaryPowerCooldownLeft() {

		return getPrimaryPower() != null ? powerCoolDowns
				.getInteger( getSecondaryPower().getUnlocalizedName() ) : 0;
	}

	public int getCooldownRemaining(Power power) {

		return power != null ? powerCoolDowns
				.getInteger( power.getUnlocalizedName() ) : 0;
	}

	public void triggerCooldown(Power power) {

		if (power != null) {
			power.triggerCooldown( theEntity );
		}
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

	public void setPreviousPowerTarget(MovingObjectPosition pos) {

		theEntity.getEntityData().setTag( TagIdentifiers.POWER_PREVIOUS_TARGET,
				UsefulMethods.movingObjectPosToNBT( pos ) );
	}

	public MovingObjectPosition getPreviousPowerTarget() {

		return UsefulMethods.movingObjectPositionFromNBT(
				theEntity.getEntityData().getCompoundTag(
						TagIdentifiers.POWER_PREVIOUS_TARGET ),
				theEntity.getEntityWorld() );
	}

	public void removePreviousPowerTarget() {

		theEntity.getEntityData().removeTag(
				TagIdentifiers.POWER_PREVIOUS_TARGET );
	}

	/**
	 * Causes the player to use the designated power.
	 * 
	 * @param power
	 */
	public void usePower(Power power) {

		if (theEntity instanceof EntityPlayer) {
			if (power != null && power.canUsePower( theEntity )) {

				theEntity.swingItem();

				if (power.onPreparePower( null, theEntity.worldObj,
						(EntityPlayer) theEntity )) {

					if (power.isConcentrationPower()) {

						if (power.cast( theEntity.worldObj, theEntity, null, 1 )) {
							setPowerInUse( power );
						}

					} else if (power.cast( theEntity.worldObj, theEntity, null,
							1 )) {

						if (power.onFinishedCasting( null, theEntity.worldObj,
								(EntityPlayer) theEntity,
								this.getPreviousPowerTarget() )) power
								.triggerCooldown( theEntity );
						removePreviousPowerTarget();

					}
				}
			}
		}

	}

	/**
	 * Uses the power designated as primary.
	 */
	public void usePrimaryPower() {

		usePower( getPrimaryPower() );
	}

	/**
	 * Uses the power designated secondary.
	 */
	public void useSecondaryPower() {

		usePower( getSecondaryPower() );
	}

	/**
	 * Makes the player stop using the power.
	 */
	public void stopUsingPower() {

		Power power = getPowerInUse();

		if (theEntity instanceof EntityPlayer && power != null) {
			if (power.onFinishedCastingEarly( null, theEntity.worldObj,
					(EntityPlayer) theEntity, getPowerUseTimeLeft(),
					getPreviousPowerTarget() )) {
				power.triggerCooldown( theEntity );
			}
			removePreviousPowerTarget();
			theEntity.getEntityData().setString( TagIdentifiers.POWER_CURRENT,
					"" );
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
			nbt.setString( TagIdentifiers.POWER_CURRENT,
					power.getUnlocalizedName() );
			nbt.setInteger( TagIdentifiers.POWER_CURRENT_USELEFT,
					power.getMaxConcentrationTime() );
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

		theEntity.getEntityData().setInteger(
				TagIdentifiers.POWER_CURRENT_USELEFT, x );
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

				if (theEntity.isSwingInProgress) {
					theEntity.swingProgressInt = 1;
				}

				if (useTime > 0) {

					if (useTime % 4 == 0) {

						power.cast( theEntity.worldObj, theEntity, null, 1 );

					}
					setPowerUseTimeLeft( useTime - 1 );

				} else if (useTime <= 0) {

					if (power.onFinishedCasting( null, theEntity.worldObj,
							(EntityPlayer) theEntity, getPreviousPowerTarget() )) power
							.triggerCooldown( theEntity );
					removePreviousPowerTarget();
					setPowerUseTimeLeft( 0 );
					setPowerInUse( null );
				}

			}

		}

	}

	public PowerEffectContainer getEffectContainer(PowerEffect effect) {

		EntityLivingBase caster = null;
		int timeRemaining = 0;

		if (!activePowerEffects.hasNoTags()) {

			for (int i = 0; i < activePowerEffects.tagCount(); ++i) {

				NBTTagCompound nbttagcompound = activePowerEffects
						.getCompoundTagAt( i );

				PowerEffect temp = PowerEffect.getEffectById( nbttagcompound
						.getShort( "id" ) );
				if (temp != effect) continue;

				timeRemaining = nbttagcompound.getInteger( "duration" );
				Entity tempEntity = theEntity.getEntityWorld().getEntityByID(
						nbttagcompound.getInteger( "caster" ) );
				caster = (EntityLivingBase) (tempEntity != null
						&& tempEntity instanceof EntityLivingBase ? tempEntity
						: null);

			}
		}

		return new PowerEffectContainer( theEntity, caster, timeRemaining,
				effect );

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

				Entity temp = theEntity.worldObj
						.getEntityByID( nbttagcompound.getInteger( "caster" ) );
				EntityLivingBase caster = temp instanceof EntityLivingBase ? (EntityLivingBase) temp
						: null;

				PowerEffect spfx = PowerEffect.getEffectById( nbttagcompound
						.getShort( "id" ) );

				Power power = Power.lookupPower( nbttagcompound
						.getString( "initiatedPower" ) );

				if (spfx != null) {

					boolean shouldNegate = (PowerEffect.negated
							.isEffecting( theEntity ) && spfx.isNegateable());

					if (timeRemaining > 0 && !shouldNegate) {

						if (spfx instanceof IPlayerOnly
								&& theEntity instanceof EntityPlayer) {

							((IPlayerOnly) spfx).onUpdate(
									(EntityPlayer) theEntity, timeRemaining,
									caster );

						} else spfx.onUpdate( theEntity, timeRemaining, caster,
								power );

						addPowerEffect( spfx, --timeRemaining, caster, power );

					}

					else if (timeRemaining < 0 && !shouldNegate) {

						/*
						 * if (!theEntity.worldObj.isRemote) {
						 * PowersAPI.proxy.network .sendToAll( new
						 * PowerEffectsClient( spfx, theEntity, caster, false, 0
						 * ) ); } }
						 */
						spfx.onUpdate( theEntity, -1, caster, power );

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
			EntityLivingBase caster, Power power) {

		effect.addTo( theEntity, duration, caster, power );

	}

	public void addPowerEffect(PowerEffectContainer container) {

		if (container != null) {
			addPowerEffect( container.getTheEffect(),
					container.getTimeRemaining(), container.getCasterEntity(),
					container.getInitiatedPower() );
		}
	}

	public void removePowerEffect(PowerEffect effect) {

		this.addPowerEffect( getEffectContainer( effect ).newWithDuration( 0 ) );
	}

	/**
	 * Removes the effect without triggering
	 * {@link PowerEffect#onRemoval()}. Still triggers cooldowns on linked spells.
	 * 
	 * @param effect
	 */
	public void removePowerEffectSparingly(PowerEffect effect) {
		if (effect == null) return;
		effect.clearQuietly( theEntity );

	}
	
	/**
	 * Clears power effect, triggering linked power cooldown.
	 * 
	 * @return
	 */
	public void removePowerEffectQuietly(PowerEffect effect) {
		if (effect == null) return;
		effect.clear( this.getEffectContainer( effect ) );
	}

	public EntityLivingBase getEntity() {

		return theEntity;
	}
	
	public void setButtonDelay( int delay ) {
		theEntity.getEntityData().setInteger( BUTTON_DELAY, delay );
	}
	
	public int getButtonDelayRemaining() {
		return theEntity.getEntityData().getInteger( BUTTON_DELAY );
	}
	
	public NBTTagCompound getModDataCompound() {
		NBTTagCompound data = null;
		
		if (!theEntity.getEntityData().hasKey( Reference.MODID, 10 )) {
			data = new NBTTagCompound();
			theEntity.getEntityData().setTag( Reference.MODID, data );
		} else {
			data = theEntity.getEntityData().getCompoundTag( Reference.MODID );
		}
		
		return data;
	}

}
