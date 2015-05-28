package com.himself12794.powersAPI.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersAPI.spell.Spell;
import com.himself12794.powersAPI.util.Reference;

public class CastSpellInstantServer implements IMessage {
	
	private int id;
	private String spell;
	private ItemStack stack;
	private NBTTagCompound modifier;

    public CastSpellInstantServer() {  }
	
	public CastSpellInstantServer(int entity, float modifier, Spell spell, ItemStack stack) {
		this.id = entity;
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("modifier", modifier);
		this.modifier = nbt;
		this.spell = spell.getUnlocalizedName();
		this.stack = stack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		id = ByteBufUtils.readVarInt(buf, 4);
		modifier = ByteBufUtils.readTag(buf);
		spell = ByteBufUtils.readUTF8String(buf);
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarInt(buf, id, 4);
		ByteBufUtils.writeTag(buf, modifier);
		ByteBufUtils.writeUTF8String(buf, spell);
		ByteBufUtils.writeItemStack(buf, stack);

	}
	
	public static class Handler implements IMessageHandler<CastSpellInstantServer, IMessage> {
    	
		String prefix = Reference.MODID + ".";
       
        @Override
        public IMessage onMessage(CastSpellInstantServer message, MessageContext ctx) {
    		
    		//UsefulThings.print("Got message from client to cast a spell");
        	
        	if (ctx.side.isServer()) {
        		
        		//UsefulThings.print("Got message from client to cast a spell");
        		
        		Spell spell = Spell.lookupSpell(message.spell);
        		float modifier = message.modifier.getFloat("modifier");
        		ItemStack stack = message.stack;
        		EntityPlayer caster = ctx.getServerHandler().playerEntity;
        		MovingObjectPosition target = new MovingObjectPosition((EntityLivingBase) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id));		
        		
        		caster.getEntityData().setBoolean(prefix + "spell.success", spell.onStrike(target.entityHit.worldObj, target, caster, modifier));
        		
        	}
        	
        	return null;
        }
	}
}
