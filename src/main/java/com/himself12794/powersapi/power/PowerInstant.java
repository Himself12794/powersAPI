package com.himself12794.powersapi.power;

import com.himself12794.powersapi.storage.PowersEntity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class PowerInstant extends Power {
	
	private int range = 40;
	
	public boolean cast(World world, EntityLivingBase caster, MovingObjectPosition mouseOver, float modifier, int state) {
		
		if (mouseOver == null) return false;
		
		onCast(world, caster, modifier, state);
		boolean successful = onStrike( world, mouseOver, caster, modifier, state );
		PowersEntity wrapper = PowersEntity.get( caster );
		
		return successful;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Instant";
	}
	
	protected void setRange(int range) { this.range = range; }
	
	public int getRange() { return range; }

}
