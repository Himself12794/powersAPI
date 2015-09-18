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

import com.himself12794.powersapi.ModConfig;
import com.himself12794.powersapi.network.PowersNetwork;
import com.himself12794.powersapi.network.client.C01PowerUse.Action;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.power.PowerInstant;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.UsefulMethods;

public final class KeyBindingsHandler {
	
	private int buttonDelayPrimary;
	private int buttonDelaySecondary;
	// To prevent the handling of a key binding event if the button state had not changed since last event
	private boolean primaryHadBeenPressed = false;
	private boolean secondaryHadBeenPressed = false;
	
	@SubscribeEvent
	public void onKeyUsagePrimary(KeyInputEvent event) {
		
		if (ModConfig.keyBindingPrimaryPower.isKeyDown() != primaryHadBeenPressed) {
			handlePrimaryPowerKeyBinding();
			primaryHadBeenPressed = !primaryHadBeenPressed;
		}
	}
	
	@SubscribeEvent
	public void onKeyUsageSecondary(KeyInputEvent event) {
		
		if (ModConfig.keyBindingSecondaryPower.isKeyDown() != secondaryHadBeenPressed) {
			this.handleSecondaryPowerKeyBinding();
			this.secondaryHadBeenPressed = !this.secondaryHadBeenPressed;
		}
	}
	
	private void handlePrimaryPowerKeyBinding() {
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		
		PowersEntity wrapper = PowersEntity.get( player );
    	
    	Power primaryPower = wrapper.getPrimaryPower();
		
		if (wrapper.isUsingPrimaryPower()){
			
	        if (!ModConfig.keyBindingPrimaryPower.isKeyDown()) {
	        	wrapper.stopUsingPrimaryPower();
	        	PowersNetwork.server().powerUse( true, null, Action.STOP );
	        }
	    } 
		
	    if (ModConfig.keyBindingPrimaryPower.isKeyDown() && ((EntityPlayer)wrapper.theEntity).getItemInUse() == null && !ModConfig.keyBindingSwitchState.isKeyDown() && !wrapper.isUsingPrimaryPower() && buttonDelayPrimary == 0) {
        	if (primaryPower != null) {

        		buttonDelayPrimary = 4;
        		float range =  primaryPower instanceof PowerInstant ? ((PowerInstant)primaryPower).getRange() : 50.0F;
        		MovingObjectPosition lookVec = UsefulMethods.getMouseOverExtended( range );
	        	wrapper.usePrimaryPower( lookVec );
	        	PowersNetwork.server().powerUse( true, lookVec, Action.START );
        	}
	    } else if (!wrapper.isUsingPrimaryPower() && ModConfig.keyBindingSwitchState.isKeyDown() && ModConfig.keyBindingPrimaryPower.isKeyDown() && buttonDelayPrimary == 0) {
	    	
	    	if (primaryPower != null) {
	    		
	    		buttonDelayPrimary = 4;
	    		wrapper.getPowerProfile( primaryPower ).cycleState(true);
	    		PowersNetwork.server().cyclePowerState( primaryPower );
	    		
	    	}
	    	
	    }	
		
	}
	
	private void handleSecondaryPowerKeyBinding() {	
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		
		PowersEntity wrapper = PowersEntity.get( player );
    	
    	Power power = wrapper.getSecondaryPower();
	    
	    if (wrapper.isUsingSecondaryPower()){
	    	
	        if (!ModConfig.keyBindingSecondaryPower.isKeyDown()) {
	        	wrapper.stopUsingSecondaryPower();
	        	PowersNetwork.server().powerUse( false, null, Action.STOP );
	        }
	    } 
	    
	    if (ModConfig.keyBindingSecondaryPower.isKeyDown() && ((EntityPlayer)wrapper.theEntity).getItemInUse() == null && !ModConfig.keyBindingSwitchState.isKeyDown() && !wrapper.isUsingSecondaryPower() &&  buttonDelaySecondary == 0) {
        	if (power != null) {

        		buttonDelaySecondary = 4;
        		float range =  power instanceof PowerInstant ? ((PowerInstant)power).getRange() : 50.0F;
        		MovingObjectPosition lookVec = UsefulMethods.getMouseOverExtended( range );
	        	wrapper.useSecondaryPower( lookVec );
	        	PowersNetwork.server().powerUse( false, lookVec, Action.START );
        	}
	    } else if (!wrapper.isUsingSecondaryPower() && ModConfig.keyBindingSwitchState.isKeyDown() && ModConfig.keyBindingSecondaryPower.isKeyDown() && buttonDelaySecondary == 0) {
	    	
	    	if (power != null) {
	    		
	    		buttonDelaySecondary = 4;
	    		wrapper.getPowerProfile( power ).cycleState(true);
	    		PowersNetwork.server().cyclePowerState( power );
	    		
	    	}
	    	
	    }
		
	}
	
	@SubscribeEvent
	public void ticker(TickEvent.ClientTickEvent event) {
		
		if (Minecraft.getMinecraft().thePlayer != null) {
			
			if (buttonDelayPrimary > 0) buttonDelayPrimary--;
			if (buttonDelaySecondary > 0) buttonDelaySecondary--;
			
			PowersEntity dw = PowersEntity.get( Minecraft.getMinecraft().thePlayer );
			
			if (dw.getPrimaryPower() instanceof PowerInstant && dw.isUsingPrimaryPower()) {
				dw.mouseOverPosPrimary = UsefulMethods.getMouseOverExtended( ((PowerInstant)dw.getPrimaryPower()).getRange() );
				PowersNetwork.server().setMouseOver( true, dw.mouseOverPosPrimary );
			}
			
			if (dw.getSecondaryPower() instanceof PowerInstant && dw.isUsingSecondaryPower()) {
				dw.mouseOverPosSecondary = UsefulMethods.getMouseOverExtended( ((PowerInstant)dw.getSecondaryPower()).getRange() );
				PowersNetwork.server().setMouseOver( false, dw.mouseOverPosSecondary );
			}

		}
	}
 
}
