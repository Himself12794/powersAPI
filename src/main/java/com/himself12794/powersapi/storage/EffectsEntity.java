package com.himself12794.powersapi.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.IEffectActivator;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.Reference;
import com.himself12794.powersapi.util.UsefulMethods;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Manages the power effects on an entity.
 * 
 * @author Himself12794
 *
 */
public class EffectsEntity extends PropertiesBase {

	public static final String POWER_EFFECTS_GROUP = Reference.MODID + ":powerEffects";
	private static final String POWER_EFFECTS = "activeEffects";

	public Map<PowerEffect, EffectContainer> powerEffects = Maps.newHashMap();

	EffectsEntity(EntityLivingBase entity) {
		super(entity);
	}

	public boolean addPowerEffect(final PowerEffect effect, final int duration,
			final EntityLivingBase caster, final Power power) {

		EffectContainer container = new EffectContainer( caster, duration, effect, power );
		container.setAffectedEntity( theEntity );
		if (container.shouldApplyEffect()) { 
			powerEffects.put( effect, container );
			container.onApplied();
			return true;
		} else {
			return false;
		}
		
	}

	public void addPowerEffect(final EffectContainer container) {
		container.setAffectedEntity( theEntity );
		powerEffects.put( container.theEffect, container );
	}

	public Collection<EffectContainer> getActiveEffectContainers() {
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
    				PowersEntity.get( cont.casterEntity ).triggerCooldown( cont.initiatedPower );
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
    				PowersEntity.get( cont.casterEntity ).triggerCooldown( cont.initiatedPower );
    			}
    		}
    		
			powerEffects.remove( effect );
		}
	}

	public void onUpdate() {
		
		boolean hasNegatedEffect = isAffectedBy(PowerEffect.negated);
		Set<PowerEffect> toRemove = Sets.newHashSet();
		
		for (EffectContainer container : powerEffects.values()) {
			
			if ((!container.theEffect.isNegateable() || !hasNegatedEffect) && container.timeRemaining != 0) {
				
				if (container.casterEntity == null && container.theEffect.requiresCaster()) {
					toRemove.add( container.theEffect );
					continue;
				}
				
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
	public Collection<PowerEffect> getNonHiddenEffects() {

		final Set<PowerEffect> effects = Sets.newHashSet();

		for (EffectContainer cont : powerEffects.values()) {
			if (!cont.theEffect.getType().isHidden()) {
				effects.add( cont.theEffect );
			}
		}

		return effects;

	}
	
	public Collection<PowerEffect> getActiveEffects() {

		final Set<PowerEffect> effects = Sets.newHashSet();

		for (EffectContainer cont : powerEffects.values() ) {

			effects.add( cont.theEffect );

		}

		return effects;
	}
	
	@Override
	public float onDamaged(EntityLivingBase affectedEntity, DamageSource source, float amount, boolean hasChanged) {
		
		float value = amount;
		
		for (EffectContainer container : powerEffects.values()) {
			value = container.onDamaged( source, value, hasChanged || value != amount );
		}
		
		return value;
	}
	
	public void resetForRespawn() {

		final Set<PowerEffect> toRemove = Sets.newHashSet();
		
		for (PowerEffect effect : powerEffects.keySet()) {
			
			if (!effect.isPersistant()) {
				toRemove.add( effect );
			}
			
		}
		
		for (Object effect : toRemove) {
			
			powerEffects.remove( effect );
			
		}

	}
	
	public int getTimeRemaining(PowerEffect effect) {
		
		if (powerEffects.containsKey( effect )) return powerEffects.get( effect ).timeRemaining;
		else return 0;
		
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
						container.onApplied();
					}
										
				}
				
			}
			
		}
		
	}

	@Override
	public void init(Entity entity, World world) {}

	@Override
	public void onJoinWorld(World world) {}
	
	public static EffectsEntity get(EntityLivingBase entity) {
		return PowersAPI.propertiesHandler().getWrapper( EffectsEntity.class, entity );
	}
	
	
}
