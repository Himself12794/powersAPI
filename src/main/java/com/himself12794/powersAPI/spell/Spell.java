package com.himself12794.powersAPI.spell;

import java.util.Map;

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
import com.himself12794.powersAPI.PowersAPI;
import com.himself12794.powersAPI.util.Reference;

public abstract class Spell {
	
	private String displayName;
	//private SpellType type = SpellType.INSTANT;
	private float power = 2.0F;
	/**Duration in ticks*/
	private int duration = 0;
	private int coolDown = 4;
	private int maxConcentrationTime = 0;
	
	/**
	 * This determines how the spell is cast, then casts it.
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
	 * Called when the spell affects a target.
	 * For instances of SpellInstant, when a target has been successfully acquired, and for instances of
	 * SpellRanged when it impacts anything. 
	 * <p>
	 * For instances of SpellInstant, returning true will mark the spell as a success, allowing the cooldown to be triggered.
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
			//UsefulThings.print("Attacking " + target.entityHit.getName());
			flag = target.entityHit.attackEntityFrom(DamageSource.magic, getPower() * modifier);
			((EntityLivingBase)target.entityHit).setLastAttacker(caster);
		} 
		return flag;
	}
	
	/**
	 * Called when the spell is cast.
	 * Determines how the spell affects the player and world when cast.
	 * Return whether or not a successful use occurred. Cooldown only occurs if both are successful.
	 * 
	 * @param world
	 * @param caster
	 * @param modifier
	 * @param stack
	 * @return whether or not the spell counts as successful, and should count as a use
	 */
	public boolean onCast(World world, EntityLivingBase caster, ItemStack stack, float modifier) {
		return true;
	}
	
	/**
	 * The action to be performed when the spell is being prepared, before it is actually cast.
	 * <p>
	 * This is used primarily to check if the player should be allowed to cast the spell or not.
	 * 
	 * @return whether or not casting should continue.
	 */
	public boolean onPrepareSpell(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		return true;
	}
	
	/**
	 * Called when spell is aborted before duration is over.
	 * Return false prevent the cooldown
	 * 
	 * @param stack
	 * @param world
	 * @param playerIn
	 * @param timeLeft
	 * @return whether or not to cancel the cooldown
	 */
	public boolean onFinishedCastingEarly(ItemStack stack, World world, EntityPlayer playerIn, int timeLeft) {
		return true;
	}
	
	/**
	 * Called when spell is done being cast, before the cool down is triggered.
	 * Return false to negate the cool down.
	 * 
	 * @param stack
	 * @param world
	 * @param caster
	 * @return whether or not to negate the cool down
	 */
	public boolean onFinishedCasting(ItemStack stack, World world, EntityPlayer caster){ 
		return true;
	}
	
