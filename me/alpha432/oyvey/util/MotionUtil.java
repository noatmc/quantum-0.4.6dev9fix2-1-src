package me.alpha432.oyvey.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class MotionUtil implements Util {
  public static boolean isMoving(EntityLivingBase entity) {
    return (entity.moveForward != 0.0F || entity.moveStrafing != 0.0F);
  }
  
  public static boolean isMovings() {
    return ((Minecraft.getMinecraft()).player.moveForward != 0.0D || (Minecraft.getMinecraft()).player.moveStrafing != 0.0D);
  }
  
  public static void setSpeed(EntityLivingBase entity, double speed) {
    double[] dir = forward(speed);
    entity.motionX = dir[0];
    entity.motionZ = dir[1];
  }
  
  public static double getBaseMoveSpeed() {
    double baseSpeed = 0.2873D;
    if ((Minecraft.getMinecraft()).player != null && (Minecraft.getMinecraft()).player.isPotionActive(Potion.getPotionById(1))) {
      int amplifier = (Minecraft.getMinecraft()).player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
      baseSpeed *= 1.0D + 0.2D * (amplifier + 1);
    } 
    return baseSpeed;
  }
  
  public static double[] forward(double speed) {
    float forward = (Minecraft.getMinecraft()).player.movementInput.moveForward;
    float side = (Minecraft.getMinecraft()).player.movementInput.moveStrafe;
    float yaw = (Minecraft.getMinecraft()).player.prevRotationYaw + ((Minecraft.getMinecraft()).player.rotationYaw - (Minecraft.getMinecraft()).player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
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
  
  public static double[] getMoveSpeed(double speed) {
    float forward = mc.player.movementInput.moveForward;
    float strafe = mc.player.movementInput.moveStrafe;
    float yaw = mc.player.rotationYaw;
    if (forward != 0.0F) {
      if (strafe >= 1.0F) {
        yaw += ((forward > 0.0F) ? -45 : 45);
        strafe = 0.0F;
      } else if (strafe <= -1.0F) {
        yaw += ((forward > 0.0F) ? 45 : -45);
        strafe = 0.0F;
      } 
      if (forward > 0.0F) {
        forward = 1.0F;
      } else if (forward < 0.0F) {
        forward = -1.0F;
      } 
    } 
    double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
    double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
    double motionX = forward * speed * cos + strafe * speed * sin;
    double motionZ = forward * speed * sin - strafe * speed * cos;
    if (!isMovingMomentum()) {
      motionX = 0.0D;
      motionZ = 0.0D;
    } 
    return new double[] { motionX, motionZ };
  }
  
  public static boolean isMovingMomentum() {
    return (mc.player.moveForward != 0.0D || mc.player.moveStrafing != 0.0D);
  }
}
