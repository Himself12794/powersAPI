package com.himself12794.powersapi.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.config.KeyBindings;
import com.himself12794.powersapi.network.SendStopUsePower;
import com.himself12794.powersapi.network.SendUsePower;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;

public final class KeyBindingsHandler {
	
	private int buttonDelay = 0;
	
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
	        	PowersAPI.network.sendToServer( new SendStopUsePower() );
	        }
	    }
	
	    if (binding.isKeyDown() && !wrapper.isUsingPower() && buttonDelay == 0) {
        	
        	Power power = binding == KeyBindings.PRIMARY_POWER ? wrapper.getPrimaryPower() : wrapper.getSecondaryPower();
        	if (power != null) {

        		buttonDelay = 4;
	        	wrapper.usePower( power );
	        	PowersAPI.network.sendToServer( new SendUsePower(power) );
        	}
	    }
		
	}
	
	@SubscribeEvent
	public void ticker(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().thePlayer != null) {
			
			if (buttonDelay > 0) buttonDelay--;
			//handleKeyBinding(KeyBindings.PRIMARY_POWER);
			//handleKeyBinding(KeyBindings.SECONDARY_POWER);
			//handleKeyBinds();
		}
	}
	
	/*private void handleKeyBinding(KeyBinding binding) {
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		
		DataWrapper wrapper = DataWrapper.get( player );
		
		if (wrapper.isUsingPower()){
			
	        if (!KeyBindings.PRIMARY_POWER.isPressed() && !KeyBindings.SECONDARY_POWER.isPressed()) {
	        	wrapper.stopUsingPower();
	        	PowersAPI.network.sendToServer( new SendStopUsePower() );
	        }
	    }

	    if (binding.isPressed() && !wrapper.isUsingPower() && buttonDelay == 0) {

        	Power power = binding == KeyBindings.PRIMARY_POWER ? wrapper.getPrimaryPower() : wrapper.getSecondaryPower();
        	if (power != null) {
	        	wrapper.usePower( power );
	        	PowersAPI.network.sendToServer( new SendUsePower(power) );
        	}
	    }
		
	}*/
	
	private void handleKeyBinds() {
		
		DataWrapper dw = DataWrapper.get( Minecraft.getMinecraft().thePlayer );
		if (dw.isUsingPower()) {
			if (!KeyBindings.SECONDARY_POWER.isKeyDown()) {
				
				dw.stopUsingPower();
				PowersAPI.network.sendToServer( new SendStopUsePower() );
                
            }
			
			/*if (!KeyBindings.PRIMARY_POWER.isKeyDown() ) {
				dw.stopUsingPower();
	        	PowersAPI.network.sendToServer( new SendStopUsePower() );
			}*/

			label435:

			while (true) {
				
				if (!KeyBindings.PRIMARY_POWER.isPressed()) {
					
		            while (KeyBindings.SECONDARY_POWER.isPressed()) {
		                ;
		            }
		
		            while (true)
		            {
		                /*if (Minecraft.getMinecraft().gameSettings.keyBindPickBlock.isPressed())
		                {
		                    continue;
		                } else if (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isPressed()) {
		                	continue;
		                } else if (Minecraft.getMinecraft().gameSettings.keyBindAttack.isPressed()) {
		                	continue;
		                }*/
		
		                break label435;
		            }
		        }
             }
        } else
        {
			System.out.println("power not in use");
            /*while (KeyBindings.PRIMARY_POWER.isPressed())
            {

            	handleUsePrimaryPower(dw);
            }*/

            while (KeyBindings.SECONDARY_POWER.isPressed())
            {

            	handleUseSecondaryPower(dw);
            }
        }
        /*if (KeyBindings.PRIMARY_POWER.isKeyDown() && buttonDelay == 0 && !dw.isUsingPower())
        {
        	handleUsePrimaryPower(dw);
        }*/
        
        if (KeyBindings.SECONDARY_POWER.isKeyDown() && buttonDelay == 0 && !dw.isUsingPower())
        {
        	System.out.println("continuous use");
        	handleUseSecondaryPower(dw);
        }
 
	}
	
	private void handleUsePrimaryPower(DataWrapper dw) {
		buttonDelay = 20;
    	if (dw.getPrimaryPower() != null) {
        	dw.usePower( dw.getPrimaryPower() );
        	PowersAPI.network.sendToServer( new SendUsePower(dw.getPrimaryPower()) );
        }
	}
	
	private void handleUseSecondaryPower(DataWrapper dw) {
		buttonDelay = 20;
    	if (dw.getSecondaryPower() != null) {
        	dw.usePower( dw.getSecondaryPower() );
        	PowersAPI.network.sendToServer( new SendUsePower(dw.getSecondaryPower()) );
        }
	}
 
}
