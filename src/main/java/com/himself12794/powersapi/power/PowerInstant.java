package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.himself12794.powersapi.storage.PowersWrapper;

public class PowerInstant extends Power {
	
	private int range = 40;
	
	public boolean cast(World world, EntityLivingBase caster, MovingObjectPosition mouseOver, float modifier) {
		
		onCast(world, caster, modifier);
		boolean successful = onStrike( world, mouseOver, caster, modifier );
		PowersWrapper wrapper = PowersWrapper.get( caster );
		
		if (mouseOver == null) return false;
		
		if (successful) {
			wrapper.prevTargetPos = mouseOver;
		}
		
		return successful;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Instant";
	}
	
	protected void setRange(int range) { this.range = range; }
	
	public int getRange() { return range; }

}
