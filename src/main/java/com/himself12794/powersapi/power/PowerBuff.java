package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.storage.PowersEntity;

/**
 * Can be used cast powers that effect the world and the caster only.
 * <p>
 * Handy to use as a way to implement activation/deactivation powers.
 * 
 * @author Himself12794
 *
 */
public class PowerBuff extends Power {
	
	public PowerBuff(String name) {
		this.setUnlocalizedName( name );
		this.setUsesToLevelUp( 50 );
	}
	
	public PowerBuff(String name, int cooldown) {
		this(name);
		this.setCost( cooldown );
	}
	
	@Override
	public final boolean cast(World world, EntityLivingBase caster, MovingObjectPosition mouseOver, float modifier, int state) {
		
		boolean result = onCast(world, caster, modifier, state);
		if (result) PowersEntity.get( caster ).prevTargetPosPrimary =  new MovingObjectPosition(caster);
		return result;
		
	}
	
	public final String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		
		return "Buff";
		
	}
	
}
