package me.alpha432.oyvey.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.mixin.mixins.accessors.IEntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityUtil implements Util {
  public static final Vec3d[] antiDropOffsetList = new Vec3d[] { new Vec3d(0.0D, -2.0D, 0.0D) };
  
  public static final Vec3d[] platformOffsetList = new Vec3d[] { new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D) };
  
  public static final Vec3d[] legOffsetList = new Vec3d[] { new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D) };
  
  public static final Vec3d[] doubleLegOffsetList = new Vec3d[] { new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -2.0D), new Vec3d(0.0D, 0.0D, 2.0D) };
  
  public static final Vec3d[] OffsetList = new Vec3d[] { new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D) };
  
  public static final Vec3d[] headpiece = new Vec3d[] { new Vec3d(0.0D, 2.0D, 0.0D) };
  
  public static final Vec3d[] offsetsNoHead = new Vec3d[] { new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D) };
  
  public static final Vec3d[] antiStepOffsetList = new Vec3d[] { new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D) };
  
  public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[] { new Vec3d(0.0D, 3.0D, 0.0D) };
  
  public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
    if (packet) {
      mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
    } else {
      mc.playerController.attackEntity((EntityPlayer)mc.player, entity);
    } 
    if (swingArm)
      mc.player.swingArm(EnumHand.MAIN_HAND); 
  }
  
  public static boolean isSafeOy(Entity entity, int height, boolean floor) {
    return (getUnsafeBlocks2(entity, height, floor).size() == 0);
  }
  
  public static boolean isSafeOy(Entity entity) {
    return isSafeOy(entity, 0, false);
  }
  
  public static Vec3d interpolateEntity(Entity entity, float time) {
    return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
  }
  
  public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
    return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(getInterpolatedAmount(entity, partialTicks));
  }
  
  public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
    return getInterpolatedPos(entity, partialTicks).subtract((mc.getRenderManager()).renderPosX, (mc.getRenderManager()).renderPosY, (mc.getRenderManager()).renderPosZ);
  }
  
  public static Vec3d getInterpolatedRenderPos(Vec3d vec) {
    return (new Vec3d(vec.x, vec.y, vec.z)).subtract((mc.getRenderManager()).renderPosX, (mc.getRenderManager()).renderPosY, (mc.getRenderManager()).renderPosZ);
  }
  
  public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
    return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
  }
  
  public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
    return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
  }
  
  public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
    return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
  }
  
  public static boolean isPassive(Entity entity) {
    if (entity instanceof EntityWolf && ((EntityWolf)entity).isAngry())
      return false; 
    if (entity instanceof net.minecraft.entity.EntityAgeable || entity instanceof net.minecraft.entity.passive.EntityAmbientCreature || entity instanceof net.minecraft.entity.passive.EntitySquid)
      return true; 
    return (entity instanceof EntityIronGolem && ((EntityIronGolem)entity).getRevengeTarget() == null);
  }
  
  public static boolean stopSneaking(boolean isSneaking) {
    if (isSneaking && mc.player != null)
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
    return false;
  }
  
  public static BlockPos getPlayerPos(EntityPlayer player) {
    return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
  }
  
  public static boolean isMobAggressive(Entity entity) {
    if (entity instanceof EntityPigZombie) {
      if (((EntityPigZombie)entity).isArmsRaised() || ((EntityPigZombie)entity).isAngry())
        return true; 
    } else {
      if (entity instanceof EntityWolf)
        return (((EntityWolf)entity).isAngry() && !mc.player.equals(((EntityWolf)entity).getOwner())); 
      if (entity instanceof EntityEnderman)
        return ((EntityEnderman)entity).isScreaming(); 
    } 
    return isHostileMob(entity);
  }
  
  public static boolean isNeutralMob(Entity entity) {
    return (entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman);
  }
  
  public static boolean isProjectile(Entity entity) {
    return (entity instanceof net.minecraft.entity.projectile.EntityShulkerBullet || entity instanceof net.minecraft.entity.projectile.EntityFireball);
  }
  
  public static boolean isVehicle(Entity entity) {
    return (entity instanceof net.minecraft.entity.item.EntityBoat || entity instanceof net.minecraft.entity.item.EntityMinecart);
  }
  
  public static boolean isFriendlyMob(Entity entity) {
    return ((entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof net.minecraft.entity.passive.EntityVillager || entity instanceof EntityIronGolem || (isNeutralMob(entity) && !isMobAggressive(entity)));
  }
  
  public static boolean isHostileMob(Entity entity) {
    return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity));
  }
  
  public static boolean isInHole(Entity entity) {
    return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
  }
  
  public static boolean isBlockValid(BlockPos blockPos) {
    return (isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos));
  }
  
  public static boolean isObbyHole(BlockPos blockPos) {
    BlockPos[] touchingBlocks;
    BlockPos[] arrayOfBlockPos1;
    int i;
    byte b;
    for (arrayOfBlockPos1 = touchingBlocks = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() }, i = arrayOfBlockPos1.length, b = 0; b < i; ) {
      BlockPos pos = arrayOfBlockPos1[b];
      IBlockState touchingState = mc.world.getBlockState(pos);
      if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.OBSIDIAN) {
        b++;
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public static boolean isBedrockHole(BlockPos blockPos) {
    BlockPos[] touchingBlocks;
    BlockPos[] arrayOfBlockPos1;
    int i;
    byte b;
    for (arrayOfBlockPos1 = touchingBlocks = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() }, i = arrayOfBlockPos1.length, b = 0; b < i; ) {
      BlockPos pos = arrayOfBlockPos1[b];
      IBlockState touchingState = mc.world.getBlockState(pos);
      if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.BEDROCK) {
        b++;
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public static boolean isBothHole(BlockPos blockPos) {
    BlockPos[] touchingBlocks;
    BlockPos[] arrayOfBlockPos1;
    int i;
    byte b;
    for (arrayOfBlockPos1 = touchingBlocks = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() }, i = arrayOfBlockPos1.length, b = 0; b < i; ) {
      BlockPos pos = arrayOfBlockPos1[b];
      IBlockState touchingState = mc.world.getBlockState(pos);
      if (touchingState.getBlock() != Blocks.AIR && (touchingState.getBlock() == Blocks.BEDROCK || touchingState.getBlock() == Blocks.OBSIDIAN)) {
        b++;
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public static double getDst(Vec3d vec) {
    return mc.player.getPositionVector().distanceTo(vec);
  }
  
  public static boolean isInWater(Entity entity) {
    if (entity == null)
      return false; 
    double y = entity.posY + 0.01D;
    for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); x++) {
      for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ) {
        BlockPos pos = new BlockPos(x, (int)y, z);
        if (!(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid)) {
          z++;
          continue;
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public static boolean isDrivenByPlayer(Entity entityIn) {
    return (mc.player != null && entityIn != null && entityIn.equals(mc.player.getRidingEntity()));
  }
  
  public static boolean isPlayer(Entity entity) {
    return entity instanceof EntityPlayer;
  }
  
  public static boolean isAboveWater(Entity entity) {
    return isAboveWater(entity, false);
  }
  
  public static boolean isAboveWater(Entity entity, boolean packet) {
    if (entity == null)
      return false; 
    double y = entity.posY - (packet ? 0.03D : (isPlayer(entity) ? 0.2D : 0.5D));
    for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); x++) {
      for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ) {
        BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
        if (!(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid)) {
          z++;
          continue;
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public static Vec3d[] getHeightOffsets(int min, int max) {
    ArrayList<Vec3d> offsets = new ArrayList<>();
    for (int i = min; i <= max; i++)
      offsets.add(new Vec3d(0.0D, i, 0.0D)); 
    Vec3d[] array = new Vec3d[offsets.size()];
    return offsets.<Vec3d>toArray(array);
  }
  
  public static BlockPos getRoundedBlockPos(Entity entity) {
    return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
  }
  
  public static boolean isLiving(Entity entity) {
    return entity instanceof EntityLivingBase;
  }
  
  public static boolean isAlive(Entity entity) {
    return (isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0F);
  }
  
  public static boolean isDead(Entity entity) {
    return !isAlive(entity);
  }
  
  public static float getHealth(Entity entity) {
    if (isLiving(entity)) {
      EntityLivingBase livingBase = (EntityLivingBase)entity;
      return livingBase.getHealth() + livingBase.getAbsorptionAmount();
    } 
    return 0.0F;
  }
  
  public static float getHealth(Entity entity, boolean absorption) {
    if (isLiving(entity)) {
      EntityLivingBase livingBase = (EntityLivingBase)entity;
      return livingBase.getHealth() + (absorption ? livingBase.getAbsorptionAmount() : 0.0F);
    } 
    return 0.0F;
  }
  
  public static boolean canEntityFeetBeSeen(Entity entityIn) {
    return (mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null);
  }
  
  public static boolean isntValid(Entity entity, double range) {
    return (entity == null || isDead(entity) || entity.equals(mc.player) || (entity instanceof EntityPlayer && OyVey.friendManager.isFriend(entity.getName())) || mc.player.getDistanceSq(entity) > MathUtil.square(range));
  }
  
  public static boolean isValid(Entity entity, double range) {
    return !isntValid(entity, range);
  }
  
  public static boolean holdingWeapon(EntityPlayer player) {
    return (player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword || player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemAxe);
  }
  
  public static double getMaxSpeed() {
    double maxModifier = 0.2873D;
    if (mc.player.isPotionActive(Objects.<Potion>requireNonNull(Potion.getPotionById(1))))
      maxModifier *= 1.0D + 0.2D * (((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(Objects.<Potion>requireNonNull(Potion.getPotionById(1))))).getAmplifier() + 1); 
    return maxModifier;
  }
  
  public static void mutliplyEntitySpeed(Entity entity, double multiplier) {
    if (entity != null) {
      entity.motionX *= multiplier;
      entity.motionZ *= multiplier;
    } 
  }
  
  public static boolean isEntityMoving(Entity entity) {
    if (entity == null)
      return false; 
    if (entity instanceof EntityPlayer)
      return (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()); 
    return (entity.motionX != 0.0D || entity.motionY != 0.0D || entity.motionZ != 0.0D);
  }
  
  public static double getEntitySpeed(Entity entity) {
    if (entity != null) {
      double distTraveledLastTickX = entity.posX - entity.prevPosX;
      double distTraveledLastTickZ = entity.posZ - entity.prevPosZ;
      double speed = MathHelper.sqrt(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ);
      return speed * 20.0D;
    } 
    return 0.0D;
  }
  
  public static boolean is32k(ItemStack stack) {
    return (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 1000);
  }
  
  public static void moveEntityStrafe(double speed, Entity entity) {
    if (entity != null) {
      MovementInput movementInput = mc.player.movementInput;
      double forward = movementInput.moveForward;
      double strafe = movementInput.moveStrafe;
      float yaw = mc.player.rotationYaw;
      if (forward == 0.0D && strafe == 0.0D) {
        entity.motionX = 0.0D;
        entity.motionZ = 0.0D;
      } else {
        if (forward != 0.0D) {
          if (strafe > 0.0D) {
            yaw += ((forward > 0.0D) ? -45 : 45);
          } else if (strafe < 0.0D) {
            yaw += ((forward > 0.0D) ? 45 : -45);
          } 
          strafe = 0.0D;
          if (forward > 0.0D) {
            forward = 1.0D;
          } else if (forward < 0.0D) {
            forward = -1.0D;
          } 
        } 
        entity.motionX = forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F)));
        entity.motionZ = forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F)));
      } 
    } 
  }
  
  public static boolean rayTraceHitCheck(Entity entity, boolean shouldCheck) {
    return (!shouldCheck || mc.player.canEntityBeSeen(entity));
  }
  
  public static Color getColor(Entity entity, int red, int green, int blue, int alpha, boolean colorFriends) {
    Color color = new Color(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
    if (entity instanceof EntityPlayer && colorFriends && OyVey.friendManager.isFriend((EntityPlayer)entity))
      color = new Color(0.33333334F, 1.0F, 1.0F, alpha / 255.0F); 
    return color;
  }
  
  public static boolean isMoving() {
    return (mc.player.moveForward != 0.0D || mc.player.moveStrafing != 0.0D);
  }
  
  public static EntityPlayer getClosestEnemy(double distance) {
    EntityPlayer closest = null;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (isntValid((Entity)player, distance))
        continue; 
      if (closest == null) {
        closest = player;
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) >= mc.player.getDistanceSq((Entity)closest))
        continue; 
      closest = player;
    } 
    return closest;
  }
  
  public static boolean checkCollide() {
    if (mc.player.isSneaking())
      return false; 
    if (mc.player.getRidingEntity() != null && (mc.player.getRidingEntity()).fallDistance >= 3.0F)
      return false; 
    return (mc.player.fallDistance < 3.0F);
  }
  
  public static BlockPos getPlayerPosWithEntity() {
    return new BlockPos((mc.player.getRidingEntity() != null) ? (mc.player.getRidingEntity()).posX : mc.player.posX, (mc.player.getRidingEntity() != null) ? (mc.player.getRidingEntity()).posY : mc.player.posY, (mc.player.getRidingEntity() != null) ? (mc.player.getRidingEntity()).posZ : mc.player.posZ);
  }
  
  public static double[] forward(double speed) {
    float forward = mc.player.movementInput.moveForward;
    float side = mc.player.movementInput.moveStrafe;
    float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
    if (forward != 0.0F) {
      if (side > 0.0F) {
        yaw += ((forward > 0.0F) ? -45 : 45);
      } else if (side < 0.0F) {
        yaw += ((forward > 0.0F) ? 45 : -45);
      } 
      side = 0.0F;
      if (forward > 0.0F) {
        forward = 1.0F;
      } else if (forward < 0.0F) {
        forward = -1.0F;
      } 
    } 
    double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
    double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
    double posX = forward * speed * cos + side * speed * sin;
    double posZ = forward * speed * sin - side * speed * cos;
    return new double[] { posX, posZ };
  }
  
  public static Map<String, Integer> getTextRadarPlayers() {
    Map<String, Integer> output = new HashMap<>();
    DecimalFormat dfHealth = new DecimalFormat("#.#");
    dfHealth.setRoundingMode(RoundingMode.CEILING);
    DecimalFormat dfDistance = new DecimalFormat("#.#");
    dfDistance.setRoundingMode(RoundingMode.CEILING);
    StringBuilder healthSB = new StringBuilder();
    StringBuilder distanceSB = new StringBuilder();
    for (EntityPlayer player : mc.world.playerEntities) {
      if (player.isInvisible() || player.getName().equals(mc.player.getName()))
        continue; 
      int hpRaw = (int)getHealth((Entity)player);
      String hp = dfHealth.format(hpRaw);
      healthSB.append("Â§");
      if (hpRaw >= 20) {
        healthSB.append("a");
      } else if (hpRaw >= 10) {
        healthSB.append("e");
      } else if (hpRaw >= 5) {
        healthSB.append("6");
      } else {
        healthSB.append("c");
      } 
      healthSB.append(hp);
      int distanceInt = (int)mc.player.getDistance((Entity)player);
      String distance = dfDistance.format(distanceInt);
      distanceSB.append("Â§");
      if (distanceInt >= 25) {
        distanceSB.append("a");
      } else if (distanceInt > 10) {
        distanceSB.append("6");
      } else {
        distanceSB.append("c");
      } 
      distanceSB.append(distance);
      output.put(healthSB.toString() + " " + (OyVey.friendManager.isFriend(player) ? (String)ChatFormatting.AQUA : (String)ChatFormatting.RED) + player.getName() + " " + distanceSB.toString() + " Â§f0", Integer.valueOf((int)mc.player.getDistance((Entity)player)));
      healthSB.setLength(0);
      distanceSB.setLength(0);
    } 
    if (!output.isEmpty())
      output = MathUtil.sortByValue(output, false); 
    return output;
  }
  
  public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
    return (entity.posY >= blockPos.getY());
  }
  
  public static boolean isCrystalAtFeet(EntityEnderCrystal crystal, double range) {
    for (EntityPlayer player : mc.world.playerEntities) {
      if (mc.player.getDistanceSq((Entity)player) > range * range)
        continue; 
      if (OyVey.friendManager.isFriend(player))
        continue; 
      for (Vec3d vec : doubleLegOffsetList) {
        if ((new BlockPos(player.getPositionVector())).add(vec.x, vec.y, vec.z) == crystal.getPosition())
          return true; 
      } 
    } 
    return false;
  }
  
  public static void swingArmNoPacket(EnumHand hand, EntityLivingBase entity) {
    ItemStack stack = entity.getHeldItem(hand);
    if (!stack.isEmpty() && stack.getItem().onEntitySwing(entity, stack))
      return; 
    if (!entity.isSwingInProgress || entity.swingProgressInt >= ((IEntityLivingBase)entity).getArmSwingAnimationEnd() / 2 || entity.swingProgressInt < 0) {
      entity.swingProgressInt = -1;
      entity.isSwingInProgress = true;
      entity.swingingHand = hand;
    } 
  }
  
  public static boolean isSafe2(Entity entity, int height, boolean floor, boolean face) {
    return (getUnsafeBlocks2(entity, height, floor).size() == 0);
  }
  
  public static List<Vec3d> getUnsafeBlocks2(Entity entity, int height, boolean floor) {
    return getUnsafeBlocksFromVec3d2(entity.getPositionVector(), height, floor);
  }
  
  public static List<Vec3d> getUnsafeBlocksFromVec3d2(Vec3d pos, int height, boolean floor) {
    ArrayList<Vec3d> vec3ds = new ArrayList<>();
    for (Vec3d vector : getOffsets2(height, floor)) {
      BlockPos targetPos = (new BlockPos(pos)).add(vector.x, vector.y, vector.z);
      Block block = mc.world.getBlockState(targetPos).getBlock();
      if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow)
        vec3ds.add(vector); 
    } 
    return vec3ds;
  }
  
  public static List<Vec3d> getOffsetList2(int y, boolean floor) {
    ArrayList<Vec3d> offsets = new ArrayList<>();
    offsets.add(new Vec3d(-1.0D, y, 0.0D));
    offsets.add(new Vec3d(1.0D, y, 0.0D));
    offsets.add(new Vec3d(0.0D, y, -1.0D));
    offsets.add(new Vec3d(0.0D, y, 1.0D));
    if (floor)
      offsets.add(new Vec3d(0.0D, (y - 1), 0.0D)); 
    return offsets;
  }
  
  public static Vec3d[] getOffsets2(int y, boolean floor) {
    List<Vec3d> offsets = getOffsetList2(y, floor);
    Vec3d[] array = new Vec3d[offsets.size()];
    return offsets.<Vec3d>toArray(array);
  }
  
  public static boolean isSafe2(Entity entity) {
    return isSafe2(entity, 0, false, true);
  }
  
  public static Vec3d[] getUnsafeBlockArrayFromVec3d2(Vec3d pos, int height, boolean floor) {
    List<Vec3d> list = getUnsafeBlocksFromVec3d2(pos, height, floor);
    Vec3d[] array = new Vec3d[list.size()];
    return list.<Vec3d>toArray(array);
  }
  
  public static Vec3d[] getUnsafeBlockArray2(Entity entity, int height, boolean floor) {
    List<Vec3d> list = getUnsafeBlocks2(entity, height, floor);
    Vec3d[] array = new Vec3d[list.size()];
    return list.<Vec3d>toArray(array);
  }
  
  public static List<Vec3d> targets2(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
    ArrayList<Vec3d> placeTargets = new ArrayList<>();
    if (antiDrop)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList)); 
    if (platform)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList)); 
    if (legs)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList)); 
    Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
    if (antiStep) {
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
    } else {
      List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d2(vec3d, 2, false);
      if (vec3ds.size() == 4)
        for (Vec3d vector : vec3ds) {
          BlockPos position = (new BlockPos(vec3d)).add(vector.x, vector.y, vector.z);
          switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
            case 0:
              break;
            case -1:
            case 1:
            case 2:
              continue;
            case 3:
              placeTargets.add(vec3d.add(vector));
              break;
            default:
              // Byte code: goto -> 234
          } 
          if (antiScaffold)
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList)); 
          return placeTargets;
        }  
    } 
    if (antiScaffold)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList)); 
    return placeTargets;
  }
  
  public static boolean isTrapped2(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
    return (getUntrappedBlocks2(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0);
  }
  
  public static List<Vec3d> getUntrappedBlocks2(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
    ArrayList<Vec3d> vec3ds = new ArrayList<>();
    if (!antiStep && getUnsafeBlocks2((Entity)player, 2, false).size() == 4)
      vec3ds.addAll(getUnsafeBlocks2((Entity)player, 2, false)); 
    for (int i = 0; i < (getTrapOffsets2(antiScaffold, antiStep, legs, platform, antiDrop)).length; i++) {
      Vec3d vector = getTrapOffsets2(antiScaffold, antiStep, legs, platform, antiDrop)[i];
      BlockPos targetPos = (new BlockPos(player.getPositionVector())).add(vector.x, vector.y, vector.z);
      Block block = mc.world.getBlockState(targetPos).getBlock();
      if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow)
        vec3ds.add(vector); 
    } 
    return vec3ds;
  }
  
  public static Vec3d[] getTrapOffsets2(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
    List<Vec3d> offsets = getTrapOffsetsList2(antiScaffold, antiStep, legs, platform, antiDrop);
    Vec3d[] array = new Vec3d[offsets.size()];
    return offsets.<Vec3d>toArray(array);
  }
  
  public static List<Vec3d> getTrapOffsetsList2(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
    ArrayList<Vec3d> offsets = new ArrayList<>(getOffsetList2(1, false));
    offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
    if (antiScaffold)
      offsets.add(new Vec3d(0.0D, 3.0D, 0.0D)); 
    if (antiStep)
      offsets.addAll(getOffsetList2(2, false)); 
    if (legs)
      offsets.addAll(getOffsetList2(0, false)); 
    if (platform) {
      offsets.addAll(getOffsetList2(-1, false));
      offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
    } 
    if (antiDrop)
      offsets.add(new Vec3d(0.0D, -2.0D, 0.0D)); 
    return offsets;
  }
  
  public static boolean isInLiquid() {
    if (mc.player.fallDistance >= 3.0F)
      return false; 
    boolean inLiquid = false;
    AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
    int y = (int)bb.minY;
    for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
      for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
        Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
        if (!(block instanceof net.minecraft.block.BlockAir)) {
          if (!(block instanceof net.minecraft.block.BlockLiquid))
            return false; 
          inLiquid = true;
        } 
      } 
    } 
    return inLiquid;
  }
  
  public static boolean isAboveLiquid(Entity entity) {
    if (entity == null)
      return false; 
    double n = entity.posY + 0.01D;
    for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); i++) {
      for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); j++) {
        if (mc.world.getBlockState(new BlockPos(i, (int)n, j)).getBlock() instanceof net.minecraft.block.BlockLiquid)
          return true; 
      } 
    } 
    return false;
  }
  
  public static boolean isOnLiquid(double offset) {
    if (mc.player.fallDistance >= 3.0F)
      return false; 
    AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D) : mc.player.getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D);
    boolean onLiquid = false;
    int y = (int)bb.minY;
    for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
      for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
        Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
        if (block != Blocks.AIR) {
          if (!(block instanceof net.minecraft.block.BlockLiquid))
            return false; 
          onLiquid = true;
        } 
      } 
    } 
    return onLiquid;
  }
  
  public static boolean checkForLiquid(Entity entity, boolean b) {
    double n;
    if (entity == null)
      return false; 
    double posY = entity.posY;
    if (b) {
      n = 0.03D;
    } else if (entity instanceof EntityPlayer) {
      n = 0.2D;
    } else {
      n = 0.5D;
    } 
    double n2 = posY - n;
    for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); i++) {
      for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); j++) {
        if (mc.world.getBlockState(new BlockPos(i, MathHelper.floor(n2), j)).getBlock() instanceof net.minecraft.block.BlockLiquid)
          return true; 
      } 
    } 
    return false;
  }
  
  public static boolean isOnLiquid() {
    double y = mc.player.posY - 0.03D;
    for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); x++) {
      for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); z++) {
        BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
        if (mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid)
          return true; 
      } 
    } 
    return false;
  }
  
  public static Vec3d[] getOffsets(int y, boolean floor, boolean face) {
    List<Vec3d> offsets = getOffsetList(y, floor, face);
    Vec3d[] array = new Vec3d[offsets.size()];
    return offsets.<Vec3d>toArray(array);
  }
  
  public static List<Vec3d> getOffsetList(int y, boolean floor, boolean face) {
    List<Vec3d> offsets = new ArrayList<>();
    if (face) {
      offsets.add(new Vec3d(-1.0D, y, 0.0D));
      offsets.add(new Vec3d(1.0D, y, 0.0D));
      offsets.add(new Vec3d(0.0D, y, -1.0D));
      offsets.add(new Vec3d(0.0D, y, 1.0D));
    } else {
      offsets.add(new Vec3d(-1.0D, y, 0.0D));
    } 
    if (floor)
      offsets.add(new Vec3d(0.0D, (y - 1), 0.0D)); 
    return offsets;
  }
  
  public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
    List<Vec3d> offsets = new ArrayList<>(getOffsetList(1, false, face));
    offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
    if (antiScaffold)
      offsets.add(new Vec3d(0.0D, 3.0D, 0.0D)); 
    if (antiStep)
      offsets.addAll(getOffsetList(2, false, face)); 
    if (legs)
      offsets.addAll(getOffsetList(0, false, face)); 
    if (platform) {
      offsets.addAll(getOffsetList(-1, false, face));
      offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
    } 
    if (antiDrop)
      offsets.add(new Vec3d(0.0D, -2.0D, 0.0D)); 
    return offsets;
  }
  
  public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
    List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop, face);
    Vec3d[] array = new Vec3d[offsets.size()];
    return offsets.<Vec3d>toArray(array);
  }
  
  public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
    List<Vec3d> vec3ds = new ArrayList<>();
    if (!antiStep && getUnsafeBlocks((Entity)player, 2, false, face).size() == 4)
      vec3ds.addAll(getUnsafeBlocks((Entity)player, 2, false, face)); 
    for (int i = 0; i < (getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop, face)).length; i++) {
      Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop, face)[i];
      BlockPos targetPos = (new BlockPos(player.getPositionVector())).add(vector.x, vector.y, vector.z);
      Block block = mc.world.getBlockState(targetPos).getBlock();
      if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow)
        vec3ds.add(vector); 
    } 
    return vec3ds;
  }
  
  public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor, boolean face) {
    return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor, face);
  }
  
  public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor, boolean face) {
    List<Vec3d> vec3ds = new ArrayList<>();
    for (Vec3d vector : getOffsets(height, floor, face)) {
      BlockPos targetPos = (new BlockPos(pos)).add(vector.x, vector.y, vector.z);
      Block block = mc.world.getBlockState(targetPos).getBlock();
      if (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow)
        vec3ds.add(vector); 
    } 
    return vec3ds;
  }
  
  public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean face) {
    List<Vec3d> placeTargets = new ArrayList<>();
    if (antiDrop)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList)); 
    if (platform)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList)); 
    if (legs)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList)); 
    Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
    if (antiStep) {
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
    } else {
      List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false, face);
      if (vec3ds.size() == 4) {
        Iterator<Vec3d> iterator = vec3ds.iterator();
        while (true) {
          if (iterator.hasNext()) {
            Vec3d vector = iterator.next();
            BlockPos position = (new BlockPos(vec3d)).add(vector.x, vector.y, vector.z);
            switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
              case -1:
              case 1:
              case 2:
                continue;
              case 3:
                placeTargets.add(vec3d.add(vector));
                break;
              default:
                break;
            } 
          } else {
            break;
          } 
          if (antiScaffold)
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList)); 
          if (!face) {
            List<Vec3d> offsets = new ArrayList<>();
            offsets.add(new Vec3d(1.0D, 1.0D, 0.0D));
            offsets.add(new Vec3d(0.0D, 1.0D, -1.0D));
            offsets.add(new Vec3d(0.0D, 1.0D, 1.0D));
            Vec3d[] array = new Vec3d[offsets.size()];
            placeTargets.removeAll(Arrays.asList((Object[])BlockUtil.convertVec3ds(vec3d, offsets.<Vec3d>toArray(array))));
          } 
          return placeTargets;
        } 
      } 
    } 
    if (antiScaffold)
      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList)); 
    if (!face) {
      ArrayList<Vec3d> arrayList = new ArrayList();
      arrayList.add(new Vec3d(1.0D, 1.0D, 0.0D));
      arrayList.add(new Vec3d(0.0D, 1.0D, -1.0D));
      arrayList.add(new Vec3d(0.0D, 1.0D, 1.0D));
      Vec3d[] arrayOfVec3d = new Vec3d[arrayList.size()];
      placeTargets.removeAll(Arrays.asList((Object[])BlockUtil.convertVec3ds(vec3d, arrayList.<Vec3d>toArray(arrayOfVec3d))));
    } 
    return placeTargets;
  }
  
  public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean noScaffoldExtend, boolean face) {
    List<Vec3d> placeTargets = new ArrayList<>();
    if (extension == 1) {
      placeTargets.addAll(targets(player.getPositionVector(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace, face));
    } else {
      int extend = 1;
      for (Vec3d vec3d : MathUtil.getBlockBlocks((Entity)player)) {
        if (extend > extension)
          break; 
        placeTargets.addAll(targets(vec3d, !noScaffoldExtend, antiStep, legs, platform, antiDrop, raytrace, face));
        extend++;
      } 
    } 
    List<Vec3d> removeList = new ArrayList<>();
    for (Vec3d vec3d : placeTargets) {
      BlockPos pos = new BlockPos(vec3d);
      if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1)
        removeList.add(vec3d); 
    } 
    for (Vec3d vec3d : removeList)
      placeTargets.remove(vec3d); 
    return placeTargets;
  }
  
  public static boolean isSafe(Entity entity, int height, boolean floor, boolean face) {
    return (getUnsafeBlocks(entity, height, floor, face).size() == 0);
  }
  
  public static boolean isSafe(Entity entity) {
    return isSafe(entity, 0, false, true);
  }
  
  public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean noScaffoldExtend, boolean face) {
    return (getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace, noScaffoldExtend, face).size() == 0);
  }
  
  public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean face) {
    return (getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop, face).size() == 0);
  }
  
  public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor, boolean face) {
    List<Vec3d> list = getUnsafeBlocks(entity, height, floor, face);
    Vec3d[] array = new Vec3d[list.size()];
    return list.<Vec3d>toArray(array);
  }
  
  public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor, boolean face) {
    List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor, face);
    Vec3d[] array = new Vec3d[list.size()];
    return list.<Vec3d>toArray(array);
  }
  
  public static void setTimer(float speed) {
    (Minecraft.getMinecraft()).timer.tickLength = 50.0F / speed;
  }
  
  public static void resetTimer() {
    (Minecraft.getMinecraft()).timer.tickLength = 50.0F;
  }
  
  public static EntityPlayer getClosestPlayer(float range) {
    EntityPlayer player = null;
    double maxDistance = 999.0D;
    int size = mc.world.playerEntities.size();
    for (int i = 0; i < size; i++) {
      EntityPlayer entityPlayer = mc.world.playerEntities.get(i);
      if (isPlayerValid(entityPlayer, range)) {
        double distanceSq = mc.player.getDistanceSq((Entity)entityPlayer);
        if (maxDistance == 999.0D || distanceSq < maxDistance) {
          maxDistance = distanceSq;
          player = entityPlayer;
        } 
      } 
    } 
    return player;
  }
  
  public static boolean isPlayerValid(EntityPlayer player, float range) {
    return (player != mc.player && mc.player.getDistance((Entity)player) < range && !isDead((Entity)player) && !OyVey.friendManager.isFriend(player.getName()));
  }
  
  public static float calculate(double posX, double posY, double posZ, EntityLivingBase entity) {
    double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0D;
    Vec3d vec3d = new Vec3d(posX, posY, posZ);
    double blockDensity = getBlockDensity(vec3d, entity.getEntityBoundingBox());
    double v = (1.0D - distancedsize) * blockDensity;
    float damage = (int)((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D);
    return getBlastReduction(entity, getDamageMultiplied(damage), new Explosion((World)mc.world, null, posX, posY, posZ, 6.0F, false, true));
  }
  
  public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
    double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
    double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
    double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
    double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
    double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
    if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
      int j2 = 0;
      int k2 = 0;
      float f;
      for (f = 0.0F; f <= 1.0F; f = (float)(f + d0)) {
        float f1;
        for (f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1)) {
          float f2;
          for (f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2)) {
            double d5 = bb.minX + (bb.maxX - bb.minX) * f;
            double d6 = bb.minY + (bb.maxY - bb.minY) * f1;
            double d7 = bb.minZ + (bb.maxZ - bb.minZ) * f2;
            if (rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec) == null)
              j2++; 
            k2++;
          } 
        } 
      } 
      return j2 / k2;
    } 
    return 0.0F;
  }
  
  private static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
    float damage = damageI;
    if (entity instanceof EntityPlayer) {
      EntityPlayer ep = (EntityPlayer)entity;
      DamageSource ds = DamageSource.causeExplosionDamage(explosion);
      damage = CombatRules.getDamageAfterAbsorb(damage, ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
      int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
      float f = MathHelper.clamp(k, 0.0F, 20.0F);
      damage *= 1.0F - f / 25.0F;
      if (entity.isPotionActive(MobEffects.RESISTANCE))
        damage -= damage / 4.0F; 
      return damage;
    } 
    damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
    return damage;
  }
  
  private static float getDamageMultiplied(float damage) {
    int diff = mc.world.getDifficulty().getId();
    return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
  }
  
  public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32) {
    int i = MathHelper.floor(vec32.x);
    int j = MathHelper.floor(vec32.y);
    int k = MathHelper.floor(vec32.z);
    int l = MathHelper.floor(vec31.x);
    int i1 = MathHelper.floor(vec31.y);
    int j1 = MathHelper.floor(vec31.z);
    BlockPos blockpos = new BlockPos(l, i1, j1);
    IBlockState iblockstate = mc.world.getBlockState(blockpos);
    Block block = iblockstate.getBlock();
    if (block.canCollideCheck(iblockstate, false) && block != Blocks.WEB)
      return iblockstate.collisionRayTrace((World)mc.world, blockpos, vec31, vec32); 
    int k1 = 200;
    double d6 = vec32.x - vec31.x;
    double d7 = vec32.y - vec31.y;
    double d8 = vec32.z - vec31.z;
    while (k1-- >= 0) {
      EnumFacing enumfacing;
      if (l == i && i1 == j && j1 == k)
        return null; 
      boolean flag2 = true;
      boolean flag = true;
      boolean flag1 = true;
      double d0 = 999.0D;
      double d1 = 999.0D;
      double d2 = 999.0D;
      if (i > l) {
        d0 = l + 1.0D;
      } else if (i < l) {
        d0 = l + 0.0D;
      } else {
        flag2 = false;
      } 
      if (j > i1) {
        d1 = i1 + 1.0D;
      } else if (j < i1) {
        d1 = i1 + 0.0D;
      } else {
        flag = false;
      } 
      if (k > j1) {
        d2 = j1 + 1.0D;
      } else if (k < j1) {
        d2 = j1 + 0.0D;
      } else {
        flag1 = false;
      } 
      double d3 = 999.0D;
      double d4 = 999.0D;
      double d5 = 999.0D;
      if (flag2)
        d3 = (d0 - vec31.x) / d6; 
      if (flag)
        d4 = (d1 - vec31.y) / d7; 
      if (flag1)
        d5 = (d2 - vec31.z) / d8; 
      if (d3 == -0.0D)
        d3 = -1.0E-4D; 
      if (d4 == -0.0D)
        d4 = -1.0E-4D; 
      if (d5 == -0.0D)
        d5 = -1.0E-4D; 
      if (d3 < d4 && d3 < d5) {
        enumfacing = (i > l) ? EnumFacing.WEST : EnumFacing.EAST;
        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
      } else if (d4 < d5) {
        enumfacing = (j > i1) ? EnumFacing.DOWN : EnumFacing.UP;
        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
      } else {
        enumfacing = (k > j1) ? EnumFacing.NORTH : EnumFacing.SOUTH;
        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
      } 
      l = MathHelper.floor(vec31.x) - ((enumfacing == EnumFacing.EAST) ? 1 : 0);
      i1 = MathHelper.floor(vec31.y) - ((enumfacing == EnumFacing.UP) ? 1 : 0);
      j1 = MathHelper.floor(vec31.z) - ((enumfacing == EnumFacing.SOUTH) ? 1 : 0);
      blockpos = new BlockPos(l, i1, j1);
      iblockstate = mc.world.getBlockState(blockpos);
      block = iblockstate.getBlock();
      if (block.canCollideCheck(iblockstate, false) && block != Blocks.WEB)
        return iblockstate.collisionRayTrace((World)mc.world, blockpos, vec31, vec32); 
    } 
    return null;
  }
}
