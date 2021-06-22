package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
  private final Setting<Mode> mode;
  
  private final Setting<Boolean> guiMove;
  
  boolean sneaking;
  
  public NoSlow() {
    super("NoSlow", "ns", Module.Category.MOVEMENT, true, false, false);
    this.mode = register(new Setting("Mode", Mode.Normal));
    this.guiMove = register(new Setting("GuiMove", Boolean.valueOf(true)));
  }
  
  private static final KeyBinding[] keys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint };
  
  public void onUpdate() {
    if (nullCheck())
      return; 
    if (((Boolean)this.guiMove.getValue()).booleanValue()) {
      for (KeyBinding bind : keys)
        KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode())); 
      if (mc.currentScreen == null)
        for (KeyBinding bind : keys) {
          if (!Keyboard.isKeyDown(bind.getKeyCode()))
            KeyBinding.setKeyBindState(bind.getKeyCode(), false); 
        }  
    } 
    if (this.mode.getValue() == Mode.Strict) {
      Item item = mc.player.getActiveItemStack().getItem();
      if (this.sneaking && ((!mc.player.isHandActive() && item instanceof net.minecraft.item.ItemFood) || item instanceof net.minecraft.item.ItemBow || item instanceof net.minecraft.item.ItemPotion || !(item instanceof net.minecraft.item.ItemFood) || !(item instanceof net.minecraft.item.ItemBow) || !(item instanceof net.minecraft.item.ItemPotion))) {
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.sneaking = false;
      } 
    } 
    if (mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) && ((Boolean)this.guiMove.getValue()).booleanValue()) {
      if (mc.currentScreen instanceof me.alpha432.oyvey.features.gui.OyVeyGui && !((Boolean)this.guiMove.getValue()).booleanValue())
        return; 
      if (Keyboard.isKeyDown(200))
        mc.player.rotationPitch -= 5.0F; 
      if (Keyboard.isKeyDown(208))
        mc.player.rotationPitch += 5.0F; 
      if (Keyboard.isKeyDown(205))
        mc.player.rotationYaw += 5.0F; 
      if (Keyboard.isKeyDown(203))
        mc.player.rotationYaw -= 5.0F; 
      if (mc.player.rotationPitch > 90.0F)
        mc.player.rotationPitch = 90.0F; 
      if (mc.player.rotationPitch < -90.0F)
        mc.player.rotationPitch = -90.0F; 
    } 
  }
  
  @SubscribeEvent
  public void onUseItem(LivingEntityUseItemEvent event) {
    if (this.mode.getValue() == Mode.Strict && 
      !this.sneaking) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      this.sneaking = true;
    } 
  }
  
  @SubscribeEvent
  public void onInputUpdate(InputUpdateEvent event) {
    if (this.mode.getValue() == Mode.Normal && 
      mc.player.isHandActive() && !mc.player.isRiding()) {
      (event.getMovementInput()).moveStrafe *= 5.0F;
      (event.getMovementInput()).moveForward *= 5.0F;
    } 
  }
  
  public enum Mode {
    Normal, Strict;
  }
}
