package com.himself12794.powersapi.network;

import com.himself12794.powersapi.network.client.C01PowerUse;
import com.himself12794.powersapi.network.client.C02SetMouseOverTarget;
import com.himself12794.powersapi.network.client.C03CyclePowerState;
import com.himself12794.powersapi.network.server.S01SyncProperty;
import com.himself12794.powersapi.network.server.S02SetPower;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * The network used to communicate between server-side and client-side.
 * 
 * @author Himself12794
 *
 */
public final class PowersNetwork {
	
	private static boolean isInit = false;
	private static boolean messagesRegistered = false;
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
	
	public static SimpleNetworkWrapper get() {
		return wrapper;
	}
	
	public static void init(SimpleNetworkWrapper wrapper) {
		
		if (!isInit) {
			PowersNetwork.wrapper = wrapper;
			CLIENT = new Client(wrapper);
			SERVER = new Server(wrapper);
			isInit = true;
		}
	}
	
	public static void registerMessages() {
		
		if (isInit && !messagesRegistered) {
			
			wrapper.registerMessage( C01PowerUse.Handler.class,
					C01PowerUse.class, ++currId, Side.SERVER );
			wrapper.registerMessage( C02SetMouseOverTarget.Handler.class,
					C02SetMouseOverTarget.class, ++currId, Side.SERVER );
			wrapper.registerMessage( C03CyclePowerState.Handler.class,
					C03CyclePowerState.class, ++currId, Side.SERVER );

			wrapper.registerMessage( S01SyncProperty.Handler.class,
					S01SyncProperty.class, ++currId, Side.CLIENT );
			wrapper.registerMessage( S02SetPower.Handler.class,
					S02SetPower.class, ++currId, Side.CLIENT );
			
			messagesRegistered = true;
		}
		
	}
	
}
