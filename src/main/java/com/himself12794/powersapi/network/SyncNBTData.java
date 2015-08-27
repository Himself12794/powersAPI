package com.himself12794.powersapi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.util.DataWrapper;

public class SyncNBTData implements IMessage {
	
	private NBTTagCompound nbttags;
	
    public SyncNBTData() {  }
    
    public SyncNBTData(EntityPlayer player) { 
    	nbttags = player.getEntityData();
    }

	@Override
	public void toBytes(ByteBuf buf) {	
		ByteBufUtils.writeTag( buf, nbttags );
	}

	@Override
	public void fromBytes(ByteBuf buf) { 
		nbttags = ByteBufUtils.readTag( buf );
	}
	
	public static class Handler implements IMessageHandler<SyncNBTData, IMessage> {
       
        @Override
        public IMessage onMessage(SyncNBTData message, MessageContext ctx) {
        	if (ctx.side.isClient()) {
        		EntityPlayer player = (EntityPlayer) DataWrapper.set( Minecraft.getMinecraft().thePlayer, message.nbttags ).getEntity();
        	}
        	
        	return null;
        }
	}
}
