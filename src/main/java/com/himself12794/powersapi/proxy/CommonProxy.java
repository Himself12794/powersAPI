package com.himself12794.powersapi.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.entity.EntitySpell;
import com.himself12794.powersapi.event.UpdatesHandler;
import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.CastPowerInstantServer;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.network.SetHomingPowerTargetServer;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.Reference;

public class CommonProxy {
	
	public static SimpleNetworkWrapper network;
	
	public void preinit(FMLPreInitializationEvent event) {
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID + " NetChannel");
		network.registerMessage(SetHomingPowerTargetServer.Handler.class, SetHomingPowerTargetServer.class, 0, Side.SERVER);
		network.registerMessage(CastPowerInstantServer.Handler.class, CastPowerInstantServer.class, 1, Side.SERVER);
		network.registerMessage(PowerEffectsClient.Handler.class, PowerEffectsClient.class, 2, Side.CLIENT);
       
		// register spells
		//Power.registerPowers();
		
		ModItems.addItems();
		
		PowerEffect.registerEffects();
		
		// register entities
		EntityRegistry.registerModEntity(EntitySpell.class, "spell", 1, PowersAPI.instance, 80, 3, true);
	}

	public void init(FMLInitializationEvent event){
		UpdatesHandler uph = new UpdatesHandler();
		
    	MinecraftForge.EVENT_BUS.register(uph);
		 
		//ModRecipes.addRecipes();

	}
	
	public Side getSide() {
		return Side.SERVER;
	}
	
}
