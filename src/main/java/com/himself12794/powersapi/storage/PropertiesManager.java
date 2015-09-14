package com.himself12794.powersapi.storage;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.PowersNetwork;

/**
 * Manages modded ExtendedEntityProperties.
 * 
 * @author Himself12794
 *
 */
public class PropertiesManager {
	
	private PropertiesManager() {}

	private static final Map<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> quickReference = Maps.newHashMap();
	private static final Map<String, Class<? extends PropertiesBase>> identifierClassAssociations = Maps.newHashMap();

	public static void registerPropertyClass(Class<? extends PropertiesBase> clazz, Class<? extends EntityLivingBase> clazz2) {
		
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
	
	public static void registerPropertiesForEntity(EntityLivingBase entity) {
		
		for (Entry<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> entry : quickReference.entrySet()) {
			
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
