package com.himself12794.powersapi.power;

import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.util.Reference;

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
	 */
	public void onApplied(EntityLivingBase entity, int time, EntityLivingBase caster) {}
	
	/**
	 * This function is called every tick the power is in effect on the target.
	 * 
	 * 
	 * @param entity The entity on which the power is effecting
	 * @param timeLeft
	 * @param caster The entity who cast the power
	 * @return
	 */
	public void onUpdate(EntityLivingBase entity, int timeLeft, EntityLivingBase caster){};
	
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
		System.out.println("Effect succesfully passed down!");
		return amount;
	}
	
	/**
	 * Called when the effect is removed.
	 * <p>
	 * Intended mostly for cleanup after effect removal. 
	 * 
	 * @param entity
	 * @param caster 
	 * @param world
	 */
	public void onRemoval(EntityLivingBase entity, EntityLivingBase caster){}
	
	/**
	 * The old method for adding a power effect to an entity.
	 * 
	 * @param target
	 * @param duration
	 * @param caster
	 */
	private final void addTo2(EntityLivingBase target, int duration, EntityLivingBase caster) {
		
		NBTTagList activeEffects = getActiveEffects(target);//target.getEntityData().getCompoundTag(Reference.TagIdentifiers.powerEffects);
		
		NBTTagCompound data = new NBTTagCompound();
		data.setShort("id", (short) id);
		data.setInteger("duration", duration);
		data.setInteger("caster", caster.getEntityId());
		//NBTTagIntArray powerEffectData = new NBTTagIntArray(data);
		
		activeEffects.appendTag(data);
		target.getEntityData().setTag(Reference.TagIdentifiers.powerEffects, activeEffects);
		
	}

	
	/**
	 * Applies the power effect to the specific entity, for a specific time.
	 * <p>
	 * Setting the duration to less than 0 makes it last until removed.
	 * 
	 * @param target 
	 * @param duration
	 * @param caster
	 */
    public final void addTo(EntityLivingBase target, int duration, EntityLivingBase caster) {
    	
        NBTTagList activeEffects = getActiveEffects(target);
        boolean flag = true;
        boolean remove = duration == 0;
        int location = -1;
        
        if (this instanceof IPlayerOnly && !(target instanceof EntityPlayer)) return;

        for (int i = 0; i < activeEffects.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = activeEffects.getCompoundTagAt(i);

            if (nbttagcompound.getShort("id") == id) {
            	
            	if (remove) location = i;
            	
            	nbttagcompound.setInteger("duration", duration);
                
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
        		onRemoval(target, caster);
        		PowersAPI.proxy.network.sendToAll(new PowerEffectsClient(this, target, caster, true, 0));
        	}
        	return;
        	
        }

        if (flag)
        {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setShort("id", (short) id);
            nbttagcompound1.setInteger("duration", duration);
            nbttagcompound1.setInteger("caster", (caster != null ? caster.getEntityId() : -1));
            activeEffects.appendTag(nbttagcompound1);
            this.onApplied( target, duration, caster );
        }

        target.getEntityData().setTag(Reference.TagIdentifiers.powerEffects, activeEffects);
    }
	
	/**
	 * Clears any traces of the effect from the entity, if they have it, 
	 * and triggers {@link PowerEffect#onRemoval(EntityLivingBase, EntityLivingBase)}
	 * 
	 * @param target
	 */
	public final void clearFrom(EntityLivingBase target, EntityLivingBase caster) {
		
		//NBTTagList activeEffects = getActiveEffects(target);
		//activeEffects.removeTag(id);
		addTo(target, 0, caster);
		onRemoval(target, caster);
		
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
	
	public static NBTTagList getActiveEffects(EntityLivingBase entity) {
		NBTTagCompound activeEffects = entity.getEntityData();//.getCompoundTag(Reference.TagIdentifiers.powerEffects);
		
		//return entity.getEntityData().getCompoundTag(Reference.TagIdentifiers.powerEffects);
		return activeEffects != null && activeEffects.hasKey(Reference.TagIdentifiers.powerEffects, 9) ? (NBTTagList)activeEffects.getTag(Reference.TagIdentifiers.powerEffects) : new NBTTagList();
	}
	
	public static int getEffectCount() {
		return PowerEffect.powerEffectCount;
	}

}
