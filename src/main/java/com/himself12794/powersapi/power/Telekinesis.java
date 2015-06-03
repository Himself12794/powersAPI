package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.api.power.PowerInstant;
import com.himself12794.powersapi.util.UsefulMethods;

public class Telekinesis extends PowerInstant {
	
	public Telekinesis() {
		
		setPower(0.0F);
		setMaxConcentrationTime(10 * 20);
		setCoolDown(60);
		setDuration(15 * 20);
		setUnlocalizedName("telekinesis");
		
	}
	
	@Override
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		
		int distance = (int) target.entityHit.getDistanceToEntity(caster);
		//System.out.println(distance);
		EntityLivingBase entity = ((EntityLivingBase)target.entityHit);
		//entity.jumpMovementFactor = 0.0F;
		entity.moveForward = 0.0F;
		entity.moveStrafing = 0.0F;
		entity.isAirBorne = true;
		
		//SpellEffect.levitate.addTo((EntityLivingBase) target.entityHit, 40, caster);
		//target.entityHit.moveEntity(x, y, z);
		if (distance < 5) {
			
			double dx = target.entityHit.posX - caster.posX;
			double dy = target.entityHit.posY - caster.posY;
			double dz = target.entityHit.posZ - caster.posZ;
			//setThrowableHeading(dx, dy, dz, getVelocity(), 0.0F);
			UsefulMethods.setMovingDirection((EntityLivingBase) target.entityHit, dx, dy, dz, 2.0F);
			
		} else if (distance > 5) {
			
			double dx = target.entityHit.posX - caster.posX;
			double dy = target.entityHit.posY - caster.posY;
			double dz = target.entityHit.posZ - caster.posZ;
			//setThrowableHeading(dx, dy, dz, getVelocity(), 0.0F);
			UsefulMethods.setMovingDirection((EntityLivingBase) target.entityHit, dx, dy, dz, -2.0F);
			
		} else {
			
			target.entityHit.setVelocity(0.0D, 0.0D, 0.0D);
			
		}
		
		
		return true;
		
	}

}
