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

/**
 * Configuration options for this mod.
 * 
 * @author Himself12794
 *
 */
public class ModConfig {

	public static final KeyBinding keyBindingPrimaryPower = new KeyBinding("key.primary.desc", Keyboard.KEY_Q, "key.powersapi.powers");
	public static final KeyBinding keyBindingSecondaryPower = new KeyBinding("key.secondary.desc", Keyboard.KEY_V, "key.powersapi.powers");
	public static final KeyBinding keyBindingSwitchState = new KeyBinding("key.switchState.desc", Keyboard.KEY_F, "key.powersapi.powers");
	final Configuration mainConfig;
	final ConfigCategory powers;
	private boolean modCommandsEnabled = true;
	
	ModConfig(FMLPreInitializationEvent event ) {
		mainConfig = new Configuration(event.getSuggestedConfigurationFile(), true);
		powers = mainConfig.getCategory("Powers API");
		powers.setLanguageKey( "powers.config" );
		powers.setComment("Configuration for powers");
		syncConfig();
	}
	
	public void syncConfig() {
		
		modCommandsEnabled = mainConfig.getBoolean( "ModCommandsEnabled", powers.getName(), true, "Whether or not mod commands are enabled" );
		
		if (mainConfig.hasChanged()) mainConfig.save();
	}
	
	// Register key bindings
	public void registerKeyBindings() {
		
		if (!PowersAPI.isInitializationComplete()) {
			ClientRegistry.registerKeyBinding(keyBindingPrimaryPower);
			ClientRegistry.registerKeyBinding(keyBindingSecondaryPower);
			ClientRegistry.registerKeyBinding(keyBindingSwitchState);
		}
		
	}
	
	@SubscribeEvent
	public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		
		if (event.modID.equals( Reference.MODID )) {
			syncConfig();
		}
		
	}
	
	public static ModConfig get() {
		return PowersAPI.config();
	}
	
	public static boolean areModCommandsEnabled() {
		return get().modCommandsEnabled;
	}
	
}
