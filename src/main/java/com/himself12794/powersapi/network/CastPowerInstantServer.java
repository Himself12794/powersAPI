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
		//spell = Spell.lookupSpell(ByteBufUtils.readUTF8String(buf));
		
	}
	
	public static class Handler implements IMessageHandler<CastPowerInstantServer, IMessage> {
    	
		String prefix = Reference.MODID + ".";
       
        @Override
        public IMessage onMessage(CastPowerInstantServer message, MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		//UsefulThings.print("Got message from client to cast a spell");
        		
        		Power spell = message.spell;
        		EntityPlayer caster = ctx.getServerHandler().playerEntity;
        		MovingObjectPosition target = new MovingObjectPosition((EntityLivingBase) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id));		
        		
        		caster.getEntityData().setBoolean(prefix + "power.success", spell.onStrike(target.entityHit.worldObj, target, caster, 1.0F));
        		
        	}
        	
        	return null;
        }
	}
}
