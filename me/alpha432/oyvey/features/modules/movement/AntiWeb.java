package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.BlockCollisionBoundingBoxEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiWeb extends Module {
  public Setting<Boolean> disableBB = register(new Setting("AddBB", Boolean.valueOf(true)));
  
  public Setting<Float> bbOffset = register(new Setting("BBOffset", Float.valueOf(0.4F), Float.valueOf(-2.0F), Float.valueOf(2.0F)));
  
  public Setting<Boolean> onGround = register(new Setting("On Ground", Boolean.valueOf(true)));
  
  public Setting<Float> motionY = register(new Setting("Set MotionY", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(20.0F)));
  
  public Setting<Float> motionX = register(new Setting("Set MotionX", Float.valueOf(0.8F), Float.valueOf(-1.0F), Float.valueOf(5.0F)));
  
  public AntiWeb() {
    super("AntiWeb", "aw", Module.Category.MOVEMENT, true, false, false);
  }
  
  @SubscribeEvent
  public void bbEvent(BlockCollisionBoundingBoxEvent event) {
    if (nullCheck())
      return; 
    if (mc.world.getBlockState(event.getPos()).getBlock() instanceof net.minecraft.block.BlockWeb && (
      (Boolean)this.disableBB.getValue()).booleanValue()) {
      event.setCanceledE(true);
      event.setBoundingBox(Block.FULL_BLOCK_AABB.contract(0.0D, ((Float)this.bbOffset.getValue()).floatValue(), 0.0D));
    } 
  }
  
  public void onUpdate() {
    if (OyVey.moduleManager.isModuleEnabled("WebTP"))
      return; 
    if ((mc.player.isInWeb && !OyVey.moduleManager.isModuleEnabled("Step")) || (mc.player.isInWeb && !OyVey.moduleManager.isModuleEnabled("StepTwo"))) {
      if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
        mc.player.isInWeb = true;
        mc.player.motionY *= ((Float)this.motionY.getValue()).floatValue();
      } else if (((Boolean)this.onGround.getValue()).booleanValue()) {
        mc.player.onGround = false;
      } 
      if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode) || Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode) || Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.keyCode) || Keyboard.isKeyDown(mc.gameSettings.keyBindRight.keyCode)) {
        mc.player.isInWeb = false;
        mc.player.motionX *= ((Float)this.motionX.getValue()).floatValue();
        mc.player.motionZ *= ((Float)this.motionX.getValue()).floatValue();
      } 
    } 
  }
}
