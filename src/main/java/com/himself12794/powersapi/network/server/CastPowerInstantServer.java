package com.himself12794.powersapi.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

public class CastPowerInstantServer implements IMessage {
	
	private NBTTagCompound mop;
	private Power spell;
	private float modifier;
	//private float modifier;

    public CastPowerInstantServer() {  }
	
	public CastPowerInstantServer(MovingObjectPosition pos, float modifier, Power spell) {
		
		this.mop = UsefulMethods.movingObjectPosToNBT( pos );
		this.modifier = modifier;
		this.spell = spell;
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeTag( buf, mop );
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat( "modifier", modifier );
		ByteBufUtils.writeTag( buf, nbt );
		ByteBufUtils.writeVarInt(buf, Power.getPowerId(spell), 4);

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		mop = ByteBufUtils.readTag( buf );
		modifier = ByteBufUtils.readTag( buf ).getFloat( "modifier" );
		spell = Power.lookupPowerById(ByteBufUtils.readVarInt(buf, 4));
		
	}
	
	private MovingObjectPosition getMovingObjectPosition(World world){
		return UsefulMethods.movingObjectPositionFromNBT( mop, world );
	}
	
	public static class Handler implements IMessageHandler<CastPowerInstantServer, IMessage> {
       
        @Override
        public IMessage onMessage(final CastPowerInstantServer message, final MessageContext ctx) {
        	
        	if (ctx.side.isServer()) {
        		
        		Runnable task = new Runnable() {
        			
					@Override
					public void run() {
						Power spell = message.spell;
		        		
		        		EntityPlayer caster = ctx.getServerHandler().playerEntity;
		        		MovingObjectPosition targetPos = message.getMovingObjectPosition( caster.getEntityWorld() );
		        		DataWrapper wrapper = DataWrapper.get( caster );
		        		boolean success = spell.onStrike(ctx.getServerHandler().playerEntity.getEntityWorld(), targetPos, caster, message.modifier);
		        		if (success) wrapper.setPreviousPowerTarget( targetPos );
		        		
			        	wrapper.setPowerSuccess( success );
						
					}
        		};
        		
        		ctx.getServerHandler().playerEntity.getServerForPlayer().addScheduledTask( task );
  
        	}
        	
        	return null;
        }
	}
}
