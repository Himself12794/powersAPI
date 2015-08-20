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
import com.himself12794.powersapi.util.Reference;

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
	
	/**
	 * This determines how the power is cast, then casts it.
	 * Returning true will trigger the cool down.
	 * 
	 * @param world
	 * @param caster
	 * @param stack
	 * @param modifier
	 * @return success
	 */
	public abstract boolean cast(World world, EntityLivingBase caster, ItemStack tome, float modifier);
	
	/**
	 * The action to be performed when the power is being prepared, before it is actually cast.
	 * <p>
	 * This is used primarily to check if the player should be allowed to cast the power or not.
	 * 
	 * @return whether or not casting should continue.
	 */
	public boolean onPreparePower(ItemStack stack, World worldIn, EntityPlayer playerIn) {
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
	public boolean onCast(World world, EntityLivingBase caster, ItemStack stack, float modifier) {return true;}
	
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
		boolean flag = false;
		
		if (target.entityHit != null) {
			
			flag = target.entityHit.attackEntityFrom(DamageSource.magic, getPower() * modifier);
			((EntityLivingBase)target.entityHit).setLastAttacker(caster);
			
		} 
		
		return flag;
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
	public boolean onFinishedCastingEarly(ItemStack stack, World world, EntityPlayer playerIn, int timeLeft) { return true; }
	
	/**
	 * Called when power is done being cast, before the cool down is triggered.
	 * Return false to negate the cool down.
	 * 
	 * @param stack
	 * @param world
	 * @param caster
	 * @return whether or not to negate the cool down
	 */
	public boolean onFinishedCasting(ItemStack stack, World world, EntityPlayer caster) { return true; }
	
	/**
	 * Determines whether or not powers that have a duration should show this on the tooltip.
	 * 
	 * @param stack
	 * @param caster
	 * @param par3 whether or not advanced tooltips are enabled
	 * @return
	 */
	public boolean showDuration(ItemStack stack, EntityPlayer caster, boolean par3) { return true; }
	
	
	/**Gets the power description. This value is localized.
	 * @param player 
	 * @param stack 
	 * 
	 * @return
	 */
	public String getInfo(ItemStack stack, EntityPlayer player) {
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
	
	public final ItemStack setPower(ItemStack stack) {
		
		NBTTagCompound nbt = null;
		String power = getUnlocalizedName();
		
		if (!stack.hasTagCompound()) {
			
			nbt = new NBTTagCompound();
			
		} else {
			
			nbt = stack.getTagCompound();
			
		}
		
		if (!powerExists(power)) {
			
			PowersAPI.logger.fatal("Cannot set unregistered power \"" + power + "\"");
			
		} else {
			
			nbt.setString(Reference.TagIdentifiers.POWER_CURRENT, power);
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
			
			return lookupPower(stack.getTagCompound().getString( Reference.TagIdentifiers.POWER_CURRENT));
			
		}
		
		return null;
		
	}
	
	public static Power lookupPower(String power) {
		
		if (Power.powerExists(power)) return (Power)powerRegistry.get(power);
		
		return null;
		
	}
	
	public static <P extends Power> P lookupPower(Class<P> power) {
		Power powered = null;
		try {
			powered = lookupPower(power.newInstance().displayName);
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
		
		NBTTagCompound coolDowns = player.getEntityData().getCompoundTag(Reference.TagIdentifiers.POWER_COOLDOWNS);
		
		return coolDowns.getInteger(getUnlocalizedName()) <= 0;
		
	}
	
	public final int getCoolDownRemaining(EntityLivingBase player) {
		NBTTagCompound coolDowns = player.getEntityData().getCompoundTag(Reference.TagIdentifiers.POWER_COOLDOWNS);
		
		return coolDowns.getInteger(getUnlocalizedName());
	}
	
	public final void setCoolDown(EntityLivingBase player, int amount) {
		int id = player.getEntityId();
		NBTTagCompound coolDowns = player.getEntityData().getCompoundTag(Reference.TagIdentifiers.POWER_COOLDOWNS);
		
		coolDowns.setInteger(getUnlocalizedName(), amount);
		player.getEntityData().setTag(Reference.TagIdentifiers.POWER_COOLDOWNS, coolDowns);
	}
	
	public final void triggerCooldown( EntityLivingBase player ) {
		
		if (player instanceof EntityPlayer)
			if (((EntityPlayer)player).capabilities.isCreativeMode) return;
		setCoolDown(player, getCoolDown());
	}
	
	public static boolean hasPower(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(Reference.TagIdentifiers.POWER_CURRENT);
	}
	
	public static Power getPower(ItemStack stack) {
		return Power.lookupPower(stack);
	}
	
	public static NBTTagCompound getCooldowns(EntityLivingBase player) {
		NBTTagCompound cooldowns = player.getEntityData().getCompoundTag(Reference.TagIdentifiers.POWER_COOLDOWNS);
		return player.getEntityData().hasKey(Reference.TagIdentifiers.POWER_COOLDOWNS) && cooldowns != null ? cooldowns : new NBTTagCompound();
	}	
}
