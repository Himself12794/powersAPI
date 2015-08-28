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
import com.himself12794.powersapi.network.CastPowerInstantServer;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

public class PowerInstant extends Power {
	
	private int range = 40;
	
	public boolean cast(World world, EntityLivingBase caster, float modifier) {
		
		boolean successful = false;
		boolean hasEffect = onCast(world, caster, modifier);
		DataWrapper wrapper = DataWrapper.get( caster );
		
		if (world.isRemote) {
			
			MovingObjectPosition pos = UsefulMethods.getMouseOverExtended(range);
			
			if (pos == null) pos = new MovingObjectPosition(new Vec3(0.0D, 0.0D, 0.0D), EnumFacing.UP);
			
			successful = onStrike( world, pos, caster, modifier );
			IMessage msg = new CastPowerInstantServer( pos, modifier, this );
			PowersAPI.network.sendToServer(msg);

			
			if (successful) {
				wrapper.setPreviousPowerTarget( pos );
			}
			
		} else {
			
			successful = wrapper.wasPowerSuccess();
			wrapper.setPowerSuccess( false );
			
		}
		
		boolean result = hasEffect && successful;
		
		return result;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Instant";
	}
	
	protected void setRange(int range) { this.range = range; }
	
	public int getRange() { return range; }

}
