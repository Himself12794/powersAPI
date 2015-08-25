package com.himself12794.powersapi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.Reference;
import com.himself12794.powersapi.util.Reference.TagIdentifiers;

public class CastPowerInstantServer implements IMessage {
	
	private int id;
	private Power spell;
	//private float modifier;

    public CastPowerInstantServer() {  }
	
	public CastPowerInstantServer(EntityLivingBase entity, float modifier, Power spell) {
		
		this.id = entity.getEntityId();
		this.spell = spell;
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeVarInt(buf, id, 4);
		ByteBufUtils.writeVarInt(buf, Power.getPowerId(spell), 4);

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		//System.out.println("Decoding data");
		id = ByteBufUtils.readVarInt(buf, 4);
		spell = Power.lookupPowerById(ByteBufUtils.readVarInt(buf, 4));
		
	}
	
	public static class Handler implements IMessageHandler<CastPowerInstantServer, IMessage> {
       
        @Override
        public IMessage onMessage(CastPowerInstantServer message, MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		Power spell = message.spell;
        		EntityPlayer caster = ctx.getServerHandler().playerEntity;
        		EntityLivingBase target = (EntityLivingBase) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
        		
        		if (target != null) {
	        		MovingObjectPosition targetPos = new MovingObjectPosition(target);		
	        		caster.getEntityData().setBoolean(TagIdentifiers.POWER_SUCCESS, spell.onStrike(targetPos.entityHit.worldObj, targetPos, caster, 1.0F));
        		} else {
        			caster.getEntityData().setBoolean( TagIdentifiers.POWER_SUCCESS, false );
        		}
        	}
        	
        	return null;
        }
	}
}
