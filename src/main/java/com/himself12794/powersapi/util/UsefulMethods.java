package com.himself12794.powersapi.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

import com.google.common.base.Predicate;
import com.himself12794.powersapi.power.PowerEffect;

public class UsefulMethods {

	/**
	 * Gets block at world position.
	 * 
	 * @param pos
	 *            The position of the block
	 * @param worldIn
	 *            The world where the block is located
	 * @return The block at location {@code pos} in world {@code worldIn}
	 */
	public static Block getBlockAtPos(BlockPos pos, World worldIn) {

		return worldIn.getBlockState( pos ).getBlock();
	}

	public static void playTameEffect(boolean outcome, Entity entity) {

		EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

		if (!outcome)
		{
			enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
		}

		for (int i = 0; i < 7; ++i)
		{
			double d0 = entity.worldObj.rand.nextGaussian() * 0.02D;
			double d1 = entity.worldObj.rand.nextGaussian() * 0.02D;
			double d2 = entity.worldObj.rand.nextGaussian() * 0.02D;
			entity.worldObj
					.spawnParticle(
							enumparticletypes,
							entity.posX
									+ (double) (entity.worldObj.rand
											.nextFloat() * entity.width * 2.0F)
									- (double) entity.width,
							entity.posY
									+ 0.5D
									+ (double) (entity.worldObj.rand
											.nextFloat() * entity.height),
							entity.posZ
									+ (double) (entity.worldObj.rand
											.nextFloat() * entity.width * 2.0F)
									- (double) entity.width, d0, d1, d2,
							new int[0] );
		}
	}

	public static void removeOneCurrentItem(EntityPlayer player) {

		ItemStack itemstack = player.getItemInUse();
		if (!player.capabilities.isCreativeMode && player.worldObj.isRemote)
		{
			--itemstack.stackSize;
		}

		if (itemstack.stackSize <= 0)
		{
			player.inventory.setInventorySlotContents(
					player.inventory.currentItem, (ItemStack) null );
		}
	}

	public static boolean isCurrentItem(EntityPlayer player, Item item) {

		return player.inventory.getCurrentItem() != null ? player.inventory
				.getCurrentItem().getItem() == item : false;
	}

	public static boolean hasEquipped(EntityLivingBase entity, Item item) {

		ItemStack[] aitemstack = entity.getInventory();
		for (int i = 0; i < aitemstack.length; i++)
			if (aitemstack[i] != null && aitemstack[i].getItem() == item
					&& aitemstack[i].getItem() instanceof ItemArmor) {
				return true;
			}
		return false;
	}

	public static MovingObjectPosition getMouseOverExtended(float dist) {

		// System.out.println("Getting the position");

		Minecraft mc = FMLClientHandler.instance().getClient();
		Entity theRenderViewEntity = mc.getRenderViewEntity();
		AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(
				theRenderViewEntity.posX - 0.5D,
				theRenderViewEntity.posY - 0.0D,
				theRenderViewEntity.posZ - 0.5D,
				theRenderViewEntity.posX + 0.5D,
				theRenderViewEntity.posY + 1.5D,
				theRenderViewEntity.posZ + 0.5D
				);

		MovingObjectPosition returnMOP = null;
		if (mc.theWorld != null)
		{
			double var2 = dist;
			returnMOP = theRenderViewEntity.rayTrace( var2, 0 );
			double calcdist = var2;
			Vec3 pos = theRenderViewEntity.getPositionEyes( 0 );
			var2 = calcdist;
			if (returnMOP != null)
			{
				calcdist = returnMOP.hitVec.distanceTo( pos );
			}

			Vec3 lookvec = theRenderViewEntity.getLook( 0 );
			Vec3 var8 = pos.addVector( lookvec.xCoord * var2,

					lookvec.yCoord * var2,

					lookvec.zCoord * var2 );
			Entity pointedEntity = null;
			float var9 = 1.0F;
			@SuppressWarnings("unchecked")
			List<Entity> list = mc.theWorld
					.getEntitiesWithinAABBExcludingEntity(

							theRenderViewEntity,

							theViewBoundingBox.addCoord(

									lookvec.xCoord * var2,

									lookvec.yCoord * var2,

									lookvec.zCoord * var2 ).expand( var9, var9,
									var9 ) );
			double d = calcdist;

			for (Entity entity : list)
			{
				if (entity.canBeCollidedWith())
				{
					float bordersize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = new AxisAlignedBB(

							entity.posX - entity.width / 2,

							entity.posY,

							entity.posZ - entity.width / 2,

							entity.posX + entity.width / 2,

							entity.posY + entity.height,

							entity.posZ + entity.width / 2 );
					aabb.expand( bordersize, bordersize, bordersize );
					MovingObjectPosition mop0 = aabb.calculateIntercept( pos,
							var8 );

					if (aabb.isVecInside( pos ))
					{
						if (0.0D < d || d == 0.0D)
						{
							pointedEntity = entity;
							d = 0.0D;
						}
					} else if (mop0 != null)
					{
						double d1 = pos.distanceTo( mop0.hitVec );

						if (d1 < d || d == 0.0D)
						{
							pointedEntity = entity;
							d = d1;
						}
					}
				}
			}

			if (pointedEntity != null && (d < calcdist || returnMOP == null))
			{
				returnMOP = new MovingObjectPosition( pointedEntity );
			}

		}
		return returnMOP;
	}

