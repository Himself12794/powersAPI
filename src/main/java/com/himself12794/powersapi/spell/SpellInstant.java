package com.himself12794.powersapi.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import com.himself12794.powersapi.Config;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.CastSpellInstantServer;
import com.himself12794.powersapi.util.Reference;
import com.himself12794.powersapi.util.UsefulMethods;

public class SpellInstant extends Spell {
	
	public boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier) {
		
		boolean successful = false;
		boolean hasEffect = onCast(world, caster, tome, modifier);
			
		
		if (world.isRemote) {
			
			MovingObjectPosition pos = UsefulMethods.getMouseOverExtended(Config.instantSpellRange);
			
			if (pos.entityHit != null ) {
				
				if (pos.entityHit instanceof EntityLivingBase) {
					
					IMessage msg = new CastSpellInstantServer( (EntityLivingBase) pos.entityHit, modifier, this );
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
