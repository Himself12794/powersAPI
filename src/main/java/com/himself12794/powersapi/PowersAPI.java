package com.himself12794.powersapi;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.proxy.CommonProxy;
import com.himself12794.powersapi.util.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * This is a mod to faciliate adding powers to minecraft. It's optimized to be used like spells,
 * or super powers, but it is definitely open to creativity.
 * 
 * @author Himself12794
 *
 */
@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.NAME, guiFactory = Reference.GUI_FACTORY, useMetadata = true )
public class PowersAPI {    

	@Mod.Instance(Reference.MODID)
	private static PowersAPI INSTANCE;
	
	@Mod.Metadata(Reference.MODID)
	private static ModMetadata META;
	
	@SidedProxy( clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY )
	private static CommonProxy PROXY;
	
	public final PropertiesHandler propertiesHandler = new PropertiesHandler();
	public final PowersRegistry powersRegistry;
	private final ModConfig modConfig;
	private boolean isInitialized;
	private Logger logger;
	
	private PowersAPI(PowersRegistry pwr) { 
		powersRegistry = pwr;
		modConfig = new ModConfig(new File("config/" + Reference.MODID + ".cfg"));
		
	}
	
	@Mod.EventHandler
    public void preinit(final FMLPreInitializationEvent event) {
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
    
    public static ModConfig config() {
    	return INSTANCE.modConfig;
    }
    
    @Mod.InstanceFactory
    public static PowersAPI initializeModInstance() {
    	return new PowersAPI(PowersRegistry.INSTANCE);
    }
    
    public static PowersAPI instance() {
    	return INSTANCE;
    }
    
    public static boolean isInitializationComplete() {
    	return INSTANCE.isInitialized;
    }
    
    public static Logger logger() {
    	return INSTANCE.logger;
    }
    
    public static ModMetadata metadata() {
    	return META;
    }
    
    public static PropertiesHandler propertiesHandler() {
    	return INSTANCE.propertiesHandler;
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
    
}