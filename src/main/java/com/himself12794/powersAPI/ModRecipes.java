package com.himself12794.powersAPI;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.himself12794.powersAPI.items.ModItems;

public class ModRecipes {
	public final static int NUMBER = 1;
	public static void addRecipes() {
		
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.magicTome), Items.book);
		
	}
}
