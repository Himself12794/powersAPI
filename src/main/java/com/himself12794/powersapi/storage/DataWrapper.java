package com.himself12794.powersapi.storage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;


public abstract class DataWrapper implements IExtendedEntityProperties {

	public final EntityLivingBase theEntity;
	
	protected DataWrapper(EntityLivingBase entity) {
		theEntity = entity;
	}
	
	public abstract void resetForRespawn();

}
