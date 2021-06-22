package me.alpha432.oyvey.features.modules.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Trails extends Module {
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.5F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Map<Entity, List<Vec3d>> renderMap = new HashMap<>();
  
  public Trails() {
    super("Trails", "Draws trails on projectiles", Module.Category.RENDER, true, false, false);
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    for (Entity entity : mc.world.loadedEntityList) {
      if (!(entity instanceof net.minecraft.entity.projectile.EntityThrowable) && !(entity instanceof net.minecraft.entity.projectile.EntityArrow))
        continue; 
      List<Vec3d> vectors = (this.renderMap.get(entity) != null) ? this.renderMap.get(entity) : new ArrayList<>();
      vectors.add(new Vec3d(entity.posX, entity.posY, entity.posZ));
      this.renderMap.put(entity, vectors);
    } 
  }
  
  public void onRender3D(Render3DEvent event) {
    for (Entity entity : mc.world.loadedEntityList) {
      if (!this.renderMap.containsKey(entity))
        continue; 
      GlStateManager.pushMatrix();
      RenderUtil.GLPre(((Float)this.lineWidth.getValue()).floatValue());
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GL11.glColor4f(((Integer)this.red.getValue()).intValue() / 255.0F, ((Integer)this.green.getValue()).intValue() / 255.0F, ((Integer)this.blue.getValue()).intValue() / 255.0F, ((Integer)this.alpha.getValue()).intValue() / 255.0F);
      GL11.glLineWidth(((Float)this.lineWidth.getValue()).floatValue());
      GL11.glBegin(1);
      for (int i = 0; i < ((List)this.renderMap.get(entity)).size() - 1; i++) {
        GL11.glVertex3d(((Vec3d)((List)this.renderMap.get(entity)).get(i)).x, ((Vec3d)((List)this.renderMap.get(entity)).get(i)).y, ((Vec3d)((List)this.renderMap.get(entity)).get(i)).z);
        GL11.glVertex3d(((Vec3d)((List)this.renderMap.get(entity)).get(i + 1)).x, ((Vec3d)((List)this.renderMap.get(entity)).get(i + 1)).y, ((Vec3d)((List)this.renderMap.get(entity)).get(i + 1)).z);
      } 
      GL11.glEnd();
      GlStateManager.resetColor();
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      RenderUtil.GlPost();
      GlStateManager.popMatrix();
    } 
  }
}
