package com.himself12794.powersapi.util;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

import com.google.common.base.Predicate;
import com.himself12794.powersapi.power.PowerEffect;
import com.himself12794.powersapi.storage.PowersEntity;

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

	public static MovingObjectPosition getMouseOverExtended(float dist) {

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

	public static BlockPos getBlockFromSide(BlockPos pos, EnumFacing side) {

		switch (side) {

			case DOWN:
				return pos.up();
			case UP:
				return pos.down();
			case NORTH:
				return pos.south();
			case SOUTH:
				return pos.north();
			case EAST:
				return pos.west();
			case WEST:
				return pos.east();
		}

		return pos;

	}

	public static BlockPos getBlockFromSideSwap(BlockPos pos, EnumFacing side) {

		switch (side) {

			case DOWN:
				return pos.down();
			case UP:
				return pos.up();
			case NORTH:
				return pos.north();
			case SOUTH:
				return pos.south();
			case EAST:
				return pos.east();
			case WEST:
				return pos.west();
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

		if (pos == null) return nbtTagCompound;
		
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

		if (nbtTagCompound == null) return null;
		
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
		Entity entityHit = !(entityId == Integer.MIN_VALUE && world == null) ? world.getEntityByID( entityId )
				: null;

		MovingObjectPosition pos = new MovingObjectPosition( vec, sideHit,
				blockPos );
		pos.entityHit = entityHit;
		pos.subHit = subHit;
		pos.typeOfHit = typeOfHit;

		return pos;
	}
	
	/**
	 * Gets a list of entities that have the specified effect. Includes players and non-players.
	 * 
	 * @param world
	 * @param effect
	 * @return
	 */
	public static List<EntityLivingBase> getEntitiesWithEffect(final World  world, final PowerEffect effect) {
		
		Predicate filter = new Predicate(){
			
			@Override
			public boolean apply(Object input) {
				
				if (input instanceof EntityLivingBase) {
					return PowersEntity.get( (EntityLivingBase)input).getPowerEffectsData().isAffectedBy( effect );
				}
				return false;
			}
			
		};
		
		List<EntityLivingBase> entities = world.getEntities(EntityLivingBase.class, filter );
		
		entities.addAll( world.getPlayers( EntityPlayer.class, filter ) );
		
		return entities;
		
	}
	
	public static Entity getEntityFromPersistentId(World world, String id, Class entity) {
		try {
			final UUID uuid = UUID.fromString( id );
			
			Entity found = world.getPlayerEntityByUUID( uuid );
			
			if (found != null) return found;
			
			List entities = world.getEntities( entity, new Predicate() {

				@Override
				public boolean apply(Object input) {
					if (input instanceof Entity) {

						return uuid.equals( ((Entity)input).getPersistentID() );
					}
					return false;
				}
				
			});
			
			if (!entities.isEmpty()) {
				found = (Entity) entities.get( 0 );
			}
			
			return found;
		} catch (IllegalArgumentException e) {
			// No action needed
		}
		return null;
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
		return value == null ? false : !value.equals( "" );
	}
	
	public static NBTTagCompound getPutKeyCompound(String key, NBTTagCompound tag) {
		
		NBTTagCompound result = null;
		
		if (tag.hasKey( key, 10 )) {
			result = tag.getCompoundTag( key );
		} else {
			result = new NBTTagCompound();
			tag.setTag( key, result );
		}
		
		return result;
		
	}
	
}
