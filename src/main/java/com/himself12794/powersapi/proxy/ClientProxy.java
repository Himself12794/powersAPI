package com.himself12794.powersapi.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.config.KeyBindings;
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.event.KeyBindingsHandler;
import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.network.SyncNBTData;
import com.himself12794.powersapi.render.RenderPower;
import com.himself12794.powersapi.util.DataHandler;

public class ClientProxy extends CommonProxy {

	private DataHandler clientDataHandler = new DataHandler();
	
	@Override
	public void preinit(FMLPreInitializationEvent event) {
		super.preinit( event );
	}

	@Override
	public void init(FMLInitializationEvent event) {

		super.init( event );
		RenderingRegistry.registerEntityRenderingHandler( EntityPower.class,
				new RenderPower(
						Minecraft.getMinecraft().getRenderManager() ) );

		ModItems.registerTextures( event );
		KeyBindings.registerKeyBindings();
		FMLCommonHandler.instance().bus().register( new KeyBindingsHandler() );
		
		if (event.getSide().isClient()) {
			clientDataHandler = new DataHandler();
		}
	}

	public Side getSide() {

		return Side.CLIENT;
	}
	
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
    
    public DataHandler getDataHandler() {
    	return clientDataHandler;
    }

}
