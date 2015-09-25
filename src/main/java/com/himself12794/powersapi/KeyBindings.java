package com.himself12794.powersapi;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

public class KeyBindings {

	public static final KeyBinding keyBindingPrimaryPower = new KeyBinding("key.primary.desc", Keyboard.KEY_Q, "key.powersapi.powers");
	public static final KeyBinding keyBindingSecondaryPower = new KeyBinding("key.secondary.desc", Keyboard.KEY_R, "key.powersapi.powers");
	public static final KeyBinding keyBindingSwitchState = new KeyBinding("key.switchState.desc", Keyboard.KEY_F, "key.powersapi.powers");
	
	// Register key bindings
	public static void registerKeyBindings() {
		
		if (!PowersAPI.isInitializationComplete()) {
			ClientRegistry.registerKeyBinding(keyBindingPrimaryPower);
			ClientRegistry.registerKeyBinding(keyBindingSecondaryPower);
			ClientRegistry.registerKeyBinding(keyBindingSwitchState);
		}
		
	}
}
