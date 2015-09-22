package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.storage.EffectContainer;
import com.himself12794.powersapi.storage.EffectsEntity;
import com.himself12794.powersapi.storage.PowerProfile;
import com.himself12794.powersapi.storage.PowersEntity;

/**
 * Can be used cast powers that effect the world and the caster only.
 * <p>
 * Handy to use as a way to implement activation/deactivation powers.
 * 
 * @author Himself12794
 *
 */
public class PowerEffectActivatorBuff extends PowerBuff implements
		IEffectActivator {
	
	private final PowerEffect linkedEffect;
	private final int effectDuration;
	
	public PowerEffectActivatorBuff(String name, int cooldown, int maxConcentrationTime, PowerEffect effect, int duration) {
		super(name);
		this.setCooldown(cooldown);
		this.setMaxConcentrationTime(maxConcentrationTime);
		this.linkedEffect = effect;
		this.effectDuration = duration;
		setDuration(effectDuration);
	}
	
	public PowerEffectActivatorBuff(String name, int cooldown, int maxConcentrationTime, PowerEffect effect, int duration, int maxLevel) {
		this(name, cooldown, maxConcentrationTime, effect, duration);
		this.setMaxLevel( maxLevel );
	}

	public final boolean onFinishedCastingEarly(World world, EntityLivingBase entityIn,
			int timeLeft, MovingObjectPosition target, int state) {

		return this.onFinishedCasting( world, entityIn, target, state );
	}

	public final boolean onFinishedCasting(World world, EntityLivingBase caster,
			MovingObjectPosition target, int state) {

		boolean alreadyAffectingEntity = false;
		EffectsEntity wrapper = EffectsEntity.get( caster );
		if (wrapper.isAffectedBy( getPowerEffect() )) {
			EffectContainer container = wrapper.getEffectContainer( getPowerEffect() );
			
			if (container.casterEntity == caster && container.theEffect == getPowerEffect()) {
				alreadyAffectingEntity = true;
			}
		}

		if (!alreadyAffectingEntity) {
			PowerProfile profile = PowersEntity.get( caster ).getPowerProfile( this );
			wrapper.addPowerEffect( getPowerEffect(), getEffectDuration(profile), caster, this );
		} else if (isRemoveableByCaster( caster, caster, wrapper.getTimeRemaining( getPowerEffect() ) )) {
			wrapper.removePowerEffect( getPowerEffect() );
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
