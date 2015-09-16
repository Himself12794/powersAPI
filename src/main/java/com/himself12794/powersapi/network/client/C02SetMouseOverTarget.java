package com.himself12794.powersapi.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.UsefulMethods;
public class C02SetMouseOverTarget implements IMessage {

	private NBTTagCompound nbttags;

	public C02SetMouseOverTarget() {

	}

	public C02SetMouseOverTarget(MovingObjectPosition pos) {

		this.nbttags = UsefulMethods.movingObjectPosToNBT( pos );
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
			IMessageHandler<C02SetMouseOverTarget, IMessage> {

		@Override
		public IMessage onMessage(final C02SetMouseOverTarget message, final MessageContext ctx) {

			if (ctx.side.isServer()) {
				Runnable task = new Runnable() {

					@Override
					public void run() {
						EntityPlayer player =  PowersAPI.proxy().getPlayerFromContext( ctx );
						if (player != null) {
							PowersEntity.get( player ).mouseOverPos = UsefulMethods.movingObjectPositionFromNBT( message.nbttags, player.worldObj );						
						}
					}
				};
				
				PowersAPI.proxy().scheduleTaskBasedOnContext( ctx, task );
			}

			return null;
		}
	}
}
