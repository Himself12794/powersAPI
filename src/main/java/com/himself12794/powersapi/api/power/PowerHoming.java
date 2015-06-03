package com.himself12794.powersapi.api.power;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;

import com.himself12794.powersapi.entity.EntitySpell;

public class PowerHoming extends PowerRanged implements IHomingPower {

	public void onUpdate(EntitySpell spell) {
		
		for (float j = 0.0F; j < 1.0F; j += 0.05F) {
		
			for (int i = 0; i < 10; ++i) {
			
				spell.worldObj.spawnParticle(EnumParticleTypes.FLAME,
					spell.prevPosX + (spell.motionX * j) - spell.worldObj.rand.nextFloat() * 0.5F,
					spell.prevPosY + (spell.motionY * j) - spell.worldObj.rand.nextFloat() * 0.5F,
					spell.prevPosZ + (spell.motionZ * j) - spell.worldObj.rand.nextFloat() * 0.5F,
					0, 0, 0);
				
			}
		}
	}

	@Override
	public MovingObjectPosition getTarget(EntitySpell spell, MovingObjectPosition target) {

/*		if (spell.target == null && spell.worldObj.isRemote) {
			
			MovingObjectPosition pos = UsefulMethods.getEntityLookEntity(spell, 50);
			SetHomingSpellTargetServer msg = new SetHomingSpellTargetServer(spell, pos);
			PowersAPI.proxy.network.sendToServer(msg);
			
			return pos;
			
		}
		return target;*/
		return null;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Homing";
	}

}
