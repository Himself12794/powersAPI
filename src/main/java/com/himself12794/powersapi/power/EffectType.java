package com.himself12794.powersapi.power;


public enum EffectType {

	BENEFICIAL, MALICIOUS, TAG, HIDDEN;
	
	public boolean isHidden() {
		return this == HIDDEN;
	}
	
}
