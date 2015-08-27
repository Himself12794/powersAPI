package com.himself12794.powersapi.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.config.KeyBindings;
import com.himself12794.powersapi.network.SendStopUsePower;
import com.himself12794.powersapi.network.SendUsePower;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;

public final class KeyBindingsHandler {
	
	@SubscribeEvent
	public void onKeyUsage(KeyInputEvent event) {
		handleKeyBinding(KeyBindings.PRIMARY_POWER);
		handleKeyBinding(KeyBindings.SECONDARY_POWER);
	}
	
	private void handleKeyBinding(KeyBinding binding) {
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		
		DataWrapper wrapper = DataWrapper.get( player );
		
		if (wrapper.isUsingPower()){
			
	        if (!KeyBindings.PRIMARY_POWER.isKeyDown() && !KeyBindings.SECONDARY_POWER.isKeyDown()) {
	        	wrapper.stopUsingPower();
	        	PowersAPI.proxy.network.sendToServer( new SendStopUsePower() );
	        }
	    }
	
	    if (binding.isKeyDown() && !wrapper.isUsingPower()) {
        	
        	Power power = binding == KeyBindings.PRIMARY_POWER ? wrapper.getPrimaryPower() : wrapper.getSecondaryPower();
        	
        	if (power != null) {
	        	wrapper.usePower( power );
	        	PowersAPI.proxy.network.sendToServer( new SendUsePower(power) );
        	}
	    }
		
	}

}
