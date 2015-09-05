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
import com.himself12794.powersapi.power.EnumPowerSelection;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowersWrapper;

// TODO solve excessive syncs
public class SetPowerClient implements IMessage {

	private Power power;
	private EnumPowerSelection selection;

	public SetPowerClient() {

	}

	public SetPowerClient(Power power, EnumPowerSelection selection) {

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
		selection = ByteBufUtils.readVarShort( buf ) == 0 ? EnumPowerSelection.PRIMARY : EnumPowerSelection.SECONDARY;
	}

	public static class Handler implements
			IMessageHandler<SetPowerClient, IMessage> {

		@Override
		public IMessage onMessage(final SetPowerClient message, final MessageContext ctx) {

			if (ctx.side.isClient()) {
				Runnable task = new Runnable() {

					@Override
					public void run() {
						
						if (PowersAPI.proxy.getPlayer() != null) {
							PowersWrapper pw = PowersWrapper.get( PowersAPI.proxy.getPlayer() );
							
							if (message.selection == EnumPowerSelection.PRIMARY) 
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
}
