package me.alpha432.oyvey.mixin.mixins.accessors;

import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Container.class})
public interface IContainer {
  @Accessor("transactionID")
  void setTransactionID(short paramShort);
}
