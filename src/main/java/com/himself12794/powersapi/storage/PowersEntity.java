package com.himself12794.powersapi.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.Reference;
import com.himself12794.powersapi.util.UsefulMethods;

/**
 * Used to manage the powers logic for an entity that can use them.
 * 
 * @author Himself12794
 *
 */
public class PowersEntity extends PropertiesBase {

	private static final String POWER_GROUP = Reference.MODID + ":power";
	private static final String POWER_CURRENT = "currentPower";
	private static final String POWER_CURRENT_USELEFT = "currentPower.useLeft";
	private static final String POWER_SET = "powerSet";
	private static final String POWER_PRIMARY = "primaryPower";
	private static final String POWER_SECONDARY = "secondaryPower";
	private static final String POWER_PREVIOUS_TARGET = "previousTarget";
	private static final String POWER_PROFILES = "powerProfiles";
	private static final int tickDelay = 2;

	private int primaryPreparationTime;
	private int secondaryPreparationTime;
	private boolean isBeingPreparedPrimary;
	private boolean isBeingPreparedSecondary;
	private int primaryUseTimeLeft;
	private int secondaryUseTimeLeft;
	private boolean secondaryPowerInUse;
	private boolean primaryPowerInUse;
	/**
	 * Information about the powers that the player has known. Just because a
	 * player has a power profile for a specific power does not mean that
	 * they currently know that power.
	 */
	public final Map<Power, PowerProfile> powerProfiles = Maps.newHashMap();
	/** The powers that player currently knows */
	public final Set<Power> learnedPowers = Sets.newHashSet();
	private Power primaryPower;
	private Power secondaryPower;
	/** Used so the same target can be used for power completion methods */
	public MovingObjectPosition prevTargetPosPrimary;
	public MovingObjectPosition prevTargetPosSecondary;
	/** Used to track updates for instant powers */
	public MovingObjectPosition mouseOverPosPrimary;
	public MovingObjectPosition mouseOverPosSecondary;

	protected PowersEntity(EntityPlayer entity) {
		super( entity );
	}

	public int getCooldownRemaining(final Power power) {

		if (powerProfiles.containsKey( power )) {
			return powerProfiles.get( power ).cooldownRemaining;
		} else {
			return 0;
		}

	}

	public EntityLivingBase getEntity() {

		return theEntity;
	}

	public Collection getLearnedPowers() {

		return learnedPowers;
	}

	private int[] getLearnedPowersAsIntArray() {
		
		int[] entries = new int[learnedPowers.size()];
		
		int i = 0;
		for (Power power : learnedPowers) {
			if (power == null) continue;
			entries[i] = power.getId();
			i++;
		}
		
		
		
		return entries;
		
	}

	public PowerProfile getPowerProfile(Power power) {

		if (powerProfiles.containsKey( power )) {
			return powerProfiles.get( power );
		} else if (power != null){

			powerProfiles.put( power, new PowerProfile( theEntity, power, null ) );
			return powerProfiles.get( power );
		} 
		
		return null;
	}

	public int getPowerInUseTimeLeft(Power power) {
		return power == secondaryPower ? secondaryUseTimeLeft : (power == primaryPower ? primaryUseTimeLeft : 0);
	}

	private NBTTagList getPowerProfilesAsNBTTagList() {
		
		NBTTagList entries = new NBTTagList();
		
		for (PowerProfile profile : powerProfiles.values() ) {
			NBTTagCompound entry = profile.getAsNBTTagCompound();
			if (entry != null) entries.appendTag( entry );
			
		}
		
		return entries;
	}

	public int getPreparationTimeLeft(Power power) {
		return power == secondaryPower ? secondaryPreparationTime : (power == primaryPower ? primaryPreparationTime : 0);
	}

	public MovingObjectPosition getPreviousPrimaryPowerTarget() {

		return prevTargetPosPrimary;
	}

	public MovingObjectPosition getPreviousSecondaryPowerTarget() {

		return prevTargetPosSecondary;
	}

	/**
	 * Gets the power designated as the primary power.
	 * 
	 * @return
	 */
	public Power getPrimaryPower() {

		return primaryPower;
	}

	public int getPrimaryPowerCooldownLeft() {

		return getPowerProfile( primaryPower ).cooldownRemaining;
	}

