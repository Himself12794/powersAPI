package com.himself12794.powersapi.spell;

import com.himself12794.powersapi.spellfx.SpellEffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Push extends SpellInstant {
	
	Push() {
		
		setPower(0.0F);
		setMaxConcentrationTime(10 * 20);
		setCoolDown(60);
		setDuration(15 * 20);
		setUnlocalizedName("push");
		
	}
	
	@Override
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		
		float j = 3.0F;
		SpellEffect.levitate.addTo((EntityLivingBase) target.entityHit, 40, caster);
		target.entityHit.addVelocity((double)(-MathHelper.sin(caster.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F), 0.1D, (double)(MathHelper.cos(caster.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F));
		
		return true;
		
	}

	

}
