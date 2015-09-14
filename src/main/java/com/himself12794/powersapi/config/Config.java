package com.himself12794.powersapi.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	
	public static ConfigCategory powersConfig;
	public static int flamethrowing;
	//public static int instantPowerRange;
	public static boolean enablePowerActivator;
	public static boolean enableCommands;
	
	public static void loadConfig(FMLPreInitializationEvent event ) {
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		
		/*Config for Spell Options*/
		ConfigCategory powers = config.getCategory("Powers");
		powers.setComment("Configuration for powers");
		
		// Flamethrowing Griefing Config Option
		Property flames = new Property("FlamethrowingGriefing", "1", Property.Type.INTEGER);
		flames.setMinValue(0);
		flames.setMaxValue(3);
		flames.comment = "Flamethrowing griefing: 0=none, 1=vines & grass, 2=all burnable blocks";
		powers.put("FlamethrowingGriefing", flames);
		
		// Whether or not to make the power activator available
		Property powerActivator = new Property("EnablePowerActivator", "false", Property.Type.BOOLEAN);
		powerActivator.comment = "Whether or not the power activator tool should be available in game";
		powers.put("EnablePowerActivator", powerActivator);
		
		// Whether or not to make the power activator available
		Property commands = new Property("EnableCommands", "true", Property.Type.BOOLEAN);
		commands.comment = "Whether or not commands are enabled";
		powers.put("EnableCommands", commands);
		
		config.load();
		
		flamethrowing = powers.get("FlamethrowingGriefing").getInt();
		enableCommands = powers.get( "enableCommands" ).getBoolean();
		enablePowerActivator = powers.get("EnablePowerActivator").getBoolean();
		
		config.save();
		
		
	}
	
}
