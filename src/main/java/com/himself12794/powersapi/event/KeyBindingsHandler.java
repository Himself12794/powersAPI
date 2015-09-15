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
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.UsefulMethods;

public final class KeyBindingsHandler {
	
	private int buttonDelay = 0;
	// To prevent the handling of a key binding event if the button state had not changed since last event
	private boolean primaryHadBeenPressed = false;
	private boolean secondaryHadBeenPressed = false;
	
	@SubscribeEvent
	public void onKeyUsage(KeyInputEvent event) {
		
		if (KeyBindings.PRIMARY_POWER.isKeyDown() && !primaryHadBeenPressed) {
			handleKeyBinding(KeyBindings.PRIMARY_POWER);
			primaryHadBeenPressed = true;
		} else if (!KeyBindings.PRIMARY_POWER.isKeyDown() && primaryHadBeenPressed) {
			handleKeyBinding(KeyBindings.PRIMARY_POWER);
			primaryHadBeenPressed = false;
		}
		
		if (KeyBindings.SECONDARY_POWER.isKeyDown() && !secondaryHadBeenPressed) {
			handleKeyBinding(KeyBindings.SECONDARY_POWER);
			secondaryHadBeenPressed = true;
		} else if (!KeyBindings.SECONDARY_POWER.isKeyDown() && secondaryHadBeenPressed) {
			handleKeyBinding(KeyBindings.SECONDARY_POWER);
			secondaryHadBeenPressed = false;
		}
	}
	
	private void handleKeyBinding(KeyBinding binding) {
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		
		PowersEntity wrapper = PowersEntity.get( player );
    	
    	Power power = binding == KeyBindings.PRIMARY_POWER ? wrapper.getPrimaryPower() : wrapper.getSecondaryPower();
		
		if (wrapper.isUsingPower()){
			
	        if (!KeyBindings.PRIMARY_POWER.isKeyDown() && !KeyBindings.SECONDARY_POWER.isKeyDown()) {
	        	wrapper.stopUsingPower();
	        	PowersNetwork.server().powerUse( null, null, Action.STOP );
	        }
	    } 
		
	    if (binding.isKeyDown() && ((EntityPlayer)wrapper.theEntity).getItemInUse() == null && !KeyBindings.SWITCH_STATE.isKeyDown() && !wrapper.isUsingPower() && buttonDelay == 0) {
        	if (power != null) {

        		buttonDelay = 4;
        		float range =  power instanceof PowerInstant ? ((PowerInstant)power).getRange() : 50.0F;
        		MovingObjectPosition lookVec = UsefulMethods.getMouseOverExtended( range );
	        	wrapper.usePower( power, lookVec );
	        	PowersNetwork.server().powerUse(power, lookVec, Action.START);
        	}
	    } else if (!wrapper.isUsingPower() && KeyBindings.SWITCH_STATE.isKeyDown() && binding.isKeyDown() && buttonDelay == 0) {
	    	
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
			
			PowersEntity dw = PowersEntity.get( Minecraft.getMinecraft().thePlayer );
			
			if (dw.getPowerInUse() instanceof PowerInstant) {
				dw.mouseOverPos = UsefulMethods.getMouseOverExtended( ((PowerInstant)dw.getPowerInUse()).getRange() );
				PowersNetwork.server().setMouseOver( dw.mouseOverPos );
			}

		}
	}
 
}
