package com.himself12794.powersAPI.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersAPI.spellfx.SpellEffect;

public class Heal extends SpellBuff {
	
	public Heal() {
		setDuration(0);
		setPower(0.25F);
		setCoolDown(1);
		//setType(SpellType.BUFF);
		setUnlocalizedName("heal");
	} 
	
	public boolean onCast(World world, EntityLivingBase caster, ItemStack stack, float modifier) {
		boolean flag = false;
		SpellEffect.spontaneousRegeneration.addTo(caster, -1, caster);
		if (caster.getHealth() < caster.getMaxHealth()) {
			flag = true;
			caster.heal(getPower() * modifier);
		}
		return flag;
	}
	
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caste, float modifier ) {
		boolean flag = false;
		EntityLivingBase entity = null;
		if (target.entityHit instanceof EntityLivingBase)
			 entity = ((EntityLivingBase)target.entityHit);
			if(entity.getHealth() < entity.getMaxHealth()) {
				flag = true;
				entity.heal(getPower() * modifier);
			}
		
		return flag;
	}
	
	public String getInfo() {
		return "Heals caster";
	}
	
	public String getCastSound() {
		return null;
	}
	
	
}
