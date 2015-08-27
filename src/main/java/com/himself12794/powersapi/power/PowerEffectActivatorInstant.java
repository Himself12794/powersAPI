package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
public abstract class PowerEffectActivatorInstant extends PowerInstant
		implements IEffectActivator {

	/**
	 * Made final to preserve logic.
	 */
	// TODO add optional onFinishedCastingEarly
	public final boolean onFinishedCastingEarly(ItemStack stack, World world,
			EntityPlayer playerIn, int timeLeft, MovingObjectPosition target) {

		return this.onFinishedCasting( stack, world, playerIn, target );
	}
	/**
	 * Made final to preserve logic.
	 */
	// TODO add optional onFinishedCasting
	public final boolean onFinishedCasting(ItemStack stack, World world,
			EntityPlayer caster, MovingObjectPosition target) {

		if (getPowerEffect() == null) return false;

		boolean alreadyAffectingEntity = false;
		EntityLivingBase entityAlreadyAffected = null;

		for (EntityLivingBase entity : UsefulMethods.getEntitiesWithEffect(
				world, getPowerEffect() )) {
			DataWrapper wrapper = DataWrapper.get( entity );

			PowerEffectContainer container = wrapper
					.getEffectContainer( getPowerEffect() );
			if (container.getCasterEntity() == caster
					&& container.getTheEffect() == getPowerEffect()) {
				alreadyAffectingEntity = true;
				entityAlreadyAffected = entity;
				break;
			}

		}

		if (!alreadyAffectingEntity
				&& target.entityHit instanceof EntityLivingBase) {
			if (getPowerEffect().shouldApplyEffect(
					(EntityLivingBase) target.entityHit, caster, this )) {
				getPowerEffect().addTo( (EntityLivingBase) target.entityHit,
						getEffectDuration(), caster, this );
			}
			
		} else if (entityAlreadyAffected != null) {
			DataWrapper.get( entityAlreadyAffected ).addPowerEffect(
					getPowerEffect(), 0, caster, this );
		}

		return alreadyAffectingEntity;
	}

}
