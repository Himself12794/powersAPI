package com.himself12794.powersapi.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.config.KeyBindings;
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.event.KeyBindingsHandler;
import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.network.SyncNBTData;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.render.RenderPower;

public class ClientProxy extends CommonProxy {

	@Override
	public void preinit(FMLPreInitializationEvent event) {
		super.preinit( event );
		
		network.registerMessage( PowerEffectsClient.Handler.class,
				PowerEffectsClient.class, currId++, Side.CLIENT );
		network.registerMessage( SyncNBTData.Handler.class, SyncNBTData.class, currId++, Side.CLIENT );
	}

	@Override
	public void init(FMLInitializationEvent event) {

		super.init( event );
		RenderingRegistry.registerEntityRenderingHandler( EntityPower.class,
				new RenderPower(
						Minecraft.getMinecraft().getRenderManager()) );
		
		ModItems.registerTextures( event );
		KeyBindings.registerKeyBindings();
		FMLCommonHandler.instance().bus().register( new KeyBindingsHandler() );
	}
	
	public void doPowerEffectUpdate(PowerEffect effect, EntityLivingBase target, int timeLeft, EntityLivingBase caster) {
		effect.onUpdate( target, timeLeft, caster );
	}

	public Side getSide() {

		return Side.CLIENT;
	}

}
