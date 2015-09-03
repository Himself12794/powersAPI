package com.himself12794.powersapi.network;

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
public class SyncNBTData implements IMessage {

	private NBTTagCompound nbttags;

	public SyncNBTData() {

	}

	public SyncNBTData(NBTTagCompound nbttags) {

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
			IMessageHandler<SyncNBTData, IMessage> {

		@Override
		public IMessage onMessage(SyncNBTData message, MessageContext ctx) {

			if (ctx.side.isClient()) {
				System.out.println("updating");
				if (PowersAPI.proxy.getPlayer() != null) {
					PowersAPI.getDataHandler().updateEntity(
							PowersAPI.proxy.getPlayer(), message.nbttags );

				}
			}

			return null;
		}
	}
}
