package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.network.Packet;

public class PacketLogger extends Module {
  private Packet[] packets;
  
  public Setting<Boolean> incoming = register(new Setting("Incoming", Boolean.valueOf(true)));
  
  public Setting<Boolean> outgoing = register(new Setting("Outgoing", Boolean.valueOf(true)));
  
  public Setting<Boolean> data = register(new Setting("Data", Boolean.valueOf(true)));
  
  public PacketLogger() {
    super("PacketLogger", "", Module.Category.MISC, true, false, false);
  }
}
