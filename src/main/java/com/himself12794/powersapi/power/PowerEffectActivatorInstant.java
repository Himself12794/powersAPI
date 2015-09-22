package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.storage.EffectContainer;
import com.himself12794.powersapi.storage.EffectsEntity;
import com.himself12794.powersapi.storage.PowerProfile;
import com.himself12794.powersapi.storage.PowersEntity;
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

	public PowerEffectActivatorInstant(String name, int cooldown,
			int maxConcentrationTime, PowerEffect effect, int duration) {

		this.setUnlocalizedName( name );
		this.setCooldown( cooldown );
		this.setMaxConcentrationTime( maxConcentrationTime );
		this.linkedEffect = effect;
		this.effectDuration = duration;
		setDuration( effectDuration );
	}

	/**
	 * Made final to preserve logic.
	 */
	public final boolean onFinishedCastingEarly(World world,
			EntityLivingBase entityIn, int timeLeft, MovingObjectPosition target, int state) {

		return this.onFinishedCasting( world, entityIn, target, state );
	}

	/**
	 * Made final to preserve logic.
	 */
	public final boolean onFinishedCasting(World world,
			EntityLivingBase caster, MovingObjectPosition target, int state) {

		if (getPowerEffect() == null) return false;

		boolean alreadyAffectingEntity = false;
		EntityLivingBase entityAlreadyAffected = null;

		for (EntityLivingBase entity : UsefulMethods.getEntitiesWithEffect(	world, getPowerEffect() )) {
			
			EffectsEntity wrapper = EffectsEntity.get( entity );
			EffectContainer container = wrapper.getEffectContainer( getPowerEffect() );
			if (container.casterEntity == caster
					&& container.theEffect == getPowerEffect()
					&& container.initiatedPower == this) {
				
				alreadyAffectingEntity = true;
				entityAlreadyAffected = entity;
				break;
			}

		}

		if (!alreadyAffectingEntity	&& target.entityHit instanceof EntityLivingBase) {

			PowerProfile profile = PowersEntity.get( caster ).getPowerProfile( this );
			EffectsEntity.get( (EntityLivingBase) target.entityHit )
					.addPowerEffect( getPowerEffect(), getEffectDuration(profile),
							caster, this );

		} else if (entityAlreadyAffected != null) {

			EffectsEntity.get( entityAlreadyAffected ).removePowerEffect( getPowerEffect() );
		}

		return alreadyAffectingEntity;
	}

	@Override
	public PowerEffect getPowerEffect() {
		return linkedEffect;
	}

	@Override
	public int getEffectDuration(PowerProfile profile) {

		return effectDuration;
	}

	@Override
	public boolean isRemoveableByCaster(EntityLivingBase affected,
			EntityLivingBase caster, int timeRemaining) {

		return true;
	}

}
