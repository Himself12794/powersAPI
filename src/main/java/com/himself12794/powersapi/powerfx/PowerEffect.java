package com.himself12794.powersapi.powerfx;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.network.PowerEffectsClient;
import com.himself12794.powersapi.util.Reference;

public abstract class PowerEffect {
	
	public static final PowerEffect[] spellEffectIds = new PowerEffect[32];
	public static final PowerEffect rapidCellularRegeneration = new RapidCellularRegeneration(0);
	public static final PowerEffect lift = new Lift(1);
	public static final PowerEffect slam = new Slam(2);
	public static final PowerEffect levitate = new Levitate(3);
	public static final PowerEffect paralysis = new Paralysis(4);
	
	private static int spellEffectCount = 0;
	
	
	public final int id;
	protected boolean negateable = false;
	
	PowerEffect(int id) {
		this.id = id;
		spellEffectIds[id] = this;
	}
	
	/**
	 * This function is called every tick the spell is in effect on the target.
	 * 
	 * 
	 * @param entity The entity on which the spell is effecting
	 * @param timeLeft
	 * @param caster The entity who cast the spell
	 * @return
	 */
	public abstract void onUpdate(EntityLivingBase entity, int timeLeft, EntityLivingBase caster);
	
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
	
	private final void addTot(EntityLivingBase target, int duration, EntityLivingBase caster) {
		
		NBTTagList activeEffects = getActiveEffects(target);//target.getEntityData().getCompoundTag(Reference.TagIdentifiers.powerEffects);
		
		NBTTagCompound data = new NBTTagCompound();
		data.setShort("id", (short) id);
		data.setInteger("duration", duration);
		data.setInteger("caster", caster.getEntityId());
		//NBTTagIntArray spellEffectData = new NBTTagIntArray(data);
		
		activeEffects.appendTag(data);
		target.getEntityData().setTag(Reference.TagIdentifiers.powerEffects, activeEffects);
		
	}

	
	/**
	 * Applies the spell effect to the specific entity, for a specific time.
	 * <p>
	 * Setting the duration to less than 0 makes it last until removed.
	 * 
	 * @param target 
	 * @param duration
	 */
    public final void addTo(EntityLivingBase target, int duration, EntityLivingBase caster) {
    	
        NBTTagList activeEffects = getActiveEffects(target);
        boolean flag = true;
        boolean remove = duration == 0;
        int location = -1;

        for (int i = 0; i < activeEffects.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = activeEffects.getCompoundTagAt(i);

            if (nbttagcompound.getShort("id") == id) {
            	
            	if (remove) location = i;
            	
            	nbttagcompound.setInteger("duration", duration);
                
                if (caster != null) {
                	
	                if (nbttagcompound.getInteger("caster") != caster.getEntityId()) 
	                	
	                	nbttagcompound.setInteger("caster", caster.getEntityId());
	                
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
        }

        target.getEntityData().setTag(Reference.TagIdentifiers.powerEffects, activeEffects);
    }
	
	/**
	 * Clears any traces of the effect from the entity, if they have it.
	 * <p>
	 * If the duration left is not 0, also triggers {@link PowerEffect#onRemoval(EntityLivingBase)}
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
	
	public static PowerEffect[] getRegisteredSpellEffects() {
		return spellEffectIds;
	}
	
	public static PowerEffect getEffectById(int id) {

		if (spellEffectIds[id] != null) return spellEffectIds[id];
		return null;
	}
	
	public static NBTTagList getActiveEffects(EntityLivingBase entity) {
		NBTTagCompound activeEffects = entity.getEntityData();//.getCompoundTag(Reference.TagIdentifiers.powerEffects);
		
		//return entity.getEntityData().getCompoundTag(Reference.TagIdentifiers.powerEffects);
		return activeEffects != null && activeEffects.hasKey(Reference.TagIdentifiers.powerEffects, 9) ? (NBTTagList)activeEffects.getTag(Reference.TagIdentifiers.powerEffects) : new NBTTagList();
	}

}
