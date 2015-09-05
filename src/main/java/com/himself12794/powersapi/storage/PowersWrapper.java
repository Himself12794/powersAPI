package com.himself12794.powersapi.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.UsefulMethods;

/**
 * Utility class used for easier access of information stored in NBT tag data.
 * 
 * @author Himself12794
 *
 */
public class PowersWrapper extends DataWrapper {

	private static final String POWER_GROUP = "power";
	private static final String POWER_CURRENT = "currentPower";
	private static final String POWER_CURRENT_USELEFT = "currentPower.useLeft";
	private static final String POWER_SET = "powerSet";
	private static final String POWER_PRIMARY = "primaryPower";
	private static final String POWER_SECONDARY = "secondaryPower";
	private static final String POWER_PREVIOUS_TARGET = "previousTarget";
	private static final String POWER_PROFILES = "powerProfiles";

	private int powerInUseTimeLeft;
	private Power powerInUse;
	/**
	 * Information about the powers that the player has known. Just because a
	 * player has a power profile for a specific power does not indicate that
	 * they actually know the power
	 */
	private final Map<Power, PowerProfile> powerProfiles = Maps.newHashMap();
	/** The powers that player currently knows */
	public final Set<Power> learnedPowers = Sets.newHashSet();
	private Power primaryPower;
	private Power secondaryPower;
	public MovingObjectPosition prevTargetPos;
	public MovingObjectPosition mouseOverPos;

