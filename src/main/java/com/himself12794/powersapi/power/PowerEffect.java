package com.himself12794.powersapi.power;

import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.storage.EffectContainer;

// TODO add tag for beneficial, malicious, or tagging
public class PowerEffect {
	
	public static final Map<String, Short> idNameMapping = Maps.newHashMap();
	public static final PowerEffect[] powerEffectIds = new PowerEffect[128];
	public static final PowerEffect negated = PowerEffect.registerEffect( new PowerEffect().setUnlocalizedName("negated") );
	
	private static int powerEffectCount = 0;
	
	private short id;
	
	protected boolean isPersistant = false;
	
	protected String name;
	
	protected boolean negateable = false;
	
	protected EffectType type = EffectType.TAG;
	
	public PowerEffect() {
		
	}
	
	public PowerEffect(final String name) {
		this(name, false);
	}
	
	public PowerEffect(final String name, final boolean negateable) {
		setUnlocalizedName(name);
		if (negateable) setNegateable();
	}
	
	public PowerEffect(final String name, final boolean negateable, final EffectType type) {
		this(name,negateable);
		setType(type);
	}
	
	public PowerEffect(final String name, final boolean negateable, final EffectType type, final boolean persistant) {
		this(name,negateable);
		setType(type);
		isPersistant = persistant;
	}
	
	public final int getId() {
		return id;
	}
	
	public EffectType getType() {
		return type;
	}
	
	public String getUnlocalizedName() {
		return name;
	}
	
	/**
	 * Whether or not the effect still works if the player has the negated power effect
	 * 
	 * @return
	 */
	public boolean isNegateable() {return negateable;}
	
	public boolean isPersistant() {
		return isPersistant;
	}
	
	/**
	 * This is called when the effect is first applied to the entity.
	 * 
	 * @param entity
	 * @param caster TODO
	 * @param effectContainer TODO
	 */
	public void onApplied(final EntityLivingBase entity, EntityLivingBase caster, EffectContainer effectContainer) {}
	
	/**
	 * Called when the entity attacks another entity.
	 * 
	 * @param damageSource
	 * @param amount
	 * @deprecated Currently unimplemented
	 */
	@Deprecated
	public float onAttack(final EntityLivingBase target, final DamageSource damageSource, final float amount, final EntityLivingBase caster) {
		return amount;
	}
	
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
	public boolean onAttacked(final DamageSource damageSource, final float amount) {
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
	public float onHurt(final DamageSource damageSource, final float amount) {
		
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
	public void onRemoval(final EntityLivingBase entity, final EntityLivingBase caster, final Power power){}
	
	/**
	 * This function is called every tick the power is in effect on the target.
	 * 
	 * 
	 * @param entity The entity on which the power is effecting
	 * @param timeLeft
	 * @param caster The entity who cast the power. May be null.
	 * @param initiatedPower The power that applied the effect. May be null.
	 * @return return false to remove effect prematurely
	 */
	public boolean onUpdate(final EntityLivingBase entity, final int timeLeft, final EntityLivingBase caster, final Power initiatedPower){ return true; }
	
	private final void setId(final short id) {
		this.id = id;
	}
	
	protected void setNegateable() {negateable = true;}
	
	protected void setType(final EffectType type) {
		this.type = type;
	}
	
	protected PowerEffect setUnlocalizedName(final String name) {
		this.name = name;
		return this;
	}
	
	protected void setPersistant() {
		isPersistant = true;
	}
	
	/**
	 * An extra check to make sure both the power and effect are ready
	 * 
	 * @param entityHit
	 * @param caster
	 * @param powerEffectActivatorInstant
	 * @return
	 */
	public boolean shouldApplyEffect(final EntityLivingBase entityHit, final EntityLivingBase caster, final Power power) {
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static PowerEffect getEffectById(final int id) {

		return powerEffectIds[id];
	}
	
	public static int getEffectCount() {
		return PowerEffect.powerEffectCount;
	}
	
	private static short getNextIndex() {
		
		for (short i = 0; i < powerEffectIds.length; i++) {
			PowersAPI.logger.debug(powerEffectIds[i]);
			if (powerEffectIds[i] == null) return i;
			
		}
		
		return -1;
		
	}
	
	public static PowerEffect getPowerEffect(final String name) {
		
		final Short index = idNameMapping.get( name );
		
		if (index != null) {
			return powerEffectIds[index];
		}
		
		return null;
		
	}
	
	public static PowerEffect[] getRegisteredSpellEffects() {
		return powerEffectIds;
	}

	public static PowerEffect registerEffect(final PowerEffect effect) {
		
		final short nextId = getNextIndex();
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

}
