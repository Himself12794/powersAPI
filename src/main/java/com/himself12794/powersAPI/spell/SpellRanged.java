package com.himself12794.powersAPI.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersAPI.entity.EntitySpell;

public class SpellRanged extends Spell {
	
	@Override
	public boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {
		boolean flag1 = onCast(world, caster, tome, modifier);
		EntitySpell casting = new EntitySpell(world, caster, this, modifier );
		boolean flag2 = world.spawnEntityInWorld(casting);
		return flag1 && flag2;
	}

	public void onUpdate(EntitySpell entitySpell) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier) {
		return true;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Ranged";
	}

	public float getSpellVelocity() {
		// TODO Auto-generated method stub
		return 2.0F;
	}

	public boolean isPiercingSpell() {
		// TODO Auto-generated method stub
		return false;
	}

}
