package com.himself12794.powersapi.util;

import java.util.Set;

import com.google.common.collect.Sets;
import com.himself12794.powersapi.power.IPersistantEffect;
import com.himself12794.powersapi.power.PowerEffect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;


public class DataWrapperP extends DataWrapper {
	
	public final EntityPlayer player;
	public final PlayerSaveHandler saveHandler;

	private DataWrapperP(EntityPlayer entity) {
		super( entity );
		player = (EntityPlayer) entity;
		saveHandler = new PlayerSaveHandler(player);
	}
	
	public static DataWrapperP get(EntityPlayer player) {
		return new DataWrapperP(player);
	}

}
