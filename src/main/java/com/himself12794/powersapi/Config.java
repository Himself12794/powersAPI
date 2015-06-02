package com.himself12794.powersapi;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	
	public static ConfigCategory powersConfig;
	public static int flamethrowing;
	public static int instantPowerRange;
	
	public static void loadConfig(FMLPreInitializationEvent event ) {
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		

		
		/*Config for Spell Options*/
		ConfigCategory powers = config.getCategory("Powers");
		powers.setComment("Configuration for powers");
		
		//Property flames = config.get(spells, "FlamethrowingGriefing");
		// Flamethrowing Griefing Config Option
		Property flames = new Property("FlamethrowingGriefing", "1", Property.Type.INTEGER);
		flames.setMinValue(0);
		flames.setMaxValue(3);
		flames.comment = "Flamethrowing griefing: 0=none, 1=vines & grass, 2=all burnable blocks";
		powers.put("FlamethrowingGriefing", flames);
				
		// Instant Spell Range Option
		Property instantPower = new Property("InstantSpellRange", "50", Property.Type.INTEGER);
		instantPower.setMinValue(1);
		instantPower.setMaxValue(100);
		instantPower.comment = "The range for instant powers. Max is 100 blocks, min is 1";
		powers.put("InstantSpellRange", instantPower);
		
		config.load();
		
		flamethrowing = powers.get("FlamethrowingGriefing").getInt();
		
		instantPowerRange = instantPower.getInt();
		
		config.save();
	}
	
}
