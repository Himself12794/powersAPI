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
	private boolean isPrimary;

	public C02SetMouseOverTarget() { }

	public C02SetMouseOverTarget(boolean isPrimary, MovingObjectPosition pos) {

		this.isPrimary = isPrimary;
		this.nbttags = UsefulMethods.movingObjectPosToNBT( pos );
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarShort( buf, isPrimary ? 1 : 0 );
		ByteBufUtils.writeTag( buf, nbttags );
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		isPrimary = ByteBufUtils.readVarShort( buf ) == 1;
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
							MovingObjectPosition pos = UsefulMethods.movingObjectPositionFromNBT( message.nbttags, player.worldObj );
							if (message.isPrimary) PowersEntity.get( player ).mouseOverPosPrimary = pos;
							else PowersEntity.get( player ).mouseOverPosSecondary = pos;
						}
					}
				};
				
				PowersAPI.proxy().scheduleTaskBasedOnContext( ctx, task );
			}

			return null;
		}
	}
}