	/**
	 * Determines whether or not spells that have a duration should show this on the tooltip.
	 * 
	 * @param stack
	 * @param caster
	 * @param par3 whether or not advanced tooltips are enabled
	 * @return
	 */
	public boolean showDuration(ItemStack stack, EntityPlayer caster, boolean par3) {
		return true;
	}
	
	
	/**Gets the spell description.
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
	public String getTypeDescriptor(ItemStack stack, EntityPlayer player) {
		return null;
	}
	
	/**
	 * Location for model if the default Magic Tome one is not desired.
	 * 
	 * @param stack
	 * @param player
	 * @param useRemaining
	 * @return
	 */
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
		return null;
	}
	
	public final ItemStack setSpell(ItemStack stack) {
		NBTTagCompound nbt = null;
		String spell = getUnlocalizedName();
		if (!stack.hasTagCompound()) {
			nbt = new NBTTagCompound();
		} else {
			nbt = stack.getTagCompound();
		}
		if (!spellExists(spell)) {
			PowersAPI.logger.fatal("Cannot set unregistered spell \"" + spell + "\"");
		} else {
			nbt.setString(Reference.MODID + ".spell.currentSpell", spell);
			stack.setTagCompound(nbt);
		}
		return stack;
	}
	
	public final boolean isSpellOnStack(ItemStack stack) {
		return Spell.hasSpell(stack) && lookupSpell(stack) == this;
	}
	
	public String getDisplayName() {
		return ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".name")).trim();
	}
	
	protected Spell setPower(float value) { power = value; return this; }

	public float getPower() { return power; }
	
	protected Spell setCoolDown(int amount) { coolDown = amount; return this; }
	
	public int getCoolDown() { return coolDown; }
	
	protected Spell setUnlocalizedName( String name ) { displayName = name; return this; }
	
	public String getUnlocalizedName() { return "spell." + displayName; }
	
	protected Spell setDuration(int time) { duration = time; return this; }
	
	public int getDuration() { return duration; }
	
	protected void setMaxConcentrationTime(int time) {maxConcentrationTime = time;}
	
	public int getMaxConcentrationTime() {return maxConcentrationTime;}
	
	public boolean isConcentrationSpell() {return maxConcentrationTime > 0;}

	public float getBrightness() { return 5.0F; }
	
	/*================================= Begin Spell Registration Section ===============================*/ 
	
	private static Map<String, Spell> spellRegistry = Maps.newHashMap();
	private static int spells = 0;
	
	public static void registerSpells() {

		registerSpell(new SpellInstant().setUnlocalizedName("damage"));	
		registerSpell(new SpellInstant().setUnlocalizedName("death").setDuration(100).setPower(1000.0F).setCoolDown(178));	
		registerSpell(new Incinerate());
		registerSpell(new Lightning());
		registerSpell(new Heal());
		registerSpell(new Dummy());
		registerSpell(new Immortalize());
		registerSpell(new Flames());
		registerSpell(new DummyHoming());
		
		PowersAPI.logger.info("Registered [" + Spell.getSpellCount() + "] spells");
		
	}
	
	private static void registerSpell(Spell spell) {
		String name = spell.getUnlocalizedName();
		if (!Spell.spellExists(name)) {
			spellRegistry.put(name, spell);
			//UsefulThings.print("Registered spell " + name);
			++spells;
		} else {
			PowersAPI.logger.error("Could not register spell " + spell + " under name \"" + name + "\", name has already been registered for " + lookupSpell(name));
		}
	}
	
	public static Spell lookupSpell(ItemStack stack) {
		if (Spell.hasSpell(stack)) {
			return spellRegistry.get(stack.getTagCompound().getString( Reference.MODID + ".spell.currentSpell"));
		}
		return null;
	}
	
	public static Spell lookupSpell(String spell) {
		if (Spell.spellExists(spell)) return (Spell)spellRegistry.get(spell);
		return null;
	}
	
	public static Map<String, Spell> getSpells() {
		return spellRegistry;
	}

	public static int getSpellCount() {
		return spells;
	}
	
	public static boolean spellExists(String unlocalizedName) {
		return Spell.getSpells().containsKey(unlocalizedName);
	}
	
	public final boolean canCastSpell( EntityPlayer player ) {
		
		NBTTagCompound coolDowns = player.getEntityData().getCompoundTag(Reference.MODID + ".spell.spellCooldowns");
		
		return coolDowns.getInteger(getUnlocalizedName()) <= 0;
	}
	
	public final int getCoolDownRemaining(EntityPlayer player) {
		NBTTagCompound coolDowns = player.getEntityData().getCompoundTag(Reference.MODID + ".spell.spellCooldowns");
		
		return coolDowns.getInteger(getUnlocalizedName());
	}
	
	public final void setCoolDown(EntityPlayer player, int amount) {
		int id = player.getEntityId();
		NBTTagCompound coolDowns = player.getEntityData().getCompoundTag(Reference.MODID + ".spell.spellCooldowns");
		
		coolDowns.setInteger(getUnlocalizedName(), amount);
		player.getEntityData().setTag(Reference.MODID + ".spell.spellCooldowns", coolDowns);
	}
	
	public final void triggerCooldown( EntityPlayer player ) {
		//UsefulThings.print("Triggering cooldown");
		if (!player.capabilities.isCreativeMode) setCoolDown(player, getCoolDown());
	}
	
	public static boolean hasSpell(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(Reference.MODID + ".spell.currentSpell");
	}
	
	public static Spell getSpell(ItemStack stack) {
		return Spell.lookupSpell(stack);
	}
	
	public static NBTTagCompound getCooldowns(EntityPlayer player) {
		return player.getEntityData().getCompoundTag(Reference.MODID + ".spell.spellCooldowns");
	}	
}
