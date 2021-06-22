package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Util;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class BackTP extends Module {
  private final Setting<RubbeMode> mode;
  
  private final Setting<Integer> Ym;
  
  public BackTP() {
    super("BackTP", "Teleports u to the latest ground pos", Module.Category.MOVEMENT, true, false, false);
    this.mode = register(new Setting("Mode", RubbeMode.Motion));
    this.Ym = register(new Setting("Motion", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(15), v -> (this.mode.getValue() == RubbeMode.Motion)));
  }
  
  public void onEnable() {
    if (Feature.fullNullCheck())
      return; 
  }
  
  public void onUpdate() {
    switch ((RubbeMode)this.mode.getValue()) {
      case Motion:
        Util.mc.player.motionY = ((Integer)this.Ym.getValue()).intValue();
        break;
      case Packet:
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + ((Integer)this.Ym.getValue()).intValue(), mc.player.posZ, true));
        break;
      case Teleport:
        mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY + ((Integer)this.Ym.getValue()).intValue(), mc.player.posZ);
        break;
    } 
    toggle();
  }
  
  public enum RubbeMode {
    Motion, Teleport, Packet;
  }
}
