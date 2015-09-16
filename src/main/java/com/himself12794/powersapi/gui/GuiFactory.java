package com.himself12794.powersapi.gui;

import java.util.Set;

import com.himself12794.powersapi.ModConfig;
import com.himself12794.powersapi.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;


public class GuiFactory implements IModGuiFactory {
	
	private Minecraft mc;

	@Override
	public void initialize(Minecraft minecraftInstance) { 
		mc = minecraftInstance;
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ModConfigGUI.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
	
	public static class ModConfigGUI extends GuiConfig {
		  public ModConfigGUI(GuiScreen parent) {
		    super(parent, new ConfigElement(ModConfig.powers).getChildElements(),
		        Reference.MODID, false, false, GuiConfig.getAbridgedConfigPath(ModConfig.config.toString()));
		  }
		}

}
