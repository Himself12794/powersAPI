package com.himself12794.powersapi.proxy;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.ModCreativeTabs;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.command.EffectsCommand;
import com.himself12794.powersapi.command.PowersCommand;
import com.himself12794.powersapi.config.Config;
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.event.EventsHandler;
import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.storage.EffectsEntity;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.storage.PropertiesBase;
import com.himself12794.powersapi.storage.PropertiesManager;
import com.himself12794.powersapi.util.Reference;

public class CommonProxy {

	public void serverStartEvent(FMLServerStartingEvent event) {

		if (Config.enableCommands) {
			event.registerServerCommand( new PowersCommand() );
			event.registerServerCommand( new EffectsCommand() );
		}
	}

	public void preinit(FMLPreInitializationEvent event) {
		
		// Registering message types
		PowersNetwork.init( NetworkRegistry.INSTANCE.newSimpleChannel( Reference.MODID ) );
		PowersNetwork.registerMessages();

		if (Config.enablePowerActivator) {
			ModCreativeTabs.addCreativeTabs();
			ModItems.addItems();
		}

		// register entities
		EntityRegistry.registerModEntity( EntityPower.class, "power", 1,
				PowersAPI.instance, 80, 3, true );
		
		PropertiesManager.registerPropertyClass( EffectsEntity.class, EntityLivingBase.class );
		PropertiesManager.registerPropertyClass( PowersEntity.class, EntityLivingBase.class );

	}

	public void init(FMLInitializationEvent event) {

		EventsHandler uph = new EventsHandler();

		MinecraftForge.EVENT_BUS.register( uph );

		FMLCommonHandler.instance().bus().register( uph );

	}

	public Side getSide() {

		return Side.SERVER;
	}

	public EntityPlayer getPlayer() {
		return null;
	}

}
