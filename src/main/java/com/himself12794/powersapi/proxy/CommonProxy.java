package com.himself12794.powersapi.proxy;

import static com.himself12794.powersapi.PowersAPI.network;
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
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.event.UpdatesHandler;
import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.CastPowerInstantServer;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.network.SendStopUsePower;
import com.himself12794.powersapi.network.SendUsePower;
import com.himself12794.powersapi.network.SyncNBTData;
import com.himself12794.powersapi.util.Reference;

public class CommonProxy {

	public void serverStartEvent(FMLServerStartingEvent event) {
		event.registerServerCommand( new PowersCommand() );
		event.registerServerCommand( new EffectsCommand() );
	}
	
	public void preinit(FMLPreInitializationEvent event) {
		
		// Registering message types
		network = NetworkRegistry.INSTANCE.newSimpleChannel( Reference.MODID );

		network.registerMessage( CastPowerInstantServer.Handler.class,
				CastPowerInstantServer.class, 0, Side.SERVER );
		network.registerMessage( SendStopUsePower.Handler.class,
				SendStopUsePower.class, 1, Side.SERVER );
		network.registerMessage( SendUsePower.Handler.class,
				SendUsePower.class, 2, Side.SERVER );

		network.registerMessage( PowerEffectsClient.Handler.class,
				PowerEffectsClient.class, 3, Side.CLIENT );
		network.registerMessage( SyncNBTData.Handler.class, SyncNBTData.class,
				4, Side.CLIENT );

		ModCreativeTabs.addCreativeTabs();
		ModItems.addItems();

		// register entities
		EntityRegistry.registerModEntity( EntityPower.class, "power", 1,
				PowersAPI.instance, 80, 3, true );
		
	}

	public void init(FMLInitializationEvent event) {

		UpdatesHandler uph = new UpdatesHandler();

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
