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
	
    public StopSyncNBTData() {  }

	@Override
	public void toBytes(ByteBuf buf) {	
	}

	@Override
	public void fromBytes(ByteBuf buf) { 
	}
	
	public static class Handler implements IMessageHandler<StopSyncNBTData, IMessage> {
       
        @Override
        public IMessage onMessage(StopSyncNBTData message, MessageContext ctx) {
        	if (ctx.side.isServer()) {
        		
        		//System.out.println("le messág dua");
        		
        		//NBTTagCompound nbt = DataWrapper.get( ctx.getServerHandler().playerEntity ).getModEntityData();
        		//nbt.setBoolean( "stopUpdates", true );
        			

        	}
        	
        	return null;
        }
	}
}
