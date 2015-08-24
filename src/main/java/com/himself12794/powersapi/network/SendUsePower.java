package com.himself12794.powersapi.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.Reference;
import com.himself12794.powersapi.util.Reference.TagIdentifiers;

public class SendUsePower implements IMessage {
	
	private Power power;

    public SendUsePower() {  }
    
    public SendUsePower(Power power) {
    	this.power = power; 
    }

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String( buf, power.getUnlocalizedName() );
	}

	@Override
	public void fromBytes(ByteBuf buf) { 
		power = Power.lookupPower( ByteBufUtils.readUTF8String( buf ) );
	}
	
	public static class Handler implements IMessageHandler<SendUsePower, IMessage> {
       
        @Override
        public IMessage onMessage(SendUsePower message, MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		EntityPlayer player = ctx.getServerHandler().playerEntity;
        		Power power = message.power;
        		
        		if (power != null) {
        			DataWrapper.get( player ).usePower( power );
        		}
        		
        	}
        	
        	return null;
        }
	}
}
