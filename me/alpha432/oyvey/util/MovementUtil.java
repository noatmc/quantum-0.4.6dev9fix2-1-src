package me.alpha432.oyvey.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;

public class MovementUtil implements Util {
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  public static double[] directionSpeed(double speed) {
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
  
  public static double[] dirSpeedNew(double speed) {
    float moveForward = mc.player.movementInput.moveForward;
    float moveStrafe = mc.player.movementInput.moveStrafe;
    float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
    if (moveForward != 0.0F) {
      if (moveStrafe > 0.0F) {
        rotationYaw += ((moveForward > 0.0F) ? -45 : 45);
      } else if (moveStrafe < 0.0F) {
        rotationYaw += ((moveForward > 0.0F) ? 45 : -45);
      } 
      moveStrafe = 0.0F;
      if (moveForward > 0.0F) {
        moveForward = 1.0F;
      } else if (moveForward < 0.0F) {
        moveForward = -1.0F;
      } 
    } 
    double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
    double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
    return new double[] { posX, posZ };
  }
  
  public static double[] futureCalc1(double d) {
    MovementInput movementInput = mc.player.movementInput;
    double d4 = movementInput.moveForward;
    double d5 = movementInput.moveStrafe;
    float f = mc.player.rotationYaw;
    double d3 = 0.0D, d2 = d3;
    if (d4 != 0.0D) {
      if (d5 > 0.0D) {
        f += ((d4 > 0.0D) ? -45 : 45);
      } else if (d5 < 0.0D) {
        f += ((d4 > 0.0D) ? 45 : -45);
      } 
      d5 = 0.0D;
      if (d4 > 0.0D) {
        d4 = 1.0D;
      } else if (d4 < 0.0D) {
        d4 = -1.0D;
      } 
    } 
    d3 = d4 * d * Math.cos(Math.toRadians((f + 90.0F))) + d5 * d * Math.sin(Math.toRadians((f + 90.0F)));
    d2 = d4 * d * Math.sin(Math.toRadians((f + 90.0F))) - d5 * d * Math.cos(Math.toRadians((f + 90.0F)));
    return new double[] { d3, d2 };
  }
}
