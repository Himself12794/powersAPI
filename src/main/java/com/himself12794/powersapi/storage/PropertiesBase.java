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

	private static final Map<Class<? extends PropertiesBase>, Class<? extends Entity>> quickReference = Maps.newHashMap();
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
	
	public static void registerPropertyClass(Class<? extends PropertiesBase> clazz, Class<? extends Entity> clazz2) {
		
		if (!quickReference.containsKey( clazz )) {
			quickReference.put( clazz, clazz2 );
			PowersAPI.logger.info( "Registered property {}", clazz );
		} else {
			PowersAPI.logger.error( "Could not register class {}, entry already exists", clazz);
		}
		
	}
	
	public static Collection<Class<? extends PropertiesBase>> getRegisteredClasses() {
		return quickReference.keySet();
	}
	
	public static void registerPropertiesForEntity(Entity entity) {
		
		for (Entry<Class<? extends PropertiesBase>, Class<? extends Entity>> entry : quickReference.entrySet()) {
			
			try {
				
				if (entry.getValue().isAssignableFrom( entity.getClass() )) {
				
					Constructor constructor = entry.getKey().getDeclaredConstructor( entry.getValue() );
					constructor.setAccessible( true );
					PropertiesBase wrapper = (PropertiesBase) constructor.newInstance( entity );
					String identifier = wrapper.getIdentifier();
					
					if (entity.getExtendedProperties( identifier ) == null) {
						entity.registerExtendedProperties( identifier, wrapper );
					}
					
					if (!identifierClassAssociations.containsKey( identifier )) {
						identifierClassAssociations.put( identifier, entry.getKey() );
					}
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
	
	public static void runOnJoinWorld(EntityLivingBase entity, World world) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getKey();
			Class association = entry.getValue();
			IExtendedEntityProperties wrapper = entity.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().isAssignableFrom( association )) {
					
					((PropertiesBase)wrapper).onJoinWorld( world );
				}
				
			}
			
		}
	}
	
	public static void runOnRespawn(EntityPlayer player) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getKey();
			Class association = entry.getValue();
			IExtendedEntityProperties wrapper = player.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().isAssignableFrom( association )) {
					
					((PropertiesBase)wrapper).resetForRespawn();
				}
				
			}
			
		}
	}
	
	public static void copyAllOver(EntityLivingBase entity1, EntityLivingBase entity2) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getKey();
			Class association = entry.getValue();
			IExtendedEntityProperties wrapper = entity1.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().isAssignableFrom( association )) {
					
					((PropertiesBase)wrapper).copyTo( entity2 );
				}
				
			}
			
		}
	}
}
