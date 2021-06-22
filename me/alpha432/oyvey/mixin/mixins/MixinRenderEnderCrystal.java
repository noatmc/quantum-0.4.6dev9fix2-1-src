package me.alpha432.oyvey.mixin.mixins;

import java.awt.Color;
import me.alpha432.oyvey.event.events.RenderEntityModelEvent;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.modules.render.CrystalScale;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
  @Shadow
  @Final
  private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
  
  @Redirect(method = {"doRender"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
  public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    if (CrystalScale.INSTANCE.isEnabled())
      if (((Boolean)CrystalScale.INSTANCE.animateScale.getValue()).booleanValue() && CrystalScale.INSTANCE.scaleMap.containsKey(entity)) {
        GlStateManager.scale(((Float)CrystalScale.INSTANCE.scaleMap.get(entity)).floatValue(), ((Float)CrystalScale.INSTANCE.scaleMap.get(entity)).floatValue(), ((Float)CrystalScale.INSTANCE.scaleMap.get(entity)).floatValue());
      } else {
        GlStateManager.scale(((Float)CrystalScale.INSTANCE.scale.getValue()).floatValue(), ((Float)CrystalScale.INSTANCE.scale.getValue()).floatValue(), ((Float)CrystalScale.INSTANCE.scale.getValue()).floatValue());
      }  
    if (CrystalScale.INSTANCE.isEnabled() && ((Boolean)CrystalScale.INSTANCE.wireframe.getValue()).booleanValue()) {
      RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      CrystalScale.INSTANCE.onRenderModel(event);
    } 
    if (CrystalScale.INSTANCE.isEnabled() && ((Boolean)CrystalScale.INSTANCE.chams.getValue()).booleanValue()) {
      GL11.glPushAttrib(1048575);
      GL11.glDisable(3008);
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(1.5F);
      GL11.glEnable(2960);
      if (((Boolean)CrystalScale.INSTANCE.rainbow.getValue()).booleanValue()) {
        Color rainbowColor1 = ((Boolean)CrystalScale.INSTANCE.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(RenderUtil.getRainbow(((Integer)CrystalScale.INSTANCE.speed.getValue()).intValue() * 100, 0, ((Integer)CrystalScale.INSTANCE.saturation.getValue()).intValue() / 100.0F, ((Integer)CrystalScale.INSTANCE.brightness.getValue()).intValue() / 100.0F));
        Color rainbowColor = EntityUtil.getColor(entity, rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue(), ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue(), true);
        if (((Boolean)CrystalScale.INSTANCE.throughWalls.getValue()).booleanValue()) {
          GL11.glDisable(2929);
          GL11.glDepthMask(false);
        } 
        GL11.glEnable(10754);
        GL11.glColor4f(rainbowColor.getRed() / 255.0F, rainbowColor.getGreen() / 255.0F, rainbowColor.getBlue() / 255.0F, ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue() / 255.0F);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (((Boolean)CrystalScale.INSTANCE.throughWalls.getValue()).booleanValue()) {
          GL11.glEnable(2929);
          GL11.glDepthMask(true);
        } 
      } else if (((Boolean)CrystalScale.INSTANCE.xqz.getValue()).booleanValue() && ((Boolean)CrystalScale.INSTANCE.throughWalls.getValue()).booleanValue()) {
        Color hiddenColor = ((Boolean)CrystalScale.INSTANCE.rainbow.getValue()).booleanValue() ? EntityUtil.getColor(entity, ((Integer)CrystalScale.INSTANCE.hiddenRed.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.hiddenGreen.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.hiddenBlue.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.hiddenAlpha.getValue()).intValue(), true) : EntityUtil.getColor(entity, ((Integer)CrystalScale.INSTANCE.hiddenRed.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.hiddenGreen.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.hiddenBlue.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.hiddenAlpha.getValue()).intValue(), true);
        Color visibleColor = ((Boolean)CrystalScale.INSTANCE.rainbow.getValue()).booleanValue() ? EntityUtil.getColor(entity, ((Integer)CrystalScale.INSTANCE.red.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.green.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.blue.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue(), true) : EntityUtil.getColor(entity, ((Integer)CrystalScale.INSTANCE.red.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.green.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.blue.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue(), true), color = visibleColor;
        if (((Boolean)CrystalScale.INSTANCE.throughWalls.getValue()).booleanValue()) {
          GL11.glDisable(2929);
          GL11.glDepthMask(false);
        } 
        GL11.glEnable(10754);
        GL11.glColor4f(hiddenColor.getRed() / 255.0F, hiddenColor.getGreen() / 255.0F, hiddenColor.getBlue() / 255.0F, ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue() / 255.0F);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (((Boolean)CrystalScale.INSTANCE.throughWalls.getValue()).booleanValue()) {
          GL11.glEnable(2929);
          GL11.glDepthMask(true);
        } 
        GL11.glColor4f(visibleColor.getRed() / 255.0F, visibleColor.getGreen() / 255.0F, visibleColor.getBlue() / 255.0F, ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue() / 255.0F);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      } else {
        Color visibleColor = ((Boolean)CrystalScale.INSTANCE.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : EntityUtil.getColor(entity, ((Integer)CrystalScale.INSTANCE.red.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.green.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.blue.getValue()).intValue(), ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue(), true), color = visibleColor;
        if (((Boolean)CrystalScale.INSTANCE.throughWalls.getValue()).booleanValue()) {
          GL11.glDisable(2929);
          GL11.glDepthMask(false);
        } 
        GL11.glEnable(10754);
        GL11.glColor4f(visibleColor.getRed() / 255.0F, visibleColor.getGreen() / 255.0F, visibleColor.getBlue() / 255.0F, ((Integer)CrystalScale.INSTANCE.alpha.getValue()).intValue() / 255.0F);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (((Boolean)CrystalScale.INSTANCE.throughWalls.getValue()).booleanValue()) {
          GL11.glEnable(2929);
          GL11.glDepthMask(true);
        } 
      } 
      GL11.glEnable(3042);
      GL11.glEnable(2896);
      GL11.glEnable(3553);
      GL11.glEnable(3008);
      GL11.glPopAttrib();
      if (((Boolean)CrystalScale.INSTANCE.glint.getValue()).booleanValue()) {
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 0.0F, 0.0F, 0.13F);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.disableAlpha();
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
      } 
    } else {
      model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    } 
    if (CrystalScale.INSTANCE.isEnabled())
      if (((Boolean)CrystalScale.INSTANCE.animateScale.getValue()).booleanValue() && CrystalScale.INSTANCE.scaleMap.containsKey(entity)) {
        GlStateManager.scale(1.0F / ((Float)CrystalScale.INSTANCE.scaleMap.get(entity)).floatValue(), 1.0F / ((Float)CrystalScale.INSTANCE.scaleMap.get(entity)).floatValue(), 1.0F / ((Float)CrystalScale.INSTANCE.scaleMap.get(entity)).floatValue());
      } else {
        GlStateManager.scale(1.0F / ((Float)CrystalScale.INSTANCE.scale.getValue()).floatValue(), 1.0F / ((Float)CrystalScale.INSTANCE.scale.getValue()).floatValue(), 1.0F / ((Float)CrystalScale.INSTANCE.scale.getValue()).floatValue());
      }  
  }
  
  private static ResourceLocation glint = new ResourceLocation("textures/glint.png");
}
