package com.himself12794.powersapi.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersWrapper;


public class C03CyclePowerState implements IMessage {
	
	private Power power;
	
	public C03CyclePowerState() { }

	public C03CyclePowerState(Power power) {
		this.power = power;
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarInt( buf, power.getId(), ByteBufUtils.varIntByteCount( Power.getPowerCount() ) );

	}

	@Override
	public void fromBytes(ByteBuf buf) {

		power = Power.lookupPowerById( ByteBufUtils.readVarInt( buf, ByteBufUtils.varIntByteCount( Power.getPowerCount() ) ) );

	}
	
	public static class Handler implements IMessageHandler<C03CyclePowerState, IMessage> {

		@Override
		public IMessage onMessage(final C03CyclePowerState message, final MessageContext ctx) {

			if (ctx.side.isServer() && message.power != null) {

				final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				
				Runnable task = new Runnable() {

					@Override
					public void run() {		
						PowersWrapper.get( player ).getPowerProfile( message.power ).cycleState(true);
					}
									
				};
				
				player.getServerForPlayer().addScheduledTask( task );
				
			}
			
			
			return null;
		}
		
	}

}
