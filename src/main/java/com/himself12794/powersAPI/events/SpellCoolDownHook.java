package com.himself12794.powersAPI.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.himself12794.powersAPI.Spells;
import com.himself12794.powersAPI.spell.Spell;

public class SpellCoolDownHook {
	
	@SubscribeEvent
	public void handleSpellCoolDown(LivingUpdateEvent event) {
		
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			
			ItemStack[] itemStacks = player.inventory.mainInventory;
			
			for (ItemStack stack : itemStacks) {
				if (stack != null && Spell.hasSpell(stack) ) {
					Spell spell = Spell.getSpell(stack);
					if (spell != null) {
						//UsefulThings.print("Updating cooldown timer on " + player.getName() + " for spell " + spell.getUnlocalizedName());
						int remaining = 0;
						if (spell.getCoolDown() > 0) {
							
							
							
							remaining  = spell.getCoolDownRemaining(player);
							if (remaining > 0) { 
								--remaining;
								//event.setCanceled(true);
							}
							
							spell.setCoolDown(player, remaining);
						}
					}
				}
					
			}
			
		}
	}

}
