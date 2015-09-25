package com.himself12794.powersapi;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersapi.util.Reference;

/**
 * Configuration options for this mod.
 * 
 * @author Himself12794
 *
 */
public class ModConfig {
	
	final Configuration mainConfig;
	final ConfigCategory powers;
	private boolean modCommandsEnabled = true;
	
	public ModConfig(File file ) {
		mainConfig = new Configuration(file, true);
		powers = mainConfig.getCategory("Powers API");
		powers.setLanguageKey( "powers.config" );
		powers.setComment("Configuration for powers");
		syncConfig();
	}
	
	@SubscribeEvent
	public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		
		System.out.println("Config changed event");
		
		ListenerList list = event.getListenerList();
		
		for (IEventListener listerner : list.getListeners(0)) {
			System.out.println(listerner.toString());
		}
		
		if (event.modID.equals( Reference.MODID )) {
			syncConfig();
		}
		
	}
	
	public void syncConfig() {
		
		modCommandsEnabled = mainConfig.getBoolean( "ModCommandsEnabled", powers.getName(), true, "Whether or not mod commands are enabled" );
		
		if (mainConfig.hasChanged()) {
			mainConfig.save();
		}
	}
	
	public static boolean areModCommandsEnabled() {
		return get().modCommandsEnabled;
	}
	
	public static ModConfig get() {
		return PowersAPI.config();
	}
	
}
