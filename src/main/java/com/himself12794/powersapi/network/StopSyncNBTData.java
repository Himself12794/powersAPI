package com.himself12794.powersapi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.util.DataWrapper;

// TODO solve excessive syncs
public class StopSyncNBTData implements IMessage {
	
	private NBTTagCompound tags;
	
    public StopSyncNBTData() {  }

    public StopSyncNBTData(NBTTagCompound tags) {
    	this.tags = tags;
    }
    
	@Override
	public void toBytes(ByteBuf buf) {	
		
		ByteBufUtils.writeTag( buf, tags );
		
	}

	@Override
	public void fromBytes(ByteBuf buf) { 
		tags = ByteBufUtils.readTag( buf );
	}
	
	public static class Handler implements IMessageHandler<StopSyncNBTData, IMessage> {
       
        @Override
        public IMessage onMessage(StopSyncNBTData message, MessageContext ctx) {
        	if (ctx.side.isServer()) {
        		
        		//PowersAPI.logger.info( DataWrapper.get( ctx.getServerHandler().playerEntity ).getModEntityData() );
        		//PowersAPI.logger.info( message.tags );
        		
        		if ( DataWrapper.get( ctx.getServerHandler().playerEntity ).getModEntityData().equals( message.tags )) {
        			PowersAPI.logger.info( "le equals" );
        			PowersAPI.proxy.setNeedsUpdate( false );
        		}
        			

        	}
        	
        	return null;
        }
	}
}
