package com.himself12794.powersapi.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

public class C01PowerUse implements IMessage {
	
	private Power power;
	private Action action;
	private NBTTagCompound lookVec;

    public C01PowerUse() {  }
    
    public C01PowerUse(Power power, MovingObjectPosition pos, Action action) {
    	this.power = power; 
    	this.lookVec = UsefulMethods.movingObjectPosToNBT( pos );
    	this.action = action;
    }

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarShort( buf, action.ordinal() );
		
		if (action == Action.START ) {
			ByteBufUtils.writeUTF8String( buf, power.getUnlocalizedName() );
			ByteBufUtils.writeTag( buf, lookVec );
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) { 
		action = Action.values()[ByteBufUtils.readVarShort( buf )];
		
		if (action == Action.START) {
			power = Power.lookupPower( ByteBufUtils.readUTF8String( buf ) );
			lookVec = ByteBufUtils.readTag( buf );
		}
	}
	
	@Override
	public String toString() {
		return "[Power:" + power + ",Action:" + action + ",LookVec:" + lookVec + "]";
	}
	
	public static class Handler implements IMessageHandler<C01PowerUse, IMessage> {
       
        @Override
        public IMessage onMessage(final C01PowerUse message, final MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		Runnable task = new Runnable() {

					@Override
					public void run() {
		        		
		        		EntityPlayer player = ctx.getServerHandler().playerEntity;
		        		Power power = message.power;
		        		NBTTagCompound nbt = message.lookVec;
		        				
	        			if (message.action.equals( Action.START ) && power != null) {
	        				PowersWrapper.get( player ).usePower( power, UsefulMethods.movingObjectPositionFromNBT( nbt, player.worldObj ) );
	        			} else {
			        		PowersWrapper.get( player ).stopUsingPower();
						}

					}
   
        		};
        		
        		ctx.getServerHandler().playerEntity.getServerForPlayer().addScheduledTask( task );
        		

        		
        	}
        	
        	return null;
        }
	}
	
	public static enum Action {
		STOP, START
	}
}
