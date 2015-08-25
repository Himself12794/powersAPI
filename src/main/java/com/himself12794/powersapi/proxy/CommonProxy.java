package com.himself12794.powersapi.proxy;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.ModCreativeTabs;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.entity.EntityPower;
import com.himself12794.powersapi.event.UpdatesHandler;
import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.CastPowerInstantServer;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.network.SendPlayerStoppedUsingPower;
import com.himself12794.powersapi.network.SendUsePower;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.util.Reference;

public class CommonProxy {

	public static SimpleNetworkWrapper network;

	protected static int currId = -1;

	public void preinit(FMLPreInitializationEvent event) {

		network = NetworkRegistry.INSTANCE.newSimpleChannel( Reference.MODID
				+ " NetChannel" );
		network.registerMessage( CastPowerInstantServer.Handler.class,
				CastPowerInstantServer.class, currId++, Side.SERVER );
		network.registerMessage( SendPlayerStoppedUsingPower.Handler.class,
				SendPlayerStoppedUsingPower.class, currId++, Side.SERVER );
		network.registerMessage( SendUsePower.Handler.class,
				SendUsePower.class, currId++, Side.SERVER );

		// register spells
		// Power.registerPowers();
		ModCreativeTabs.addCreativeTabs();

		ModItems.addItems();

		// register entities
		EntityRegistry.registerModEntity( EntityPower.class, "spell", 1,
				PowersAPI.instance, 80, 3, true );
	}

	public void init(FMLInitializationEvent event) {

		UpdatesHandler uph = new UpdatesHandler();

		MinecraftForge.EVENT_BUS.register( uph );
		
		FMLCommonHandler.instance().bus().register( uph );

		// ModRecipes.addRecipes();

	}

	public void doPowerEffectUpdate(PowerEffect effect,
			EntityLivingBase target, int timeLeft, EntityLivingBase caster) {

		effect.onUpdate( target, timeLeft, caster );
		network.sendToAll( new PowerEffectsClient( effect, target, caster,
				false, timeLeft ) );
	}

	public Side getSide() {

		return Side.SERVER;
	}

}
