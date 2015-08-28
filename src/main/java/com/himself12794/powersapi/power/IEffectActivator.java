package com.himself12794.powersapi.power;

/**
 * Indicates that a PowerBuff is used to activate/deactivate a power effect
 * 
 * @author Himself12794
 *
 */
public interface IEffectActivator {
	
	PowerEffect getPowerEffect();
	
	int getEffectDuration();

}
