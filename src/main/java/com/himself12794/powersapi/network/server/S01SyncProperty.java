package com.himself12794.powersapi.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.storage.PropertiesBase;

public class S01SyncProperty implements IMessage {

	private String identifier;
	private NBTTagCompound compound;

	public S01SyncProperty() {

	}

	public S01SyncProperty(PropertiesBase properties) {

		identifier = properties.getIdentifier();
		compound = new NBTTagCompound();
		properties.saveNBTData( compound );
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeUTF8String( buf, identifier );
		ByteBufUtils.writeTag( buf, compound );
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		identifier = ByteBufUtils.readUTF8String( buf );
		compound = ByteBufUtils.readTag( buf );
	}

	public static class Handler implements IMessageHandler<S01SyncProperty, IMessage> {

		@Override
		public IMessage onMessage(final S01SyncProperty message, final MessageContext ctx) {

			if (ctx.side.isClient()) {
				Runnable task = new Runnable() {
					
					@Override
					public void run() {
						
						EntityPlayer player = PowersAPI.proxy().getPlayerFromContext(ctx);
						
						if (player != null) {
							
							PropertiesBase wrapper = (PropertiesBase) player.getExtendedProperties( message.identifier );
							
							if (wrapper != null) {
								wrapper.loadNBTData( message.compound );
							} 
						}
					}
				};
				
				PowersAPI.proxy().scheduleTaskBasedOnContext( ctx, task );
			}

			return null;
		}
	}
}
