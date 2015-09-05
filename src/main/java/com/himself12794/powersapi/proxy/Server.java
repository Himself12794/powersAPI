package com.himself12794.powersapi.proxy;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.himself12794.powersapi.network.client.C01PowerUse;
import com.himself12794.powersapi.network.client.C02SetMouseOverTarget;
import com.himself12794.powersapi.power.Power;

public class Server {

	private final SimpleNetworkWrapper network;

	Server(SimpleNetworkWrapper network) {

		this.network = network;
	}

	public void powerUse(Power power, MovingObjectPosition pos, C01PowerUse.Action action) {
		network.sendToServer( new C01PowerUse(power, pos, action) );
	}
	
	public void setMouseOver(MovingObjectPosition pos) {
		network.sendToServer( new C02SetMouseOverTarget( pos ));
	}
	
}
