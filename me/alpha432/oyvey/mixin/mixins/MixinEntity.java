package me.alpha432.oyvey.mixin.mixins;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import me.alpha432.oyvey.event.events.PushEvent;
import me.alpha432.oyvey.event.events.StepEvent;
import me.alpha432.oyvey.features.modules.misc.BetterPortals;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Entity.class})
public abstract class MixinEntity {
  @Shadow
  public double posX;
  
  @Shadow
  public double posY;
  
  @Shadow
  public double posZ;
  
  @Shadow
  public double motionX;
  
  @Shadow
  public double motionY;
  
  @Shadow
  public double motionZ;
  
  @Shadow
  public float rotationYaw;
  
  @Shadow
  public float rotationPitch;
  
  @Shadow
  public boolean onGround;
  
  @Shadow
  public boolean noClip;
  
  @Shadow
  public float prevDistanceWalkedModified;
  
  @Shadow
  public World world;
  
  @Shadow
  @Final
  private double[] pistonDeltas;
  
  @Shadow
  private long pistonDeltasGameTime;
  
  @Shadow
  protected boolean isInWeb;
  
  @Shadow
  public float stepHeight;
  
  @Shadow
  public boolean collidedHorizontally;
  
  @Shadow
  public boolean collidedVertically;
  
  @Shadow
  public boolean collided;
  
  @Shadow
  public float distanceWalkedModified;
  
  @Shadow
  public float distanceWalkedOnStepModified;
  
  @Shadow
  private int fire;
  
  @Shadow
  private int nextStepDistance;
  
  @Shadow
  private float nextFlap;
  
  @Shadow
  protected Random rand;
  
  @Shadow
  public abstract boolean isSprinting();
  
  @Shadow
  public abstract boolean isRiding();
  
  @Shadow
  public abstract boolean isSneaking();
  
  @Shadow
  public abstract void setEntityBoundingBox(AxisAlignedBB paramAxisAlignedBB);
  
  @Shadow
  public abstract AxisAlignedBB getEntityBoundingBox();
  
  @Shadow
  public abstract void resetPositionToBB();
  
  @Shadow
  protected abstract void updateFallState(double paramDouble, boolean paramBoolean, IBlockState paramIBlockState, BlockPos paramBlockPos);
  
  @Shadow
  protected abstract boolean canTriggerWalking();
  
  @Shadow
  public abstract boolean isInWater();
  
  @Shadow
  public abstract boolean isBeingRidden();
  
  @Shadow
  public abstract Entity getControllingPassenger();
  
  @Shadow
  public abstract void playSound(SoundEvent paramSoundEvent, float paramFloat1, float paramFloat2);
  
  @Shadow
  protected abstract void doBlockCollisions();
  
  @Shadow
  public abstract boolean isWet();
  
  @Shadow
  protected abstract void playStepSound(BlockPos paramBlockPos, Block paramBlock);
  
  @Shadow
  protected abstract SoundEvent getSwimSound();
  
  @Shadow
  protected abstract float playFlySound(float paramFloat);
  
  @Shadow
  protected abstract boolean makeFlySound();
  
  @Shadow
  public abstract void addEntityCrashInfo(CrashReportCategory paramCrashReportCategory);
  
  @Shadow
  protected abstract void dealFireDamage(int paramInt);
  
  @Shadow
  public abstract void setFire(int paramInt);
  
  @Shadow
  protected abstract int getFireImmuneTicks();
  
  @Shadow
  public abstract boolean isBurning();
  
  @Shadow
  public abstract int getMaxInPortalTime();
  
