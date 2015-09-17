package com.himself12794.powersapi.storage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

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
	 * @param world the world the entity is joining
	 */
	public abstract void onJoinWorld(World world);
	
	/**
	 * Called every time the entity takes damage.
	 * @param affectedEntity TODO
	 * @param source
	 * @param amount
	 * @param hasChanged whether or not the amount has been modified by other instances of {@link PropertiesBase}
	 * 
	 * @return the new damage amount
	 */
	public float onDamaged(EntityLivingBase affectedEntity, DamageSource source, float amount, boolean hasChanged) {
		return amount;
	}

	/**
	 * Called when when the entity respawns. (If player)
	 */
	public abstract void resetForRespawn();

	public abstract String getIdentifier();
	
	public boolean isCreativePlayer() {
		if (theEntity instanceof EntityPlayer) {
			return ((EntityPlayer)theEntity).capabilities.isCreativeMode;
		}
		
		return false;
	}

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
