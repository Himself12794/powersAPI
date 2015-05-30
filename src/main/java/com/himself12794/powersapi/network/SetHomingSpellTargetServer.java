package com.himself12794.powersapi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.entity.EntitySpell;
import com.himself12794.powersapi.util.Reference;

public class SetHomingSpellTargetServer implements IMessage {
	
	private int spellid;
	private boolean isEntityTarget;
	private int entityId;
	private Vec3 vectorPos;

    public SetHomingSpellTargetServer() {  }
	
	public SetHomingSpellTargetServer(EntitySpell spell, MovingObjectPosition target) {
		spellid = spell.getEntityId();
		
		if ( target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && target.entityHit != null ) {
			isEntityTarget = true;
			entityId = target.entityHit.getEntityId();
		} else {
			isEntityTarget = false;
			entityId = 0;
		}
		
		vectorPos = target.hitVec;

	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeVarInt(buf, spellid, 4);
		ByteBufUtils.writeVarShort(buf, isEntityTarget ? 1 : 0);
		
		NBTTagCompound vectorCoords = new NBTTagCompound();
		vectorCoords.setDouble("x", vectorPos.xCoord);
		vectorCoords.setDouble("y", vectorPos.yCoord);
		vectorCoords.setDouble("z", vectorPos.zCoord);
		ByteBufUtils.writeTag(buf, vectorCoords);
		
		ByteBufUtils.writeVarInt(buf, entityId, 4);
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		spellid = ByteBufUtils.readVarInt(buf, 4);
		isEntityTarget = ByteBufUtils.readVarShort(buf) == 1;
		
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		double x =nbt.getDouble("x");
		double y =nbt.getDouble("y");
		double z =nbt.getDouble("z");
		vectorPos = new Vec3(x, y, z);
		
		entityId = ByteBufUtils.readVarInt(buf, 4);
		
	}
	
	public static class Handler implements IMessageHandler<SetHomingSpellTargetServer, IMessage> {
    	
		String prefix = Reference.MODID + ".";
       
        @Override
        public IMessage onMessage(SetHomingSpellTargetServer message, MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		EntitySpell spell = (EntitySpell) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.spellid);
        		
        		if (spell != null) {
        			
            		MovingObjectPosition target = null;
        			
        			if (message.isEntityTarget) {
        				target = new MovingObjectPosition(ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityId), message.vectorPos);
        			} 
        			
        			spell.target = target;
        			
        		}
        		
        	}
        	
        	return null;
        }
	}
}