	public int getPrimaryPowerUseTimeLeft() {

		return primaryUseTimeLeft;
	}

	/**
	 * Gets the power designated as the secondary power.
	 * 
	 * @return
	 */
	public Power getSecondaryPower() {

		return secondaryPower;
	}

	public int getSecondaryPowerCooldownLeft() {

		return getPowerProfile( secondaryPower ).cooldownRemaining;
	}

	public int getSecondaryPowerUseTimeLeft() {

		return secondaryUseTimeLeft;
	}

	@Override
	public void init(Entity entity, World world) {

	}
	
	public boolean isPowerInUse(Power power) {
		return primaryPowerInUse || secondaryPowerInUse;
	}
	
	public boolean isUsingPrimaryPower() {

		return primaryPowerInUse;
	}
	
	public boolean isUsingSecondaryPower() {

		return secondaryPowerInUse;
	}

	/**
	 * Detects if the player knows the specfified power.
	 * 
	 * @param power
	 * @return
	 */
	public boolean knowsPower(final Power power) {

		return learnedPowers.contains( power );
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		
		if (compound.hasKey( Reference.MODID, 10 )) {
			
			NBTTagCompound modData = compound.getCompoundTag( Reference.MODID );
			
			if (modData.hasKey( POWER_GROUP, 10 )) {
				
				NBTTagCompound powersData = modData.getCompoundTag( POWER_GROUP );
				
				//if (theEntity instanceof EntityPlayer) PowersAPI.logger.info( powersData );
			
				if (powersData.hasKey( POWER_PROFILES, 9 )) {
						
					NBTTagList powerProfiles = powersData.getTagList( POWER_PROFILES, 10 );
					
					for (int i = 0; i < powerProfiles.tagCount(); i++) {
						
						PowerProfile profile = PowerProfile.getFromNBT( theEntity, powerProfiles.getCompoundTagAt( i ) );
						
						if (profile != null) {
							this.powerProfiles.put( profile.thePower, profile );
						}
											
					}
					
				}
				
				if (powersData.hasKey( POWER_SET, 11 )) {
					
					int[] learnedPowers = powersData.getIntArray( POWER_SET );
					
					for (int i = 0; i < learnedPowers.length; i++) {
						
						Power power = PowersRegistry.lookupPowerById( learnedPowers[i] );
						
						if (power != null) {
							this.learnedPowers.add( power );
						}
					}
				}
				
				primaryPower = PowersRegistry.lookupPowerById( powersData.getInteger( POWER_PRIMARY ) );
				secondaryPower = PowersRegistry.lookupPowerById( powersData.getInteger( POWER_SECONDARY ) );
			}
		}

	}

	@Override
	public void onJoinWorld(World world) {

		removeNullPowers();
		
	}

	@Override
	public void onUpdate() {
		
		updateCooldowns();
		updateKnowledgeTicks();
		updateUsingPrimaryPower();
		updateUsingSecondaryPower();

	}

	/**
	 * Added in case a power is removed from the mod while an entity knows it.
	 */
	private void removeNullPowers() {
		if (learnedPowers.contains( null )) {
			learnedPowers.remove( null );
		}
	}
	
	/**
	 * Prepares the player to respawn, removing power effects that don't persist
	 * after death.
	 */
	@Override
	public void resetForRespawn() {

		for (PowerProfile profile : powerProfiles.values()) {
			profile.cooldownRemaining = 0;
		}

	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		
		NBTTagCompound modData = UsefulMethods.getPutKeyCompound( Reference.MODID, compound );
		NBTTagCompound powersData = UsefulMethods.getPutKeyCompound( POWER_GROUP, modData );
		
		powersData.setTag( POWER_PROFILES, getPowerProfilesAsNBTTagList() );
		powersData.setIntArray( POWER_SET, getLearnedPowersAsIntArray() );
		powersData.setInteger( POWER_PRIMARY, primaryPower != null ? primaryPower.getId() : 0 );
		powersData.setInteger( POWER_SECONDARY, secondaryPower != null ? secondaryPower.getId() : 0);
		
	}

