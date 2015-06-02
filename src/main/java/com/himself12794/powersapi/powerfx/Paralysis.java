package com.himself12794.powersapi.powerfx;

import com.himself12794.powersapi.entity.ai.EntityAIDoNothing;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Paralysis extends PowerEffect {

	Paralysis(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onUpdate(EntityLivingBase entity, int timeLeft, EntityLivingBase caster) {
		
		if (entity instanceof EntityLiving) {
			EntityLiving target = (EntityLiving)entity;
			target.tasks.addTask(1, new EntityAIDoNothing(target));
		} else if (entity instanceof EntityPlayer) {
			
			
			EntityPlayer player = (EntityPlayer)entity;
			entity.setPosition(player.prevPosX, player.prevPosY, player.prevPosZ);
			//entity.prevPosX = entity.lastTickPosX;
			
		}
				
	}
	
	public void onRemoval(EntityLivingBase entity, EntityLivingBase caster){
		
		if (entity instanceof EntityLiving) {
			EntityLiving target = (EntityLiving)entity;
			target.tasks.removeTask(new EntityAIDoNothing(target));
		}  
		
	}
	
	

}
