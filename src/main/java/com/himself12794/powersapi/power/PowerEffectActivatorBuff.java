package com.himself12794.powersapi.power;

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
public abstract class PowerEffectActivatorBuff extends PowerBuff implements
		IEffectActivator {

	{
		setDuration( getEffectDuration() );
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

}
