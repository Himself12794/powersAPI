package com.himself12794.powersAPI.proxy;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.himself12794.powersAPI.Config;
import com.himself12794.powersAPI.ModRecipes;
import com.himself12794.powersAPI.PowersAPI;
import com.himself12794.powersAPI.entity.EntitySpell;
import com.himself12794.powersAPI.events.SpellCoolDownHook;
import com.himself12794.powersAPI.events.SpellEffectHandler;
import com.himself12794.powersAPI.items.ModItems;
import com.himself12794.powersAPI.network.CastSpellInstantServer;
import com.himself12794.powersAPI.network.SetHomingSpellTargetServer;
import com.himself12794.powersAPI.spell.Spell;
import com.himself12794.powersAPI.util.Reference;

public class CommonProxy {
	
	public static SimpleNetworkWrapper network;
	
	public void preinit(FMLPreInitializationEvent event) {

		//side = Side.SERVER;
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("[" + Reference.MODID + "] NetChannel");
		network.registerMessage(SetHomingSpellTargetServer.Handler.class, SetHomingSpellTargetServer.class, 0, Side.SERVER);
		network.registerMessage(CastSpellInstantServer.Handler.class, CastSpellInstantServer.class, 1, Side.SERVER);
		
		// load config
		Config.loadConfig(event);
		
		// register items
		ModItems.addItems();
		if (ModItems.NUMBER > 0) PowersAPI.logger.info("Added [" + ModItems.NUMBER + "] new items");
       
		// register blocks
		//ModBlocks.addBlocks();
		//if (ModBlocks.NUMBER > 0) UsefulThings.logger.info("Added [" + ModBlocks.NUMBER + "] new blocks");
       
		// register spells
		Spell.registerSpells();
		
		// register entities
		EntityRegistry.registerModEntity(EntitySpell.class, "spell", 1, PowersAPI.instance, 80, 3, true);
		PowersAPI.logger.info("Registered [1] new entity");
	}

	public void init(FMLInitializationEvent event){
		SpellCoolDownHook scdh = new SpellCoolDownHook();
		SpellEffectHandler spfx = new SpellEffectHandler();
		
    	MinecraftForge.EVENT_BUS.register(scdh);
    	MinecraftForge.EVENT_BUS.register(spfx);
		 
		ModRecipes.addRecipes();

	}
	
	public Side getSide() {
		return Side.SERVER;
	}

	public double getReverseRendering(ItemStack stack) {
		return 2.0D;
		
	}

	public boolean showDamage(ItemStack stack) {
		return false;
	}
}
