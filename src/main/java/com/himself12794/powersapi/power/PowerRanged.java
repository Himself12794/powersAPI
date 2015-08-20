package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.entity.EntitySpell;

/**
 * Base class for all ranged Power entities.
 * 
 * @author Himself12794
 *
 */
public class PowerRanged extends Power {
	
	@Override
	public boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {
		
		boolean flag1 = onCast(world, caster, tome, modifier);
		EntitySpell casting = new EntitySpell(world, caster, this, modifier );
		boolean flag2 = world.spawnEntityInWorld(casting);
		return flag1 && flag2;
		
	}

	public void onUpdate(EntitySpell entitySpell) {}
	
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier) {
		return true;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Ranged";
	}

	public float getSpellVelocity() {
		return 2.0F;
	}

	public boolean isPiercingSpell() {
		return false;
	}

}
