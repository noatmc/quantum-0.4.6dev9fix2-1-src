package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.RenderEntityModelEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CrystalScale extends Module {
  public static CrystalScale INSTANCE;
  
  public Setting<Boolean> animateScale = register(new Setting("AnimateScale", Boolean.valueOf(false)));
  
  public Setting<Boolean> chams = register(new Setting("Chams", Boolean.valueOf(false)));
  
  public Setting<Boolean> throughWalls = register(new Setting("ThroughWalls", Boolean.valueOf(true)));
  
  public Setting<Boolean> wireframeThroughWalls = register(new Setting("WireThroughWalls", Boolean.valueOf(true)));
  
  public Setting<Boolean> glint = register(new Setting("Glint", Boolean.valueOf(false), v -> ((Boolean)this.chams.getValue()).booleanValue()));
  
  public Setting<Boolean> wireframe = register(new Setting("Wireframe", Boolean.valueOf(false)));
  
  public Setting<Float> scale = register(new Setting("Scale", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(10.0F)));
  
  public Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false)));
  
  public Setting<Integer> saturation = register(new Setting("Saturation", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> brightness = register(new Setting("Brightness", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> speed = register(new Setting("Speed", Integer.valueOf(40), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> xqz = register(new Setting("XQZ", Boolean.valueOf(false), v -> (!((Boolean)this.rainbow.getValue()).booleanValue() && ((Boolean)this.throughWalls.getValue()).booleanValue())));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> hiddenRed = register(new Setting("Hidden Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> hiddenGreen = register(new Setting("Hidden Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> hiddenBlue = register(new Setting("Hidden Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> hiddenAlpha = register(new Setting("Hidden Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Map<EntityEnderCrystal, Float> scaleMap = new ConcurrentHashMap<>();
  
  public CrystalScale() {
    super("CrystalMod", "Modifies crystal rendering in different ways", Module.Category.RENDER, true, false, false);
    INSTANCE = this;
  }
  
  public void onUpdate() {
    for (Entity crystal : mc.world.loadedEntityList) {
      if (!(crystal instanceof EntityEnderCrystal))
        continue; 
      if (!this.scaleMap.containsKey(crystal)) {
        this.scaleMap.put((EntityEnderCrystal)crystal, Float.valueOf(3.125E-4F));
      } else {
        this.scaleMap.put((EntityEnderCrystal)crystal, Float.valueOf(((Float)this.scaleMap.get(crystal)).floatValue() + 3.125E-4F));
      } 
      if (((Float)this.scaleMap.get(crystal)).floatValue() < 0.0625F * ((Float)this.scale.getValue()).floatValue())
        continue; 
      this.scaleMap.remove(crystal);
    } 
  }
  
  @SubscribeEvent
  public void onReceivePacket(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketDestroyEntities) {
      SPacketDestroyEntities packet = (SPacketDestroyEntities)event.getPacket();
      for (int id : packet.getEntityIDs()) {
        Entity entity = mc.world.getEntityByID(id);
        if (entity instanceof EntityEnderCrystal)
          this.scaleMap.remove(entity); 
      } 
    } 
  }
  
  public void onRenderModel(RenderEntityModelEvent event) {
    if (event.getStage() != 0 || !(event.entity instanceof EntityEnderCrystal) || !((Boolean)this.wireframe.getValue()).booleanValue())
      return; 
    Color color = ((Boolean)this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)this.saturation.getValue()).intValue()) : EntityUtil.getColor(event.entity, ((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue(), false);
    boolean fancyGraphics = mc.gameSettings.fancyGraphics;
    mc.gameSettings.fancyGraphics = false;
    float gamma = mc.gameSettings.gammaSetting;
    mc.gameSettings.gammaSetting = 10000.0F;
    GL11.glPushMatrix();
    GL11.glPushAttrib(1048575);
    GL11.glPolygonMode(1032, 6913);
    GL11.glDisable(3553);
    GL11.glDisable(2896);
    if (((Boolean)this.wireframeThroughWalls.getValue()).booleanValue())
      GL11.glDisable(2929); 
    GL11.glEnable(2848);
    GL11.glEnable(3042);
    GlStateManager.blendFunc(770, 771);
    GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    GlStateManager.glLineWidth(((Float)this.lineWidth.getValue()).floatValue());
    event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }
}
