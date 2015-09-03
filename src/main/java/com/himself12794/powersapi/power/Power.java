package com.himself12794.powersapi.power;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.PowerProfile;
import com.himself12794.powersapi.util.UsefulMethods;

/**
 * This class is used to add powers to Minecraft. This is manifested as a power on
 * a generic power actuator, and all powers registered via {@link Power#registerPower(Power)}
 * will appear in the creative tab.
 * <p>
 * Every power has a power field, which is primarily used to display a power field for documentation
 * purposes, and not necessarily to determine their magnitude, but can be if desired.
 * <p>
 * The field {@code duration} is primarily for documentation as well, but again can be used for other purposes.
 * <p>
 * The field {@code coolDown} is used as a timeout after the power has been used before it can be used again. This
 * is used to balance more potent or potentially game-breaking powers.
 * <p>
 * The field {@code maxConcentrationTime} is used to determine how long the spell can be used before cooldown is forced.
 * Ceasing from using the spell before the concentration time is meant will sitll trigger the cooldown. 
 * @author Himself12794
 *
 */
public abstract class Power {
	
	private String displayName;
	private float power = 2.0F;
	/**Duration in ticks*/
	private int duration = 0;
	/**Cool down time in ticks*/
	private int coolDown = 4;
	/**How long the power can be used until a cooldown is forced*/
	private int maxConcentrationTime = 0;
	private boolean visibility = true;
	private int preparationTime;
	private float maxLevel;
	
	/**
	 * This determines how the power is cast, then casts it.
	 * Returning true will trigger the cool down.
	 * 
	 * @param world
	 * @param caster
	 * @param mouseOver TODO
	 * @param modifier
	 * @param stack
	 * @return success
	 */
	public abstract boolean cast(World world, EntityLivingBase caster, MovingObjectPosition mouseOver, float modifier);
	
	/**
	 * The action to be performed when the power is being prepared, before it is actually cast.
	 * <p>
	 * This is used primarily to check if the player should be allowed to cast the power or not.
	 * 
	 * @return whether or not casting should continue.
	 */
	public boolean onPreparePower(World worldIn, EntityPlayer playerIn) {
		return true;
	}
	
	/**
	 * Called when the power is cast.
	 * Determines how the power affects the player and world when cast.
	 * Return whether or not a successful use occurred. Cooldown only occurs if both are successful.
	 * 
	 * @param world
	 * @param caster
	 * @param modifier
	 * @param stack
	 * @return whether or not the power counts as successful, and should count as a use
	 */
	public boolean onCast(World world, EntityLivingBase caster, float modifier) {return true;}
	
