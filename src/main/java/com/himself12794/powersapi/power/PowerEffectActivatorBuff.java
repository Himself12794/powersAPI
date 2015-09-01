package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.PowerEffectContainer;

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

	public boolean onFinishedCastingEarly(World world, EntityPlayer playerIn,
			int timeLeft, MovingObjectPosition target) {

		return this.onFinishedCasting( world, playerIn, target );
	}

	public boolean onFinishedCasting(World world, EntityPlayer caster,
			MovingObjectPosition target) {

		boolean alreadyAffectingEntity = false;
		DataWrapper wrapper = DataWrapper.get( caster );

		PowerEffectContainer container = wrapper
				.powerEffectsData.getEffectContainer( getPowerEffect() );
		if (container.getCasterEntity() == caster
				&& container.getTheEffect() == getPowerEffect()) {
			alreadyAffectingEntity = true;

		}

		if (!alreadyAffectingEntity) {
			getPowerEffect().addTo( caster, getEffectDuration(), caster, this );
		} else if (isRemoveableByCaster( caster, caster,
				container.getTimeRemaining() )) {
			wrapper.powerEffectsData.addPowerEffect( getPowerEffect(), 0, caster, this );
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
