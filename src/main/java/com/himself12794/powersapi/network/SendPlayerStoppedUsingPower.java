package com.himself12794.powersapi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.util.DataWrapper;

public class SendPlayerStoppedUsingPower implements IMessage {

    public SendPlayerStoppedUsingPower() {  }

	@Override
	public void toBytes(ByteBuf buf) {	}

	@Override
	public void fromBytes(ByteBuf buf) {  }
	
	public static class Handler implements IMessageHandler<SendPlayerStoppedUsingPower, IMessage> {
       
        @Override
        public IMessage onMessage(SendPlayerStoppedUsingPower message, MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		EntityPlayer player = ctx.getServerHandler().playerEntity;
        		DataWrapper.get( player ).stopUsingPowerEarly();
        		
        	}
        	
        	return null;
        }
	}
}
