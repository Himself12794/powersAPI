package com.himself12794.powersapi.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.ModConfig;
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.event.KeyBindingsHandler;
import com.himself12794.powersapi.render.RenderPower;

public class ClientProxy extends CommonProxy {
	
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

		ModConfig.registerKeyBindings();
		FMLCommonHandler.instance().bus().register( new KeyBindingsHandler(Minecraft.getMinecraft()) );
		
	}

	public Side getSide() {

		return Side.CLIENT;
	}
	
	public EntityPlayer getPlayerFromContext(MessageContext ctx) {
		if (ctx.side.isClient()) {
			return Minecraft.getMinecraft().thePlayer;
		} else {
			return super.getPlayerFromContext( ctx );
		}
	}
	
	public void scheduleTaskBasedOnContext(MessageContext ctx, Runnable task) {
		if (ctx.side.isClient()) {
			Minecraft.getMinecraft().addScheduledTask( task );
		} else {
			super.scheduleTaskBasedOnContext( ctx, task );
		}
	}

}
