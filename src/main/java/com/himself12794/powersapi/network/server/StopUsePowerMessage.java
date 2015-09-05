package com.himself12794.powersapi.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.storage.PowersWrapper;

public class StopUsePowerMessage implements IMessage {

    public StopUsePowerMessage() {  }

	@Override
	public void toBytes(ByteBuf buf) {	}

	@Override
	public void fromBytes(ByteBuf buf) {  }
	
	public static class Handler implements IMessageHandler<StopUsePowerMessage, IMessage> {
       
        @Override
        public IMessage onMessage(StopUsePowerMessage message, final MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		Runnable task = new Runnable() {

					@Override
					public void run() {
		        		
		        		EntityPlayer player = ctx.getServerHandler().playerEntity;
		        		PowersWrapper.get( player ).stopUsingPower();
						
					}
   
        		};
        		
        		ctx.getServerHandler().playerEntity.getServerForPlayer().addScheduledTask( task );
        		
        	}
        	
        	return null;
        }
	}
}
