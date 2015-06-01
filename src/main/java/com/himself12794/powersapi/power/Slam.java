package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.powerfx.PowerEffect;

public class Slam extends PowerInstant {
	
	Slam() {
		
		setPower(10.0F);
		setCoolDown(7 * 20);
		setUnlocalizedName("slam");
		
	}
	
	@Override
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		
		//PowerEffect.slam.addTo(caster, 20, caster);
		PowerEffect.slam.addTo((EntityLivingBase) target.entityHit, 20, caster);
		return true;
		
	}

}
