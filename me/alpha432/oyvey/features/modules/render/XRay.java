package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XRay extends Module {
  private static XRay INSTANCE = new XRay();
  
  public Setting<String> newBlock = register(new Setting("NewBlock", "Add Block..."));
  
  public Setting<Boolean> showBlocks = register(new Setting("ShowBlocks", Boolean.valueOf(false)));
  
  public XRay() {
    super("XRay", "Lets you look through walls.", Module.Category.RENDER, false, false, true);
    setInstance();
  }
  
  public static XRay getInstance() {
    if (INSTANCE == null)
      INSTANCE = new XRay(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onEnable() {
    mc.renderGlobal.loadRenderers();
  }
  
  public void onDisable() {
    mc.renderGlobal.loadRenderers();
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this))
      if (event.getSetting().equals(this.newBlock) && !shouldRender((String)this.newBlock.getPlannedValue())) {
        register(new Setting((String)this.newBlock.getPlannedValue(), Boolean.valueOf(true), v -> ((Boolean)this.showBlocks.getValue()).booleanValue()));
        Command.sendMessage("<Xray> Added new Block: " + (String)this.newBlock.getPlannedValue());
        if (isOn())
          mc.renderGlobal.loadRenderers(); 
        event.setCanceled(true);
      } else {
        Setting setting = event.getSetting();
        if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newBlock) || setting.equals(this.showBlocks))
          return; 
        if (setting.getValue() instanceof Boolean && !((Boolean)setting.getPlannedValue()).booleanValue()) {
          unregister(setting);
          if (isOn())
            mc.renderGlobal.loadRenderers(); 
          event.setCanceled(true);
        } 
      }  
  }
  
  public boolean shouldRender(Block block) {
    return shouldRender(block.getLocalizedName());
  }
  
  public boolean shouldRender(String name) {
    for (Setting setting : getSettings()) {
      if (!name.equalsIgnoreCase(setting.getName()))
        continue; 
      return true;
    } 
    return false;
  }
}
