package com.himself12794.powersapi.storage;

import net.minecraft.entity.player.EntityPlayer;


public class DataWrapperP extends PowersWrapper {
	
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
