package com.himself12794.powersapi.entity;

import java.util.List;

import com.himself12794.powersapi.PowersRegistry;
import com.himself12794.powersapi.power.PowerRanged;
import com.himself12794.powersapi.storage.PowersEntity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This is pretty much the same as an EntityThrowable. Biggest difference is
 * that it does not lose speed.
 */
public class EntityPower extends Entity implements IProjectile
{

	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private Block inTile;
	protected boolean inGround;
	public int throwableShake;
	/** The entity that cast this power. */
	private EntityLivingBase thrower;
	public MovingObjectPosition target;
	private String throwerName;
	private int ticksInGround;
	private int ticksInAir;
	protected PowerRanged power;
	protected float modifier = 1.0F;
	public int castState;

	public EntityPower(World worldIn) {
		super( worldIn );
	}

	public EntityPower(World worldIn, EntityLivingBase throwerIn, PowerRanged spell, float modifier) {
		super( worldIn );
		this.power = spell;
		this.modifier = modifier;
		this.thrower = throwerIn;
		// this.setSize(0.25F, 0.25F);
		this.setSize( 10.0F, 10.0F );
		float range = power.getRange();
		Vec3 lv = throwerIn.getLookVec();
		
		setLocationAndAngles( 
				throwerIn.posX, 
				throwerIn.posY + (double) throwerIn.getEyeHeight(), 
				throwerIn.posZ,
				throwerIn.rotationYaw,
				throwerIn.rotationPitch 
		);
		int offset = PowersEntity.get( throwerIn ).getSecondaryPower() == spell ? -1 : 1;
		
		prevPosX = lastTickPosX = posX -= (double) (MathHelper.cos( this.rotationYaw / 180.0F * (float) Math.PI ) * 0.65F * offset);
		prevPosY = lastTickPosY = posY -= 0.25;
		prevPosZ = lastTickPosZ = posZ -= (double) (MathHelper.sin( this.rotationYaw / 180.0F * (float) Math.PI ) * 0.65F * offset);
		
		this.setPosition( this.posX, this.posY, this.posZ );
		
		float f = 0.4F;
		
		this.motionX = (double) (-MathHelper.sin( this.rotationYaw / 180.0F
				* (float) Math.PI )
				* MathHelper.cos( this.rotationPitch / 180.0F * (float) Math.PI ) * f);
		
		this.motionZ = (double) (MathHelper.cos( this.rotationYaw / 180.0F
				* (float) Math.PI )
				* MathHelper.cos( this.rotationPitch / 180.0F * (float) Math.PI ) * f);
		
		this.motionY = (double) (-MathHelper.sin( this.rotationPitch / 180.0F 
				* (float) Math.PI ) * f);
		
		Vec3 eyePos = throwerIn.getPositionVector().addVector(0.0D, throwerIn.getEyeHeight(), 0.0D);
		Vec3 temp = new Vec3(eyePos.xCoord + lv.xCoord * range, eyePos.yCoord + lv.yCoord * range, eyePos.zCoord + lv.zCoord * range);
		
		temp = temp.subtract(eyePos);
		
		this.setThrowableHeading( temp.xCoord, temp.yCoord, temp.zCoord, getVelocity(), 1.0F );
	}

	public void setCastState(int state) {
		this.castState = state;
	}

	protected void entityInit() {}

