package me.alpha432.oyvey.features.modules.player;

import java.util.Objects;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Yaw extends Module {
  public Setting<Boolean> lockYaw = register(new Setting("LockYaw", Boolean.valueOf(false)));
  
  public Setting<Boolean> byDirection = register(new Setting("ByDirection", Boolean.valueOf(false)));
  
  public Setting<Direction> direction = register(new Setting("Direction", Direction.NORTH, v -> ((Boolean)this.byDirection.getValue()).booleanValue()));
  
  public Setting<Integer> yaw = register(new Setting("Yaw", Integer.valueOf(0), Integer.valueOf(-180), Integer.valueOf(180), v -> !((Boolean)this.byDirection.getValue()).booleanValue()));
  
  public Setting<Boolean> lockPitch = register(new Setting("LockPitch", Boolean.valueOf(false)));
  
  public Setting<Integer> pitch = register(new Setting("Pitch", Integer.valueOf(0), Integer.valueOf(-180), Integer.valueOf(180)));
  
  public Yaw() {
    super("Yaw", "Locks your yaw", Module.Category.PLAYER, true, false, false);
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (((Boolean)this.lockYaw.getValue()).booleanValue())
      if (((Boolean)this.byDirection.getValue()).booleanValue()) {
        switch ((Direction)this.direction.getValue()) {
          case NORTH:
            setYaw(180);
            break;
          case NE:
            setYaw(225);
            break;
          case EAST:
            setYaw(270);
            break;
          case SE:
            setYaw(315);
            break;
          case SOUTH:
            setYaw(0);
            break;
          case SW:
            setYaw(45);
            break;
          case WEST:
            setYaw(90);
            break;
          case NW:
            setYaw(135);
            break;
        } 
      } else {
        setYaw(((Integer)this.yaw.getValue()).intValue());
      }  
    if (((Boolean)this.lockPitch.getValue()).booleanValue()) {
      if (mc.player.isRiding())
        ((Entity)Objects.requireNonNull((T)mc.player.getRidingEntity())).rotationPitch = ((Integer)this.pitch.getValue()).intValue(); 
      mc.player.rotationPitch = ((Integer)this.pitch.getValue()).intValue();
    } 
  }
  
  private void setYaw(int yaw) {
    if (mc.player.isRiding())
      ((Entity)Objects.requireNonNull((T)mc.player.getRidingEntity())).rotationYaw = yaw; 
    mc.player.rotationYaw = yaw;
  }
  
  public enum Direction {
    NORTH, NE, EAST, SE, SOUTH, SW, WEST, NW;
  }
}
