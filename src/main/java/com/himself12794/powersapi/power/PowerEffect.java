package com.himself12794.powersapi.power;

import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.PowerEffectContainer;

public class PowerEffect {
	
	public static final Map<String, Integer> idNameMapping = Maps.newHashMap();
	public static final PowerEffect[] powerEffectIds = new PowerEffect[128];
	public static final PowerEffect negated = PowerEffect.registerEffect( new PowerEffect().setUnlocalizedName("negated") );
	
	private static int powerEffectCount = 0;
	
	private int id;
	protected String name;
	protected boolean negateable = true;
	
	public String getUnlocalizedName() {
		return name;
	}
	
	protected PowerEffect setUnlocalizedName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * This is called when the effect is first applied to the entity.
	 * 
	 * @param entity
	 * @param time
	 * @param caster
	 * @param power the power that applied this effect, if applicable
	 */
	public void onApplied(EntityLivingBase entity, int time, EntityLivingBase caster, Power power) {}
	
	/**
	 * This function is called every tick the power is in effect on the target.
	 * 
	 * 
	 * @param entity The entity on which the power is effecting
	 * @param timeLeft
	 * @param caster The entity who cast the power
	 * @param power the power that applied the effect, if applicable
	 * @return
	 */
	public void onUpdate(EntityLivingBase entity, int timeLeft, EntityLivingBase caster, Power power){};
	
	/**
	 * Called when the entity is attacked, before damage is registered.
	 * Return false to cancel.<br> 
	 * <b>Note:</b> the parameters are immutable, this is to determine whether or not
	 * the attack should continue. Useful for adding type immunities.
	 * If you want to change the amount of damage, see {@link PowerEffect#onHurt(DamageSource, float)}
	 * 
	 * @param damageSource
	 * @param amount
	 * @return
	 */
	public boolean onAttacked(DamageSource damageSource, float amount) {
		return true;
		
	}
	
	/**
	 * Called when the entity is damaged.
	 * Return false to cancel. 
	 * This allows you to change damage amounts. Will not be called if the attack was
	 * canceled in {@link PowerEffect#onAttacked(DamageSource, float)}<br>
	 * <b>Note:</b> Canceling this will still play the hurt animation and sound. 
	 * 
	 * @param damageSource
	 * @param amount
	 * @return the new damage amount.
	 */
	public float onHurt(DamageSource damageSource, float amount) {
		
		return amount;
	}
	
	/**
	 * Called when the entity attacks another entity.
	 * 
	 * @param damageSource
	 * @param amount
	 */
	public float onAttack(EntityLivingBase target, DamageSource damageSource, float amount, EntityLivingBase caster) {
		return amount;
	}
	
	/**
	 * Called when the effect is removed.
	 * <p>
	 * Useful for adding countdowns. 
	 * 
	 * @param entity
	 * @param caster
	 * @param power the power that applied this effect, if applicable
	 */
	public void onRemoval(EntityLivingBase entity, EntityLivingBase caster, Power power){}
	
	/**
	 * An extra check to make sure both the power and effect are ready
	 * 
	 * @param entityHit
	 * @param caster
	 * @param powerEffectActivatorInstant
	 * @return
	 */
	public boolean shouldApplyEffect(EntityLivingBase entityHit, EntityLivingBase caster, Power power) {
		return true;
	}
	
	public final void addTo(EntityLivingBase target, int duration, EntityLivingBase caster) {
		addTo(target, duration, caster, null);
	}
	
	/**
	 * Applies the power effect to the specific entity, for a specific time.
	 * <p>
	 * Setting the duration to less than 0 makes it last until removed.
	 * 
	 * @param target 
	 * @param duration
	 * @param caster
	 * @param power the power that applied this affect, if applicable
	 */
    public final void addTo(EntityLivingBase target, int duration, EntityLivingBase caster, Power power) {
    	
        NBTTagList activeEffects = getActiveEffects(target);
        boolean flag = true;
        boolean remove = duration == 0;
        int location = -1;
        
        if (this instanceof IPlayerOnly && !(target instanceof EntityPlayer)) return;

        for (int i = 0; i < activeEffects.tagCount(); ++i)  {
        	
            NBTTagCompound nbttagcompound = activeEffects.getCompoundTagAt(i);

            if (nbttagcompound.getShort("id") == id) {
            	
            	if (remove) location = i;
            	
            	nbttagcompound.setInteger("duration", duration);
                nbttagcompound.setString( "initiatedPower", power != null ? power.getUnlocalizedName() : "" );
                
                if (caster != null) {
                	
	                if (nbttagcompound.getInteger("caster") != caster.getEntityId()) {
	                	
	                	nbttagcompound.setInteger("caster", caster.getEntityId());
	                	
	                }
	                
                }

                flag = false;
                break;
            }
        }
        
        if (remove) {
        	
        	if (location > -1) {
        		activeEffects.removeTag(location);
        		onRemoval(target, caster, power);
        		if (power instanceof IEffectActivator && caster != null) {
        			power.triggerCooldown(caster);
        		}
        	}
        	return;
        	
        }

        if (flag && this.shouldApplyEffect(caster, target, power)) {
        	
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setShort("id", (short) id);
            nbttagcompound1.setInteger("duration", duration);
            nbttagcompound1.setInteger("caster", (caster != null ? caster.getEntityId() : -1));
            nbttagcompound1.setString( "initiatedPower", power != null ? power.getUnlocalizedName() : "" );
            activeEffects.appendTag(nbttagcompound1);
            onApplied( target, duration, caster, power );
        }
    }
	
