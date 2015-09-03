package com.himself12794.powersapi.util;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;


public final class DataHandler {
	
	//private static final DataHandler INSTANCE = new DataHandler();
	
	private final Map<UUID, NBTTagCompound> data = Maps.newHashMap();
	
	public DataHandler() {
		
		System.out.println("initialized on " + Thread.currentThread().getName());
		
	}
	
	public NBTTagCompound getData(EntityPlayer entity) {
		
		//System.out.println(entity.worldObj.isRemote);
		
		NBTTagCompound value = null;
		
		if (!data.containsKey( entity.getUniqueID() )) {
			data.put( entity.getUniqueID(), new NBTTagCompound() );
			value = data.get( entity.getUniqueID() );
		} else {
			value = data.get( entity.getUniqueID() );
		}
		
		return value;
	}
	
	public DataWrapper updateEntity(EntityPlayer entity, NBTTagCompound tags) {
		data.put( entity.getUniqueID(), tags );
		
		return DataWrapper.get( entity );
		
	}
	
	/*public static DataHandler getInstance() {
		return INSTANCE;
	}*/

}
