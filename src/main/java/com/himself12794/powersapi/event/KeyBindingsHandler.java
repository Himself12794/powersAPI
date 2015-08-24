package com.himself12794.powersapi.event;

import org.lwjgl.input.Keyboard;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.config.KeyBindings;
import com.himself12794.powersapi.item.ModItems;
import com.himself12794.powersapi.network.SendPlayerStoppedUsingPower;
import com.himself12794.powersapi.network.SendUsePower;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public final class KeyBindingsHandler {
	
	private final ItemStack primaryPower = new ItemStack(ModItems.powerActivator);
	private final ItemStack secondaryPower = new ItemStack(ModItems.powerActivator);
	
	@SubscribeEvent
	public void onKeyUsage(KeyInputEvent event) {

		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = Minecraft.getMinecraft().theWorld;
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		
		DataWrapper wrapper = DataWrapper.get( player );
		
		if (wrapper.isUsingPower()){
			
	        if (!KeyBindings.PRIMARY_POWER.isKeyDown()) {
	        	wrapper.stopUsingPowerEarly();
	        	PowersAPI.getNetWrapper().sendToServer( new SendPlayerStoppedUsingPower() );
	        }
	
	        label435:
	
	        while (true)
	        {
	            if (!gameSettings.keyBindAttack.isPressed())
	            {
	                while (KeyBindings.PRIMARY_POWER.isPressed())
	                {
	                    ;
	                }
	
	                while (true)
	                {
	                    if (gameSettings.keyBindPickBlock.isPressed()) {
	                    	
	                        continue;
	                    }
	
	                    break label435;
	                }
	            }
	        }
	    }
	    else {
	
	        while (KeyBindings.PRIMARY_POWER.isPressed()) {
	        	
	        	if (wrapper.getPrimaryPower() != null) {
		        	wrapper.usePrimaryPower();
		        	PowersAPI.getNetWrapper().sendToServer( new SendUsePower(wrapper.getPrimaryPower()) );
	        	}
	        }
	    }
	
	    if (KeyBindings.PRIMARY_POWER.isKeyDown() /*&& this.rightClickDelayTimer == 0*/ && !wrapper.isUsingPower()) {
        	
        	if (wrapper.getPrimaryPower() != null) {
	        	wrapper.usePrimaryPower();
	        	PowersAPI.getNetWrapper().sendToServer( new SendUsePower(wrapper.getPrimaryPower()) );
        	}
	    }
	}

}