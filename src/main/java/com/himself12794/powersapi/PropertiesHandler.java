package com.himself12794.powersapi;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.storage.PropertiesBase;
import com.himself12794.powersapi.util.Reference;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

/**
 * Manages modded IExtendedEntityProperties. Automatically registers them whenever an
 * entity is constructed, if the Wrapper is assignable for the entity class as registered.
 * Automatically calls associated methods with associated events.
 * 
 * @author Himself12794
 *
 */
public class PropertiesHandler {
	
	private final Map<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> entityMapping = Maps.newHashMap();

	private final Map<Class<? extends PropertiesBase>, String> identifierClassAssociations = Maps.newHashMap();
	
	PropertiesHandler() {}
	
	@SubscribeEvent
	public void copyAllOver(PlayerEvent.Clone event) {
		
		EntityLivingBase entity1 = event.original; 
		EntityLivingBase entity2 = event.entityPlayer;
		
		if (!event.wasDeath) {
			return;
		}
		
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
	
	public Class<? extends EntityLivingBase> getAssociatedEntityType(Class<? extends PropertiesBase> clazz) {
		return entityMapping.get( clazz );	
	}

	public String getIdentifierForPropertyClass(Class<? extends PropertiesBase> clazz) {
		return identifierClassAssociations.get( clazz );
	}
	
	private String getModClassIdentifier(Class<? extends PropertiesBase> clazz) {
		return Reference.MODID + ":" + clazz.getName();
	}
	
	public Class<? extends PropertiesBase> getPropertyClassForIdentifier(String identifier) {
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {
			if (entry.getValue().equals( identifier )) {
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	public Collection<Class<? extends PropertiesBase>> getRegisteredClasses() {
		return identifierClassAssociations.keySet();
	}
	
	public <T extends PropertiesBase> T getWrapper(Class<T> clazz, EntityLivingBase entity) {
		return (T) entity.getExtendedProperties( getIdentifierForPropertyClass( clazz ) );
	}
	
	public PropertiesBase getWrapperForIdentifier(String identifier, EntityLivingBase entity) {
		return getWrapper( getPropertyClassForIdentifier(identifier), entity );
	}
	
	@SubscribeEvent
	public void registerPropertiesForEntity(EntityEvent.EntityConstructing event) {
		
		if (event.entity instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) event.entity;
			for (Entry<Class<? extends PropertiesBase>, Class<? extends EntityLivingBase>> entry : entityMapping.entrySet()) {
				
				if (entry.getValue().isAssignableFrom( entity.getClass() )) {
					
					try {
						
						Constructor constructor = entry.getKey().getDeclaredConstructor( entry.getValue() );
						constructor.setAccessible( true );
						PropertiesBase wrapper = (PropertiesBase) constructor.newInstance( entity );
						
						String identifier = getIdentifierForPropertyClass( entry.getKey() );
						
						if (entity.getExtendedProperties( identifier ) == null) {
							entity.registerExtendedProperties( identifier, wrapper );
						}
						
					} catch (Exception e) {
						PowersAPI.logger().error( e );
					} 
				}
				
			}
			
		}
		
	}
	
	public void registerPropertyClass(Class<? extends PropertiesBase> clazz) {
		registerPropertyClass( clazz, null, null );
	}
	
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
		
		Class registered = clazz2 == null ? EntityLivingBase.class : clazz2;
		
		if (!entityMapping.containsKey( clazz )) {
			entityMapping.put( clazz, registered );
			PowersAPI.logger().info( "Registered entity class {}, for property class {}", registered, clazz );
		} else {
			PowersAPI.logger().error( "Could not register entity for class {}, entry already exists", clazz);
		}
		
	}
	
	public void registerPropertyClass(Class<? extends PropertiesBase> clazz, String identifier) {
		registerPropertyClass( clazz, null, identifier );
	}
	
	@SubscribeEvent
	public void runOnDamaged(LivingHurtEvent event) {
		
		EntityLivingBase entity = event.entityLiving; 
		DamageSource source = event.source;
		float value = event.ammount;
		
		for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {

			String identifier = entry.getValue();
			Class association = entry.getKey();
			IExtendedEntityProperties wrapper = entity.getExtendedProperties( identifier );
			
			if(wrapper != null) {
				
				if (wrapper.getClass().isAssignableFrom( association )) {
					
					value = ((PropertiesBase)wrapper).onDamaged( entity, source, value, value != event.ammount );
				}
				
			}
			
		}
		
		event.ammount = value;
	}
	
	@SubscribeEvent
	public void runOnJoinWorld(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityLivingBase) {
			
			for (Entry<Class<? extends PropertiesBase>, String> entry : identifierClassAssociations.entrySet()) {
	
				String identifier = entry.getValue();
				Class association = entry.getKey();
				IExtendedEntityProperties wrapper = event.entity.getExtendedProperties( identifier );
				
				if(wrapper != null) {
					
					if (wrapper.getClass().isAssignableFrom( association )) {
						
						((PropertiesBase)wrapper).onJoinWorld( event.world );
					}
					
				}
				
			}
			
			if (event.entity instanceof EntityPlayerMP) {
				syncPlayerToClient( (EntityPlayerMP) event.entity );
			}
		}
	}
	
	@SubscribeEvent
	public void runOnRespawn(PlayerRespawnEvent event) {
		
		EntityPlayer player = event.player;
		
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
		
		syncPlayerToClient( (EntityPlayerMP) event.player );
	}
	
	@SubscribeEvent
	public void runUpdates(LivingUpdateEvent event) {
		
		EntityLivingBase entity = event.entityLiving;
		
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
			IExtendedEntityProperties wrapper = entityPlayer.getExtendedProperties( entry.getValue() );
	
			if (wrapper.getClass().isAssignableFrom( entry.getKey() )) {
				PowersNetwork.client().syncProperties( ((PropertiesBase)wrapper), entityPlayer);
			}
			
		}
	}
	
}
