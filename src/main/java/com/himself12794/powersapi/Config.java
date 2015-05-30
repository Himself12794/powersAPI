package com.himself12794.powersapi;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	
	public static ConfigCategory spellsConfig;
	public static int flamethrowing;
	public static int instantSpellRange;
	
	public static void loadConfig(FMLPreInitializationEvent event ) {
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		

		
		/*Config for Spell Options*/
		ConfigCategory spells = config.getCategory("Spells");
		spells.setComment("Configuration for spells");
		
		//Property flames = config.get(spells, "FlamethrowingGriefing");
		// Flamethrowing Griefing Config Option
		Property flames = new Property("FlamethrowingGriefing", "1", Property.Type.INTEGER);
		flames.setMinValue(0);
		flames.setMaxValue(3);
		flames.comment = "Flamethrowing griefing: 0=none, 1=vines & grass, 2=all burnable blocks";
		spells.put("FlamethrowingGriefing", flames);
				
		// Instant Spell Range Option
		Property instantSpell = new Property("InstantSpellRange", "50", Property.Type.INTEGER);
		instantSpell.setMinValue(1);
		instantSpell.setMaxValue(100);
		instantSpell.comment = "The range for instant spells. Max is 100 blocks, min is 1";
		spells.put("InstantSpellRange", instantSpell);
		
		config.load();
		
		flamethrowing = spells.get("FlamethrowingGriefing").getInt();
		
		instantSpellRange = instantSpell.getInt();
		
		config.save();
	}
	
}
