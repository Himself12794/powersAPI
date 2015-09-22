package com.himself12794.powersapi.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.UsefulMethods;

/**
 * Manages setting a power in use, or stopping a power that is already in use.
 * 
 * @author Himself12794
 *
 */
public class C01PowerUse implements IMessage {
	
	private Action action;
	private NBTTagCompound lookVec;
	private boolean isPrimary;

    public C01PowerUse() {  }
    
    public C01PowerUse(boolean primary, MovingObjectPosition pos, Action action) {
    	this.lookVec = UsefulMethods.movingObjectPosToNBT( pos );
    	this.action = action;
    	this.isPrimary = primary;
    }

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarShort( buf, action.ordinal() );
		ByteBufUtils.writeVarShort( buf, isPrimary ? 1 : 0 );
		
		if (action == Action.START ) {
			ByteBufUtils.writeTag( buf, lookVec );
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) { 
		action = Action.values()[ByteBufUtils.readVarShort( buf )];
		isPrimary = ByteBufUtils.readVarShort( buf ) == 1;
		
		if (action == Action.START) {
			lookVec = ByteBufUtils.readTag( buf );
		}
	}
	
	@Override
	public String toString() {
		return "[IsPrimary: " + isPrimary + ", Action:" + action + ",LookVec:" + lookVec + "]";
	}
	
	public static class Handler implements IMessageHandler<C01PowerUse, IMessage> {
       
        @Override
        public IMessage onMessage(final C01PowerUse message, MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		final EntityPlayer player = PowersAPI.proxy().getPlayerFromContext( ctx );
        		
        		Runnable task = new Runnable() {

					@Override
					public void run() {
		        		NBTTagCompound nbt = message.lookVec;
		        				
	        			if (message.action.equals( Action.START )) {
	        				if (message.isPrimary) PowersEntity.get( player ).usePrimaryPower( UsefulMethods.movingObjectPositionFromNBT( nbt, player.worldObj ) );
	        				else PowersEntity.get( player ).useSecondaryPower( UsefulMethods.movingObjectPositionFromNBT( nbt, player.worldObj ) );
	        			} else {
			        		if (message.isPrimary) PowersEntity.get( player ).stopUsingPrimaryPower();
			        		else PowersEntity.get( player ).stopUsingSecondaryPower();
						}

					}
   
        		};
				
				PowersAPI.proxy().scheduleTaskBasedOnContext( ctx, task );
        	}
        	
        	return null;
        }
	}
	
	public static enum Action {
		STOP, START
	}
}