	public static MovingObjectPosition getEntityLookEntity(
			Entity theRenderViewEntity, float dist)
	{

		AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(
				theRenderViewEntity.posX - 0.5D,
				theRenderViewEntity.posY - 0.0D,
				theRenderViewEntity.posZ - 0.5D,
				theRenderViewEntity.posX + 0.5D,
				theRenderViewEntity.posY + 1.5D,
				theRenderViewEntity.posZ + 0.5D
				);
		MovingObjectPosition returnMOP = null;
		if (theRenderViewEntity.worldObj != null)
		{
			double var2 = dist;
			returnMOP = theRenderViewEntity.rayTrace( var2, 0 );
			double calcdist = var2;
			Vec3 pos = theRenderViewEntity.getPositionEyes( 0 );
			var2 = calcdist;
			if (returnMOP != null)
			{
				calcdist = returnMOP.hitVec.distanceTo( pos );
			}

			Vec3 lookvec = theRenderViewEntity.getLook( 0 );
			Vec3 var8 = pos.addVector( lookvec.xCoord * var2,

					lookvec.yCoord * var2,

					lookvec.zCoord * var2 );
			Entity pointedEntity = null;
			float var9 = 1.0F;
			@SuppressWarnings("unchecked")
			List<Entity> list = theRenderViewEntity.worldObj
					.getEntitiesWithinAABBExcludingEntity(

							theRenderViewEntity,

							theViewBoundingBox.addCoord(

									lookvec.xCoord * var2,

									lookvec.yCoord * var2,

									lookvec.zCoord * var2 ).expand( var9, var9,
									var9 ) );
			double d = calcdist;

			for (Entity entity : list)
			{
				if (entity.canBeCollidedWith())
				{
					float bordersize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = new AxisAlignedBB(

							entity.posX - entity.width / 2,

							entity.posY,

							entity.posZ - entity.width / 2,

							entity.posX + entity.width / 2,

							entity.posY + entity.height,

							entity.posZ + entity.width / 2 );
					aabb.expand( bordersize, bordersize, bordersize );
					MovingObjectPosition mop0 = aabb.calculateIntercept( pos,
							var8 );

					if (aabb.isVecInside( pos ))
					{
						if (0.0D < d || d == 0.0D)
						{
							pointedEntity = entity;
							d = 0.0D;
						}
					} else if (mop0 != null)
					{
						double d1 = pos.distanceTo( mop0.hitVec );

						if (d1 < d || d == 0.0D)
						{
							pointedEntity = entity;
							d = d1;
						}
					}
				}
			}

			if (pointedEntity != null && (d < calcdist || returnMOP == null))
			{
				returnMOP = new MovingObjectPosition( pointedEntity );
			}

		}
		return returnMOP;
	}

	public static boolean isArmorSetEquipped(EntityPlayer player,
			ArmorMaterial material) {

		ItemStack[] armor = player.inventory.armorInventory;

		for (int i = 0; i < armor.length; i++) {
			if (armor[i] == null) return false;
			else if (((ItemArmor) armor[i].getItem()).getArmorMaterial() != material) return false;
		}

		return true;
	}

	public static BlockPos getBlockFromSide(BlockPos pos, EnumFacing side) {

		switch (side) {

			case DOWN:
				pos.up();
			case UP:
				pos.down();
			case NORTH:
				pos.south();
			case SOUTH:
				pos.north();
			case EAST:
				pos.west();
			case WEST:
				pos.east();
		}

		return pos;

	}

	public static double distanceAboveGround(EntityLivingBase entity) {

		BlockPos currentLocation = entity.getPosition();
		BlockPos checkedLocation = entity.getPosition();

		for (int y = currentLocation.getY(); y >= 0; y--) {
			checkedLocation = new BlockPos( currentLocation.getX(), y,
					currentLocation.getZ() );
			Block ground = getBlockAtPos( checkedLocation, entity.worldObj );
			if (ground.getMaterial() != Material.air) break;
		}

		return currentLocation.getY() - checkedLocation.getY();

	}

