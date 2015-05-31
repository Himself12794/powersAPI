package com.himself12794.powersapi.entity.ai;

import com.himself12794.powersapi.powerfx.SpellEffect;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIDoNothing extends EntityAIBase {

	
	private EntityLiving entityLiving;
    public EntityAIDoNothing(EntityLiving entity)
    {
        entityLiving = entity;
        this.setMutexBits(1);
    }

	@Override
	public boolean shouldExecute() {
		
		return SpellEffect.paralysis.isEffecting(entityLiving);
		
	}
	
    public void updateTask() {

    	//entityLiving.moveForward = 0;
    	//entityLiving.moveStrafing = 0;
    	entityLiving.getNavigator().clearPathEntity();
    	
    }

}
