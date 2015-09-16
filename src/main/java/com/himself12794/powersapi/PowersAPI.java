package com.himself12794.powersapi;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.Logger;

import com.himself12794.powersapi.power.Power;
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

	@Mod.Instance(Reference.MODID)
	private static PowersAPI INSTANCE;
	
	@Mod.Metadata(Reference.MODID)
	private static ModMetadata META;
	
	@SidedProxy(
			clientSide="com.himself12794.powersapi.proxy.ClientProxy", 
			serverSide="com.himself12794.powersapi.proxy.CommonProxy")
	private static CommonProxy PROXY;
	
	private Logger logger;
	private final PropertiesRegistry propertyManager;
	private final PowersRegistry powersRegistry;
	
	private PowersAPI(PropertiesRegistry pr, PowersRegistry pwr) { 
		propertyManager = pr;
		powersRegistry = pwr;
	}
	
    @Mod.EventHandler
    public void preinit(final FMLPreInitializationEvent event) {
    	logger = event.getModLog();
    	PROXY.preinit(event);
    }
	
	@Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
    	PROXY.init(event);
        
    }
    
    @Mod.EventHandler
	public void serverStart(final FMLServerStartingEvent event) {
		PROXY.serverStartEvent( event );
	}
    
    @Mod.InstanceFactory
    public static PowersAPI initializeMod() {
    	return new PowersAPI(new PropertiesRegistry(), PowersRegistry.INSTANCE);
    }
    
    public static Logger logger() {
    	return INSTANCE.logger;
    }
    
    public static PowersAPI instance() {
    	return INSTANCE;
    }
    
    public static PropertiesRegistry propertiesManager() {
    	return INSTANCE.propertyManager;
    }
    
    public static ModMetadata metadata() {
    	return META;
    }
    
    public static CommonProxy proxy() {
    	return PROXY;
    }
    
    public static void registerPower(Power power) {
    	INSTANCE.powersRegistry.registerPower( power );
    }
    
}