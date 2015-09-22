package com.himself12794.powersapi;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.Logger;

import com.himself12794.powersapi.event.VisualEvents;
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
@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.NAME, useMetadata = true, guiFactory = Reference.GUI_FACTORY )
public class PowersAPI {    

	@Mod.Instance(Reference.MODID)
	private static PowersAPI INSTANCE;
	
	@Mod.Metadata(Reference.MODID)
	private static ModMetadata META;
	
	@SidedProxy( clientSide=Reference.CLIENT_PROXY,	serverSide=Reference.COMMON_PROXY )
	private static CommonProxy PROXY;
	
	public final PropertiesHandler propertiesHandler = new PropertiesHandler();
	public final PowersRegistry powersRegistry;
	private ModConfig modConfig;
	private boolean isInitialized;
	private Logger logger;
	
	private PowersAPI(PowersRegistry pwr) { 
		powersRegistry = pwr;
		
	}
	
    @Mod.EventHandler
    public void preinit(final FMLPreInitializationEvent event) {

		modConfig = new ModConfig(event);
    	logger = event.getModLog();
    	PROXY.preinit(event);
    }
	
	@Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
    	PROXY.init(event);
    	INSTANCE.isInitialized = true;
    }
    
    @Mod.EventHandler
	public void serverStart(final FMLServerStartingEvent event) {
		PROXY.serverStartEvent( event );
	}
    
    @Mod.InstanceFactory
    public static PowersAPI initializeModInstance() {
    	return new PowersAPI(PowersRegistry.INSTANCE);
    }
    
    public static Logger logger() {
    	return INSTANCE.logger;
    }
    
    public static PowersAPI instance() {
    	return INSTANCE;
    }
    
    public static PropertiesHandler propertiesHandler() {
    	return INSTANCE.propertiesHandler;
    }
    
    public static ModMetadata metadata() {
    	return META;
    }
    
    public static CommonProxy proxy() {
    	return PROXY;
    }
    
    /**
     * Called to register a power with this mod.
     * 
     * @param power
     */
    public static void registerPower(Power power) {
    	INSTANCE.powersRegistry.registerPower( power );
    }
    
    public static boolean isInitializationComplete() {
    	return INSTANCE.isInitialized;
    }
    
    public static ModConfig config() {
    	return INSTANCE.modConfig;
    }
    
}