package com.himself12794.powersapi;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.input.Keyboard;

import com.himself12794.powersapi.util.Reference;

public class ModConfig {

	public static final KeyBinding keyBindingPrimaryPower = new KeyBinding("key.primary.desc", Keyboard.KEY_Q, "key.powersapi.powers");
	public static final KeyBinding keyBindingSecondaryPower = new KeyBinding("key.secondary.desc", Keyboard.KEY_V, "key.powersapi.powers");
	public static final KeyBinding keyBindingSwitchState = new KeyBinding("key.switchState.desc", Keyboard.KEY_F, "key.powersapi.powers");
	public static boolean modCommandsEnabled = true;
	public static Configuration config;
	public static ConfigCategory powers;
	
	public static void loadConfig(FMLPreInitializationEvent event ) {
		config = new Configuration(event.getSuggestedConfigurationFile(), true);
		powers = config.getCategory("Powers API");
		powers.setLanguageKey( "powers.config" );
		powers.setComment("Configuration for powers");
		syncConfig();
	}
	
	public static void syncConfig() {
		
		modCommandsEnabled = config.getBoolean( "ModCommandsEnabled", powers.getName(), true, "Whether or not mod commands are enabled" );
		
		if (config.hasChanged()) config.save();
	}
	
	// Register key bindings
	public static void registerKeyBindings() {
		
		if (!PowersAPI.isInitializationComplete()) {
			ClientRegistry.registerKeyBinding(keyBindingPrimaryPower);
			ClientRegistry.registerKeyBinding(keyBindingSecondaryPower);
			ClientRegistry.registerKeyBinding(keyBindingSwitchState);
		}
		
	}
	
	@SubscribeEvent
	public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		
		if (event.modID.equals( Reference.MODID )) {
			System.out.println("Config changed, syncing");
			syncConfig();
		}
		
	}
	
}
