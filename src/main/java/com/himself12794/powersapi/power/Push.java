package com.himself12794.powersapi.power;

import com.himself12794.powersapi.powerfx.PowerEffect;
import com.himself12794.powersapi.util.UsefulMethods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Push extends PowerInstant {
	
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
		//SpellEffect.levitate.addTo((EntityLivingBase) target.entityHit, 40, caster);
		//target.entityHit.addVelocity((double)(-MathHelper.sin(caster.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F), 0.1D, (double)(MathHelper.cos(caster.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F));
		double dx = target.entityHit.posX - caster.posX;
		double dy = target.entityHit.posY - caster.posY;
		double dz = target.entityHit.posZ - caster.posZ;
		//setThrowableHeading(dx, dy, dz, getVelocity(), 0.0F);
		UsefulMethods.setMovingDirection((EntityLivingBase) target.entityHit, dx, dy, dz, 4.0F);
		
		return true;
		
	}

	

}
