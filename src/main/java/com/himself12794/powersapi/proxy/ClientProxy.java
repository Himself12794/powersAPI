package com.himself12794.powersapi.proxy;

import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.PowerEffectsClient;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	
    @Override
    public void preinit(FMLPreInitializationEvent event) {
    	super.preinit(event);

		network.registerMessage(PowerEffectsClient.Handler.class, PowerEffectsClient.class, 2, Side.CLIENT);
    }


    @Override
    public void init(FMLInitializationEvent event)
    {
    	super.init(event);    	
    	ModItems.registerTextures(event);
    }
    
    public Side getSide() {
    	return Side.CLIENT;
    }
    
    
}