	public static void accelerateEntityTowards(Entity target, double x,
			double y, double z, float velocity) {

		float f2 = MathHelper.sqrt_double( x * x + y * y + z * z );
		x /= (double) f2;
		y /= (double) f2;
		z /= (double) f2;
		x *= (double) velocity;
		y *= (double) velocity;
		z *= (double) velocity;
		target.motionX = x;
		target.motionY = y;
		target.motionZ = z;
		// float f3 = MathHelper.sqrt_double(x * x + z * z);
		// target.prevRotationYaw = target.rotationYaw = (float)(Math.atan2(x,
		// z) * 180.0D / Math.PI);
		// target.prevRotationPitch = target.rotationPitch =
		// (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);

	}
	
	public static void accelerateEntityTowards(Entity target, Entity target2, float velocity) {
		accelerateEntityTowards(target, target2.posX, target2.posY, target2.posZ, velocity);
	}

	public static NBTTagCompound movingObjectPosToNBT(MovingObjectPosition pos) {

		NBTTagCompound nbtTagCompound = new NBTTagCompound();

		nbtTagCompound.setInteger( "entityId",
				pos.entityHit != null ? pos.entityHit.getEntityId()
						: Integer.MIN_VALUE );
		nbtTagCompound.setDouble( "vecX", pos.hitVec.xCoord );
		nbtTagCompound.setDouble( "vecY", pos.hitVec.yCoord );
		nbtTagCompound.setDouble( "vecZ", pos.hitVec.zCoord );
		nbtTagCompound.setInteger( "sideHit",
				pos.sideHit != null ? pos.sideHit.getIndex() : 0 );
		nbtTagCompound.setInteger( "subHit", pos.subHit );
		nbtTagCompound.setIntArray( "blockPos",
				pos.getBlockPos() != null ? new int[] {
						pos.getBlockPos().getX(), pos.getBlockPos().getY(),
						pos.getBlockPos().getZ() } : new int[3] );
		nbtTagCompound.setString( "typeOfHit", pos.typeOfHit.name() );

		return nbtTagCompound;
	}

	/**
	 * Constructs MovingObjectPosition from NBTTagCompound generated by
	 * {@link UsefulMethods#movingObjectPosToNBT(MovingObjectPosition)} If world
	 * is null, entity cannot be set.
	 * 
	 * @param nbtTagCompound
	 * @param world
	 * @return
	 */
	public static MovingObjectPosition movingObjectPositionFromNBT(
			NBTTagCompound nbtTagCompound, World world) {

		Vec3 vec = new Vec3(
				nbtTagCompound.getDouble( "vecX" ),
				nbtTagCompound.getDouble( "vecY" ),
				nbtTagCompound.getDouble( "vecZ" ) );

		EnumFacing sideHit = EnumFacing.getFront( nbtTagCompound
				.getInteger( "sideHit" ) );
		int subHit = nbtTagCompound.getInteger( "subHit" );

		int[] c = nbtTagCompound.getIntArray( "blockPos" );
		if (c.length < 3) c = new int[] { 0, 0, 0 };
		BlockPos blockPos = new BlockPos( c[0], c[1], c[2] );

		MovingObjectType typeOfHit = MovingObjectType.MISS;
		for (MovingObjectType mot : MovingObjectType.values()) {
			if (mot.name().equals( nbtTagCompound.getString( "typeOfHit" ) )) typeOfHit = mot;
		}

		int entityId = nbtTagCompound.getInteger( "entityId" );
		Entity entityHit = !(entityId == Integer.MIN_VALUE && entityId == 0 && world == null) ? world
				.getEntityByID( entityId )
				: null;

		MovingObjectPosition pos = new MovingObjectPosition( vec, sideHit,
				blockPos );
		pos.entityHit = entityHit;
		pos.subHit = subHit;
		pos.typeOfHit = typeOfHit;

		return pos;
	}
	
	public static List<EntityLivingBase> getEntitiesWithEffect(final World  world, final PowerEffect effect) {
		
		List<EntityLivingBase> entities = world.getEntities(EntityLivingBase.class, new Predicate(){
			
			@Override
			public boolean apply(Object input) {
				
				if (input instanceof EntityLivingBase) {
					return DataWrapper.get( (EntityLivingBase)input).isAffectedBy( effect );
				}
				return false;
			}
			
		});
		
		return entities;
		
	}
	
	public static boolean isCreativeModePlayerOrNull(Entity player) {
		return player instanceof EntityPlayer ? ((EntityPlayer)player).capabilities.isCreativeMode : player == null;
	}
	
	public static boolean selfOrCreative(Entity affected, Entity effector) {
		
		if (affected == null && effector == null) return false;
		
		if (affected == effector) {
			return true;
		} else if (effector instanceof EntityPlayer) {
			return UsefulMethods.isCreativeModePlayerOrNull( (EntityLivingBase) effector );
		}
		
		return false;
	}
	
	public static boolean canTeachPower(Entity entity) {
		
		return entity == null ? true : (entity instanceof EntityPlayer ? ((EntityPlayer)entity).capabilities.isCreativeMode : true);
		
	}
	
	public static boolean nullOrEmptyString(String value) {
		return value.equals( "" ) || value == null;
	}
}
