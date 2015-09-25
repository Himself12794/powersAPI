package com.himself12794.powersapi.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.ModConfig;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.PropertiesHandler;
import com.himself12794.powersapi.command.EffectsCommand;
import com.himself12794.powersapi.command.PowersCommand;
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.storage.EffectsEntity;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.Reference;

public class CommonProxy {

	public void preinit(FMLPreInitializationEvent event) {
		
		configureMetadata(PowersAPI.metadata());
		
		// Registering message types
		PowersNetwork.init( NetworkRegistry.INSTANCE.newSimpleChannel( Reference.MODID ) );
		PowersNetwork.registerMessages();

		// register entities
		EntityRegistry.registerModEntity( EntityPower.class, "power", 1,
				PowersAPI.instance(), 80, 3, true );

	}

	public void init(FMLInitializationEvent event) {

		PropertiesHandler ph = PowersAPI.instance().propertiesHandler;
		
		MinecraftForge.EVENT_BUS.register( ph );
		FMLCommonHandler.instance().bus().register( ph );
		
		ph.registerPropertyClass( EffectsEntity.class );
		ph.registerPropertyClass( PowersEntity.class, EntityPlayer.class );
		
		FMLCommonHandler.instance().bus().register( ModConfig.get() );
		

	}

	public void serverStartEvent(FMLServerStartingEvent event) {

		if (ModConfig.areModCommandsEnabled()) {
			event.registerServerCommand( new PowersCommand() );
			event.registerServerCommand( new EffectsCommand() );
		}
	}

	public Side getSide() {
		return Side.SERVER;
	}

	public EntityPlayer getPlayerFromContext(MessageContext ctx) {
		if (ctx.side.isServer()) {
			return ctx.getServerHandler().playerEntity;
		} else {
			return null;
		}
	}
	
	public void scheduleTaskBasedOnContext(MessageContext ctx, Runnable task) {
		if (ctx.side.isServer()) {
			ctx.getServerHandler().playerEntity.getServerForPlayer().addScheduledTask( task );
		}
	}
    
    private void configureMetadata(ModMetadata meta) {

		meta.name = Reference.NAME;
		meta.modId = Reference.MODID;
		meta.version = Reference.VERSION;
		meta.authorList.add( Reference.AUTHOR );
    }

}
