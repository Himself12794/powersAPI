package com.himself12794.powersapi.util;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;

/**
 * Utility class used for easier access of information stored in NBT tag data.
 * 
 * @author Himself12794
 *
 */
// TODO finish central data collection
public class DataWrapper {

	private static final String POWER_GROUP = "power";
	private static final String POWER_CURRENT = "currentPower";
	private static final String POWER_CURRENT_USELEFT = "currentPower.useLeft";
	private static final String POWER_SUCCESS = "success";
	private static final String POWER_COOLDOWNS = "cooldowns";
	private static final String POWER_SET = "powerSet";
	private static final String POWER_PRIMARY = "primaryPower";
	private static final String POWER_SECONDARY = "secondaryPower";
	private static final String POWER_PREVIOUS_TARGET = "previousTarget";
	private static final String POWER_PROFILES = "powerProfiles";

	private static final String POWER_EFFECTS_GROUP = "powerEffects";
	private static final String POWER_EFFECTS = "activeEffects";

	private static final String BUTTON_DELAY = Reference.MODID + ".buttonDelay";
	private static final String LAST_UPDATE = "lastUpdate";
	private static final String HAS_SYNCED = "hasSynced";
	private static final String SUB_MODS = "subModData";

	protected final EntityLivingBase theEntity;

	public final PowerEffectsWrapper powerEffectsData;	

	protected DataWrapper(final EntityLivingBase entity) {

		theEntity = entity;
		powerEffectsData = new PowerEffectsWrapper( entity, getModEntityData() );
	}

	public int getCooldownRemaining(final Power power) {

		if (power == null) return 0;
		final NBTTagCompound coolDowns = getPowerCooldowns();

		return coolDowns.getInteger( power.getUnlocalizedName() );
	}

	public EntityLivingBase getEntity() {

		return theEntity;
	}

	public boolean getHasSynced() {

		return getModEntityData().getBoolean( HAS_SYNCED );
	}

	public int getLastUpdate() {

		return getModEntityData().getInteger( LAST_UPDATE );
	}

	public NBTTagList getLearnedPowers() {

		final NBTTagList names = getPowerData().hasKey( POWER_SET, 9 ) ? (NBTTagList) getPowerData()
				.getTag( POWER_SET )
				: new NBTTagList();
		getPowerData().setTag( POWER_SET, names );

		return names;
	}

