package com.himself12794.powersapi;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

import com.himself12794.powersapi.config.Config;
import com.himself12794.powersapi.proxy.CommonProxy;
import com.himself12794.powersapi.util.Reference;

/**
 * This is a mod to faciliate adding powers to minecraft. It's optimized to be used like spells,
 * or super powers, but it is definitely open to creativity.
 * 
 * @author Himself12794
 *
 */
@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.NAME)
public class PowersAPI {    

	@Instance(value = Reference.MODID)
	public static PowersAPI instance;
	
	public static Logger logger;
	public static void print(Object msg) {
		logger.info(msg);
	}
	
	@SidedProxy(
			clientSide="com.himself12794.powersapi.proxy.ClientProxy", 
			serverSide="com.himself12794.powersapi.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static SimpleNetworkWrapper getNetWrapper() {
		return proxy.network;
	}
	
    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
    	
    	logger = event.getModLog();		// load config
		Config.loadConfig(event);
    	proxy.preinit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	proxy.init(event);
        
    }
}