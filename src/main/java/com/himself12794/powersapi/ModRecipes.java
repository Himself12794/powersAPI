package com.himself12794.powersapi;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.himself12794.powersapi.config.Config;
import com.himself12794.powersapi.item.ModItems;

public class ModRecipes {
	public final static int NUMBER = 1;
	public static void addRecipes() {
		
		if (Config.enablePowerActivator) GameRegistry.addShapelessRecipe(new ItemStack(ModItems.powerActivator), Items.book);
		
	}
}