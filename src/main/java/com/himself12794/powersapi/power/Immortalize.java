package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.powerfx.SpellEffect;

public class Immortalize extends PowerBuff {
	
	Immortalize() {
		
		setUnlocalizedName("immortalize");
		
	}
	
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		if (target.entityHit != null) {
			if (target.entityHit instanceof EntityLivingBase) SpellEffect.rapidCellularRegeneration.addTo((EntityLivingBase) target.entityHit, -1, caster);
		}
		return false;
	}
	
	public boolean onCast(World world, EntityLivingBase caster, ItemStack stack, float modifier) {
		
		SpellEffect.rapidCellularRegeneration.addTo(caster, -1, caster);
		
		//SpellEffect.ground.addTo(caster, 5 * 20, caster);
		
		return true;
	}

}
