package com.himself12794.powersAPI.spell;

import com.himself12794.powersAPI.PowersAPI;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Incinerate extends SpellInstant {
	
	public Incinerate() {
		setPower(0.0F);
		setCoolDown(60);
		setDuration(10 * 20);
		setUnlocalizedName("incinerate");
	}
	
	@Override
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		boolean flag = false;
		if (!target.entityHit.isImmuneToFire()) {
			PowersAPI.print("Setting " + target.entityHit.getName() + " on fire");
			flag = true;
			target.entityHit.setFire(MathHelper.ceiling_float_int(( getDuration() / 20 ) * modifier));
			if (!target.entityHit.isDead) ((EntityLivingBase)target.entityHit).setLastAttacker(caster);
		}
		return flag;
	}

}
