package com.himself12794.powersapi.item;

import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.himself12794.powersapi.PowersAPI;
import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.power.Power;
import com.himself12794.powersapi.storage.PowerProfile;
import com.himself12794.powersapi.storage.PowersEntity;
import com.himself12794.powersapi.util.Reference;

/**
 * Was used to cast powers, now used to simply teach players the power. No longer registered,
 * however, and doesn't exist in the game.  
 * 
 * @author Himself12794
 *
 */
@SuppressWarnings("deprecation")
@Deprecated
public class PowerActivator extends Item {
	
	private final String name = "powerActivator";
	
	public PowerActivator() {
		setMaxStackSize(1);
		setHasSubtypes(true);
		setUnlocalizedName(Reference.MODID + "_" + name);
	}
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
    	Power power = PowersRegistry.getPower(stack);
    	
    	if (power != null) {
    		PowersEntity.get( player ).teachPower( power );
    	}
    	return stack;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4){

		Power power = PowersRegistry.lookupPower(stack);
		float modifier = 1.0F;
    	if (power != null) {
    		
    		PowerProfile profile = PowersEntity.get( player ).getPowerProfile( power );
    		String description = power.getInfo( null );
    		String[] lines = description.split( "\\n" );
    		for (final String line : lines) {
				list.add(line);
    		}
    		
			if (!power.getInfo( null ).equals("")) list.add("");
			
			if (power.getTypeDescriptor(stack, player) != null) list.add(EnumChatFormatting.YELLOW + "Type: " + power.getTypeDescriptor(stack, player));
			list.add(EnumChatFormatting.RED + "Power: " + power.getPower(modifier));
			list.add(EnumChatFormatting.BLUE + "Cooldown: " + String.format("%.2f",(float)power.getCooldown(profile) / 20.0F) + "s");
			
			if (power.getDuration() > 0 && power.showDuration(stack, player, par4)) 
				list.add(EnumChatFormatting.GREEN + "Duration: " + String.format("%.2f",(float)power.getDuration() * modifier / 20.0F) + "s");
			else if (power.getDuration() <= -1)
				list.add( EnumChatFormatting.GREEN + "Duration: Until Removed");
			
			int remaining = PowersEntity.get( player ).getCooldownRemaining( power );
			if ( remaining > 0 ) list.add(EnumChatFormatting.GRAY + "Time left: " + String.format("%.2f",(float)remaining / 20.0F ) + "s");
			
		} else list.add("Casts Powers");

    } 
   
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        Map<String, Power> spells = PowersRegistry.getPowers();
        
        subItems.add(new ItemStack(itemIn));
        
		for (Power spell : spells.values()) {
			if (spell.isVisible()) subItems.add(spell.setPower(new ItemStack(itemIn)));
		}    		
    }
    
    @Override
    public int getItemStackLimit(ItemStack stack) {
        return PowersRegistry.hasPower(stack) ? 1 : 64;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
    	
    	String name = getUnlocalizedName();
    	Power spell = PowersRegistry.lookupPower(stack);
    	
    	if (spell != null) {
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
    	if (PowersAPI.proxy().getSide().isClient()) {
        	
    	    EntityPlayer player = Minecraft.getMinecraft().thePlayer;	    	
    	    if (PowersRegistry.getPower(stack) != null) return PowersRegistry.hasPower(stack) && PowersEntity.get( player ).getCooldownRemaining(PowersRegistry.getPower(stack)) > 0;
    	    return false;
    		
    	} else {
    		return false;
    	}
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack) {
    	
    	if (PowersAPI.proxy().getSide().isClient()) {
    		
        	if (!PowersRegistry.hasPower(stack)) return 1.0D;
        	
        	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        	Power power = PowersRegistry.getPower(stack);
        	PowerProfile profile = PowersEntity.get( player ).getPowerProfile( power );
        	
            return ((double)PowersEntity.get( player ).getCooldownRemaining( power ) ) / (double)power.getCooldown(profile);
    		
    	}
    	return 2.0D;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect( ItemStack itemStack ){
    	return PowersRegistry.hasPower(itemStack);    	
    }
	
	public String getName() {
		return name;
	}
}