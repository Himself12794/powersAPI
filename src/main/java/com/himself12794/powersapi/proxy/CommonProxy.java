package com.himself12794.powersapi.proxy;

import net.minecraft.entity.EntityLivingBase;
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
import com.himself12794.powersapi.command.EffectsCommand;
import com.himself12794.powersapi.command.PowersCommand;
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.event.EventsHandler;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.storage.EffectsEntity;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.Reference;

public class CommonProxy {

	public void serverStartEvent(FMLServerStartingEvent event) {

		if (ModConfig.modCommandsEnabled) {
			event.registerServerCommand( new PowersCommand() );
			event.registerServerCommand( new EffectsCommand() );
		}
	}

	public void preinit(FMLPreInitializationEvent event) {
		
		ModConfig.loadConfig(event);
		configureMetadata(PowersAPI.metadata());
		
		// Registering message types
		PowersNetwork.init( NetworkRegistry.INSTANCE.newSimpleChannel( Reference.MODID ) );
		PowersNetwork.registerMessages();

		// register entities
		EntityRegistry.registerModEntity( EntityPower.class, "power", 1,
				PowersAPI.instance(), 80, 3, true );

	}

	public void init(FMLInitializationEvent event) {

		PowersAPI.propertiesManager().registerPropertyClass( EffectsEntity.class, EntityLivingBase.class );
		PowersAPI.propertiesManager().registerPropertyClass( PowersEntity.class, EntityLivingBase.class );
		
		EventsHandler uph = new EventsHandler();
		MinecraftForge.EVENT_BUS.register( uph );
		FMLCommonHandler.instance().bus().register( uph );
		
		FMLCommonHandler.instance().bus().register( new ModConfig() );
		

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
