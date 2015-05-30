package com.himself12794.powersapi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.spell.Spell;
import com.himself12794.powersapi.util.Reference;

public class CastSpellInstantServer implements IMessage {
	
	private int id;
	private Spell spell;
	//private float modifier;

    public CastSpellInstantServer() {  }
	
	public CastSpellInstantServer(EntityLivingBase entity, float modifier, Spell spell) {
		
		this.id = entity.getEntityId();
		this.spell = spell;
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeVarInt(buf, id, 4);
		ByteBufUtils.writeVarInt(buf, Spell.getSpellId(spell), 4);

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		//System.out.println("Decoding data");
		id = ByteBufUtils.readVarInt(buf, 4);
		spell = Spell.lookupSpellById(ByteBufUtils.readVarInt(buf, 4));
		//spell = Spell.lookupSpell(ByteBufUtils.readUTF8String(buf));
		
	}
	
	public static class Handler implements IMessageHandler<CastSpellInstantServer, IMessage> {
    	
		String prefix = Reference.MODID + ".";
       
        @Override
        public IMessage onMessage(CastSpellInstantServer message, MessageContext ctx) {
    		
    		//UsefulThings.print("Got message from client to cast a spell");
        	
        	if (ctx.side.isServer()) {
        		
        		//UsefulThings.print("Got message from client to cast a spell");
        		
        		Spell spell = message.spell;
        		EntityPlayer caster = ctx.getServerHandler().playerEntity;
        		MovingObjectPosition target = new MovingObjectPosition((EntityLivingBase) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id));		
        		
        		caster.getEntityData().setBoolean(prefix + "spell.success", spell.onStrike(target.entityHit.worldObj, target, caster, 1.0F));
        		
        	}
        	
        	return null;
        }
	}
}
