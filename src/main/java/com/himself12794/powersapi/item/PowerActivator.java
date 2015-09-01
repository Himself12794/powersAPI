package com.himself12794.powersapi.item;

import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
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

import com.himself12794.powersapi.ModCreativeTabs;
import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.config.Config;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.util.DataWrapper;
import com.himself12794.powersapi.util.Reference;

@SuppressWarnings("unchecked")
public class PowerActivator extends Item {
	
	private final String name = "powerActivator";
	
	public PowerActivator() {
		setMaxStackSize(1);
		setHasSubtypes(true);
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(Reference.MODID + "_" + name);
		if (Config.enablePowerActivator) setCreativeTab(ModCreativeTabs.powersAPI);
	}
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
    	Power power = Power.getPower(stack);
    	
    	if (power != null) {
    		DataWrapper.get( player ).setPrimaryPower( power );
    	}
		
    	/*if (DataWrapper.get( player ).isUsingPower()) return stack;
    	
    	if (spell != null && spell.canUsePower(player)) {
    		
    		if (spell.onPreparePower(stack, world, player)) {
    			
    			if ( spell.isConcentrationPower() ) {
    				
    				if (spell.cast(world, player, stack, 1)) {
    					player.setItemInUse(stack, spell.getMaxConcentrationTime());
    				}
        			
    			} else if (spell.cast(world, player, stack, 1)) {
    	    		
    	    		DataWrapper wrapper = DataWrapper.get( player );
    				if (spell.onFinishedCasting(stack, world, player, wrapper.getPreviousPowerTarget())) spell.triggerCooldown(player);
    				wrapper.removePreviousPowerTarget();
    				
    			}
    		}
    	}*/
    	return stack;
    }
	
	/*@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		
		Power spell = Power.lookupPower(stack);
		if (spell != null) {
			
			if ( count % 4  == 0 ) {
				
				spell.cast(player.worldObj, player, stack, 1);
				
			}
			
		}
	}
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer playerIn, int timeLeft) {
    	Power spell = Power.getPower(stack);
    	if (spell != null) {
    		
    		DataWrapper wrapper = DataWrapper.get( playerIn );
    		if (spell.onFinishedCastingEarly(stack, world, playerIn, timeLeft, wrapper.getPreviousPowerTarget())) spell.triggerCooldown(playerIn);
    		wrapper.removePreviousPowerTarget();
    		
    	}
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
    	Power spell = Power.getPower(stack);
    	
    	if (spell != null) {
    		
    		DataWrapper wrapper = DataWrapper.get( playerIn );
    		if( spell.onFinishedCasting(stack, worldIn, playerIn, wrapper.getPreviousPowerTarget())) spell.triggerCooldown(playerIn);
    		wrapper.removePreviousPowerTarget();
    	}
    	
        return stack;
    }*/
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4){

		Power power = Power.lookupPower(stack);
		float modifier = 1.0F;
    	if (power != null) {
    		
    		String description = power.getInfo( null );
    		String[] lines = description.split( "\\n" );
    		for (final String line : lines) {
				list.add(line);
    		}
    		
			if (!power.getInfo( null ).equals("")) list.add("");
			
			if (power.getTypeDescriptor(stack, player) != null) list.add(EnumChatFormatting.YELLOW + "Type: " + power.getTypeDescriptor(stack, player));
			list.add(EnumChatFormatting.RED + "Power: " + power.getPower());
			list.add(EnumChatFormatting.BLUE + "Cooldown: " + String.format("%.2f",(float)power.getCoolDown() / 20.0F) + "s");
			
			if (power.getDuration() > 0 && power.showDuration(stack, player, par4)) 
				list.add(EnumChatFormatting.GREEN + "Duration: " + String.format("%.2f",(float)power.getDuration() * modifier / 20.0F) + "s");
			else if (power.getDuration() <= -1)
				list.add( EnumChatFormatting.GREEN + "Duration: Until Removed");
			
			int remaining = DataWrapper.get( player ).getCooldownRemaining( power );
			if ( remaining > 0 ) list.add(EnumChatFormatting.GRAY + "Time left: " + String.format("%.2f",(float)remaining / 20.0F ) + "s");
			
		} else list.add("Casts Powers");

    } 
   
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        Map<String, Power> spells = Power.getPowers();
        
        subItems.add(new ItemStack(itemIn));
        
		for (Power spell : spells.values()) {
			if (spell.isVisible()) subItems.add(spell.setPower(new ItemStack(itemIn)));
		}    		
    }
    
    @Override
    public int getItemStackLimit(ItemStack stack) {
        return Power.hasPower(stack) ? 1 : 64;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
    	String name = getUnlocalizedName();
    	Power spell = Power.lookupPower(stack);
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
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack) {
    	if (PowersAPI.proxy.getSide().isClient()) {
        	
    	    EntityPlayer player = Minecraft.getMinecraft().thePlayer;	    	
    	    if (Power.getPower(stack) != null) return Power.hasPower(stack) && DataWrapper.get( player ).getCooldownRemaining(Power.getPower(stack)) > 0;
    	    return false;
    		
    	} else {
    		return false;
    	}
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack) {
    	
    	if (PowersAPI.proxy.getSide().isClient()) {
    		
        	if (!Power.hasPower(stack)) return 1.0D;
        	
        	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        	Power power = Power.getPower(stack);
        	
            return ((double)DataWrapper.get( player ).getCooldownRemaining( power ) ) / (double)power.getCoolDown();
    		
    	}
    	return 2.0D;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect( ItemStack itemStack ){
    	return Power.hasPower(itemStack);    	
    }
    
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
    	if (Power.hasPower(stack) && Power.getPower(stack) != null) return Power.getPower(stack).getModel(stack, player, useRemaining);
    	return null;
    }
	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
}