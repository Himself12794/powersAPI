package com.himself12794.powersapi.network;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.himself12794.powersapi.network.client.C01PowerUse;
import com.himself12794.powersapi.network.client.C02SetMouseOverTarget;
import com.himself12794.powersapi.network.client.C03CyclePowerState;
import com.himself12794.powersapi.power.Power;

/**
 * Manages sending messages to the server.
 * 
 * @author Himself12794
 *
 */
public class Server {

	private final SimpleNetworkWrapper network;

	Server(SimpleNetworkWrapper network) {

		this.network = network;
	}

	public void powerUse(boolean isPrimary, MovingObjectPosition pos, C01PowerUse.Action action) {
		network.sendToServer( new C01PowerUse(isPrimary, pos, action) );
	}
	
	public void setMouseOver( boolean isPrimary, MovingObjectPosition pos ) {
		network.sendToServer( new C02SetMouseOverTarget( isPrimary, pos ));
	}
	
	public void cyclePowerState(Power power) {
		network.sendToServer( new C03CyclePowerState( power ) );
	}
	
}
