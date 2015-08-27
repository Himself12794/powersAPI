package com.himself12794.powersapi.util;

import net.minecraft.entity.EntityLivingBase;

import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;

public class PowerEffectContainer {
	
	private final EntityLivingBase affectedEntity;
	private final EntityLivingBase casterEntity;
	private final int timeRemaining;
	private final PowerEffect theEffect;
	private final Power initiatedPower;
	
	public PowerEffectContainer(final EntityLivingBase affected, final EntityLivingBase caster, final int time, final PowerEffect effect) {
		this(affected, caster, time, effect, null);
	}
	
	public PowerEffectContainer(final EntityLivingBase affected, final EntityLivingBase caster, final int time, final PowerEffect effect, Power power) {
		affectedEntity = affected;
		casterEntity = caster;
		timeRemaining = time;
		theEffect = effect;
		initiatedPower = power;
	}

	public EntityLivingBase getAffectedEntity() {
		return affectedEntity;
	}

	public EntityLivingBase getCasterEntity() {
		return casterEntity;
	}

	public int getTimeRemaining() {
		return timeRemaining;
	}

	public PowerEffect getTheEffect() {
		return theEffect;
	}
	
	public Power getInitiatedPower() {
		return initiatedPower;
	}
	
	

}