  @Overwrite
  public void move(MoverType type, double x, double y, double z) {
    Entity _this = (Entity)this;
    if (this.noClip) {
      setEntityBoundingBox(getEntityBoundingBox().offset(x, y, z));
      resetPositionToBB();
    } else {
      if (type == MoverType.PISTON) {
        long i = this.world.getTotalWorldTime();
        if (i != this.pistonDeltasGameTime) {
          Arrays.fill(this.pistonDeltas, 0.0D);
          this.pistonDeltasGameTime = i;
        } 
        if (x != 0.0D) {
          int j = EnumFacing.Axis.X.ordinal();
          double d0 = MathHelper.clamp(x + this.pistonDeltas[j], -0.51D, 0.51D);
          x = d0 - this.pistonDeltas[j];
          this.pistonDeltas[j] = d0;
          if (Math.abs(x) <= 9.999999747378752E-6D)
            return; 
        } else if (y != 0.0D) {
          int l4 = EnumFacing.Axis.Y.ordinal();
          double d12 = MathHelper.clamp(y + this.pistonDeltas[l4], -0.51D, 0.51D);
          y = d12 - this.pistonDeltas[l4];
          this.pistonDeltas[l4] = d12;
          if (Math.abs(y) <= 9.999999747378752E-6D)
            return; 
        } else {
          if (z == 0.0D)
            return; 
          int i5 = EnumFacing.Axis.Z.ordinal();
          double d13 = MathHelper.clamp(z + this.pistonDeltas[i5], -0.51D, 0.51D);
          z = d13 - this.pistonDeltas[i5];
          this.pistonDeltas[i5] = d13;
          if (Math.abs(z) <= 9.999999747378752E-6D)
            return; 
        } 
      } 
      this.world.profiler.startSection("move");
      double d10 = this.posX;
      double d11 = this.posY;
      double d1 = this.posZ;
      if (this.isInWeb) {
        this.isInWeb = false;
        x *= 0.25D;
        y *= 0.05000000074505806D;
        z *= 0.25D;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
      } 
      double d2 = x;
      double d3 = y;
      double d4 = z;
      if ((type == MoverType.SELF || type == MoverType.PLAYER) && this.onGround && isSneaking() && _this instanceof net.minecraft.entity.player.EntityPlayer) {
        double d5 = 0.05D;
        while (x != 0.0D && this.world.getCollisionBoxes(_this, getEntityBoundingBox().offset(x, -this.stepHeight, 0.0D)).isEmpty()) {
          x = (x < 0.05D && x >= -0.05D) ? 0.0D : ((x > 0.0D) ? (x -= 0.05D) : (x += 0.05D));
          d2 = x;
        } 
        while (z != 0.0D && this.world.getCollisionBoxes(_this, getEntityBoundingBox().offset(0.0D, -this.stepHeight, z)).isEmpty()) {
          z = (z < 0.05D && z >= -0.05D) ? 0.0D : ((z > 0.0D) ? (z -= 0.05D) : (z += 0.05D));
          d4 = z;
        } 
        while (x != 0.0D && z != 0.0D && this.world.getCollisionBoxes(_this, getEntityBoundingBox().offset(x, -this.stepHeight, z)).isEmpty()) {
          x = (x < 0.05D && x >= -0.05D) ? 0.0D : ((x > 0.0D) ? (x -= 0.05D) : (x += 0.05D));
          d2 = x;
          z = (z < 0.05D && z >= -0.05D) ? 0.0D : ((z > 0.0D) ? (z -= 0.05D) : (z += 0.05D));
          d4 = z;
        } 
      } 
      List<AxisAlignedBB> list1 = this.world.getCollisionBoxes(_this, getEntityBoundingBox().expand(x, y, z));
      AxisAlignedBB axisalignedbb = getEntityBoundingBox();
      if (y != 0.0D) {
        int l = list1.size();
        for (int k = 0; k < l; k++)
          y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(getEntityBoundingBox(), y); 
        setEntityBoundingBox(getEntityBoundingBox().offset(0.0D, y, 0.0D));
      } 
      if (x != 0.0D) {
        int l5 = list1.size();
        for (int j5 = 0; j5 < l5; j5++)
          x = ((AxisAlignedBB)list1.get(j5)).calculateXOffset(getEntityBoundingBox(), x); 
        if (x != 0.0D)
          setEntityBoundingBox(getEntityBoundingBox().offset(x, 0.0D, 0.0D)); 
      } 
      if (z != 0.0D) {
        int i6 = list1.size();
        for (int k5 = 0; k5 < i6; k5++)
          z = ((AxisAlignedBB)list1.get(k5)).calculateZOffset(getEntityBoundingBox(), z); 
        if (z != 0.0D)
          setEntityBoundingBox(getEntityBoundingBox().offset(0.0D, 0.0D, z)); 
      } 
      boolean flag = (this.onGround || (d3 != y && d3 < 0.0D)), bl = flag;
      if (this.stepHeight > 0.0F && flag && (d2 != x || d4 != z)) {
        StepEvent preEvent = new StepEvent(0, _this);
        MinecraftForge.EVENT_BUS.post((Event)preEvent);
        double d14 = x;
        double d6 = y;
        double d7 = z;
        AxisAlignedBB axisalignedbb1 = getEntityBoundingBox();
        setEntityBoundingBox(axisalignedbb);
        y = preEvent.getHeight();
        List<AxisAlignedBB> list = this.world.getCollisionBoxes(_this, getEntityBoundingBox().expand(d2, y, d4));
        AxisAlignedBB axisalignedbb2 = getEntityBoundingBox();
        AxisAlignedBB axisalignedbb3 = axisalignedbb2.expand(d2, 0.0D, d4);
        double d8 = y;
        int k1 = list.size();
        for (int j1 = 0; j1 < k1; j1++)
          d8 = ((AxisAlignedBB)list.get(j1)).calculateYOffset(axisalignedbb3, d8); 
        axisalignedbb2 = axisalignedbb2.offset(0.0D, d8, 0.0D);
        double d18 = d2;
        int i2 = list.size();
        for (int l1 = 0; l1 < i2; l1++)
          d18 = ((AxisAlignedBB)list.get(l1)).calculateXOffset(axisalignedbb2, d18); 
        axisalignedbb2 = axisalignedbb2.offset(d18, 0.0D, 0.0D);
        double d19 = d4;
        int k2 = list.size();
        for (int j2 = 0; j2 < k2; j2++)
          d19 = ((AxisAlignedBB)list.get(j2)).calculateZOffset(axisalignedbb2, d19); 
        axisalignedbb2 = axisalignedbb2.offset(0.0D, 0.0D, d19);
        AxisAlignedBB axisalignedbb4 = getEntityBoundingBox();
        double d20 = y;
        int i3 = list.size();
        for (int l2 = 0; l2 < i3; l2++)
          d20 = ((AxisAlignedBB)list.get(l2)).calculateYOffset(axisalignedbb4, d20); 
        axisalignedbb4 = axisalignedbb4.offset(0.0D, d20, 0.0D);
        double d21 = d2;
        int k3 = list.size();
        for (int j3 = 0; j3 < k3; j3++)
          d21 = ((AxisAlignedBB)list.get(j3)).calculateXOffset(axisalignedbb4, d21); 
        axisalignedbb4 = axisalignedbb4.offset(d21, 0.0D, 0.0D);
        double d22 = d4;
        int i4 = list.size();
        for (int l3 = 0; l3 < i4; l3++)
          d22 = ((AxisAlignedBB)list.get(l3)).calculateZOffset(axisalignedbb4, d22); 
        axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d22);
        double d23 = d18 * d18 + d19 * d19;
        double d9 = d21 * d21 + d22 * d22;
        if (d23 > d9) {
          x = d18;
          z = d19;
          y = -d8;
          setEntityBoundingBox(axisalignedbb2);
        } else {
          x = d21;
          z = d22;
          y = -d20;
          setEntityBoundingBox(axisalignedbb4);
        } 
        int k4 = list.size();
        for (int j4 = 0; j4 < k4; j4++)
          y = ((AxisAlignedBB)list.get(j4)).calculateYOffset(getEntityBoundingBox(), y); 
        setEntityBoundingBox(getEntityBoundingBox().offset(0.0D, y, 0.0D));
        if (d14 * d14 + d7 * d7 >= x * x + z * z) {
          x = d14;
          y = d6;
          z = d7;
          setEntityBoundingBox(axisalignedbb1);
        } else {
          StepEvent postEvent = new StepEvent(1, _this);
          MinecraftForge.EVENT_BUS.post((Event)postEvent);
        } 
      } 
      this.world.profiler.endSection();
      this.world.profiler.startSection("rest");
      resetPositionToBB();
      this.collidedHorizontally = (d2 != x || d4 != z);
      this.collidedVertically = (d3 != y);
      this.onGround = (this.collidedVertically && d3 < 0.0D);
      this.collided = (this.collidedHorizontally || this.collidedVertically);
      int j6 = MathHelper.floor(this.posX);
      int i1 = MathHelper.floor(this.posY - 0.20000000298023224D);
      int k6 = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(j6, i1, k6);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      BlockPos blockpos1;
      IBlockState iblockstate1;
      Block block1;
      if (iblockstate.getMaterial() == Material.AIR && (block1 = (iblockstate1 = this.world.getBlockState(blockpos1 = blockpos.down())).getBlock() instanceof net.minecraft.block.BlockFence || block1 instanceof net.minecraft.block.BlockWall || block1 instanceof net.minecraft.block.BlockFenceGate)) {
        iblockstate = iblockstate1;
        blockpos = blockpos1;
      } 
      updateFallState(y, this.onGround, iblockstate, blockpos);
      if (d2 != x)
        this.motionX = 0.0D; 
      if (d4 != z)
        this.motionZ = 0.0D; 
      Block block = iblockstate.getBlock();
      if (d3 != y)
        block.onLanded(this.world, _this); 
      if (canTriggerWalking() && (!this.onGround || !isSneaking() || !(_this instanceof net.minecraft.entity.player.EntityPlayer)) && !isRiding()) {
        double d15 = this.posX - d10;
        double d16 = this.posY - d11;
        double d17 = this.posZ - d1;
        if (block != Blocks.LADDER)
          d16 = 0.0D; 
        if (block != null && this.onGround)
          block.onEntityWalk(this.world, blockpos, _this); 
        this.distanceWalkedModified = (float)(this.distanceWalkedModified + MathHelper.sqrt(d15 * d15 + d17 * d17) * 0.6D);
        this.distanceWalkedOnStepModified = (float)(this.distanceWalkedOnStepModified + MathHelper.sqrt(d15 * d15 + d16 * d16 + d17 * d17) * 0.6D);
        if (this.distanceWalkedOnStepModified > this.nextStepDistance && iblockstate.getMaterial() != Material.AIR) {
          this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
          if (isInWater()) {
            Entity entity = (isBeingRidden() && getControllingPassenger() != null) ? getControllingPassenger() : _this;
            float f = (entity == _this) ? 0.35F : 0.4F;
            float f1 = MathHelper.sqrt(entity.motionX * entity.motionX * 0.20000000298023224D + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * 0.20000000298023224D) * f;
            if (f1 > 1.0F)
              f1 = 1.0F; 
            playSound(getSwimSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
          } else {
            playStepSound(blockpos, block);
          } 
        } else if (this.distanceWalkedOnStepModified > this.nextFlap && makeFlySound() && iblockstate.getMaterial() == Material.AIR) {
          this.nextFlap = playFlySound(this.distanceWalkedOnStepModified);
        } 
      } 
      try {
        doBlockCollisions();
      } catch (Throwable throwable) {
        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
        CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
        addEntityCrashInfo(crashreportcategory);
        throw new ReportedException(crashreport);
      } 
      boolean flag1 = isWet();
      if (this.world.isFlammableWithin(getEntityBoundingBox().shrink(0.001D))) {
        dealFireDamage(1);
        if (!flag1) {
          this.fire++;
          if (this.fire == 0)
            setFire(8); 
        } 
      } else if (this.fire <= 0) {
        this.fire = -getFireImmuneTicks();
      } 
      if (flag1 && isBurning()) {
        playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
        this.fire = -getFireImmuneTicks();
      } 
      this.world.profiler.endSection();
    } 
  }
  
  @Redirect(method = {"onEntityUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getMaxInPortalTime()I"))
  private int getMaxInPortalTimeHook(Entity entity) {
    int time = getMaxInPortalTime();
    if (BetterPortals.getInstance().isOn() && ((Boolean)(BetterPortals.getInstance()).fastPortal.getValue()).booleanValue())
      time = ((Integer)(BetterPortals.getInstance()).time.getValue()).intValue(); 
    return time;
  }
  
  @Redirect(method = {"applyEntityCollision"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
  public void addVelocityHook(Entity entity, double x, double y, double z) {
    PushEvent event = new PushEvent(entity, x, y, z, true);
    MinecraftForge.EVENT_BUS.post((Event)event);
    if (!event.isCanceled()) {
      entity.motionX += event.x;
      entity.motionY += event.y;
      entity.motionZ += event.z;
      entity.isAirBorne = event.airbone;
    } 
  }
}
