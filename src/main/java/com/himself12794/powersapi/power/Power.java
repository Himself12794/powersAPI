package com.himself12794.powersapi.power;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.storage.PowerProfile;
import com.himself12794.powersapi.storage.PowersEntity;
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
	
	private static final ResourceLocation defaultTexture = new ResourceLocation("textures/blocks/beacon.png");
	private String displayName;
	private ResourceLocation texture;
	private float power = 2.0F;
	/**Duration in ticks*/
	private int duration;
	/**Cool down time in ticks*/
	private int cooldown = 20;
	/**How long the power can be used until a cooldown is forced*/
	private int maxConcentrationTime;
	private int highestFunctionalState;
	private boolean visibility = true;
	private int preparationTime;
	private int maxLevel = 1;
	private int usesToLevelUp = 100;
	private boolean isNegateable = true;
	
	/**
	 * The action to be performed when the power is being prepared, before it is actually cast.
	 * <p>
	 * This is used primarily to check if the player should be allowed to cast the power or not.
	 * @param profile the profile of this power
	 * @return whether or not casting should continue.
	 */
	public boolean canCastPower(PowerProfile profile) {
		return true;
	}
	
	/**
	 * Called every tick a power is being prepared. (Before it is cast)
	 * 
	 * @param player
	 * @param world
	 * @param profile
	 * @param timeLeft 
	 * @return
	 */
	public boolean onPrepareTick(EntityPlayer player, World world, PowerProfile profile, int timeLeft) { return true; }
	
	/**
	 * This determines how the power is cast, then casts it.
	 * Normally, this means that this method is responsible for gathering information
	 * from the world and passing it to {@link Power#onCast(World, EntityLivingBase, float, int)} 
	 * and {@link Power#onStrike(World, MovingObjectPosition, EntityLivingBase, float, int)}, then 
	 * returning their responses.
	 * <p>
	 * Returning true will trigger the cool down.
	 * 
	 * @param world
	 * @param caster
	 * @param mouseOver 
	 * @param modifier
	 * @param state 
	 * @param stack
	 * @return success
	 */
	public abstract boolean cast(World world, EntityLivingBase caster, MovingObjectPosition mouseOver, float modifier, int state);
	
	/**
	 * Called when the power is cast.
	 * Determines how the power affects the player and world when cast.
	 * Return whether or not a successful use occurred. Cooldown only occurs if both are successful.
	 * 
	 * @param world
	 * @param caster
	 * @param modifier
	 * @param state 
	 * @param stack
	 * @return whether or not the power counts as successful, and should count as a use
	 */
	public boolean onCast(World world, EntityLivingBase caster, float modifier, int state) {return true;}
	
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
	 * @param state
	 * @param stack
	 * @return success
	 */
	public boolean onStrike(World world, MovingObjectPosition target, EntityLivingBase caster, float modifier, int state ) {
		return true;
	}
	
	/**
	 * Called when power is aborted before concentration time is over.
	 * Return false prevent the cooldown
	 * @param world
	 * @param entityIn
	 * @param timeLeft
	 * @param state
	 * @param stack
	 * 
	 * @return whether or not to cancel the cooldown
	 */
	public boolean onFinishedCastingEarly(World world, EntityLivingBase entityIn, int timeLeft, MovingObjectPosition target, int state) { return true; }
	
	/**
	 * Called when power is done being cast, before the cool down is triggered.
	 * Return false to negate the cool down.
	 * @param world
	 * @param caster
	 * @param movingObjectPosition 
	 * @param state
	 * @param stack
	 * 
	 * @return whether or not to negate the cool down
	 */
	public boolean onFinishedCasting(World world, EntityLivingBase caster, MovingObjectPosition movingObjectPosition, int state) { return true; }
	
	/**
	 * Called when the power state is changed, after the change. Useful for adding chat messages about the changed state.
	 * @param world 
	 * @param caster
	 * @param prevState
	 * @param currState
	 */
	public void onStateChanged(World world, EntityLivingBase caster, int prevState, int currState) {}
	
	/**
	 * Called every tick on entities that know this power.
	 * 
	 * @param profile
	 */
	public void onKnowledgeTick(PowerProfile profile) {}
	
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
	 * Called every time a use is incremented to determine whether or not to increase level.
	 * 
	 * @param profile
	 * @return
	 */
	public boolean shouldLevelUp(PowerProfile profile) {
		return profile.getUses() % usesToLevelUp == 0;
	}
	
	/**
	 * Provides general, non-Power Profile dependent description of this power.
	 * 
	 * @return
	 */
	public String getDescription() {
		String info = ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".description")).trim();
		
		if (info.equals(getUnlocalizedName() + ".description")) info = "";
		
		return info;
	}
	
	/**
	 * PowerProfile sensitive version of {@link Power#getDescription()}
	 * 
	 * Implementation of this method is up to the user.
	 * 
	 * @param profile
	 * @return
	 */
	public String getDescription(PowerProfile profile) {
		return getDescription();
	}
	
	/**
	 * Gets the information to be shown under the "Type" tag on the magic tome.
	 * 
	 * @param stack
	 * @param player
	 * @return
	 */
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) { return null; }
	
	/**
	 * Location for model if the default one is not desired.
	 * 
	 * @deprecated Powers are no longer bound to PowerActivator, and models are no longer applicable.
	 * Use {@link Power#getIcon(PowerProfile)}
	 * @param stack
	 * @param player
	 * @param useRemaining
	 * @return
	 */
	@Deprecated
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) { return null; }
	
	public final ItemStack setPower(ItemStack stack) {
		
		NBTTagCompound nbt = null;
		String power = getUnlocalizedName();
		
		if (!stack.hasTagCompound()) {
			
			nbt = new NBTTagCompound();
			
		} else {
			
			nbt = stack.getTagCompound();
			
		}
		
		if (!PowersRegistry.powerExists(power)) {
			
			PowersAPI.logger().error("Cannot set unregistered power \"" + power + "\"");
			
		} else {
			
			nbt.setString("currentPower", power);
			stack.setTagCompound(nbt);
			
		}
		
		return stack;
		
	}
	
	/**
	 * Power profile sensitive version of {@link Power#getDisplayName()}
	 * 
	 * @param profile
	 * @return
	 */
	public String getDisplayName(PowerProfile profile) {
		return getDisplayName();
	}
	
	public String getDisplayName() {
		return ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".name")).trim();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Power)) return false;
		else {
			Power other = (Power)obj;
			return this.getId() == other.getId();
		}
		
		
	}
	
	/**
	 * Gets the icon to render.
	 * 
	 * @param profile
	 * @return
	 * @deprecated Because I suck at rendering images and switched to text-only.
	 */
	@Deprecated
	public ResourceLocation getIcon(PowerProfile profile) { 
		
		if (texture == null) {
			return defaultTexture;
		} else {
			return texture;
		}
		
	}
	
	/**
	 * Gets additional information to print under the power in the gui.
	 * If this returns null, this line will not be rendered.
	 * This should be much shorter than the description.
	 * 
	 * @param profile
	 * @return
	 */
	public String getInfo(PowerProfile profile) {
		return null;
	}
	
	protected void setUsesToLevelUp(int value) { value = usesToLevelUp; }
	
	public int getUsesToLevelUp() { return usesToLevelUp; }
	
	protected void setPreparationTime(int value) { preparationTime = value; }
	
	public int getPreparationTime(PowerProfile profile) { return preparationTime; }
	
	protected void setMaxLevel(int value) { maxLevel = value; }
	
	public int getMaxLevel(PowerProfile profile) { return maxLevel; }
	
	protected void setMaxFunctionalState(int value) { highestFunctionalState = value; }
	
	public int getMaxFunctionalState(){ return highestFunctionalState; }
	
	/**
	 * Power profile sensitive version of {@link Power#getMaxFunctionalState()}
	 * 
	 * @param profile
	 * @return
	 */
	public int getMaxFunctionalState(PowerProfile profile) { return highestFunctionalState; }
	
	protected void setNegateble(boolean value) { isNegateable = value; }
	
	public boolean isNegateable() { return isNegateable; } 
	
	public Power setVisibility(boolean value) {visibility = value; return this;}
	
	public boolean isVisible() { return visibility; }
	
	public Power setPower(float value) { power = value; return this; }

	public float getPower(float modifier) { return power * modifier; }
	
	protected Power setCooldown(int amount) { cooldown = amount; return this; }
	
	public int getCooldown() { return cooldown; }
	
	public int getCooldown(PowerProfile profile) { return cooldown; }
	
	public Power setUnlocalizedName( String name ) { displayName = name; return this; }
	
	public String getUnlocalizedName() { return "power." + displayName; }
	
	protected Power setDuration(int time) { duration = time; return this; }
	
	public int getDuration() { return duration; }
	
	protected void setMaxConcentrationTime(int time) { maxConcentrationTime = time; }
	
	public int getMaxConcentrationTime() { return maxConcentrationTime; }
	
	public boolean isConcentrationPower() { return maxConcentrationTime > 0; }
	
	public boolean hasPreparationTime() { return preparationTime > 0; }

	public float getBrightness() { return 5.0F; }
	
	/**
	 * Gets the name the power is registered under.
	 * 
	 * @return
	 */
	public String getSimpleName() { return displayName; }
	
	public int getId() {
		return PowersRegistry.getPowerId(this);
	}
	
	public final boolean canUsePower( EntityLivingBase player ) {
		return (PowersEntity.get( player ).getCooldownRemaining( this ) <= 0) || UsefulMethods.isCreativeModePlayerOrNull( player );
	}
	
	public String toString() {
		return getUnlocalizedName();
	}
}
