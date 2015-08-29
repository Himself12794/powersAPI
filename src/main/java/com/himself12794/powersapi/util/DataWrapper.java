package com.himself12794.powersapi.util;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.IPlayerOnly;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;

/**
 * Utility class used for easier access of information stored in NBT tag data.
 * 
 * @author Himself12794
 *
 */
public class DataWrapper {

	protected static final String POWER_GROUP = "power";
	protected static final String POWER_CURRENT = "currentPower";
	protected static final String POWER_CURRENT_USELEFT = "currentPower.useLeft";
	protected static final String POWER_SUCCESS = "success";
	protected static final String POWER_COOLDOWNS = "cooldowns";
	protected static final String POWER_SET = "powerSet";
	protected static final String POWER_PRIMARY = "primaryPower";
	protected static final String POWER_SECONDARY = "primarySecondary";
	protected static final String POWER_PREVIOUS_TARGET = "previousTarget";

	protected static final String POWER_EFFECTS_GROUP = "powerEffects";
	protected static final String POWER_EFFECTS = "activeEffects";

	protected static final String BUTTON_DELAY = Reference.MODID + ".buttonDelay";
	protected static final String LAST_UPDATE = "lastUpdate";
	protected static final String HAS_SYNCED = "hasSynced";

	protected EntityLivingBase theEntity;

	public static DataWrapper get(EntityLivingBase entity) {
		if (entity == null) {
			throw new NullPointerException("Entity is null");
		}
		return new DataWrapper( entity );
	}

	/**
	 * Used to sync entity data client-side. Sets the mod data tag as data, so
	 * be careful when using this.
	 * 
	 * @param entity
	 * @param data
	 * @return
	 */
	public static DataWrapper set(EntityLivingBase entity, NBTTagCompound data) {
		
		NBTTagCompound nbt = entity.getEntityData();

		nbt.setTag( Reference.MODID, data );
		
		//System.out.println(nbt);

		return new DataWrapper( entity );

	}

	protected DataWrapper(EntityLivingBase entity) {
		
		/*if (entity instanceof EntityPlayer) {
			NBTTagCompound data = entity.getEntityData().getCompoundTag( Reference.MODID );
			if (data.hasKey( POWER_GROUP )) {
				if (!data.getCompoundTag( POWER_GROUP ).hasKey( POWER_SET )) {
					Thread.dumpStack();
				}
			}
		}*/
		
		theEntity = entity;
	}

	/**
	 * Sets the primary power. If the entity does not know the power, it teaches
	 * it to them.
	 * 
	 * @param power
	 */
	public void setPrimaryPower(Power power) {
		
		if (power == null || getPrimaryPower() == power) return;
		
		if (!knowsPower(power)) teachPower( power );
		
		getPowerData().setString( POWER_PRIMARY, power.getUnlocalizedName() );

		ChatComponentTranslation message = new ChatComponentTranslation(
				"command.setPrimaryPower", power.getDisplayName() );

		if (theEntity instanceof EntityPlayer && !theEntity.worldObj.isRemote) theEntity
				.addChatMessage( message );
	}

	/**
	 * Sets the secondary power. If the entity does not know the power, it
	 * teaches it to them.
	 * 
	 * @param power
	 */
	public void setSecondaryPower(Power power) {

		if (power == null) return;

		teachPower( power );
		getPowerData().setString( POWER_SECONDARY, power.getUnlocalizedName() );

		ChatComponentTranslation message = new ChatComponentTranslation(
				"command.setSecondaryPower", power.getDisplayName() );

		if (theEntity instanceof EntityPlayer) theEntity
				.addChatMessage( message );
	}

	/**
	 * Gets the power designated as the primary power.
	 * 
	 * @return
	 */
	public Power getPrimaryPower() {

		return Power.lookupPower( getPowerData().getString( POWER_PRIMARY ) );
	}

	/**
	 * Gets the power designated as the secondary power.
	 * 
	 * @return
	 */
	public Power getSecondaryPower() {

		return Power.lookupPower( getPowerData().getString(
				POWER_SECONDARY ) );
	}

	public int getPrimaryPowerCooldownLeft() {

		return getPrimaryPower() != null ? getPowerCooldowns()
				.getInteger( getPrimaryPower().getUnlocalizedName() ) : 0;
	}

