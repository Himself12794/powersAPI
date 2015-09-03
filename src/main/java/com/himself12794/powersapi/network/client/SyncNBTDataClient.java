package com.himself12794.powersapi.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.util.DataWrapper;

// TODO solve excessive syncs
public class SyncNBTDataClient implements IMessage {

	private NBTTagCompound nbttags;

	public SyncNBTDataClient() {

	}

	public SyncNBTDataClient(NBTTagCompound nbttags) {

		this.nbttags = nbttags;
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeTag( buf, nbttags );
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		nbttags = ByteBufUtils.readTag( buf );
	}

	public static class Handler implements
			IMessageHandler<SyncNBTDataClient, IMessage> {

		@Override
		public IMessage onMessage(final SyncNBTDataClient message, final MessageContext ctx) {

			if (ctx.side.isClient()) {
				Runnable task = new Runnable() {

					@Override
					public void run() {
						
						if (PowersAPI.proxy.getPlayer() != null) {
							DataWrapper.set( PowersAPI.proxy.getPlayer(), message.nbttags );						
						}
					}
				};
				
				Minecraft.getMinecraft().addScheduledTask( task );
				
			}

			return null;
		}
	}
}
