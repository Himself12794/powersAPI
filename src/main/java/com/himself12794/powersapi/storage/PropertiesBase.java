package com.himself12794.powersapi.storage;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.PowersNetwork;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

	private static final List<Class<? extends PropertiesBase>> quickReference = Lists.newArrayList();
	private static final Map<String, Class<? extends PropertiesBase>> identifierClassAssociations = Maps.newHashMap();
	public final EntityLivingBase theEntity;
	
	public PropertiesBase(EntityLivingBase entity) {
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

	public abstract String getIdentifier();
	
	public static void registerPropertyClass(Class<? extends PropertiesBase> clazz) {
		
		if (!quickReference.contains( clazz )) {
			quickReference.add( clazz );
			PowersAPI.logger.info( "Registered property {}", clazz );
		} else {
			PowersAPI.logger.error( "Could not register class {}, entry already exists", clazz);
		}
		
	}
	
	public static Collection<Class<? extends PropertiesBase>> getRegisteredClasses() {
		return quickReference;
	}
	
	public static void registerPropertiesForEntity(EntityLivingBase entity) {
		
		for (Class clazz : quickReference) {
			
			try {
				
				Constructor constructor = clazz.getDeclaredConstructor( EntityLivingBase.class );
				constructor.setAccessible( true );
				PropertiesBase wrapper = (PropertiesBase) constructor.newInstance( entity );
				String identifier = wrapper.getIdentifier();
				
				if (entity.getExtendedProperties( identifier ) == null) {
					entity.registerExtendedProperties( identifier, wrapper );
				}
				
				if (!identifierClassAssociations.containsKey( identifier )) {
					identifierClassAssociations.put( identifier, clazz );
				}
				
			} catch (Exception e) {
				PowersAPI.logger.error( e );
			} 
			
		}
		
	}
	
	public static void runUpdates(EntityLivingBase entity) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getKey();
			Class association = entry.getValue();
			IExtendedEntityProperties wrapper = entity.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().isAssignableFrom( association )) {
					
					((PropertiesBase)wrapper).onUpdate();
				}
				
			}
			
		}
		
	}
	
	public static void syncPlayerToClient(EntityPlayerMP entityPlayer) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {
			
			String identifier = entry.getKey();
			Class clazz = entry.getValue();
			IExtendedEntityProperties wrapper = entityPlayer.getExtendedProperties( identifier );
	
			if (wrapper.getClass().isAssignableFrom( clazz )) {
				PowersNetwork.client().syncProperties( ((PropertiesBase)wrapper), entityPlayer);
			}
			
		}
	}
}
