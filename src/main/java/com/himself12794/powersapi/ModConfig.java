package com.himself12794.powersapi;

import org.lwjgl.input.Keyboard;

import com.himself12794.powersapi.util.Reference;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModConfig {

	
	// Instantiate the key bindings
	public static final KeyBinding KEY_BINDING_PRIMARY_POWER = new KeyBinding("key.primary.desc", Keyboard.KEY_Q, "key.powersapi.powers");
	public static final KeyBinding KEY_BINDING_SECONDARY_POWER = new KeyBinding("key.secondary.desc", Keyboard.KEY_V, "key.powersapi.powers");
	public static final KeyBinding KEY_BINDING_SWITCH_STATE = new KeyBinding("key.switchState.desc", Keyboard.KEY_F, "key.powersapi.powers");
	public static boolean enableCommands;
	
	public static void loadConfig(FMLPreInitializationEvent event ) {
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		
		/*Config for Power Options*/
		ConfigCategory powers = config.getCategory(Reference.NAME);
		powers.setComment("Configuration for " + Reference.NAME);
		
		// Whether or not to make commands available
		Property commands = new Property("EnableCommands", "true", Property.Type.BOOLEAN);
		commands.comment = "Whether or not commands are enabled";
		powers.put("EnableCommands", commands);
		
		config.load();
		
		enableCommands = powers.get( "EnableCommands" ).getBoolean();
		
		config.save();
	}
	
	// Register key bindings
	public static void registerKeyBindings() {
		
		if (!PowersAPI.initializationComplete()) {
			ClientRegistry.registerKeyBinding(KEY_BINDING_PRIMARY_POWER);
			ClientRegistry.registerKeyBinding(KEY_BINDING_SECONDARY_POWER);
			ClientRegistry.registerKeyBinding(KEY_BINDING_SWITCH_STATE);
		}
		
	}
	
}
