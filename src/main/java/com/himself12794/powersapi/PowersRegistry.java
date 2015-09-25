package com.himself12794.powersapi;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.power.IEffectActivator;
import com.himself12794.powersapi.power.Power;

/**
 * The registration for powers. All powers registered by this should be accessible.
 * 
 * @author Himself12794
 *
 */
public class PowersRegistry {
	
	static final PowersRegistry INSTANCE = new PowersRegistry();
		
	private final Map<Integer, String> powerIds = Maps.newHashMap();
	private final Map<String, Power> powerRegistry = Maps.newHashMap();
	private int powers = 1;
	
	private PowersRegistry() {}
	
	/**
	 * Used to register created powers. Powers are stored with their unlocalized name, as well
	 * as a generated id. 
	 * <p>
	 * <b>NOTE: </b>Powers should be registered AFTER PowerEffects, because of the {@link IEffectActivator}
	 * 
	 * @param power
	 * @return 
	 */
	public Power registerPower(Power power) {
		
		String name = power.getRegisteredName();
		
		if (!powerExists(name)) {
			
			powerRegistry.put(name, power);
			powerIds.put(powers++, name);
			PowersAPI.logger().info( "Registered power " + name );
			return power;
			
		} else {
			
			PowersAPI.logger().error("Could not register power " + power + " under name \"" + name + "\", name has already been registered for " + lookupPower(name));
			return null;
			
		}
	}
	
	public static Power getPower(ItemStack stack) {
		return lookupPower(stack);
	}
	
	public static int getPowerCount() {
		return INSTANCE.powers;
	}
	
	public static int getPowerId(Power power) {
		
		if (INSTANCE.powerIds.containsValue(power.getRegisteredName())) {
			
			for (Entry<Integer, String> value : INSTANCE.powerIds.entrySet()) {
				
				if (value.getValue().equals(power.getRegisteredName())) {
					return value.getKey();
				}
				
			}    		
		}
		
		return 0;
		
	}
	
	public static Map<String, Power> getPowers() {
		return INSTANCE.powerRegistry;
	}
	
	public static boolean hasPower(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("currentPower");
	}
	
	public static <P extends Power> P lookupPower(Class<P> power) {
		Power powered = null;
		try {
			powered = lookupPower(power.newInstance().getRegisteredName());
		} catch (Exception e) {
			PowersAPI.logger().error( "Could not find class " + power, e );
		} 
		
		if (powered != null) {
			if (powered.getClass().equals( power )) {
				return (P)powered;
			}
		}
		
		return null;
		
	}

	public static Power lookupPower(ItemStack stack) {
		
		if (hasPower(stack)) {
			return lookupPower(stack.getTagCompound().getString( "currentPower"));
		}
		
		return null;
		
	}
	
	/**
	 * Looks up the power by name. If it doesn't exist, returns null.
	 * 
	 * @param power
	 * @return
	 */
	public static Power lookupPower(String power) {
		
		if (powerExists(power)) {
			return INSTANCE.powerRegistry.get(power);
		} else if (powerExists("power." + power)) {
			return INSTANCE.powerRegistry.get("power." + power);
		}
		
		return null;
		
	}
	
	public static Power lookupPowerById(int id) {
		return lookupPower(INSTANCE.powerIds.get(id));
	}
	
	public static boolean powerExists(String unlocalizedName) {
		return getPowers().containsKey(unlocalizedName);
	}
	
	/**
	 * If the given power is valid, returns unlocalized name, else returns "".
	 * 
	 * @param power
	 * @return
	 */
	public static String validatePowerName(Power power) {
		return power != null ? power.getRegisteredName() : "";
	}
}
