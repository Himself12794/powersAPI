package com.himself12794.powersapi.storage;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.PowersNetwork;

/**
 * Implementation of IExtendedEntityProperties for this mod. Base class used by
 * data handlers. Only used for instances of EntityLivingBase.
 * 
 * @author Himself12794
 *
 */
public abstract class PropertiesBase implements IExtendedEntityProperties {

	public final EntityLivingBase theEntity;
	
	public PropertiesBase(EntityLivingBase entity) {
		theEntity = entity;
	}

	/**
	 * Called every Living update tick.
	 */
	public abstract void onUpdate();
	
	/**
	 * {@link IExtendedEntityProperties#init(net.minecraft.entity.Entity, net.minecraft.world.World)} is called
	 * before the entity is loaded from NBT, so this is for after the entity is added.
	 * @param world TODO
	 */
	public abstract void onJoinWorld(World world);

	/**
	 * Called when when the entity respawns. (If player)
	 */
	public abstract void resetForRespawn();

	public abstract String getIdentifier();

	public final PropertiesBase copyTo(EntityLivingBase entity) {

		PropertiesBase wrapper;
		NBTTagCompound data;

		wrapper = (PropertiesBase) entity.getExtendedProperties( getIdentifier() );

		if (wrapper == null) {
			entity.registerExtendedProperties( getIdentifier(), this );
		} else {
			data = new NBTTagCompound();
			saveNBTData( data );
			entity.getExtendedProperties( getIdentifier() ).loadNBTData( data );
		}

		return (PropertiesBase) entity.getExtendedProperties( getIdentifier() );

	}
}
