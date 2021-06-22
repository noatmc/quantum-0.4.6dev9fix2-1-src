package me.alpha432.oyvey.util;

import me.alpha432.oyvey.features.modules.client.ClickGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class RotationUtil implements Util {
  public static Vec3d getEyesPos() {
    return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
  }
  
  public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
    double dirx = me.posX - px;
    double diry = me.posY - py;
    double dirz = me.posZ - pz;
    double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
    double pitch = Math.asin(diry /= len);
    double yaw = Math.atan2(dirz /= len, dirx /= len);
    pitch = pitch * 180.0D / Math.PI;
    yaw = yaw * 180.0D / Math.PI;
    return new double[] { yaw += 90.0D, pitch };
  }
  
  public static float[] getLegitRotations(Vec3d vec) {
    Vec3d eyesPos = getEyesPos();
    double diffX = vec.x - eyesPos.x;
    double diffY = vec.y - eyesPos.y;
    double diffZ = vec.z - eyesPos.z;
    double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
    float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
    float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
    return new float[] { mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
  }
  
  public static void faceYawAndPitch(float yaw, float pitch) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
  }
  
  public static void faceVector(Vec3d vec, boolean normalizeAngle) {
    float[] rotations = getLegitRotations(vec);
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.normalizeAngle((int)rotations[1], 360) : rotations[1], mc.player.onGround));
  }
  
  public static void faceEntity(Entity entity) {
    float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
    faceYawAndPitch(angle[0], angle[1]);
  }
  
  public static float[] getAngle(Entity entity) {
    return MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
  }
  
  public static int getDirection4D() {
    return MathHelper.floor((mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3;
  }
  
  public static String getDirection4D(boolean northRed) {
    int dirnumber = getDirection4D();
    if (dirnumber == 0)
      return "South (+Z)"; 
    if (dirnumber == 1)
      return "West (-X)"; 
    if (dirnumber == 2)
      return (northRed ? "Â§c" : "") + "North (-Z)"; 
    if (dirnumber == 3)
      return "East (+X)"; 
    return "Loading...";
  }
  
  public static float[] simpleFacing(EnumFacing facing) {
    switch (facing) {
      case DOWN:
        return new float[] { mc.player.rotationYaw, 90.0F };
      case UP:
        return new float[] { mc.player.rotationYaw, -90.0F };
      case NORTH:
        return new float[] { 180.0F, 0.0F };
      case SOUTH:
        return new float[] { 0.0F, 0.0F };
      case WEST:
        return new float[] { 90.0F, 0.0F };
    } 
    return new float[] { 270.0F, 0.0F };
  }
  
  public static boolean isInFov(BlockPos pos) {
    return (pos != null && (mc.player.getDistanceSq(pos) < 4.0D || yawDist(pos) < (getHalvedfov() + 2.0F)));
  }
  
  public static boolean isInFov(Entity entity) {
    return (entity != null && (mc.player.getDistanceSq(entity) < 4.0D || yawDist(entity) < (getHalvedfov() + 2.0F)));
  }
  
  public static boolean isInFov(Vec3d vec3d, Vec3d other) {
    if ((mc.player.rotationPitch > 30.0F) ? (other.y > mc.player.posY) : (mc.player.rotationPitch < -30.0F && other.y < mc.player.posY))
      return true; 
    float angle = MathUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
    if (angle < -270.0F)
      return true; 
    float fov = (((Boolean)(ClickGui.getInstance()).customFov.getValue()).booleanValue() ? ((Float)(ClickGui.getInstance()).fov.getValue()).floatValue() : mc.gameSettings.fovSetting) / 2.0F;
    return (angle < fov + 10.0F && angle > -fov - 10.0F);
  }
  
  public static double yawDist(BlockPos pos) {
    if (pos != null) {
      Vec3d difference = (new Vec3d((Vec3i)pos)).subtract(mc.player.getPositionEyes(mc.getRenderPartialTicks()));
      double d = Math.abs(mc.player.rotationYaw - Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0D) % 360.0D;
      return (d > 180.0D) ? (360.0D - d) : d;
    } 
    return 0.0D;
  }
  
  public static double yawDist(Entity e) {
    if (e != null) {
      Vec3d difference = e.getPositionVector().add(0.0D, (e.getEyeHeight() / 2.0F), 0.0D).subtract(mc.player.getPositionEyes(mc.getRenderPartialTicks()));
      double d = Math.abs(mc.player.rotationYaw - Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0D) % 360.0D;
      return (d > 180.0D) ? (360.0D - d) : d;
    } 
    return 0.0D;
  }
  
  public static float getHalvedfov() {
    return getFov() / 2.0F;
  }
  
  public static float getFov() {
    return ((Boolean)(ClickGui.getInstance()).customFov.getValue()).booleanValue() ? ((Float)(ClickGui.getInstance()).fov.getValue()).floatValue() : mc.gameSettings.fovSetting;
  }
  
  public static float transformYaw() {
    float yaw = mc.player.rotationYaw % 360.0F;
    if (mc.player.rotationYaw > 0.0F) {
      if (yaw > 180.0F)
        yaw = -180.0F + yaw - 180.0F; 
    } else if (yaw < -180.0F) {
      yaw = 180.0F + yaw + 180.0F;
    } 
    if (yaw < 0.0F)
      return 180.0F + yaw; 
    return -180.0F + yaw;
  }
}
