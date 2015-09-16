package com.himself12794.powersapi;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.power.IEffectActivator;
import com.himself12794.powersapi.power.Power;


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
	Power registerPower(Power power) {
		
		String name = power.getUnlocalizedName();
		
		if (!powerExists(name)) {
			
			powerRegistry.put(name, power);
			powerIds.put(powers, name);
			PowersAPI.logger().info( "Registered " + name );
			++powers;
			return power;
			
		} else {
			
			PowersAPI.logger().error("Could not register power " + power + " under name \"" + name + "\", name has already been registered for " + lookupPower(name));
			return null;
			
		}
	}
	
	public static Power lookupPowerById(int id) {
		
		return lookupPower(INSTANCE.powerIds.get(id));
		
	}
	
	public static int getPowerId(Power power) {
		
		if (INSTANCE.powerIds.containsValue(power.getUnlocalizedName())) {
			
			for (Entry<Integer, String> value : INSTANCE.powerIds.entrySet()) {
				
				if (value.getValue().equals(power.getUnlocalizedName()))
					return value.getKey();
				
			}    		
		}
		
		return -1;
		
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
		
		if (powerExists(power)) return (Power)INSTANCE.powerRegistry.get(power);
		else if (powerExists("power." + power)) return (Power)INSTANCE.powerRegistry.get("power." + power);
		
		return null;
		
	}
	
	public static <P extends Power> P lookupPower(Class<P> power) {
		Power powered = null;
		try {
			powered = lookupPower(power.newInstance().getUnlocalizedName());
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
	
	public static Map<String, Power> getPowers() {
		return INSTANCE.powerRegistry;
	}

	public static int getPowerCount() {
		return INSTANCE.powers;
	}
	
	public static boolean powerExists(String unlocalizedName) {
		return getPowers().containsKey(unlocalizedName);
	}
	
	public static boolean hasPower(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("currentPower");
	}
	
	public static Power getPower(ItemStack stack) {
		return lookupPower(stack);
	}
	
	/**
	 * If the given power is valid, returns unlocalized name, else returns "".
	 * 
	 * @param power
	 * @return
	 */
	public static String validatePowerName(Power power) {
		return power != null ? power.getUnlocalizedName() : "";
	}
}