	protected PowersWrapper(final EntityLivingBase entity) {

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

	public Collection getPowerProfiles() {

		return Sets.newHashSet( powerProfiles.values() );

	}

	public PowerProfile getPowerProfile(Power power) {
		return getOrCreatePowerProfile(power);
	}

	private PowerProfile getOrCreatePowerProfile(Power power) {

		if (powerProfiles.containsKey( power )) {
			return powerProfiles.get( power );
		} else {

			powerProfiles.put( power, new PowerProfile( theEntity, power, null ) );
			return powerProfiles.get( power );
		}
	}

	public EffectsWrapper getPowerEffectsData() {

		return EffectsWrapper.get( theEntity );
	}

	public Power getPowerInUse() {

		return powerInUse;
	}

	public int getPowerUseTimeLeft() {

		return powerInUseTimeLeft;
	}

	public MovingObjectPosition getPreviousPowerTarget() {

		return prevTargetPos;
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

		return getOrCreatePowerProfile( primaryPower ).cooldownRemaining;
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

		return getOrCreatePowerProfile( secondaryPower ).cooldownRemaining;
	}

	public boolean isUsingPower() {

		return powerInUse != null;
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

	/**
	 * Prepares the player to respawn, removing power effects that don't persist
	 * after death.
	 * 
	 * @return
	 */
	@Override
	public void resetForRespawn() {

		for (PowerProfile profile : powerProfiles.values()) {
			profile.cooldownRemaining = 0;
		}

	}

	/**
	 * Sets the primary power. If the entity does not know the power, it teaches
	 * it to them.
	 * 
	 * @param power
	 */
	public void setPrimaryPower(final Power power) {

		if (power == null || primaryPower == power) return;

		if (!this.knowsPower( power )) teachPower(power);
		primaryPower = power;
		
		System.out.println(this);
		
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

		if (power == null || secondaryPower == power) return;
		
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
	public void stopUsingPower() {

		if (theEntity instanceof EntityPlayer && powerInUse != null) {

			final boolean flag = powerInUse.onFinishedCastingEarly(
					theEntity.worldObj,
					(EntityPlayer) theEntity, getPowerUseTimeLeft(),
					getPreviousPowerTarget() );

			if (flag) {
				getOrCreatePowerProfile( powerInUse ).triggerCooldown();
			}

			powerInUseTimeLeft = 0;
			prevTargetPos = null;
			mouseOverPos = null;
			powerInUse = null;
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
			theEntity.addChatMessage( new ChatComponentTranslation(
					"command.power.learned", power.getDisplayName() ) );
		}
	}

	public void triggerCooldown(final Power power) {

		if (power != null) {
			getOrCreatePowerProfile( power ).cooldownRemaining = power.getCooldown();
		}
	}

	public void updateAll() {

		//if (theEntity instanceof EntityPlayer) PowersAPI.logger.info( this );
		getPowerEffectsData().updatePowerEffects();
		updateCooldowns();
		updateUsingPowers();

	}

	public void updateCooldowns() {

		for (PowerProfile profile : powerProfiles.values()) {

			if (profile.cooldownRemaining > 0) {
				profile.cooldownRemaining--;
			}

		}

	}

	public void updateUsingPowers() {

		if (theEntity instanceof EntityPlayer) {

			final int useTime = getPowerUseTimeLeft();

			if (powerInUse != null) {

				PowerProfile profile = getOrCreatePowerProfile( powerInUse );

				if (theEntity.isSwingInProgress) {
					theEntity.swingProgressInt = 1;
				}

				if (this.powerInUseTimeLeft > 0) {

					if (this.powerInUseTimeLeft % 4 == 0) {
						powerInUse.cast( theEntity.worldObj, theEntity, mouseOverPos, profile.useModifier );
					}
					this.powerInUseTimeLeft--;

				} else if (this.powerInUseTimeLeft <= 0) {

					if (powerInUse.onFinishedCasting( theEntity.worldObj, (EntityPlayer) theEntity, prevTargetPos )) profile
							.triggerCooldown();
					prevTargetPos = null;
					this.powerInUseTimeLeft = 0;
					powerInUse = null;
					mouseOverPos = null;
				}

			}

		}

	}

	/**
	 * Causes the player to use the designated power. Doesn't check whether or
	 * not they actually know it.
	 * 
	 * @param power
	 * @param lookVec
	 *            TODO
	 */
	public void usePower(final Power power, MovingObjectPosition lookVec) {

		if (theEntity instanceof EntityPlayer) {
			if (power != null && power.canUsePower( theEntity )) {

				PowerProfile profile = getOrCreatePowerProfile( power );

				if (power.onPreparePower( theEntity.worldObj, (EntityPlayer) theEntity )) {

					theEntity.swingItem();

					if (power.isConcentrationPower()) {

						if (power.cast( theEntity.worldObj, theEntity, lookVec, profile.useModifier )) {
							profile.addUse();
							powerInUse = power;
						}

					} else if (power.cast( theEntity.worldObj, theEntity, lookVec, profile.useModifier )) {
						profile.addUse();
						if (power.onFinishedCasting( theEntity.worldObj, (EntityPlayer) theEntity, prevTargetPos )) profile
								.triggerCooldown();
						prevTargetPos = null;

					}
				}
			}
		}

	}

	/**
	 * Uses the power designated as primary.
	 */
	public void usePrimaryPower(MovingObjectPosition lookVec) {

		usePower( primaryPower, lookVec );
	}

	/*
	 * public boolean hasPowerEffect(PowerEffect effect) { return effect != null
	 * ? effect.isAffecting( theEntity ) : false; }
	 */

	/**
	 * Uses the power designated secondary.
	 */
	public void useSecondaryPower(MovingObjectPosition lookVec) {

		usePower( secondaryPower, lookVec );
	}

	public void setMouseOver(MovingObjectPosition pos) {

		mouseOverPos = pos;
	}
	
	private NBTTagList getPowerProfilesAsNBTTagList() {
		
		NBTTagList entries = new NBTTagList();
		
		for (PowerProfile profile : powerProfiles.values() ) {
			NBTTagCompound entry = profile.getAsNBTTagCompound();
			if (entry != null) entries.appendTag( entry );
			
		}
		
		return entries;
	}
	
	private int[] getLearnedPowersAsIntArray() {
		
		int[] entries = new int[learnedPowers.size()];
		
		int i = 0;
		for (Power power : learnedPowers) {
			entries[i] = power.getId();
			i++;
		}
		
		
		
		return entries;
		
	}

	public static PowersWrapper get(final EntityLivingBase entity) {
		return (PowersWrapper) entity.getExtendedProperties( POWER_GROUP );
	}

	public static void register(EntityLivingBase entity) {
		entity.registerExtendedProperties( POWER_GROUP, new PowersWrapper( entity ) );
	}
	
	public void copyTo(EntityLivingBase entity) {
		entity.registerExtendedProperties( POWER_GROUP, this );
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		
		if (theEntity instanceof EntityPlayer) System.out.println(this);
		
		NBTTagCompound modData = UsefulMethods.getPutKeyCompound( Reference.MODID, compound );
		NBTTagCompound powersData = UsefulMethods.getPutKeyCompound( POWER_GROUP, modData );
		
		powersData.setTag( POWER_PROFILES, getPowerProfilesAsNBTTagList() );
		powersData.setIntArray( POWER_SET, getLearnedPowersAsIntArray() );
		powersData.setInteger( POWER_PRIMARY, primaryPower != null ? primaryPower.getId() : 0 );
		powersData.setInteger( POWER_SECONDARY, primaryPower != null ? secondaryPower.getId() : 0);
		
		//System.out.println(powersData);
		
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
						
						Power power = Power.lookupPowerById( learnedPowers[i] );
						
						if (power != null) {
							this.learnedPowers.add( power );
						}
					}
				}
				
				primaryPower = Power.lookupPowerById( powersData.getInteger( POWER_PRIMARY ) );
				secondaryPower = Power.lookupPowerById( powersData.getInteger( POWER_SECONDARY ) );
			}
		}

	}

	@Override
	public void init(Entity entity, World world) {

		// TODO Auto-generated method stub

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
		result.append( ",PowerProfiles:" );
		result.append( powerProfiles.values() );
		result.append( ",LearnedPowers:" );
		result.append( learnedPowers );
		result.append( "]" );
		
		return result.toString();
		
	}

}