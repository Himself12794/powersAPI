package com.himself12794.powersapi.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	
    @Override
    public void preinit(FMLPreInitializationEvent event) {
    	super.preinit(event);
    }


    @Override
    public void init(FMLInitializationEvent event)
    {
    	super.init(event);    	
    	//ModItems.registerTextures(event);
    }
    
    public Side getSide() {
    	return Side.CLIENT;
    }
    
    
}

