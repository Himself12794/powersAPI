package com.himself12794.powersapi.api.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import com.himself12794.powersapi.Config;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.CastPowerInstantServer;
import com.himself12794.powersapi.util.Reference;
import com.himself12794.powersapi.util.UsefulMethods;

public class PowerInstant extends Power {
	
	public boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {
		
		boolean successful = false;
		boolean hasEffect = onCast(world, caster, tome, modifier);
			
		
		if (world.isRemote) {
			
			MovingObjectPosition pos = UsefulMethods.getMouseOverExtended(Config.instantPowerRange);
			
			if (pos.entityHit != null ) {
				
				if (pos.entityHit instanceof EntityLivingBase) {
					
					IMessage msg = new CastPowerInstantServer( (EntityLivingBase) pos.entityHit, modifier, this );
					PowersAPI.proxy.network.sendToServer(msg);
					
					successful = true;
					
				} 
			}
			
		} else {
			
			successful = caster.getEntityData().getBoolean(Reference.MODID + ".spell.success");
			caster.getEntityData().setBoolean(Reference.MODID + ".spell.success", false);
			
		}
		
		return hasEffect && successful;
	}
	
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return "Instant";
	}

}