	/**
	 * Clears the effect from the entity without calling PowerEffect#onRemoval().
	 * 
	 * @param target
	 */
	public final void clear(PowerEffectContainer target) {
		
		NBTTagList effects = getActiveEffects(target.getAffectedEntity());
		
		for (int i = 0; i < effects.tagCount(); i++) {
			if (effects.getCompoundTagAt( i ).getInteger( "id" ) == id) {
				effects.removeTag( i );

        		if (target.getInitiatedPower() instanceof IEffectActivator) {
        			target.getInitiatedPower().triggerCooldown(target.getCasterEntity());
        		}
			}
		}
		
	}
	
	/**
	 * Clears the effect from the entity without side effects.
	 * 
	 * @param target
	 */
	public final void clearQuietly(EntityLivingBase target) {
		
		NBTTagList effects = getActiveEffects(target);
		
		for (int i = 0; i < effects.tagCount(); i++) {
			if (effects.getCompoundTagAt( i ).getInteger( "id" ) == id) {
				effects.removeTag( i );
			}
		}
		
	}
	
	/**
	 * Returns the time remaining for this effect on the target. If the target does not 
	 * have the effect, it returns 0.
	 * 
	 * @param target
	 * @return
	 */
	public final int getEffectTimeRemainingOn(EntityLivingBase target){
		
		NBTTagList activeEffects = getActiveEffects(target);
		boolean flag = true;
		
		for (int i = 0; i < activeEffects.tagCount(); ++i) {

	        NBTTagCompound nbttagcompound = activeEffects.getCompoundTagAt(i);
	
	        if (nbttagcompound.getShort("id") == id) {
	      
	            return nbttagcompound.getInteger("duration");
	            
	        }
		}
		
		return 0;
	}
	
	/**
	 * Checks if the effect is on the target
	 * 
	 * @param entity
	 * @return
	 */
	public final boolean isEffecting(EntityLivingBase entity) {

		return getEffectTimeRemainingOn(entity) != 0;
	}
	
	private final void setId(int id) {
		this.id = id;
	}
	
	public final int getId() {
		return id;
	}
	
	protected void setNegateable(boolean state) {negateable = state;}
	
	public boolean isNegateable() {return negateable;}
	
	public String toString() {
		return name;
	}
	
	public static PowerEffect registerEffect(PowerEffect effect) {
		
		int nextId = getNextIndex();
		if (nextId != -1) {
			
			effect.setId(nextId);
			powerEffectIds[nextId] = effect;
			idNameMapping.put( effect.name, nextId );
			++powerEffectCount;
			PowersAPI.logger.debug("Registered effect " + effect.getClass().getSimpleName());
			return effect;
			
		} else {
			return powerEffectIds[0];
		}
		
	}
	
	public static PowerEffect getPowerEffect(String name) {
		
		final Integer index = idNameMapping.get( name );
		
		if (index != null) {
			return powerEffectIds[index];
		}
		
		return null;
		
	}
	
	private static int getNextIndex() {
		
		//System.out.println("Getting next index");
		
		for (int i = 0; i < powerEffectIds.length; i++) {
			PowersAPI.logger.debug(powerEffectIds[i]);
			if (powerEffectIds[i] == null) return i;
			
		}
		
		return -1;
		
	}
	
	public static PowerEffect[] getRegisteredSpellEffects() {
		return powerEffectIds;
	}
	
	public static PowerEffect getEffectById(int id) {

		return powerEffectIds[id];
	}
	
	public static int getEffectCount() {
		return PowerEffect.powerEffectCount;
	}
	
	public static NBTTagList getActiveEffects(EntityLivingBase target) {
		return DataWrapper.get( target ).getActiveEffects();
	}

}
