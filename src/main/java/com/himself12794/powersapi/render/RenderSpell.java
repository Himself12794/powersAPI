package com.himself12794.powersapi.render;

import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.himself12794.powersapi.entity.EntitySpell;

public class RenderSpell extends RenderFireball {

	public RenderSpell(RenderManager p_i46176_1_, float p_i46176_2_) {

		super( p_i46176_1_, p_i46176_2_ );
		// TODO Auto-generated constructor stub
	}

	protected ResourceLocation func_180556_a(EntitySpell p_180556_1_)
	{

		return TextureMap.locationBlocksTexture;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity entity) {

		return this.func_180556_a( (EntitySpell) entity );
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker
	 * function which does the actual work. In all probabilty, the class Render
	 * is generic (Render<T extends Entity>) and this method has signature
	 * public void func_76986_a(T entity, double d, double d1, double d2, float
	 * f, float f1). But JAD is pre 1.5 so doe
	 */
	public void doRender(Entity entity, double x, double y, double z,
			float p_76986_8_, float partialTicks) {

		this.doRender( (EntitySpell) entity, x, y, z, p_76986_8_,
				partialTicks );
	}

}
