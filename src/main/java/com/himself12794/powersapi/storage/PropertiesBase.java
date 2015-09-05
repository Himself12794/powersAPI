package com.himself12794.powersapi.storage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Implementation of IExtendedEntityProperties for this mod. Base class used by
 * data handlers. Only used for instances of EntityLivingBase.
 * 
 * @author Himself12794
 *
 */
public abstract class PropertiesBase implements IExtendedEntityProperties {

	private final String identifier;
	public final EntityLivingBase theEntity;

	protected PropertiesBase(String identifier, EntityLivingBase entity) {

		this.identifier = identifier;
		theEntity = entity;
	}

	/**
	 * Called every Living update tick.
	 */
	public abstract void onUpdate();

	/**
	 * Called when when the entity respawns. (If player)
	 * 
	 * @return this, for convenience in data synchronization
	 */
	public abstract PropertiesBase resetForRespawn();

	public final PropertiesBase copyTo(EntityLivingBase entity) {

		PropertiesBase wrapper;
		NBTTagCompound data;

		wrapper = (PropertiesBase) entity.getExtendedProperties( identifier );

		if (wrapper == null) {
			entity.registerExtendedProperties( identifier, this );
		} else {
			data = new NBTTagCompound();
			saveNBTData( data );
			entity.getExtendedProperties( identifier ).loadNBTData( data );
		}

		return (PropertiesBase) entity.getExtendedProperties( identifier );

	}

	public String getIdentifier() {

		return identifier;
	}

}
