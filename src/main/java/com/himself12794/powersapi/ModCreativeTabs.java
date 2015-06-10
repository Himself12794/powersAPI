package com.himself12794.powersapi;

import com.himself12794.powersapi.config.Config;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ModCreativeTabs {
	
	public static CreativeTabs powersAPI;
	
	public static void addCreativeTabs() {
		
		if (Config.enablePowerActivator) {
			
			powersAPI = new CreativeTabs("powersAPI") {
			    
				@Override
			    @SideOnly(Side.CLIENT)
			    public Item getTabIconItem() {
			        return Items.enchanted_book;
			    }
			};
			
		}
		
	}
}
