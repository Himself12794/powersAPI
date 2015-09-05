package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.storage.EffectContainer;
import com.himself12794.powersapi.storage.EffectsWrapper;

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
		this.setUnlocalizedName(name);
		this.setCoolDown(cooldown);
		this.setMaxConcentrationTime(maxConcentrationTime);
		this.linkedEffect = effect;
		this.effectDuration = duration;
		setDuration(effectDuration);
	}

	public boolean onFinishedCastingEarly(World world, EntityLivingBase entityIn,
			int timeLeft, MovingObjectPosition target) {

		return this.onFinishedCasting( world, entityIn, target );
	}

	public boolean onFinishedCasting(World world, EntityLivingBase caster,
			MovingObjectPosition target) {

		boolean alreadyAffectingEntity = false;
		EffectsWrapper wrapper = EffectsWrapper.get( caster );
		if (wrapper.isAffectedBy( getPowerEffect() )) {
			EffectContainer container = wrapper.getEffectContainer( getPowerEffect() );
			
			if (container.casterEntity == caster && container.theEffect == getPowerEffect()) {
				alreadyAffectingEntity = true;
			}
		}

		if (!alreadyAffectingEntity) {
			wrapper.addPowerEffect( getPowerEffect(), getEffectDuration(), caster, this );
		} else if (isRemoveableByCaster( caster, caster, wrapper.getTimeRemaining( getPowerEffect() ) )) {
			wrapper.removePowerEffect( getPowerEffect() );
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
