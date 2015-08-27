package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.himself12794.powersapi.PowersAPI;

/**
 * Can be used cast powers that effect the world and the caster only.
 * <p>
 * Handy to use as a way to implement activation/deactivation powers.
 * 
 * @author Himself12794
 *
 */
public class PowerBuff extends Power {
	
	@Override
	public final boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {

		return onCast(world, caster, tome, modifier);
		
	}
	
	@Override
	public boolean onCast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {
		
		boolean flag = super.onCast( world, caster, tome, modifier );
		
		if (this instanceof IEffectActivator && flag) {
			
			PowerEffect pfx = ((IEffectActivator)this).getPowerEffect();
			
			if (!pfx.isEffecting( caster )) { 
				pfx.addTo( caster, getDuration(), caster, this );
				PowersAPI.print( "Effect " + pfx.getId() + " added to " + caster.getName() );
			} else pfx.addTo( caster, getDuration(), caster, this );
			
			return true;
			
		}
		
		return flag;
	}
	
	public final String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		
		return "Buff";
		
	}
	
}