	public int getSecondaryPowerCooldownLeft() {

		return getPrimaryPower() != null ? getPowerCooldowns()
				.getInteger( getSecondaryPower().getUnlocalizedName() ) : 0;
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
	public void teachPower(Power power) {
		
        NBTTagList learnedPowers = getLearnedPowers();
        
        if (knowsPower(power)) return;
        
        learnedPowers.appendTag( new NBTTagString(power.getUnlocalizedName()) );
        getPowerData().setTag(POWER_SET, learnedPowers);
        
        theEntity.addChatMessage( new ChatComponentTranslation("command.power.learned", power.getDisplayName()) );
	}

	/**
	 * Detects if the player knows the specfified power.
	 * 
	 * @param power
	 * @return
	 */
	public boolean knowsPower(Power power) {

		if (power == null) return false;

        for (int i = 0; i < getLearnedPowers().tagCount(); ++i) {
        	
            String powerName = getLearnedPowers().getStringTagAt(i);
            if (powerName.equals( power.getUnlocalizedName() )) return true;
            
        }
        
        return false;
	}

	public void setPreviousPowerTarget(MovingObjectPosition pos) {

		getPowerData().setTag( POWER_PREVIOUS_TARGET,
				UsefulMethods.movingObjectPosToNBT( pos ) );
	}

	public MovingObjectPosition getPreviousPowerTarget() {

		return UsefulMethods.movingObjectPositionFromNBT(
				getPowerData().getCompoundTag( POWER_PREVIOUS_TARGET ),
				theEntity.getEntityWorld() );
	}

	public void removePreviousPowerTarget() {

		if (getPowerData().hasKey( POWER_PREVIOUS_TARGET )) getPowerData()
				.removeTag( POWER_PREVIOUS_TARGET );
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

				if (power.onPreparePower( theEntity.worldObj,
						(EntityPlayer) theEntity )) {

					if (power.isConcentrationPower()) {

						if (power.cast( theEntity.worldObj, theEntity, 1.0F )) {
							setPowerInUse( power );
						}

					} else if (power.cast( theEntity.worldObj, theEntity, 1.0F )) {

						if (power.onFinishedCasting( theEntity.worldObj,
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

			boolean flag = power.onFinishedCastingEarly( theEntity.worldObj,
					(EntityPlayer) theEntity, getPowerUseTimeLeft(),
					getPreviousPowerTarget() );

			if (flag) {
				power.triggerCooldown( theEntity );
			}

			removePreviousPowerTarget();
			getPowerData().setString( POWER_CURRENT, "" );
		}
	}

	/**
	 * Sets the current power in use.
	 * 
	 * @param power
	 */
	public void setPowerInUse(Power power) {

		NBTTagCompound nbt = getPowerData();
		if (power != null) {
			nbt.setString( POWER_CURRENT, power.getUnlocalizedName() );
			nbt.setInteger( POWER_CURRENT_USELEFT,
					power.getMaxConcentrationTime() );
		}
	}

	public Power getPowerInUse() {

		return Power.lookupPower( getPowerData().getString( POWER_CURRENT ) );
	}

	public int getPowerUseTimeLeft() {

		return getPowerData().getInteger( POWER_CURRENT_USELEFT );
	}

	public void setPowerUseTimeLeft(int x) {

		getPowerData().setInteger( POWER_CURRENT_USELEFT, x );
	}

	public boolean isUsingPower() {

		return Power.lookupPower( getPowerData()
				.getString( POWER_CURRENT ) ) != null;
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
						power.cast( theEntity.worldObj, theEntity, 1 );
					}
					setPowerUseTimeLeft( useTime - 1 );

				} else if (useTime <= 0) {

					if (power.onFinishedCasting( theEntity.worldObj,
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

		if (!getActiveEffects().hasNoTags()) {

			for (int i = 0; i < getActiveEffects().tagCount(); ++i) {

				NBTTagCompound nbttagcompound = getActiveEffects()
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

		for (Object name : this.getPowerCooldowns().getKeySet()) {

			String powerName = (String) name;
			int remaining = this.getPowerCooldowns().getInteger( powerName );
			Power power = Power.lookupPower( powerName );

			if (remaining > 0 && power != null) {

				power.setCoolDown( theEntity, --remaining );

			}

		}

	}

	public void updatePowerEffects() {

		if (!getActiveEffects().hasNoTags()) {

			for (int i = 0; i < getActiveEffects().tagCount(); ++i) {

				NBTTagCompound nbttagcompound = getActiveEffects()
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

						spfx.onUpdate( theEntity, -1, caster, power );

					}
				}
			}
		}
	}
	
	public void updateTimers() {
		int lastUpdate = getLastUpdate();
		
		if (lastUpdate <= 0) {
			getModEntityData().setInteger( LAST_UPDATE, 20 );
		} else {
			getModEntityData().setInteger( LAST_UPDATE, lastUpdate - 1 );
		}
	}

	public void updateAll() {
		
		updatePowerEffects();
		updateCooldowns();
		updateUsingPowers();
		updateTimers();

	}

	public boolean onAttacked(DamageSource ds, float amount) {

		if (!getActiveEffects().hasNoTags()) {

			for (int i = 0; i < getActiveEffects().tagCount(); ++i) {

				NBTTagCompound nbttagcompound = getActiveEffects()
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

		if (!getActiveEffects().hasNoTags()) {

			for (int i = 0; i < getActiveEffects().tagCount(); ++i) {

				NBTTagCompound nbttagcompound = getActiveEffects()
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

		if (!getActiveEffects().hasNoTags()) {

			for (int i = 0; i < getActiveEffects().tagCount(); ++i) {

				NBTTagCompound nbttagcompound = getActiveEffects()
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
	 * Removes the effect without triggering {@link PowerEffect#onRemoval()} or
	 * activating linked power cooldown.
	 * 
	 * @param effect
	 */
	public void removePowerEffectQuietly(PowerEffect effect) {

		if (effect == null) return;
		effect.clearQuietly( theEntity );

	}

	/**
	 * Clears power effect, triggering linked power cooldown.
	 * 
	 * @return
	 */
	public void removePowerEffectSparingly(PowerEffect effect) {

		if (effect == null) return;
		effect.clear( this.getEffectContainer( effect ) );
	}

	public EntityLivingBase getEntity() {

		return theEntity;
	}

	public void setButtonDelay(int delay) {

		theEntity.getEntityData().setInteger( BUTTON_DELAY, delay );
	}

	public int getButtonDelayRemaining() {

		return theEntity.getEntityData().getInteger( BUTTON_DELAY );
	}

	public NBTTagCompound getModEntityData() {

		NBTTagCompound data = null;

		if (!theEntity.getEntityData().hasKey( Reference.MODID, 10 )) {
			data = new NBTTagCompound();
			theEntity.getEntityData().setTag( Reference.MODID, data );
		} else {
			data = theEntity.getEntityData().getCompoundTag( Reference.MODID );
		}

		return data;
	}

	public NBTTagCompound getPowerData() {

		NBTTagCompound modData = getModEntityData();
		NBTTagCompound powerData = null;

		if (!modData.hasKey( POWER_GROUP, 10 )) {
			powerData = new NBTTagCompound();
			modData.setTag( POWER_GROUP, powerData );
		} else {
			powerData = modData.getCompoundTag( POWER_GROUP );
		}

		return powerData;
	}

	public NBTTagCompound getPowerEffectData() {

		NBTTagCompound modData = getModEntityData();
		NBTTagCompound powerData = null;

		if (!modData.hasKey( POWER_EFFECTS_GROUP, 10 )) {
			powerData = new NBTTagCompound();
			modData.setTag( POWER_EFFECTS_GROUP, powerData );
		} else {
			powerData = modData.getCompoundTag( POWER_EFFECTS_GROUP );
		}

		return powerData;
	}

	public NBTTagList getLearnedPowers() {

		NBTTagList names = getPowerData().hasKey( POWER_SET, 9 ) ? (NBTTagList) getPowerData()
				.getTag( POWER_SET ) : new NBTTagList();
		getPowerData().setTag( POWER_SET, names );

		return names;
	}

	public NBTTagCompound getPowerCooldowns() {

		NBTTagCompound cooldowns = null;

		if (!getPowerData().hasKey( POWER_COOLDOWNS, 10 )) {
			cooldowns = new NBTTagCompound();
			getPowerData().setTag( POWER_COOLDOWNS, cooldowns );
		} else {
			cooldowns = getPowerData().getCompoundTag( POWER_COOLDOWNS );
		}

		return cooldowns;
	}
	
	public int getCooldownRemaining(Power power) {
		
		if (power == null) return 0;
		NBTTagCompound coolDowns = getPowerCooldowns();
		
		return coolDowns.getInteger(power.getUnlocalizedName());
	}
	
	public NBTTagList getActiveEffects() {
		NBTTagCompound effectData = getPowerEffectData();
		NBTTagList activeEffects = null;
		
		if (effectData.hasKey(POWER_EFFECTS, 9)) { 
			activeEffects = (NBTTagList)effectData.getTag(POWER_EFFECTS);
		} else {
			activeEffects = new NBTTagList();
			effectData.setTag( POWER_EFFECTS, activeEffects );
		}
		
		return activeEffects;
	}
	
	public boolean wasPowerSuccess() {
		return getPowerData().getBoolean( POWER_SUCCESS );
	}
	
	public void setPowerSuccess(boolean status) {
		getPowerData().setBoolean( POWER_SUCCESS, status );
	}
	
	public boolean getHasSynced() {
		return getModEntityData().getBoolean( HAS_SYNCED );
	}
	
	public void setHasSynced(boolean status) {
		getModEntityData().setBoolean( HAS_SYNCED, status );
	}
	
	public int getLastUpdate() {
		return getModEntityData().getInteger( LAST_UPDATE );
	}
	
	public Set<Power> getPowersAsSet() {
		
		Set<Power> powers = Sets.newHashSet();
		
		NBTTagList list = getLearnedPowers();
		
		for (int i = 0; i < list.tagCount(); i++) {
			String powerName = list.getStringTagAt( i );
			Power power = Power.lookupPower( powerName );
			if (power != null) powers.add( power );
		}
		
		return powers;
		
	}
	
	@Override
	public String toString() {
		return getModEntityData().toString();
	}
	
}
