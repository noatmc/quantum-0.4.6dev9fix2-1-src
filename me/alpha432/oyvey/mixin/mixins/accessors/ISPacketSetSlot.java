package me.alpha432.oyvey.mixin.mixins.accessors;

import net.minecraft.network.play.server.SPacketSetSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SPacketSetSlot.class})
public interface ISPacketSetSlot {
  @Accessor("windowId")
  int getId();
  
  @Accessor("windowId")
  void setWindowId(int paramInt);
}
