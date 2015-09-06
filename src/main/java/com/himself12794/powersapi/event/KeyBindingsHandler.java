package com.himself12794.powersapi.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.himself12794.powersapi.config.KeyBindings;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.network.client.C01PowerUse.Action;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerInstant;
import com.himself12794.powersapi.storage.PowersWrapper;
import com.himself12794.powersapi.util.UsefulMethods;

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
		
		PowersWrapper wrapper = PowersWrapper.get( player );
    	
    	Power power = binding == KeyBindings.PRIMARY_POWER ? wrapper.getPrimaryPower() : wrapper.getSecondaryPower();
		
		if (wrapper.isUsingPower()){
			
	        if (!KeyBindings.PRIMARY_POWER.isKeyDown() && !KeyBindings.SECONDARY_POWER.isKeyDown()) {
	        	wrapper.stopUsingPower();
	        	PowersNetwork.server().powerUse( null, null, Action.STOP );
	        }
	    } 
	
	    if (binding.isKeyDown() && ((EntityPlayer)wrapper.theEntity).getItemInUse() == null && !wrapper.isUsingPower() && buttonDelay == 0) {
        	if (power != null) {

        		buttonDelay = 4;
        		float range =  power instanceof PowerInstant ? ((PowerInstant)power).getRange() : 50.0F;
        		MovingObjectPosition lookVec = UsefulMethods.getMouseOverExtended( range );
	        	wrapper.usePower( power, lookVec );
	        	PowersNetwork.server().powerUse(power, lookVec, Action.START);
        	}
	    } else if (!wrapper.isUsingPower() && gameSettings.keyBindUseItem.isKeyDown() && binding.isKeyDown() && buttonDelay == 0) {
	    	
	    	if (power != null) {
	    		
	    		buttonDelay = 4;
	    		wrapper.getPowerProfile( power ).cycleState(true);
	    		PowersNetwork.server().cyclePowerState( power );
	    		
	    	}
	    	
	    }
		
	}
	
	@SubscribeEvent
	public void ticker(TickEvent.ClientTickEvent event) {
		
		if (Minecraft.getMinecraft().thePlayer != null) {
			
			if (buttonDelay > 0) buttonDelay--;
			
			PowersWrapper dw = PowersWrapper.get( Minecraft.getMinecraft().thePlayer );
			
			if (dw.getPowerInUse() instanceof PowerInstant) {
				dw.mouseOverPos = UsefulMethods.getMouseOverExtended( ((PowerInstant)dw.getPowerInUse()).getRange()  );
				PowersNetwork.server().setMouseOver( dw.mouseOverPos );
			}

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
	
	/*private void handleKeyBinds() {
		
		DataWrapper dw = DataWrapper.get( Minecraft.getMinecraft().thePlayer );
		if (dw.isUsingPower()) {
			if (!KeyBindings.SECONDARY_POWER.isKeyDown()) {
				
				dw.stopUsingPower();
				PowersAPI.network.sendToServer( new StopUsePowerMessage() );
                
            }
			
			/*if (!KeyBindings.PRIMARY_POWER.isKeyDown() ) {
				dw.stopUsingPower();
	        	PowersAPI.network.sendToServer( new SendStopUsePower() );
			}

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
		                }
		
		                break label435;
		            }
		        }
             }
        } else
        {

            /*while (KeyBindings.PRIMARY_POWER.isPressed())
            {

            	handleUsePrimaryPower(dw);
            }

            while (KeyBindings.SECONDARY_POWER.isPressed())
            {

            	handleUseSecondaryPower(dw);
            }
        }
        /*if (KeyBindings.PRIMARY_POWER.isKeyDown() && buttonDelay == 0 && !dw.isUsingPower())
        {
        	handleUsePrimaryPower(dw);
        }
        
        if (KeyBindings.SECONDARY_POWER.isKeyDown() && buttonDelay == 0 && !dw.isUsingPower())
        {

        	handleUseSecondaryPower(dw);
        }
 
	}*/
	
	/*private void handleUsePrimaryPower(DataWrapper dw) {
		buttonDelay = 20;
    	if (dw.getPrimaryPower() != null) {
        	dw.usePower( dw.getPrimaryPower(), lookVec );
        	PowersAPI.network.sendToServer( new UsePowerMessage(dw.getPrimaryPower()) );
        }
	}
	
	private void handleUseSecondaryPower(DataWrapper dw) {
		buttonDelay = 20;
    	if (dw.getSecondaryPower() != null) {
        	dw.usePower( dw.getSecondaryPower(), lookVec );
        	PowersAPI.network.sendToServer( new UsePowerMessage(dw.getSecondaryPower()) );
        }
	}*/
 
}
