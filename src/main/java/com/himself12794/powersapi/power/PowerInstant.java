package com.himself12794.powersapi.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.server.CastPowerInstantServer;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

public class PowerInstant extends Power {
	
	private int range = 40;
	
	public boolean cast(World world, EntityLivingBase caster, MovingObjectPosition mouseOver, float modifier) {
		
		onCast(world, caster, modifier);
		boolean successful = onStrike( world, mouseOver, caster, modifier );
		DataWrapper wrapper = DataWrapper.get( caster );
		
		if (mouseOver == null) return false;
		
		if (successful) {
			wrapper.setPreviousPowerTarget( mouseOver );
		}
		
		return successful;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Instant";
	}
	
	protected void setRange(int range) { this.range = range; }
	
	public int getRange() { return range; }

}