	/**
	 * Sets the primary power. If the entity does not know the power, it teaches
	 * it to them.
	 * 
	 * @param power
	 */
	public void setPrimaryPower(final Power power) {

		if (power == null || primaryPower == power || isPowerInUse(power)) return;
		
		if (secondaryPower == power) {
			secondaryPower = primaryPower;
			primaryPower = power;
		}

		if (!this.knowsPower( power )) teachPower(power);
		primaryPower = power;
		
		final ChatComponentTranslation message = new ChatComponentTranslation(
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
	public void setSecondaryPower(final Power power) {

		if (power == null || secondaryPower == power || isPowerInUse(power)) return;
		
		if (primaryPower == power) {
			primaryPower = secondaryPower;
			secondaryPower = power;
		}
		
		if (!this.knowsPower( power )) teachPower(power);
		secondaryPower = power;
		
		final ChatComponentTranslation message = new ChatComponentTranslation(
				"command.setSecondaryPower", power.getDisplayName() );

		if (theEntity instanceof EntityPlayer && !theEntity.worldObj.isRemote) theEntity
				.addChatMessage( message );
	}
	
	/**
	 * Makes the player stop using the power.
	 */
	public void stopUsingPrimaryPower() {
		
		if (theEntity instanceof EntityPlayer && primaryPowerInUse) {
			
			theEntity.isSwingInProgress = false;
			theEntity.swingProgressInt = 0;
			
			if (!isBeingPreparedPrimary) {
				
				if (!(EffectsEntity.get( theEntity ).isAffectedBy( PowerEffect.negated ) && primaryPower.isNegateable())) {
					final boolean flag = primaryPower.onFinishedCastingEarly(
							theEntity.worldObj,
							(EntityPlayer) theEntity, getPrimaryPowerUseTimeLeft(),
							getPreviousPrimaryPowerTarget(), getPowerProfile( primaryPower ).getState() );
		
					if (flag) {
						getPowerProfile( primaryPower ).triggerCost();
					}
					
				} else {
					getPowerProfile( primaryPower ).triggerCost();
				}
				
			}
			
			primaryPreparationTime = 0;
			isBeingPreparedPrimary = false;
			primaryUseTimeLeft = 0;
			prevTargetPosPrimary = null;
			mouseOverPosPrimary = null;
			primaryPowerInUse = false;
		}
	}

	/**
	 * Makes the player stop using the power.
	 */
	public void stopUsingSecondaryPower() {
		
		if (theEntity instanceof EntityPlayer && secondaryPowerInUse) {
			
			if (!isBeingPreparedSecondary) {
				
				if (!(EffectsEntity.get( theEntity ).isAffectedBy( PowerEffect.negated ) && secondaryPower.isNegateable())) {
					final boolean flag = secondaryPower.onFinishedCastingEarly(
							theEntity.worldObj,
							(EntityPlayer) theEntity, getSecondaryPowerUseTimeLeft(),
							getPreviousSecondaryPowerTarget(), getPowerProfile( secondaryPower ).getState() );
		
					if (flag) {
						getPowerProfile( secondaryPower ).triggerCost();
					}
					
				} else {
					getPowerProfile( secondaryPower ).triggerCost();
				}
				
			}
			
			secondaryPreparationTime = 0;
			isBeingPreparedSecondary = false;
			secondaryUseTimeLeft = 0;
			prevTargetPosSecondary = null;
			mouseOverPosSecondary = null;
			secondaryPowerInUse = false;
		}
	}
	
	/**
	 * Teaches the entity the designated power.
	 * 
	 * @param power
	 * @return
	 */
	public void teachPower(final Power power) {

		if (!learnedPowers.contains( power )) {
			learnedPowers.add( power );
		}
	}
	
	public void removePower( Power power ) {
		
		if (knowsPower( power )) {
			learnedPowers.remove( power );
			if (primaryPower == power) primaryPower = null;
			if (secondaryPower == power) secondaryPower = null;
		}
		
	}

	@Override
	public String toString() {
		
		StringBuilder result = new StringBuilder(); 
		
		result.append( "PowersWrapper[Entity:" );
		result.append( theEntity.getName() );
		result.append( ",PrimaryPower:" );
		result.append( primaryPower );
		result.append( ",SecondaryPower:" );
		result.append( secondaryPower );
		result.append( ",LearnedPowers:" );
		result.append( learnedPowers );
		result.append( "]" );
		
		return result.toString();
		
	}

	public void triggerCooldown( Power power ) {

		if (power != null) {
			PowerProfile profile = getPowerProfile( power );
			profile.triggerCost();
		}
	}

	private void updateCooldowns() {

		for (PowerProfile profile : powerProfiles.values()) {

			if (profile.cooldownRemaining > 0) {
				profile.cooldownRemaining--;
			}

		}

	}

	private void updateKnowledgeTicks() {
		
		for (PowerProfile profile : powerProfiles.values()) {
			if (this.knowsPower( profile.thePower )) profile.update();
		}
		
	}
	
	private void updateUsingPrimaryPower() {

		if (theEntity instanceof EntityPlayer) {

			if (primaryPowerInUse) {

				PowerProfile profile = getPowerProfile( primaryPower );
				
				if (!isBeingPreparedPrimary) {
					
					if (primaryPower.isNegateable() && EffectsEntity.get( theEntity ).isAffectedBy( PowerEffect.negated )) {
						stopUsingPrimaryPower();
						return;
					}
	
					if (theEntity.isSwingInProgress) {
						theEntity.swingProgressInt = 1;
					}
	
					if (primaryUseTimeLeft > 0) {
	
						if (primaryUseTimeLeft % tickDelay == 0) {
							if (primaryPower.cast( theEntity.worldObj, theEntity, mouseOverPosPrimary, profile.useModifier, profile.getState() ))
								prevTargetPosPrimary = mouseOverPosPrimary;
						}
						this.primaryUseTimeLeft--;
	
					} else if (this.primaryUseTimeLeft <= 0) {
	
						if (primaryPower.onFinishedCasting( theEntity.worldObj, (EntityPlayer) theEntity, prevTargetPosPrimary, profile.getState() )) 
							profile.triggerCost();
						
						theEntity.swingProgress = 0.0F;
						theEntity.isSwingInProgress = false;
						prevTargetPosPrimary = null;
						primaryPreparationTime = 0;
						primaryUseTimeLeft = 0;
						primaryPowerInUse = false;
						mouseOverPosPrimary = null;
					}
	
				} else {
					primaryPower.onPrepareTick( (EntityPlayer) theEntity, theEntity.worldObj, profile, primaryPreparationTime );
					theEntity.swingProgressInt = 0;

					usePowerAsPrimary(primaryPower, mouseOverPosPrimary, primaryPreparationTime);
					primaryPreparationTime--;
				}
				
			}

		}

	}
	
	private void updateUsingSecondaryPower() {

		if (theEntity instanceof EntityPlayer) {

			if (secondaryPowerInUse) {

				PowerProfile profile = getPowerProfile( secondaryPower );
				
				if (!isBeingPreparedSecondary) {
					
					if (secondaryPower.isNegateable() && EffectsEntity.get( theEntity ).isAffectedBy( PowerEffect.negated )) {
						stopUsingSecondaryPower();
						return;
					}
	
					if (this.secondaryUseTimeLeft > 0) {
	
						if (this.secondaryUseTimeLeft % tickDelay == 0) {
							if (secondaryPower.cast( theEntity.worldObj, theEntity, mouseOverPosSecondary, profile.useModifier, profile.getState() ))
								prevTargetPosSecondary = mouseOverPosSecondary;
						}
						this.secondaryUseTimeLeft--;
	
					} else if (this.secondaryUseTimeLeft <= 0) {
	
						if (secondaryPower.onFinishedCasting( theEntity.worldObj, (EntityPlayer) theEntity, prevTargetPosSecondary, profile.getState() )) 
							profile.triggerCost();
						
						prevTargetPosSecondary = null;
						secondaryPreparationTime = 0;
						secondaryUseTimeLeft = 0;
						secondaryPowerInUse = false;
						mouseOverPosSecondary = null;
					}
	
				} else {
					secondaryPower.onPrepareTick( (EntityPlayer) theEntity, theEntity.worldObj, profile, secondaryPreparationTime );
					usePowerAsSecondary(secondaryPower, mouseOverPosSecondary, secondaryPreparationTime);
					secondaryPreparationTime--;
				}
				
			}

		}

	}

	/**
	 * Causes the player to use the designated power. Doesn't check whether or
	 * not they actually know it.
	 * c
	 * @param power
	 * @param lookVec
	 */
	private void usePowerAsPrimary(final Power power, MovingObjectPosition lookVec) {
		if (theEntity instanceof EntityPlayer && secondaryPower != power) {
			
			PowerProfile profile = getPowerProfile(power);
			
			if (power != null && getPowerProfile(power).cooldownRemaining <= 0 && power.canUsePower(profile)) {
				
				if (!power.hasPreparationTime()) {
					usePowerAsPrimary(power, lookVec, 0);
				} else {
					theEntity.isSwingInProgress = true;
					theEntity.swingProgressInt = 0;
					primaryPreparationTime = power.getPreparationTime( getPowerProfile(power) );
					isBeingPreparedPrimary = true;
					primaryPowerInUse = true;
				}
			}
		}
	}

	private void usePowerAsPrimary(final Power power, MovingObjectPosition lookVec, int preparationTimeLeft) {
				
		PowerProfile profile = getPowerProfile( power );
		
		if (EffectsEntity.get( theEntity ).isAffectedBy( PowerEffect.negated ) && power.isNegateable()) return;

		if (preparationTimeLeft > 0) {
			return;
		} else {
			isBeingPreparedPrimary = false;
		}
		

		
		if (power.canCastPower( profile )) {

			if (power.isConcentrationPower()) {
				if (power.cast( theEntity.worldObj, theEntity, lookVec, profile.useModifier, profile.getState() )) {
					prevTargetPosPrimary = lookVec;
					primaryPowerInUse = true;
					primaryUseTimeLeft = power.getMaxConcentrationTime();
				}

			} else if (power.cast( theEntity.worldObj, theEntity, lookVec, profile.useModifier, profile.getState() )) {
				
				theEntity.swingItem();
				if (power.onFinishedCasting( theEntity.worldObj, (EntityPlayer) theEntity, lookVec, profile.getState() )) {
					profile.triggerCost();
				}
				prevTargetPosPrimary = null;

			}
		}
	}

	/**
	 * Causes the player to use the designated power. Doesn't check whether or
	 * not they actually know it.
	 * c
	 * @param power
	 * @param lookVec
	 */
	private void usePowerAsSecondary(final Power power, MovingObjectPosition lookVec) {
		if (theEntity instanceof EntityPlayer && primaryPower != power ) {
			
			PowerProfile profile = getPowerProfile(power);
			
			if (power != null && profile.cooldownRemaining <= 0 && power.canUsePower(profile)) {
				
				if (!power.hasPreparationTime()) {
					usePowerAsSecondary(power, lookVec, 0);
				} else {
					secondaryPreparationTime = power.getPreparationTime( getPowerProfile(power) );
					isBeingPreparedSecondary = true;
					secondaryPowerInUse = true;
				}
			}
		}
	}

	private void usePowerAsSecondary(final Power power, MovingObjectPosition lookVec, int preparationTimeLeft) {
				
		PowerProfile profile = getPowerProfile( power );
		
		if (EffectsEntity.get( theEntity ).isAffectedBy( PowerEffect.negated ) && power.isNegateable()) return;

		if (preparationTimeLeft > 0) {
			return;
		} else {
			isBeingPreparedSecondary = false;
		}
		

		
		if (power.canCastPower( profile )) {

			if (power.isConcentrationPower()) {
				if (power.cast( theEntity.worldObj, theEntity, lookVec, profile.useModifier, profile.getState() )) {
					prevTargetPosSecondary = lookVec;
					secondaryPowerInUse = true;
					secondaryUseTimeLeft = power.getMaxConcentrationTime();
				}

			} else if (power.cast( theEntity.worldObj, theEntity, lookVec, profile.useModifier, profile.getState() )) {
				
				//theEntity.swingItem();
				if (power.onFinishedCasting( theEntity.worldObj, (EntityPlayer) theEntity, lookVec, profile.getState() )) 
					profile.triggerCost();
				prevTargetPosSecondary = null;

			}
		}
	}

	/**
	 * Uses the power designated as primary.
	 */
	public void usePrimaryPower(MovingObjectPosition lookVec) {

		usePowerAsPrimary( primaryPower, lookVec );
	}
	
	/**
	 * Uses the power designated secondary.
	 */
	public void useSecondaryPower(MovingObjectPosition lookVec) {

		usePowerAsSecondary( secondaryPower, lookVec );
	}

	public static PowersEntity get(final EntityLivingBase entity) {
		return PowersAPI.propertiesHandler().getWrapper( PowersEntity.class, entity );
	}

}
