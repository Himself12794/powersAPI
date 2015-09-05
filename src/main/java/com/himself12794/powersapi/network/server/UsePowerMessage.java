package com.himself12794.powersapi.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

public class UsePowerMessage implements IMessage {
	
	private Power power;
	private NBTTagCompound lookVec;

    public UsePowerMessage() {  }
    
    public UsePowerMessage(Power power, MovingObjectPosition pos) {
    	this.power = power; 
    	this.lookVec = UsefulMethods.movingObjectPosToNBT( pos );
    }

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String( buf, power.getUnlocalizedName() );
		ByteBufUtils.writeTag( buf, lookVec );
	}

	@Override
	public void fromBytes(ByteBuf buf) { 
		power = Power.lookupPower( ByteBufUtils.readUTF8String( buf ) );
		lookVec = ByteBufUtils.readTag( buf );
	}
	
	public static class Handler implements IMessageHandler<UsePowerMessage, IMessage> {
       
        @Override
        public IMessage onMessage(final UsePowerMessage message, final MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		Runnable task = new Runnable() {

					@Override
					public void run() {
		        		
		        		EntityPlayer player = ctx.getServerHandler().playerEntity;
		        		Power power = message.power;
		        		NBTTagCompound nbt = message.lookVec;
		        		
		        		if (power != null) {
		        			PowersWrapper.get( player ).usePower( power, UsefulMethods.movingObjectPositionFromNBT( nbt, player.worldObj ) );
		        		}
						
					}
   
        		};
        		
        		ctx.getServerHandler().playerEntity.getServerForPlayer().addScheduledTask( task );
        		

        		
        	}
        	
        	return null;
        }
	}
}
