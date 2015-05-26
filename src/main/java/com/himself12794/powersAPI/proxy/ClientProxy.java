package com.himself12794.powersAPI.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersAPI.items.ModItems;
import com.himself12794.powersAPI.spell.Spell;

public class ClientProxy extends CommonProxy {
	
    @Override
    public void preinit(FMLPreInitializationEvent event) {
    	super.preinit(event);
    }


    @Override
    public void init(FMLInitializationEvent event)
    {
    	super.init(event);    	
    	//EagleVision.init();
    	//FMLCommonHandler.instance().bus().register(handler);
    	//MinecraftForge.EVENT_BUS.register(handler);

        // do client-specific stuff
    	ModItems.registerTextures(event);
    }
    
    public double getReverseRendering(ItemStack stack) {
    	super.getReverseRendering(stack);
    	if (!Spell.hasSpell(stack)) return 1.0D;
    	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    	Spell spell = Spell.getSpell(stack);
        return ((double)spell.getCoolDownRemaining(player) ) / (double)spell.getCoolDown();
    }
    
    public boolean showDamage(ItemStack stack) {
    	super.showDamage(stack);
    	
	    EntityPlayer player = Minecraft.getMinecraft().thePlayer;	    	
	    return Spell.hasSpell(stack) && Spell.getSpell(stack).getCoolDownRemaining(player) > 0;
    }
    
    public Side getSide() {
    	super.getSide();
    	
    	return Side.CLIENT;
    }
    
    
}

