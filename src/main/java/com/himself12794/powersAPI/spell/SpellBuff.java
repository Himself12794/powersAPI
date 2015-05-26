package com.himself12794.powersAPI.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpellBuff extends Spell {

	public final boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {
		return onCast(world, caster, tome, modifier);
	}
	
	public final String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Buff";
	}
	
}
