package com.himself12794.powersapi.storage;

import com.himself12794.powersapi.PowersAPI;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Implementation of IExtendedEntityProperties for this mod. Base class used by
 * data handlers that adds a couple more event methods, and some utility ones. Only used for instances of EntityLivingBase.
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
	 * Called every living update tick.
	 */
	public abstract void onUpdate();
	
	/**
	 * {@link IExtendedEntityProperties#init(net.minecraft.entity.Entity, net.minecraft.world.World)} is called
	 * before the entity is loaded from NBT and added to the world, so this is for after the entity is added to the world.
	 * @param world the world the entity is joining
	 */
	public abstract void onJoinWorld(World world);
	
	/**
	 * Called every time the entity takes damage.
	 * @param affectedEntity entity affected
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
	 * Called when when the entity re-spawns. (If player)
	 */
	public abstract void resetForRespawn();
	
	public boolean isCreativePlayer() {
		if (theEntity instanceof EntityPlayer) {
			return ((EntityPlayer)theEntity).capabilities.isCreativeMode;
		}
		
		return false;
	}
	
	/**
	 * Copies wrapper data from one entity to another. Must have registered the class for this to work.
	 * Mostly intended for re-spawning players.
	 * 
	 * @param entity
	 * @return
	 */
	public final PropertiesBase copyTo(EntityLivingBase entity) {

		PropertiesBase wrapper = PowersAPI.propertiesHandler().getWrapper( getClass(), entity );
		NBTTagCompound data = new NBTTagCompound();

		saveNBTData( data );
		if (wrapper != null) wrapper.loadNBTData( data );

		return wrapper;

	}
}
