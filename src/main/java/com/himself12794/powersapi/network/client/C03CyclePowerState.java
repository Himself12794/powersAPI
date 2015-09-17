package com.himself12794.powersapi.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersEntity;


public class C03CyclePowerState implements IMessage {
	
	private Power power;
	private boolean primary;
	
	public C03CyclePowerState() { }

	public C03CyclePowerState(Power power) {
		this.power = power;
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarInt( buf, power.getId(), ByteBufUtils.varIntByteCount( PowersRegistry.getPowerCount() ) );

	}

	@Override
	public void fromBytes(ByteBuf buf) {

		power = PowersRegistry.lookupPowerById( ByteBufUtils.readVarInt( buf, ByteBufUtils.varIntByteCount( PowersRegistry.getPowerCount() ) ) );

	}
	
	public static class Handler implements IMessageHandler<C03CyclePowerState, IMessage> {

		@Override
		public IMessage onMessage(final C03CyclePowerState message, final MessageContext ctx) {

			if (ctx.side.isServer() && message.power != null) {

				final EntityPlayer player =  PowersAPI.proxy().getPlayerFromContext( ctx );
				
				Runnable task = new Runnable() {

					@Override
					public void run() {		
						PowersEntity.get( player ).getPowerProfile( message.power ).cycleState(true);
					}
									
				};
				
				ctx.getServerHandler().playerEntity.getServerForPlayer().addScheduledTask( task );
				
			}
			
			
			return null;
		}
		
	}

}
