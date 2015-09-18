package com.himself12794.powersapi;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.storage.PropertiesBase;
import com.himself12794.powersapi.util.Reference;

/**
 * Manages modded ExtendedEntityProperties.
 * 
 * @author Himself12794
 *
 */
public class PropertiesRegistry {
	
	PropertiesRegistry() {}

	private final Map<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> quickReference = Maps.newHashMap();
	private final Map<String, Class<? extends PropertiesBase>> identifierClassAssociations = Maps.newHashMap();

	public void registerPropertyClass(Class<? extends PropertiesBase> clazz, Class<? extends EntityLivingBase> clazz2) {
		
		if (!quickReference.containsKey( clazz )) {
			quickReference.put( clazz, clazz2 );
			PowersAPI.logger().info( "Registered property {}", clazz );
		} else {
			PowersAPI.logger().error( "Could not register class {}, entry already exists", clazz);
		}
		
	}
	
	public Collection<Class<? extends PropertiesBase>> getRegisteredClasses() {
		return quickReference.keySet();
	}
	
	public Class<? extends EntityLivingBase> getAssociatedEntityType(Class<? extends PropertiesBase> clazz) {
		
		if (quickReference.containsKey( clazz )) 
			return quickReference.get( clazz );
		else return null;
		
	}
	
	public Class<? extends PropertiesBase> getPropertyClassForIdentifier(String identifier) {
		if (this.identifierClassAssociations.containsKey( identifier ))
			return this.identifierClassAssociations.get( identifier );
		else return null;
	}
	
	public PropertiesBase getWrapperForIdentifier(String identifier, EntityLivingBase entity) {
		Class clazz = getPropertyClassForIdentifier(identifier);
		
		if (clazz != null) {
			return getWrapper( clazz, entity );
		} else return null;
	}
	
	public String getModClassIdentifier(Class<? extends PropertiesBase> clazz) {
		return Reference.MODID + ":" + clazz.getName();
	}
	
	public void registerPropertiesForEntity(EntityLivingBase entity) {
		
		for (Entry<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> entry : quickReference.entrySet()) {

			
			if (entry.getValue().isAssignableFrom( entity.getClass() )) {
				
				try {
					
					Constructor constructor = entry.getKey().getDeclaredConstructor( entry.getValue() );
					constructor.setAccessible( true );
					PropertiesBase wrapper = (PropertiesBase) constructor.newInstance( entity );
					
					String identifier = getModClassIdentifier(wrapper.getClass());
					
					if (entity.getExtendedProperties( identifier ) == null) {
						entity.registerExtendedProperties( identifier, wrapper );
					}
					
					if (!identifierClassAssociations.containsKey( identifier )) {
						identifierClassAssociations.put( identifier, entry.getKey() );
					}
					
				} catch (Exception e) {
					PowersAPI.logger().error( e );
				} 
			}
			
		}
		
	}
	
	public void runUpdates(EntityLivingBase entity) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getKey();
			Class association = entry.getValue();
			IExtendedEntityProperties wrapper = entity.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().equals( association )) {
					
					((PropertiesBase)wrapper).onUpdate();
				}
				
			}
			
		}
		
	}
	
	public void syncPlayerToClient(EntityPlayerMP entityPlayer) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {
			
			String identifier = entry.getKey();
			Class clazz = entry.getValue();
			IExtendedEntityProperties wrapper = entityPlayer.getExtendedProperties( identifier );
	
			if (wrapper.getClass().isAssignableFrom( clazz )) {
				PowersNetwork.client().syncProperties( ((PropertiesBase)wrapper), entityPlayer);
			}
			
		}
	}
	
	public void runOnJoinWorld(EntityLivingBase entity, World world) {
		
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
	
	public float runOnDamaged(EntityLivingBase entity, DamageSource source, float amount) {
		
		float value = amount;
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getKey();
			Class association = entry.getValue();
			IExtendedEntityProperties wrapper = entity.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().isAssignableFrom( association )) {
					
					value = ((PropertiesBase)wrapper).onDamaged( entity, source, value, value != amount );
				}
				
			}
			
		}
		
		return value;
	}
	
	public void runOnRespawn(EntityPlayer player) {
		
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
	
	public void copyAllOver(EntityLivingBase entity1, EntityLivingBase entity2) {
		
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
	
	/**
	 * Gets the wrapper for the associated class, or null of it does not exist.
	 * 
	 * @param clazz
	 * @param entity
	 * @return
	 */
	public <T extends PropertiesBase> T getWrapper(Class<T> clazz, EntityLivingBase entity) {
		
		for (Entry<String, Class<? extends PropertiesBase>> entry : this.identifierClassAssociations.entrySet()) {
			
			if (entry.getValue().equals( clazz )) {
				return (T) entity.getExtendedProperties( entry.getKey() );
			}
			
		}
		
		return null;
	}
	
}