	/**
	 * Checks if the entity is in range to render by using the past in distance
	 * and comparing it to its average edge length * 64 * renderDistanceWeight
	 * Args: distance
	 */
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		
		if (this.power != null && this.power.shouldRender()) { 
			double d1 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
			d1 *= 64.0D;
			return distance < d1 * d1;
		}
		return false;

	}

	protected float getVelocity() {

		if (power != null) return power.getSpellVelocity();
		return 2.0F;

	}

	protected float getInaccuracy()
	{

		return 0.0F;
	}

	/**
	 * Similar to setArrowHeading, it's point the throwable entity to a x, y, z
	 * direction.
	 * 
	 * @param inaccuracy
	 *            Higher means more error.
	 */
	public void setThrowableHeading(double x, double y, double z,
			float velocity, float inaccuracy)
	{

		float f2 = MathHelper.sqrt_double( x * x + y * y + z * z );
		x /= (double) f2;
		y /= (double) f2;
		z /= (double) f2;
		x += this.rand.nextGaussian() * 0.007499999832361937D
				* (double) inaccuracy;
		y += this.rand.nextGaussian() * 0.007499999832361937D
				* (double) inaccuracy;
		z += this.rand.nextGaussian() * 0.007499999832361937D
				* (double) inaccuracy;
		x *= (double) velocity;
		y *= (double) velocity;
		z *= (double) velocity;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f3 = MathHelper.sqrt_double( x * x + z * z );
		this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2( x, z ) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2( y,
				(double) f3 ) * 180.0D / Math.PI);
		this.ticksInGround = 0;
	}

	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{

		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double( x * x + z * z );
			this.prevRotationYaw = this.rotationYaw = (float) (Math
					.atan2( x, z ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(
					y, (double) f ) * 180.0D / Math.PI);
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {

		if (power != null) {

			power.onUpdate( this );

		} 

		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		super.onUpdate();

		if (this.throwableShake > 0)
		{
			--this.throwableShake;
		}

		if (this.inGround)
		{
			if (this.worldObj.getBlockState(
					new BlockPos( this.xTile, this.yTile, this.zTile ) )
					.getBlock() == this.inTile)
			{
				++this.ticksInGround;

				if (this.ticksInGround == 1200)
				{
					this.setDead();
				}

				return;
			}

			this.inGround = false;
			this.ticksInGround = 0;
			this.ticksInAir = 0;
		}
		else
		{
			++this.ticksInAir;
		}

		Vec3 vec3 = new Vec3( this.posX, this.posY, this.posZ );
		Vec3 vec31 = new Vec3( this.posX + this.motionX, this.posY
				+ this.motionY, this.posZ + this.motionZ );
		MovingObjectPosition movingobjectposition = this.worldObj
				.rayTraceBlocks( vec3, vec31 );
		vec3 = new Vec3( this.posX, this.posY, this.posZ );
		vec31 = new Vec3( this.posX + this.motionX, this.posY + this.motionY,
				this.posZ + this.motionZ );

		if (movingobjectposition != null)
		{
			vec31 = new Vec3( movingobjectposition.hitVec.xCoord,
					movingobjectposition.hitVec.yCoord,
					movingobjectposition.hitVec.zCoord );
		}

		if (!this.worldObj.isRemote)
		{
			Entity entity = null;
			List<?> list = this.worldObj
					.getEntitiesWithinAABBExcludingEntity(
							this,
							this.getEntityBoundingBox()
									.addCoord( this.motionX, this.motionY,
											this.motionZ )
									.expand( 1.0D, 1.0D, 1.0D ) );
			double d0 = 0.0D;
			EntityLivingBase entitylivingbase = this.getThrower();

			for (int j = 0; j < list.size(); ++j)
			{
				Entity entity1 = (Entity) list.get( j );

				if (entity1.canBeCollidedWith()
						&& (entity1 != entitylivingbase || this.ticksInAir >= 5))
				{
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1
							.getEntityBoundingBox().expand( (double) f,
									(double) f, (double) f );
					MovingObjectPosition movingobjectposition1 = axisalignedbb
							.calculateIntercept( vec3, vec31 );

					if (movingobjectposition1 != null)
					{
						double d1 = vec3
								.distanceTo( movingobjectposition1.hitVec );

						if (d1 < d0 || d0 == 0.0D)
						{
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null)
			{
				movingobjectposition = new MovingObjectPosition( entity );
			}
		}

		if (movingobjectposition != null)
		{
			if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
					&& this.worldObj.getBlockState(
							movingobjectposition.getBlockPos() ).getBlock() == Blocks.portal)
			{
				this.setInPortal();
			}
			else
			{
				this.onImpact( movingobjectposition );
			}
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f1 = MathHelper.sqrt_double( this.motionX * this.motionX
				+ this.motionZ * this.motionZ );
		this.rotationYaw = (float) (Math.atan2( this.motionX, this.motionZ ) * 180.0D / Math.PI);

		for (this.rotationPitch = (float) (Math.atan2( this.motionY,
				(double) f1 ) * 180.0D / Math.PI); this.rotationPitch
				- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
		{
			;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
		{
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F)
		{
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
		{
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch
				+ (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw
				+ (this.rotationYaw - this.prevRotationYaw) * 0.2F;
		float f2 = 1.0F;
		float f3 = this.getGravityVelocity();

		if (this.isInWater())
		{
			for (int i = 0; i < 4; ++i)
			{
				float f4 = 0.25F;
				this.worldObj.spawnParticle( EnumParticleTypes.WATER_BUBBLE,
						this.posX - this.motionX * (double) f4, this.posY
								- this.motionY * (double) f4, this.posZ
								- this.motionZ * (double) f4, this.motionX,
						this.motionY, this.motionZ, new int[0] );
			}

			f2 = 0.8F;
		}

		this.motionX *= (double) f2;
		this.motionY *= (double) f2;
		this.motionZ *= (double) f2;
		this.motionY -= (double) f3;
		this.setPosition( this.posX, this.posY, this.posZ );
	}

	/**
	 * Gets the amount of gravity to apply to the thrown entity with each tick.
	 */
	protected float getGravityVelocity()
	{

		return 0.0F;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(MovingObjectPosition movingObject) {

		if (power != null) {
			PowersEntity wrapper = PowersEntity.get( getThrower() );
			
			power.onStrike( worldObj, movingObject, getThrower(), modifier, wrapper.getPowerProfile( power ).getState() );
			wrapper.prevTargetPosPrimary = movingObject;
			setDead();

		} else setDead();
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound)
	{

		tagCompound.setShort( "xTile", (short) this.xTile );
		tagCompound.setShort( "yTile", (short) this.yTile );
		tagCompound.setShort( "zTile", (short) this.zTile );
		ResourceLocation resourcelocation = (ResourceLocation) Block.blockRegistry.getNameForObject( this.inTile );
		tagCompound.setString( "inTile", resourcelocation == null ? "" : resourcelocation.toString() );
		tagCompound.setByte( "shake", (byte) this.throwableShake );
		tagCompound.setByte( "inGround", (byte) (this.inGround ? 1 : 0) );
		if (power != null)
			tagCompound.setInteger( "rangedPower", power.getId() );

		if ((this.throwerName == null || this.throwerName.length() == 0)
				&& this.thrower instanceof EntityPlayer)
		{
			this.throwerName = this.thrower.getName();
		}

		tagCompound.setString( "ownerName", this.throwerName == null ? ""
				: this.throwerName );
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompound)
	{

		this.xTile = tagCompound.getShort( "xTile" );
		this.yTile = tagCompound.getShort( "yTile" );
		this.zTile = tagCompound.getShort( "zTile" );

		if (tagCompound.hasKey( "inTile", 8 ))
		{
			this.inTile = Block.getBlockFromName( tagCompound
					.getString( "inTile" ) );
		}
		else
		{
			this.inTile = Block
					.getBlockById( tagCompound.getByte( "inTile" ) & 255 );
		}

		this.throwableShake = tagCompound.getByte( "shake" ) & 255;
		this.inGround = tagCompound.getByte( "inGround" ) == 1;
		this.power = (PowerRanged) (PowersRegistry.lookupPowerById( tagCompound.getInteger( "powerRanged" ) ) instanceof PowerRanged ? PowersRegistry.lookupPowerById( tagCompound.getInteger( "powerRanged" ) ) : null);
		this.throwerName = tagCompound.getString( "ownerName" );

		if (this.throwerName != null && this.throwerName.length() == 0)
		{
			this.throwerName = null;
		}
	}

	public EntityLivingBase getThrower()
	{

		if (this.thrower == null && this.throwerName != null
				&& this.throwerName.length() > 0)
		{
			this.thrower = this.worldObj
					.getPlayerEntityByName( this.throwerName );
		}

		return this.thrower;
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_)
	{

		BlockPos blockpos = new BlockPos( this.posX, 0.0D, this.posZ );

		if (this.worldObj.isBlockLoaded( blockpos ))
		{
			double d0 = (this.getEntityBoundingBox().maxY - this
					.getEntityBoundingBox().minY) * 0.66D;
			int i = MathHelper.floor_double( this.posY + d0 );
			return this.worldObj.getCombinedLight( blockpos.up( i ), 0 );
		}
		else
		{
			return 0;
		}
	}

	/*public float getBrightness(float p_70013_1_) {

		return power.getBrightness();
	}*/

	public float getTicksInAir() {

		return ticksInAir;
	}

	public float getTicksInGround() {

		return ticksInGround;
	}
}
