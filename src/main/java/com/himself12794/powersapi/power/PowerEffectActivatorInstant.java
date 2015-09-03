package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.PowerEffectContainer;
import com.himself12794.powersapi.util.UsefulMethods;

/**
 * This is a specialized PowerInstant that applies an effect to an entity and
 * then tracks its progress, allowing the caster to manually end the effect. The
 * cooldown triggers if the player manually ends the effect, or the effect ends
 * naturally.
 * 
 * @author Himself12794
 *
 */
public class PowerEffectActivatorInstant extends PowerInstant
		implements IEffectActivator {
	
	private final PowerEffect linkedEffect;
	private final int effectDuration;
	
	public PowerEffectActivatorInstant(String name, int cooldown, int maxConcentrationTime, PowerEffect effect, int duration) {
		this.setUnlocalizedName(name);
		this.setCoolDown(cooldown);
		this.setMaxConcentrationTime(maxConcentrationTime);
		this.linkedEffect = effect;
		this.effectDuration = duration;
		setDuration(effectDuration);
	}

	/**
	 * Made final to preserve logic.
	 */
	// TODO add optional onFinishedCastingEarly
	public final boolean onFinishedCastingEarly(World world,
			EntityPlayer playerIn, int timeLeft, MovingObjectPosition target) {

		return this.onFinishedCasting( world, playerIn, target );
	}

	/**
	 * Made final to preserve logic.
	 */
	// TODO add optional onFinishedCasting
	public final boolean onFinishedCasting(World world,
			EntityPlayer caster, MovingObjectPosition target) {

		if (getPowerEffect() == null) return false;

		boolean alreadyAffectingEntity = false;
		EntityLivingBase entityAlreadyAffected = null;

		for (EntityLivingBase entity : UsefulMethods.getEntitiesWithEffect(
				world, getPowerEffect() )) {
			DataWrapper wrapper = DataWrapper.get( entity );

			PowerEffectContainer container = wrapper
					.getPowerEffectsData().getEffectContainer( getPowerEffect() );
			if (container.getCasterEntity() == caster
					&& container.getTheEffect() == getPowerEffect()
					&& container.getInitiatedPower() == this) {
				alreadyAffectingEntity = true;
				entityAlreadyAffected = entity;
				break;
			}

		}

		if (!alreadyAffectingEntity
				&& target.entityHit instanceof EntityLivingBase) {
			if (getPowerEffect().shouldApplyEffect(
					(EntityLivingBase) target.entityHit, caster, this )) {
				getPowerEffect().addTo(
						(EntityLivingBase) target.entityHit,
						getEffectDuration(), caster, this );
			}
			
		} else if (entityAlreadyAffected != null) {

			DataWrapper.get( entityAlreadyAffected ).getPowerEffectsData()
					.addPowerEffect(
							getPowerEffect(), 0, caster, this );
		}

		return alreadyAffectingEntity;
	}

	@Override
	public PowerEffect getPowerEffect() {

		// TODO Auto-generated method stub
		return linkedEffect;
	}

	@Override
	public int getEffectDuration() {

		return effectDuration;
	}

	@Override
	public boolean isRemoveableByCaster(EntityLivingBase affected,
			EntityLivingBase caster, int timeRemaining) {

		return true;
	}

}
