package com.himself12794.powersapi.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersEntity;

public class S02SetPower implements IMessage {

	private Power power;
	private Selection selection;

	public S02SetPower() {

	}

	public S02SetPower(Power power, Selection selection) {

		this.power = power;
		this.selection = selection;
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarInt( buf, power.getId(), 4 );
		ByteBufUtils.writeVarShort( buf, selection.ordinal() );
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		power = Power.lookupPowerById( ByteBufUtils.readVarInt( buf, 4 ) );
		selection = ByteBufUtils.readVarShort( buf ) == 0 ? Selection.PRIMARY : Selection.SECONDARY;
	}

	public static class Handler implements
			IMessageHandler<S02SetPower, IMessage> {

		@Override
		public IMessage onMessage(final S02SetPower message, final MessageContext ctx) {

			if (ctx.side.isClient()) {
				Runnable task = new Runnable() {

					@Override
					public void run() {
						
						if (PowersAPI.proxy.getPlayer() != null) {
							PowersEntity pw = PowersEntity.get( PowersAPI.proxy.getPlayer() );
							
							if (message.selection == Selection.PRIMARY) 
								pw.setPrimaryPower( message.power );
							else
								pw.setSecondaryPower( message.power );
						}
					}
				};
				
				Minecraft.getMinecraft().addScheduledTask( task );
				
			}

			return null;
		}
	}
	
	public static enum Selection {
		PRIMARY, SECONDARY
	}
}