	/**
	 * Called when the power affects a target.
	 * For instances of PowerInstant, when a target has been successfully acquired, and for instances of
	 * PowerRanged, when it impacts anything. 
	 * <p>
	 * For instances of PowerInstant, returning true will mark the power as a success, allowing the cooldown to be triggered.
	 * 
	 * @param world
	 * @param target
	 * @param caster
	 * @param modifier
	 * @param stack
	 * @return success
	 */
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier ) {
		
		/*if (target.entityHit instanceof EntityLivingBase) {
			((EntityLivingBase)target.entityHit).attackEntityFrom( DamageSource.magic, getPower() );
			return true;
		}
		

		if (this instanceof PowerInstant) return false;
		else return true;*/
		return true;
		
	}
	
	/**
	 * Called when power is aborted before concentration time is over.
	 * Return false prevent the cooldown
	 * 
	 * @param stack
	 * @param world
	 * @param playerIn
	 * @param timeLeft
	 * @return whether or not to cancel the cooldown
	 */
	public boolean onFinishedCastingEarly(World world, EntityPlayer playerIn, int timeLeft, MovingObjectPosition target) { return true; }
	
	/**
	 * Called when power is done being cast, before the cool down is triggered.
	 * Return false to negate the cool down.
	 * 
	 * @param stack
	 * @param world
	 * @param caster
	 * @param movingObjectPosition 
	 * @return whether or not to negate the cool down
	 */
	public boolean onFinishedCasting(World world, EntityPlayer caster, MovingObjectPosition movingObjectPosition) { return true; }
	
	/**
	 * Determines whether or not powers that have a duration should show this on the tooltip.
	 * 
	 * @param stack
	 * @param caster
	 * @param par3 whether or not advanced tooltips are enabled
	 * @return
	 */
	public boolean showDuration(ItemStack stack, EntityPlayer caster, boolean par3) { return true; }
	
	
	/**
	 * Gets the power description. This value is localized.
	 * 
	 * @param profile unique object for each power, for every EntityPlayer
	 * @return
	 */
	public String getInfo(PowerProfile profile) {
		String info = ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".description")).trim();
		
		if (info.equals(getUnlocalizedName() + ".description")) info = "";
		
		return info;
	}
	
	/**
	 * Gets the information to be shown under the "Type" tag on the magic tome.
	 * 
	 * @param stack
	 * @param player
	 * @return
	 */
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {return null; }
	
	/**
	 * Location for model if the default one is not desired.
	 * 
	 * @param stack
	 * @param player
	 * @param useRemaining
	 * @return
	 */
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) { return null; }
	
	/**
	 * Teaches the entity the power.
	 * 
	 * @param target 
	 * @param duration
	 * @param caster
	 */
    public final void teachPower(EntityLivingBase target) {
    	DataWrapper.get(target).teachPower( this );
    }
	
	public final ItemStack setPower(ItemStack stack) {
		
		NBTTagCompound nbt = null;
		String power = getUnlocalizedName();
		
		if (!stack.hasTagCompound()) {
			
			nbt = new NBTTagCompound();
			
		} else {
			
			nbt = stack.getTagCompound();
			
		}
		
		if (!powerExists(power)) {
			
			PowersAPI.logger.error("Cannot set unregistered power \"" + power + "\"");
			
		} else {
			
			nbt.setString("currentPower", power);
			stack.setTagCompound(nbt);
			
		}
		
		return stack;
		
	}
	
	public final boolean isPowerOnStack(ItemStack stack) {
		return Power.hasPower(stack) && lookupPower(stack) == this;
	}
	
	public String getDisplayName() {
		return ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".name")).trim();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Power)) return false;
		else {
			Power power = (Power)obj;
			return this.getSimpleName().equals( power.getSimpleName() );
		}
		
		
	}
	
	public Power setVisibility(boolean value) {visibility = value; return this;}
	
	public boolean isVisible() { return visibility; }
	
	public Power setPower(float value) { power = value; return this; }

	public float getPower() { return power; }
	
	public Power setCoolDown(int amount) { coolDown = amount; return this; }
	
	public int getCoolDown() { return coolDown; }
	
	public Power setUnlocalizedName( String name ) { displayName = name; return this; }
	
	public String getUnlocalizedName() { return "power." + displayName; }
	
	protected Power setDuration(int time) { duration = time; return this; }
	
	public int getDuration() { return duration; }
	
	protected void setMaxConcentrationTime(int time) { maxConcentrationTime = time; }
	
	public int getMaxConcentrationTime() { return maxConcentrationTime; }
	
	public boolean isConcentrationPower() { return maxConcentrationTime > 0; }

	public float getBrightness() { return 5.0F; }
	
	/**
	 * Gets the name the power is registered under.
	 * 
	 * @return
	 */
	public String getSimpleName() { return displayName; }
	
	/*================================= Begin Power Registration Section ===============================*/ 
	
	private static Map<Integer, String> powerIds = Maps.newHashMap();
	private static Map<String, Power> powerRegistry = Maps.newHashMap();
	private static int powers = 0;
	
	public static void registerPowers() {
		
		PowersAPI.logger.info("Registered [" + Power.getPowerCount() + "] powers");
		
	}
	
	/**
	 * Used to register created powers. Powers are stored with their unlocalized name, as well
	 * as a generated id.
	 * 
	 * @param power
	 * @return 
	 */
	public static Power registerPower(Power power) {
		
		String name = power.getUnlocalizedName();
		
		if (!Power.powerExists(name)) {
			
			powerRegistry.put(name, power);
			powerIds.put(powers, name);
			++powers;
			return power;
			
		} else {
			
			PowersAPI.logger.error("Could not register power " + power + " under name \"" + name + "\", name has already been registered for " + lookupPower(name));
			return null;
			
		}
	}
	
	public static Power lookupPowerById(int id) {
		
		return lookupPower(powerIds.get(id));
		
	}
	
	public static int getPowerId(Power power) {
		
		if (powerIds.containsValue(power.getUnlocalizedName())) {
			
			for (Entry<Integer, String> value : powerIds.entrySet()) {
				
				if (value.getValue().equals(power.getUnlocalizedName()))
					return value.getKey();
				
			}    		
		}
		
		return -1;
		
	}
	
	public static Power lookupPower(ItemStack stack) {
		
		if (Power.hasPower(stack)) {
			
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
		
		if (Power.powerExists(power)) return (Power)powerRegistry.get(power);
		
		return null;
		
	}
	
	public static <P extends Power> P lookupPower(Class<P> power) {
		Power powered = null;
		try {
			powered = lookupPower(power.newInstance().getUnlocalizedName());
		} catch (Exception e) {
			PowersAPI.logger.error( "Could not instantiate class " + power, e );
		} 
		
		if (powered != null) {
			if (powered.getClass().equals( power )) {
				return (P)powered;
			}
		}
		
		return null;
		
	}
	
	public static Map<String, Power> getPowers() {
		return powerRegistry;
	}

	public static int getPowerCount() {
		return powers;
	}
	
	public static boolean powerExists(String unlocalizedName) {
		return Power.getPowers().containsKey(unlocalizedName);
	}
	
	public final boolean canUsePower( EntityLivingBase player ) {
		return (DataWrapper.get( player ).getCooldownRemaining( this ) <= 0) || UsefulMethods.isCreativeModePlayerOrNull( player );
	}
	
	public final void setCoolDown(EntityLivingBase player, int amount) {
		
		DataWrapper data = DataWrapper.get( player );
		
		NBTTagCompound coolDowns = data.getPowerCooldowns();
		
		coolDowns.setInteger(getUnlocalizedName(), amount);
	}
	
	public final void triggerCooldown( EntityLivingBase player ) {
		
		if (player instanceof EntityPlayer) {
			if (((EntityPlayer)player).capabilities.isCreativeMode) return;
		}
		setCoolDown(player, getCoolDown());
	}
	
	public static boolean hasPower(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("currentPower");
	}
	
	public static Power getPower(ItemStack stack) {
		return Power.lookupPower(stack);
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
	
	public String toString() {
		return getUnlocalizedName();
	}
}
