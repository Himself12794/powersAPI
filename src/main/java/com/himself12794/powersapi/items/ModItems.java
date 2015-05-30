package com.himself12794.powersapi.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersapi.util.Reference;

public final class ModItems {
	
	public static final int NUMBER = 1;
	public static Item magicTome;
	
	public static void addItems() {
		
		magicTome = new MagicTome();
		
	}
	
	public static void registerTextures( FMLInitializationEvent event ) {
		if(event.getSide() == Side.CLIENT) {
			
		    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		    
		    renderItem.getItemModelMesher().register(magicTome, 0, new ModelResourceLocation(Reference.MODID + ":" + ((MagicTome) magicTome).getName(), "inventory"));
		    
		}
	}
}
