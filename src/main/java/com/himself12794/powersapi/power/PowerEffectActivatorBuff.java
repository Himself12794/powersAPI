package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.PowerEffectContainer;
import com.himself12794.powersapi.util.UsefulMethods;

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

	/**
	 * To ensure core stability, this is final. For additional operations, override
	 * {@link PowerEffectActivatorBuff#onCastAdditional(World, EntityLivingBase, ItemStack, float)}.
	 * <p>
	 * This method will be called after this method has finished its operations.
	 */
	@Override
	public final boolean onCast(World world, EntityLivingBase caster,
			ItemStack tome, float modifier) {

		boolean alreadyAffectingEntity = false;
		DataWrapper wrapper = DataWrapper.get( caster );

		PowerEffectContainer container = wrapper
				.getEffectContainer( getPowerEffect() );
		if (container.getCasterEntity() == caster
				&& container.getTheEffect() == getPowerEffect()) {
			alreadyAffectingEntity = true;

		}

		if (!alreadyAffectingEntity) {
			getPowerEffect().addTo( caster, getEffectDuration(), caster, this );
		} else {
			wrapper.addPowerEffect( getPowerEffect(), 0, caster, this );
		}

		return alreadyAffectingEntity
				&& onCastAdditional( world, caster, tome, modifier );
	}

	/**
	 * Allows for additional operations without compromising core mechanics.
	 * 
	 * @param world
	 * @param caster
	 * @param tome
	 * @param modifier
	 * @return
	 */
	public boolean onCastAdditional(World world, EntityLivingBase caster,
			ItemStack tome, float modifier) {

		return true;
	}

}
