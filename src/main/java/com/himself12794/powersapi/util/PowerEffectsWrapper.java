package com.himself12794.powersapi.util;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.IPlayerOnly;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerEffect;

public class PowerEffectsWrapper {

	private static final String POWER_EFFECTS_GROUP = "powerEffects";
	private static final String POWER_EFFECTS = "activeEffects";

	private final EntityLivingBase theEntity;
	private final NBTTagCompound nbt;

	PowerEffectsWrapper(EntityLivingBase entity, NBTTagCompound modEntityData) {

		theEntity = entity;

		if (!modEntityData.hasKey( POWER_EFFECTS_GROUP, 10 )) {
			nbt = new NBTTagCompound();
			modEntityData.setTag( POWER_EFFECTS_GROUP, nbt );
		} else {
			nbt = modEntityData.getCompoundTag( POWER_EFFECTS_GROUP );
		}
	}

	public void addPowerEffect(final PowerEffect effect, final int duration,
			final EntityLivingBase caster, final Power power) {

		effect.addTo( theEntity, duration, caster, power );

	}

	public void addPowerEffect(final PowerEffectContainer container) {

		if (container != null) {
			addPowerEffect( container.getTheEffect(),
					container.getTimeRemaining(), container.getCasterEntity(),
					container.getInitiatedPower() );
		}
	}

	public NBTTagList getActiveEffects() {

		NBTTagList activeEffects = null;

		if (nbt.hasKey( POWER_EFFECTS, 9 )) {
			activeEffects = (NBTTagList) nbt.getTag( POWER_EFFECTS );
		} else {
			activeEffects = new NBTTagList();
			nbt.setTag( POWER_EFFECTS, activeEffects );
		}

		return activeEffects;
	}

	public PowerEffectContainer getEffectContainer(final PowerEffect effect) {

		EntityLivingBase caster = null;
		int timeRemaining = 0;

		if (!getActiveEffects().hasNoTags()) {

			for (int i = 0; i < getActiveEffects().tagCount(); ++i) {

				final NBTTagCompound nbttagcompound = getActiveEffects()
						.getCompoundTagAt( i );

				final PowerEffect temp = PowerEffect
						.getEffectById( nbttagcompound
								.getShort( "id" ) );
				if (temp != effect) continue;

				timeRemaining = nbttagcompound.getInteger( "duration" );
				final Entity tempEntity = theEntity.getEntityWorld()
						.getEntityByID(
								nbttagcompound.getInteger( "caster" ) );
				caster = (EntityLivingBase) (tempEntity != null
						&& tempEntity instanceof EntityLivingBase ? tempEntity
						: null);

			}
		}

		return new PowerEffectContainer( theEntity, caster, timeRemaining,
				effect );

	}

	public void removePowerEffect(final PowerEffect effect) {

		this.addPowerEffect( getEffectContainer( effect ).newWithDuration( 0 ) );
	}

	/**
	 * Removes the effect without triggering {@link PowerEffect#onRemoval()} or
	 * activating linked power cooldown.
	 * 
	 * @param effect
	 */
	public void removePowerEffectQuietly(final PowerEffect effect) {

		if (effect == null) return;
		effect.clearQuietly( theEntity );

	}

	/**
	 * Clears power effect without calling PowerEffect#onRemoval, but still
	 * triggers linked power cooldown.
	 * 
	 * @return
	 */
	public void removePowerEffectSparingly(final PowerEffect effect) {

		if (effect == null) return;
		effect.clear( this.getEffectContainer( effect ) );
	}

	public void updatePowerEffects() {

		if (!getActiveEffects().hasNoTags()) {

			for (int i = 0; i < getActiveEffects().tagCount(); ++i) {

				final NBTTagCompound nbttagcompound = getActiveEffects()
						.getCompoundTagAt( i );

				int timeRemaining = nbttagcompound.getInteger( "duration" );

				final Entity temp = theEntity.worldObj
						.getEntityByID( nbttagcompound.getInteger( "caster" ) );
				final EntityLivingBase caster = temp instanceof EntityLivingBase ? (EntityLivingBase) temp
						: null;

				final PowerEffect spfx = PowerEffect.getEffectById( nbttagcompound
						.getShort( "id" ) );

				final Power power = Power.lookupPower( nbttagcompound
						.getString( "initiatedPower" ) );

				if (spfx != null) {

					final boolean shouldNegate = isAffectedBy( PowerEffect.negated )
							&& spfx.isNegateable();

					if (timeRemaining > 0 && !shouldNegate) {

						if (spfx instanceof IPlayerOnly
								&& theEntity instanceof EntityPlayer) {

							((IPlayerOnly) spfx).onUpdate(
									(EntityPlayer) theEntity, timeRemaining,
									caster );

						} else spfx.onUpdate( theEntity, timeRemaining, caster,
								power );

						addPowerEffect( spfx, --timeRemaining, caster, power );

					}

					else if (timeRemaining < 0 && !shouldNegate) {

						spfx.onUpdate( theEntity, -1, caster, power );

					}
				}
			}
		}
	}

	public boolean isAffectedBy(final PowerEffect effect) {

		final NBTTagList effects = getActiveEffects();
		if (effect == null) return false;

		for (int i = 0; i < effects.tagCount(); i++) {
			final short id = effects.getCompoundTagAt( i ).getShort( "id" );
			if (id == effect.getId()) {
				return true;
			}
		}

		return false;

	}

	/**
	 * All effects afflicting this entity that have a remaining time of < 0
	 * 
	 * @return
	 */
	public Set<PowerEffect> getNonHiddenEffects() {

		final Set<PowerEffect> effects = Sets.newHashSet();

		for (int i = 0; i < getActiveEffects().tagCount(); i++) {

			final int id = getActiveEffects().getCompoundTagAt( i ).getInteger( "id" );
			final int remaining = getActiveEffects().getCompoundTagAt( i )
					.getInteger( "duration" );
			final PowerEffect effect = PowerEffect.getEffectById( id );

			if (effect != null && !effect.getType().isHidden()) {
				effects.add( effect );
			}

		}

		return effects;

	}
}
