package com.himself12794.powersapi.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.events.PowerEffectHandler;
import com.himself12794.powersapi.items.ModItems;
import com.himself12794.powersapi.power.Power;

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
    	MinecraftForge.EVENT_BUS.register(new PowerEffectHandler());

        // do client-specific stuff
    	ModItems.registerTextures(event);
    	
    	//MinecraftForge.EVENT_BUS.register(new EagleVision());
    	//FMLCommonHandler.instance().bus().register(new SpellEffectHandler()); 
    }
    
    public double getReverseRendering(ItemStack stack) {
    	super.getReverseRendering(stack);
    	if (!Power.hasPower(stack)) return 1.0D;
    	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    	Power spell = Power.getPower(stack);
        return ((double)spell.getCoolDownRemaining(player) ) / (double)spell.getCoolDown();
    }
    
    public boolean showDamage(ItemStack stack) {
    	super.showDamage(stack);
    	
	    EntityPlayer player = Minecraft.getMinecraft().thePlayer;	    	
	    if (Power.getPower(stack) != null) return Power.hasPower(stack) && Power.getPower(stack).getCoolDownRemaining(player) > 0;
	    return false;
    }
    
    public Side getSide() {
    	super.getSide();
    	
    	return Side.CLIENT;
    }
    
    
}

