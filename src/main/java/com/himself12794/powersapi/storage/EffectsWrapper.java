package com.himself12794.powersapi.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.IEffectActivator;
import com.himself12794.powersapi.power.IPlayerOnly;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.UsefulMethods;

public class EffectsWrapper extends DataWrapper {

	private static final String POWER_EFFECTS_GROUP = "powerEffects";
	private static final String POWER_EFFECTS = "activeEffects";

	public Map<PowerEffect, EffectContainer> powerEffects = Maps.newHashMap();

	EffectsWrapper(EntityLivingBase entity) {
		super(entity);
	}

	public boolean addPowerEffect(final PowerEffect effect, final int duration,
			final EntityLivingBase caster, final Power power) {

		EffectContainer container = new EffectContainer( theEntity, caster, duration, effect, power );
		if (container.shouldApplyEffect()) { 
			powerEffects.put( effect, container );
			return true;
		} else {
			return false;
		}
		
	}

	public void addPowerEffect(final EffectContainer container) {
		powerEffects.put( container.theEffect, container );
	}

	public Collection getActiveEffectContainers() {
		return powerEffects.values();
	}

	public EffectContainer getEffectContainer(final PowerEffect effect) {
		
		if (powerEffects.containsKey( effect )) {
			return powerEffects.get( effect );
		}
		
		return null;
		
	}

	public void removePowerEffect(final PowerEffect effect) {
		
		if (powerEffects.containsKey( effect )) {
			
			EffectContainer cont = powerEffects.get( effect );
			cont.onRemoval();
			
    		if (cont.initiatedPower instanceof IEffectActivator && cont.casterEntity != null) {
    			if (((IEffectActivator)cont.initiatedPower).getPowerEffect() == cont.theEffect) {
    				PowersWrapper.get( cont.casterEntity ).triggerCooldown( cont.initiatedPower );
    			}
    		}
    		
			powerEffects.remove( effect );
		}
		
	}

	/**
	 * Removes the effect without triggering {@link PowerEffect#onRemoval()} or
	 * activating linked power cooldown.
	 * 
	 * @param effect
	 */
	public void removePowerEffectQuietly(final PowerEffect effect) {

		if (powerEffects.containsKey( effect )) {
			powerEffects.remove( effect );
		}

	}

	/**
	 * Clears power effect without calling PowerEffect#onRemoval, but still
	 * triggers linked power cooldown.
	 * 
	 * @return
	 */
	public void removePowerEffectSparingly(final PowerEffect effect) {

		if (powerEffects.containsKey( effect )) {
			
			EffectContainer cont = powerEffects.get( effect );
				
    		if (cont.initiatedPower instanceof IEffectActivator && cont.casterEntity != null) {
    			if (((IEffectActivator)cont.initiatedPower).getPowerEffect() == cont.theEffect) {
    				PowersWrapper.get( cont.casterEntity ).triggerCooldown( cont.initiatedPower );
    			}
    		}
    		
			powerEffects.remove( effect );
		}
	}

	public void updatePowerEffects() {
		
		boolean hasNegatedEffect = isAffectedBy(PowerEffect.negated);
		Set toRemove = Sets.newHashSet();
		
		for (EffectContainer container : powerEffects.values()) {
			
			if ((!container.theEffect.isNegateable() || !hasNegatedEffect) && container.timeRemaining != 0) {

				if (!container.onUpdate()) {
					toRemove.add( container.theEffect );
					continue;
				}
			}
			
			if (container.timeRemaining == 0) {
				toRemove.add( container.theEffect );
			} else if (container.timeRemaining > 0) {
				container.timeRemaining--;
			}

		}
		
		for (Object effect : toRemove) {
			removePowerEffect((PowerEffect)effect);
		}
	}

	public boolean isAffectedBy(final PowerEffect effect) {
		return powerEffects.containsKey( effect );

	}

	/**
	 * All effects afflicting this entity that have a remaining time of < 0
	 * 
	 * @return
	 */
	public Collection getNonHiddenEffects() {

		final Set<PowerEffect> effects = Sets.newHashSet();

		for (EffectContainer cont : powerEffects.values()) {
			if (!cont.theEffect.getType().isHidden()) {
				effects.add( cont.theEffect );
			}
		}

		return effects;

	}
	
	public Collection getActiveEffects() {

		final Set<PowerEffect> effects = Sets.newHashSet();

		for (EffectContainer cont : powerEffects.values() ) {

			effects.add( cont.theEffect );

		}

		return effects;
	}
	
	public void resetForRespawn() {

		final Set toRemove = Sets.newHashSet();
		
		powerEffects.keySet().forEach( new Consumer<PowerEffect>() {

			@Override
			public void accept(PowerEffect t) {
				
				if (t.isPersistant()) toRemove.add( t );
				
			}
			
		});
		
		toRemove.forEach( new Consumer<PowerEffect>() {

			@Override
			public void accept(PowerEffect t) {
				removePowerEffect(t);
			}
			
		});

	}
	
	public int getTimeRemaining(PowerEffect effect) {
		
		if (powerEffects.containsKey( effect )) return powerEffects.get( effect ).timeRemaining;
		else return 0;
		
	}
	
	public void copyTo(EntityLivingBase entity) {
		entity.registerExtendedProperties( POWER_EFFECTS_GROUP, this );
	}
	
	public static void register(EntityLivingBase entity) {
		entity.registerExtendedProperties( POWER_EFFECTS_GROUP, new EffectsWrapper( entity ) );
	}
	
	public static void register(EntityLivingBase entity, EffectsWrapper other) {
		entity.registerExtendedProperties( POWER_EFFECTS_GROUP, other );
	}
	
	public static EffectsWrapper get(EntityLivingBase entity) {
		return (EffectsWrapper) entity.getExtendedProperties( POWER_EFFECTS_GROUP );
	}
	
	private NBTTagList getEffectsAsList() {

		NBTTagList powerEffects = new NBTTagList();
		
		for (EffectContainer cont : this.powerEffects.values()) {
			
			NBTTagCompound asNBT = cont.getAsNBT();
			
			if (asNBT != null) powerEffects.appendTag( asNBT );
			
		}
		
		return powerEffects;
		
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		
		NBTTagCompound modData = UsefulMethods.getPutKeyCompound( Reference.MODID, compound );
		modData.setTag( POWER_EFFECTS, getEffectsAsList() );
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {

		if (compound.hasKey( Reference.MODID, 10 )) {
			
			NBTTagCompound modData = compound.getCompoundTag( Reference.MODID );
			
			if (modData.hasKey( POWER_EFFECTS, 9 )) {
					
				NBTTagList powerEffects = modData.getTagList( POWER_EFFECTS, 10 );
				
				for (int i = 0; i < powerEffects.tagCount(); i++) {
					
					EffectContainer container = EffectContainer.getFromNBT( powerEffects.getCompoundTagAt( i ), theEntity );
					
					if (container != null) {
						this.powerEffects.put( container.theEffect, container );
					}
										
				}
				
				if (theEntity instanceof EntityPlayer) {
					//PowersAPI.logger.info( powerEffects );
				}
				
			}
			
		}
		
	}

	@Override
	public void init(Entity entity, World world) {
		
	}
}