	public NBTTagCompound getModEntityData() {
		
		if (theEntity instanceof EntityPlayer) {
			return PowersAPI.getDataHandler().getData( (EntityPlayer) theEntity );
		} else {
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

	private NBTTagList getPowerProfiles() {

		NBTTagList tag;

		if (!getPowerData().hasKey( POWER_PROFILES, 9 )) {
			tag = new NBTTagList();
			getPowerData().setTag( POWER_PROFILES, tag );
		} else {
			tag = (NBTTagList) getPowerData().getTag( POWER_PROFILES );
		}

		return tag;

	}

	public PowerProfile getPowerProfile(Power power) {

		if (!(theEntity instanceof EntityPlayer) || power == null) return null;

		NBTTagCompound profile = null;

		for (int i = 0; i < getPowerProfiles().tagCount(); ++i) {

			profile = (NBTTagCompound) getPowerProfiles().get( i );

			if (Power
					.lookupPower( profile.getString( PowerProfile.POWER_NAME ) ) == power) {
				break;
			}

		}

		if (profile != null) {
			return PowerProfile.getFromNBT( (EntityPlayer) theEntity, profile );
		} else {
			profile = new NBTTagCompound();
			profile.setString( PowerProfile.POWER_NAME,
					power.getUnlocalizedName() );
			getPowerProfiles().appendTag( profile );

			return PowerProfile.getFromNBT( (EntityPlayer) theEntity, profile );

		}

	}

	public NBTTagCompound getPowerData() {

		final NBTTagCompound modData = getModEntityData();
		NBTTagCompound powerData = null;

		if (!modData.hasKey( POWER_GROUP, 10 )) {
			powerData = new NBTTagCompound();
			modData.setTag( POWER_GROUP, powerData );
		} else {
			powerData = modData.getCompoundTag( POWER_GROUP );
		}

		return powerData;
	}

	public PowerEffectsWrapper getPowerEffectsData() {

		return powerEffectsData;
	}

	public Power getPowerInUse() {

		return Power.lookupPower( getPowerData().getString( POWER_CURRENT ) );
	}

	public Set<Power> getPowersAsSet() {

		final Set<Power> powers = Sets.newHashSet();

		final NBTTagList list = getLearnedPowers();

		for (int i = 0; i < list.tagCount(); i++) {
			final String powerName = list.getStringTagAt( i );
			final Power power = Power.lookupPower( powerName );
			if (power != null) powers.add( power );
		}

		return powers;

	}

	public int getPowerUseTimeLeft() {

		return getPowerData().getInteger( POWER_CURRENT_USELEFT );
	}

	public MovingObjectPosition getPreviousPowerTarget() {

		return UsefulMethods.movingObjectPositionFromNBT(
				getPowerData().getCompoundTag( POWER_PREVIOUS_TARGET ),
				theEntity.getEntityWorld() );
	}

	/**
	 * Gets the power designated as the primary power.
	 * 
	 * @return
	 */
	public Power getPrimaryPower() {

		return Power.lookupPower( getPowerData().getString( POWER_PRIMARY ) );
	}

	public int getPrimaryPowerCooldownLeft() {

		return getPrimaryPower() != null ? getPowerCooldowns()
				.getInteger( getPrimaryPower().getUnlocalizedName() ) : 0;
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

	public int getSecondaryPowerCooldownLeft() {

		return getPrimaryPower() != null ? getPowerCooldowns()
				.getInteger( getSecondaryPower().getUnlocalizedName() ) : 0;
	}

	/**
	 * Use this tag to add additional information for a sub mod. It has the
	 * convenience of automatically be preserved across death, and it is saved
	 * to disk as well.
	 * 
	 * @return
	 */
	public NBTTagCompound getSubModData() {

		final NBTTagCompound nbt = getModEntityData();
		NBTTagCompound data = null;

		if (nbt.hasKey( SUB_MODS, 10 )) {
			data = nbt.getCompoundTag( SUB_MODS );
		} else {
			data = new NBTTagCompound();
			nbt.setTag( SUB_MODS, data );
		}

		return data;
	}

	public boolean isUsingPower() {

		return Power.lookupPower( getPowerData()
				.getString( POWER_CURRENT ) ) != null;
	}

	/**
	 * Detects if the player knows the specfified power.
	 * 
	 * @param power
	 * @return
	 */
	public boolean knowsPower(final Power power) {

		if (power == null) return false;

		for (int i = 0; i < getLearnedPowers().tagCount(); ++i) {

			final String powerName = getLearnedPowers().getStringTagAt( i );
			if (powerName.equals( power.getUnlocalizedName() )) return true;

		}

		return false;
	}

	public void removePreviousPowerTarget() {
 
			if (getPowerData().hasKey( POWER_PREVIOUS_TARGET )) getPowerData()
					.removeTag( POWER_PREVIOUS_TARGET );

	}

	/**
	 * Prepares the player to respawn, removing power effects that don't persist after death.
	 * 
	 * @return
	 */
	public DataWrapper resetForRespawn() {

		if (getPowerData().hasKey( POWER_COOLDOWNS )) getPowerData().removeTag(
				POWER_COOLDOWNS );

		final Set<PowerEffect> toRemove = Sets.newHashSet();
		for (int i = 0; i < powerEffectsData.getActiveEffects().tagCount(); i++) {
			final PowerEffect effect = PowerEffect
					.getEffectById( powerEffectsData.getActiveEffects()
							.getCompoundTagAt( i ).getInteger( "id" ) );
			if (!effect.isPersistant()) {
				toRemove.add( effect );
			}
		}

		for (final PowerEffect effect : toRemove) {
			powerEffectsData.removePowerEffectQuietly( effect );
		}

		getPowerData().setString( POWER_CURRENT, "" );
		getPowerData().setInteger( POWER_CURRENT_USELEFT, 0 );
		getPowerData().setTag( POWER_PREVIOUS_TARGET, new NBTTagCompound() );
		setHasSynced( false );

		return this;
	}

	public void setHasSynced(final boolean status) {

		getModEntityData().setBoolean( HAS_SYNCED, status );
	}

	/**
	 * Sets the current power in use.
	 * 
	 * @param power
	 */
	public void setPowerInUse(final Power power) {

		final NBTTagCompound nbt = getPowerData();
		if (power != null) {
			nbt.setString( POWER_CURRENT, power.getUnlocalizedName() );
			nbt.setInteger( POWER_CURRENT_USELEFT,
					power.getMaxConcentrationTime() );
		}
	}

	public void setPowerSuccess(final boolean status) {

		getPowerData().setBoolean( POWER_SUCCESS, status );
	}

	public void setPowerUseTimeLeft(final int x) {

		getPowerData().setInteger( POWER_CURRENT_USELEFT, x );
	}

	public void setPreviousPowerTarget(final MovingObjectPosition pos) {

		getPowerData().setTag( POWER_PREVIOUS_TARGET,
				UsefulMethods.movingObjectPosToNBT( pos ) );
	}

	/**
	 * Sets the primary power. If the entity does not know the power, it teaches
	 * it to them.
	 * 
	 * @param power
	 */
	public void setPrimaryPower(final Power power) {

		if (power == null || getPrimaryPower() == power) return;

		if (!knowsPower( power )) teachPower( power );
		
		getPowerData().setString( POWER_PRIMARY, power.getUnlocalizedName() );
		
		final ChatComponentTranslation message = new ChatComponentTranslation(
				"command.setPrimaryPower", power.getDisplayName() );

		theEntity.addChatMessage( message );
	}

	/**
	 * Sets the secondary power. If the entity does not know the power, it
	 * teaches it to them.
	 * 
	 * @param power
	 */
	public void setSecondaryPower(final Power power) {

		if (power == null) return;

		teachPower( power );
		getPowerData().setString( POWER_SECONDARY, power.getUnlocalizedName() );

		final ChatComponentTranslation message = new ChatComponentTranslation(
				"command.setSecondaryPower", power.getDisplayName() );

		if (theEntity instanceof EntityPlayer) theEntity
				.addChatMessage( message );
	}

	/**
	 * Makes the player stop using the power.
	 */
	public void stopUsingPower() {

		final Power power = getPowerInUse();
		
		setPowerUseTimeLeft(0);
		
		if (theEntity instanceof EntityPlayer && power != null) {

			final boolean flag = power.onFinishedCastingEarly(
					theEntity.worldObj,
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
	 * Teaches the entity the designated power.
	 * 
	 * @param power
	 * @return
	 */
	public void teachPower(final Power power) {

		final NBTTagList learnedPowers = getLearnedPowers();

		if (knowsPower( power )) return;

		learnedPowers
				.appendTag( new NBTTagString( power.getUnlocalizedName() ) );
		getPowerData().setTag( POWER_SET, learnedPowers );

		theEntity.addChatMessage( new ChatComponentTranslation(
				"command.power.learned", power.getDisplayName() ) );
	}

	@Override
	public String toString() {

		return getModEntityData().toString();
	}

	public void triggerCooldown(final Power power) {

		if (power != null) {
			power.triggerCooldown( theEntity );
		}
	}

	public void updateAll() {
		
		powerEffectsData.updatePowerEffects();
		updateCooldowns();
		updateUsingPowers();

	}

	public void updateCooldowns() {

		for (final Object name : this.getPowerCooldowns().getKeySet()) {

			final String powerName = (String) name;
			int remaining = this.getPowerCooldowns().getInteger( powerName );
			final Power power = Power.lookupPower( powerName );

			if (remaining > 0 && power != null) {

				power.setCoolDown( theEntity, --remaining );

			}

		}

	}

	public void updateUsingPowers() {

		if (theEntity instanceof EntityPlayer) {
			final Power power = getPowerInUse();
			final int useTime = getPowerUseTimeLeft();

			if (power != null) {

				if (theEntity.isSwingInProgress) {
					theEntity.swingProgressInt = 1;
				}

				if (useTime > 0) {
					PowersAPI.logger.info( (theEntity.worldObj.isRemote ? "client" : "server") + " use tick " + useTime);
					if (useTime % 4 == 0) {
						
						PowersAPI.logger.info((theEntity.worldObj.isRemote ? "client" : "server") + " cast tick " + useTime);
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

	/**
	 * Causes the player to use the designated power.
	 * 
	 * @param power
	 */
	public void usePower(final Power power) {

		if (theEntity instanceof EntityPlayer) {
			if (power != null && power.canUsePower( theEntity )) {
				if (power.onPreparePower( theEntity.worldObj,
						(EntityPlayer) theEntity )) {

					theEntity.swingItem();
					System.out.println("power cast");

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

	public boolean wasPowerSuccess() {

		return getPowerData().getBoolean( POWER_SUCCESS );
	}
	
	public int getButtonDelay() {
		return getModEntityData().getInteger( BUTTON_DELAY );
	}

	public static DataWrapper get(final EntityLivingBase entity) {
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
	public static DataWrapper set(final EntityLivingBase entity,
			final NBTTagCompound data) {

		if (entity instanceof EntityPlayer) return PowersAPI.getDataHandler().updateEntity( (EntityPlayer) entity, data );

		final NBTTagCompound nbt = entity.getEntityData();

		nbt.setTag( Reference.MODID, data );
		
		return new DataWrapper(entity);

	}

}
