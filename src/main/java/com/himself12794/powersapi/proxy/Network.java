package com.himself12794.powersapi.proxy;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.network.client.C01PowerUse;
import com.himself12794.powersapi.network.client.C02SetMouseOverTarget;
import com.himself12794.powersapi.network.server.S01SyncProperty;
import com.himself12794.powersapi.network.server.S02SetPower;


public final class Network {
	
	private static boolean isInit = false;
	private static int currId = 0;
	private static Client CLIENT;
	private static Server SERVER;
	private static SimpleNetworkWrapper wrapper;

	public static Client client() {
		if (isInit) {
			return CLIENT;
		} else {
			return null;
		}
		
	}

	public static Server server() {
		if (isInit) {
			return SERVER;
		} else {
			return null;
		}
		
	}
	
	static void init(SimpleNetworkWrapper wrapper) {
		Network.wrapper = wrapper;
		CLIENT = new Client(wrapper);
		SERVER = new Server(wrapper);
		isInit = true;
	}
	
	static void registerMessages() {
		
		if (isInit) {
			
			wrapper.registerMessage( C01PowerUse.Handler.class,
					C01PowerUse.class, ++currId, Side.SERVER );
			wrapper.registerMessage( C02SetMouseOverTarget.Handler.class,
					C02SetMouseOverTarget.class, ++currId, Side.SERVER );

			wrapper.registerMessage( S01SyncProperty.Handler.class,
					S01SyncProperty.class, ++currId, Side.CLIENT );
			wrapper.registerMessage( S02SetPower.Handler.class,
					S02SetPower.class, ++currId, Side.CLIENT );
			
		}
		
	}
	
}
