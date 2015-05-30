package com.himself12794.powersapi.items;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.spell.Spell;
import com.himself12794.powersapi.util.Reference;

public class MagicTome extends Item {
	private final String name = "magicTome";
	
	public MagicTome() {
		setMaxStackSize(1);
		setHasSubtypes(true);
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(Reference.MODID + "_" + name);
		setCreativeTab(PowersAPI.powersAPI);
	}
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
    	Spell spell = Spell.getSpell(stack);
		
    	if (spell != null && spell.canCastSpell(player)) {
    		
    		//UsefulThings.print("Casting spell: " + spell.getDisplayName());
    		
    		if (spell.onPrepareSpell(stack, world, player)) {
    			
    			if ( spell.isConcentrationSpell() ) {
    				
    				if (spell.cast(world, player, stack, 1)) 
    					player.setItemInUse(stack, spell.getDuration());
        			
    			} else if (spell.cast(world, player, stack, 1)) {
    				//UsefulThings.print("The spell was successfully cast.");
    				if (spell.onFinishedCasting(stack, world, player)) spell.triggerCooldown(player);
    				//UsefulThings.print("Cooldown is: " + Spells.getCoolDownRemaining(stack) );
    			}
    		}
    	}
    	return stack;
    }
	
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		
		Spell spell = Spell.lookupSpell(stack);
		if (spell != null) {
			
			if ( count % 4 == 0 ) {
				
				spell.cast(player.worldObj, player, stack, 1);
				
			}
			
		}
	}
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer playerIn, int timeLeft) {
    	Spell spell = Spell.getSpell(stack);
    	if (spell != null) {
    		
    		if (spell.onFinishedCastingEarly(stack, world, playerIn, timeLeft)) spell.triggerCooldown(playerIn);
    		
    	}
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
    	Spell spell = Spell.getSpell(stack);
    	
    	if (spell != null) {
    		if( spell.onFinishedCasting(stack, worldIn, playerIn)) spell.triggerCooldown(playerIn);
    		
    	}
    	
        return stack;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4){

		Spell spell = Spell.lookupSpell(stack);
		float modifier = 1.0F;
    	if (spell != null) {
    		
			list.add(spell.getInfo(stack, player));
			if (!spell.getInfo(stack, player).equals("")) list.add("");
			
			if (spell.getTypeDescriptor(stack, player) != null) list.add(EnumChatFormatting.YELLOW + "Type: " + spell.getTypeDescriptor(stack, player));
			list.add(EnumChatFormatting.RED + "Power: " + spell.getPower());
			list.add(EnumChatFormatting.BLUE + "Cooldown: " + String.format("%.2f",(float)spell.getCoolDown() / 20.0F) + "s");
			
			if (spell.getDuration() > 0 && spell.showDuration(stack, player, par4)) 
				list.add(EnumChatFormatting.GREEN + "Duration: " + String.format("%.2f",(float)spell.getDuration() * modifier / 20.0F) + "s");
			
			int remaining = spell.getCoolDownRemaining(player);
			if ( remaining > 0 ) list.add(EnumChatFormatting.GRAY + "Time left: " + String.format("%.2f",(float)remaining / 20.0F ) + "s");
			
		} else list.add("Casts Spells");

    } 
   
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        Map<String, Spell> spells = Spell.getSpells();

		for (Entry<String, Spell> spell : spells.entrySet()) {
			subItems.add(spell.getValue().setSpell(new ItemStack(itemIn)));
		}    		
    }
    
    @Override
    public int getItemStackLimit(ItemStack stack) {
        return Spell.hasSpell(stack) ? 1 : 64;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
    	String name = getUnlocalizedName();
    	Spell spell = Spell.lookupSpell(stack);
    	if (spell != null) {
    		//name += ".spell." + spell.getUnlocalizedName();
    		//name = spell.getDisplayName();
    		name = spell.getUnlocalizedName();
    	}
        return name;
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.BLOCK; }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
    	return 72000;
    }
    
    @Override
    //@SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack) {    	
	    return PowersAPI.proxy.showDamage(stack);
    }
    
    @Override
    //@SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack) {
    	return PowersAPI.proxy.getReverseRendering(stack);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect( ItemStack itemStack ){
    	return Spell.hasSpell(itemStack);    	
    }
    
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
    	if (Spell.hasSpell(stack) && Spell.getSpell(stack) != null) return Spell.getSpell(stack).getModel(stack, player, useRemaining);
    	return null;
    }
	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
}