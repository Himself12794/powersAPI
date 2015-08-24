package com.himself12794.powersapi.config;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;


public final class KeyBindings {
	
	// Instantiate the key bindings
	public static final KeyBinding PRIMARY_POWER = new KeyBinding("key.primary.desc", Keyboard.KEY_Q, "key.powersapi.powers");
	public static final KeyBinding SECONDARY_POWER = new KeyBinding("key.secondary.desc", Keyboard.KEY_V, "key.powersapi.powers");
	
	// Register key bindings
	public static void registerKeyBindings() {
		
		ClientRegistry.registerKeyBinding(PRIMARY_POWER);
		ClientRegistry.registerKeyBinding(SECONDARY_POWER);
		
	}

}