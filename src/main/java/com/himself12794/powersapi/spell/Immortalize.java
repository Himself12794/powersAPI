package com.himself12794.powersapi.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.himself12794.powersapi.spellfx.SpellEffect;

public class Immortalize extends SpellBuff {
	
	Immortalize() {
		
		setUnlocalizedName("immortalize");
		
	}
	
	public boolean onCast(World world, EntityLivingBase caster, ItemStack stack, float modifier) {
		
		SpellEffect.rapidCellularRegeneration.addTo(caster, -1, caster);
		EntityPlayer player = (EntityPlayer)caster;
		
		return true;
	}

}
