package com.himself12794.powersapi.power;

/**
 * Signifies the type of power effect. Currently, only the hidden type is used.
 * 
 * @author Himself12794
 *
 */
public enum EffectType {

	BENEFICIAL, MALICIOUS, TAG, HIDDEN;
	
	public boolean isHidden() {
		return this == HIDDEN;
	}
	
}
