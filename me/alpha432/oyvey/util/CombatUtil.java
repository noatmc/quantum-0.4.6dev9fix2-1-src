package me.alpha432.oyvey.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class CombatUtil implements Util {
  public static EntityPlayer getTarget(float range) {
    EntityPlayer currentTarget = null;
    int size = Util.mc.world.playerEntities.size();
    for (int i = 0; i < size; i++) {
      EntityPlayer player = Util.mc.world.playerEntities.get(i);
      if (!EntityUtil.isntValid((Entity)player, range))
        if (currentTarget == null) {
          currentTarget = player;
        } else if (Util.mc.player.getDistanceSq((Entity)player) < Util.mc.player.getDistanceSq((Entity)currentTarget)) {
          currentTarget = player;
        }  
    } 
    return currentTarget;
  }
}
