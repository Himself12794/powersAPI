package com.himself12794.powersapi.network;

import com.himself12794.powersapi.entity.EntitySpell;
import com.himself12794.powersapi.powerfx.PowerEffect;
import com.himself12794.powersapi.util.Reference;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PowerEffectsClient implements IMessage {
	
	private PowerEffect effect;
	private int affectedEntityId;
	private int casterEntityId;
	private boolean isDone;
	private int timeLeft;
	
	public PowerEffectsClient () {
		
	}
	
	public PowerEffectsClient(PowerEffect pfx, EntityLivingBase affectedEntity, EntityLivingBase caster, boolean isDone, int timeLeft) {
		
		effect = pfx;
		this.affectedEntityId = affectedEntity.getEntityId();
		casterEntityId = caster != null ? caster.getEntityId() : 0;
		this.isDone = isDone;
		this.timeLeft = timeLeft;
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeVarShort(buf, effect.id);
		ByteBufUtils.writeVarInt(buf, affectedEntityId, 4);
		ByteBufUtils.writeVarInt(buf, casterEntityId, 4);
		ByteBufUtils.writeVarShort(buf, isDone ? 1 : 0);
		ByteBufUtils.writeVarInt(buf, timeLeft, 4);
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		effect = PowerEffect.getEffectById(ByteBufUtils.readVarShort(buf));
		affectedEntityId = ByteBufUtils.readVarInt(buf, 4);
		casterEntityId = ByteBufUtils.readVarInt(buf, 4);
		isDone = ByteBufUtils.readVarShort(buf) == 1;
		timeLeft = ByteBufUtils.readVarInt(buf, 4);
		
	}
	
	public static class Handler implements IMessageHandler<PowerEffectsClient, IMessage> {
       
        @Override
        public IMessage onMessage(PowerEffectsClient message, MessageContext ctx) {
        	
        	if (ctx.side.isClient()) {
        		
        		World world = Minecraft.getMinecraft().theWorld;
        		EntityLivingBase target = null;
        		if (world.getEntityByID(message.affectedEntityId) instanceof EntityLivingBase) target = (EntityLivingBase) world.getEntityByID(message.affectedEntityId);
        		EntityLivingBase caster = null;
        		if (world.getEntityByID(message.casterEntityId) instanceof EntityLivingBase) caster = (EntityLivingBase) world.getEntityByID(message.casterEntityId);
        		
        		
        		if (message.isDone) message.effect.onRemoval(target, caster);
        		else message.effect.onUpdate(target, message.timeLeft, caster);
    
        	}
        	return null;
        }
	}



}
