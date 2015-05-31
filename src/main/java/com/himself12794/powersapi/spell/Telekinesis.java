package com.himself12794.powersapi.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.spellfx.SpellEffect;

public class Telekinesis extends SpellInstant {
	
	Telekinesis() {
		
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
			setMovingDirection((EntityLivingBase) target.entityHit, dx, dy, dz, 2.0F);
			
		} else if (distance > 5) {

			
			double dx = target.entityHit.posX - caster.posX;
			double dy = target.entityHit.posY - caster.posY;
			double dz = target.entityHit.posZ - caster.posZ;
			//setThrowableHeading(dx, dy, dz, getVelocity(), 0.0F);
			setMovingDirection((EntityLivingBase) target.entityHit, dx, dy, dz, -2.0F);
			
		} else {
			
			target.entityHit.setVelocity(0.0D, 0.0D, 0.0D);
			
		}
		
		
		return true;
		
	}
	
    public void setMovingDirection(EntityLivingBase target, double x, double y, double z, float velocity ) {
    	
        float f2 = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= (double)f2;
        y /= (double)f2;
        z /= (double)f2;
        x *= (double)velocity;
        y *= (double)velocity;
        z *= (double)velocity;
        target.motionX = x;
        target.motionY = y;
        target.motionZ = z;
        //float f3 = MathHelper.sqrt_double(x * x + z * z);
        //target.prevRotationYaw = target.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
        //target.prevRotationPitch = target.rotationPitch = (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);
 
    }

}
