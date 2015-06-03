package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Can be used cast powers that effect the world and the caster only.
 * <p>
 * Handy to use as a way to implement activation/deactivation powers.
 * 
 * @author Himself12794
 *
 */
public class PowerBuff extends Power {

	public final boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {
		
		return onCast(world, caster, tome, modifier);
		
	}
	
	public final String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		
		return "Buff";
		
	}
	
}
