package me.alpha432.oyvey.util;

import net.minecraft.client.Minecraft;

public interface Util {
  public static final Minecraft mc = Minecraft.getMinecraft();
  
  static double[] directionSpeed(double speed) {
    float forward = (Wrapper.INSTANCE.mc()).player.movementInput.moveForward;
    float side = (Wrapper.INSTANCE.mc()).player.movementInput.moveStrafe;
    float yaw = (Wrapper.INSTANCE.mc()).player.prevRotationYaw + ((Wrapper.INSTANCE.mc()).player.rotationYaw - (Wrapper.INSTANCE.mc()).player.prevRotationYaw) * Wrapper.INSTANCE.mc().getRenderPartialTicks();
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
}
