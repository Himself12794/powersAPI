package com.himself12794.powersapi.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.himself12794.powersapi.network.server.S01SyncProperty;
import com.himself12794.powersapi.network.server.S02SetPower;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PropertiesBase;

/**
 * Handles sending messages to the client.
 * 
 * @author Himself12794
 *
 */
public class Client {
	
	private final SimpleNetworkWrapper network;
	
	Client(SimpleNetworkWrapper network) {
		this.network = network;
	}
	
	public void syncProperties(PropertiesBase properties, EntityPlayer player) {
		network.sendTo( new S01SyncProperty( properties ), (EntityPlayerMP) player );
	}

	public void setPower(Power power, S02SetPower.Selection selection, EntityPlayer player) {
		network.sendTo( new S02SetPower(power, selection), (EntityPlayerMP) player);
	}

}
