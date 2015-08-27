package com.himself12794.powersapi.util;

public final class Reference {
	
	private Reference() {}
	
    public static final String MODID = "powersAPI";
    public static final String VERSION = "1.0";
    public static final String NAME = "Powers API";
    
    public static final class TagIdentifiers {
    	
    	private TagIdentifiers() {}
    	
    	public static final String POWER_CURRENT = MODID + ".power.currentPower";
    	public static final String POWER_CURRENT_USELEFT = MODID + ".power.currentPower.useLeft";
    	public static final String POWER_SUCCESS = MODID + ".power.success";
    	public static final String POWER_COOLDOWNS = MODID + ".power.powerCooldowns";
    	public static final String POWER_EFFECTS = MODID + ".power.powerEffects";
    	public static final String POWER_SET = MODID + ".power.powerSet";
    	public static final String POWER_PRIMARY = MODID + ".power.primaryPower";
    	public static final String POWER_SECONDARY = MODID + ".power.primarySecondary";
    	public static final String POWER_PREVIOUS_TARGET = MODID + ".power.previousTarget";
    	
    }
    
}