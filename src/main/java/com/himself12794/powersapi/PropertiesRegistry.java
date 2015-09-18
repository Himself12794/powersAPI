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

	private final Map<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> entityMapping = Maps.newHashMap();
	private final Map<Class<? extends PropertiesBase>, String> identifierClassAssociations = Maps.newHashMap();

	public void registerPropertyClass(Class<? extends PropertiesBase> clazz, Class<? extends EntityLivingBase> clazz2) {
		registerPropertyClass( clazz, clazz2, null );
	}
	
	public void registerPropertyClass(Class<? extends PropertiesBase> clazz, Class<? extends EntityLivingBase> clazz2, String identifier) {
		
		String id = identifier != null && !"".equals( identifier ) ? identifier : getModClassIdentifier( clazz );
		
		if (!identifierClassAssociations.containsKey( clazz )) {
			this.identifierClassAssociations.put( clazz, id );
			PowersAPI.logger().info( "Registered class mapping for property class {}", clazz );
		} else {
			PowersAPI.logger().error( "Could not register properties class {}, entry already exists", clazz);
		}
		
		if (!entityMapping.containsKey( clazz )) {
			entityMapping.put( clazz, clazz2 );
			PowersAPI.logger().info( "Registered entity mapping for property class {}", clazz );
		} else {
			PowersAPI.logger().error( "Could not register entity for class {}, entry already exists", clazz);
		}
		
	}
	
	public Collection<Class<? extends PropertiesBase>> getRegisteredClasses() {
		return identifierClassAssociations.keySet();
	}
	
	public Class<? extends EntityLivingBase> getAssociatedEntityType(Class<? extends PropertiesBase> clazz) {
		return entityMapping.get( clazz );	
	}
	
	public String getIdentifierForClass(Class<? extends PropertiesBase> clazz) {
		return identifierClassAssociations.get( clazz );
	}
	
	public Class<? extends PropertiesBase> getPropertyClassForIdentifier(String identifier) {
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : this.identifierClassAssociations.entrySet()) {
			if (entry.getValue().equals( identifier )) return entry.getKey();
		}
		
		return null;
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
		
		for (Entry<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> entry : entityMapping.entrySet()) {
			
			if (entry.getValue().isAssignableFrom( entity.getClass() )) {
				
				try {
					
					Constructor constructor = entry.getKey().getDeclaredConstructor( entry.getValue() );
					constructor.setAccessible( true );
					PropertiesBase wrapper = (PropertiesBase) constructor.newInstance( entity );
					
					String identifier = getIdentifierForClass( entry.getKey() );
					
					if (entity.getExtendedProperties( identifier ) == null) {
						entity.registerExtendedProperties( identifier, wrapper );
					}
					
				} catch (Exception e) {
					PowersAPI.logger().error( e );
				} 
			}
			
		}
		
	}
	
	public void runUpdates(EntityLivingBase entity) {
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getValue();
			Class association = entry.getKey();
			IExtendedEntityProperties wrapper = entity.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().equals( association )) {
					
					((PropertiesBase)wrapper).onUpdate();
				}
				
			}
			
		}
		
	}
	
	public void syncPlayerToClient(EntityPlayerMP entityPlayer) {
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {
			
			String identifier = entry.getValue();
			Class clazz = entry.getKey();
			IExtendedEntityProperties wrapper = entityPlayer.getExtendedProperties( identifier );
	
			if (wrapper.getClass().isAssignableFrom( clazz )) {
				PowersNetwork.client().syncProperties( ((PropertiesBase)wrapper), entityPlayer);
			}
			
		}
	}
	
	public void runOnJoinWorld(EntityLivingBase entity, World world) {
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getValue();
			Class association = entry.getKey();
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
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getValue();
			Class association = entry.getKey();
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
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getValue();
			Class association = entry.getKey();
			IExtendedEntityProperties wrapper = player.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().isAssignableFrom( association )) {
					
					((PropertiesBase)wrapper).resetForRespawn();
				}
				
			}
			
		}
	}
	
	public void copyAllOver(EntityLivingBase entity1, EntityLivingBase entity2) {
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getValue();
			Class association = entry.getKey();
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
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : this.identifierClassAssociations.entrySet()) {
			
			if (entry.getKey().equals( clazz )) {
				return (T) entity.getExtendedProperties( entry.getValue() );
			}
			
		}
		
		return null;
	}
	
}
