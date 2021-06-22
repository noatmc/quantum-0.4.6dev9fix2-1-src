package me.alpha432.oyvey.mixin.mixins;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import me.alpha432.oyvey.MinecraftInstance;
import me.alpha432.oyvey.event.events.PerspectiveEvent;
import me.alpha432.oyvey.features.modules.client.Notifiers;
import me.alpha432.oyvey.features.modules.misc.NoHitBox;
import me.alpha432.oyvey.features.modules.player.Speedmine;
import me.alpha432.oyvey.features.modules.render.CameraClip;
import me.alpha432.oyvey.features.modules.render.NoRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {
  private boolean injection = true;
  
  @Shadow
  public ItemStack itemActivationItem;
  
  @Shadow
  @Final
  public Minecraft mc;
  
  @Shadow
  public abstract void getMouseOver(float paramFloat);
  
  @Redirect(method = {"getMouseOver"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
  public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
    if (NoHitBox.getINSTANCE().isOn() && (((Minecraft.getMinecraft()).player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe && ((Boolean)(NoHitBox.getINSTANCE()).pickaxe.getValue()).booleanValue()) || ((Minecraft.getMinecraft()).player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && ((Boolean)(NoHitBox.getINSTANCE()).crystal.getValue()).booleanValue()) || ((Minecraft.getMinecraft()).player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && ((Boolean)(NoHitBox.getINSTANCE()).gapple.getValue()).booleanValue()) || (Minecraft.getMinecraft()).player.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL || (Minecraft.getMinecraft()).player.getHeldItemMainhand().getItem() == Items.TNT_MINECART))
      return new ArrayList<>(); 
    return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
  }
  
  @Redirect(method = {"setupCameraTransform"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
  private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
    PerspectiveEvent event = new PerspectiveEvent(MinecraftInstance.mc.displayWidth / MinecraftInstance.mc.displayHeight);
    MinecraftForge.EVENT_BUS.post((Event)event);
    Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
  }
  
  @Redirect(method = {"renderWorldPass"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
  private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
    PerspectiveEvent event = new PerspectiveEvent(MinecraftInstance.mc.displayWidth / MinecraftInstance.mc.displayHeight);
    MinecraftForge.EVENT_BUS.post((Event)event);
    Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
  }
  
  @Redirect(method = {"renderCloudsCheck"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
  private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
    PerspectiveEvent event = new PerspectiveEvent(MinecraftInstance.mc.displayWidth / MinecraftInstance.mc.displayHeight);
    MinecraftForge.EVENT_BUS.post((Event)event);
    Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
  }
  
  @Inject(method = {"renderItemActivation"}, at = {@At("HEAD")}, cancellable = true)
  public void renderItemActivationHook(CallbackInfo info) {
    if (this.itemActivationItem != null && NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).totemPops.getValue()).booleanValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING)
      info.cancel(); 
  }
  
  @Inject(method = {"updateLightmap"}, at = {@At("HEAD")}, cancellable = true)
  private void updateLightmap(float partialTicks, CallbackInfo info) {
    if (NoRender.getInstance().isOn() && ((NoRender.getInstance()).skylight.getValue() == NoRender.Skylight.ENTITY || (NoRender.getInstance()).skylight.getValue() == NoRender.Skylight.ALL))
      info.cancel(); 
  }
  
  @Inject(method = {"getMouseOver(F)V"}, at = {@At("HEAD")}, cancellable = true)
  public void getMouseOverHook(float partialTicks, CallbackInfo info) {
    if (this.injection) {
      info.cancel();
      this.injection = false;
      try {
        getMouseOver(partialTicks);
      } catch (Exception e) {
        e.printStackTrace();
        if (Notifiers.getInstance().isOn() && ((Boolean)(Notifiers.getInstance()).crash.getValue()).booleanValue())
          Notifiers.displayCrash(e); 
      } 
      this.injection = true;
    } 
  }
  
  @Redirect(method = {"setupCameraTransform"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
  public float prevTimeInPortalHook(EntityPlayerSP entityPlayerSP) {
    if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).nausea.getValue()).booleanValue())
      return -3.4028235E38F; 
    return entityPlayerSP.prevTimeInPortal;
  }
  
  @Inject(method = {"setupFog"}, at = {@At("HEAD")}, cancellable = true)
  public void setupFogHook(int startCoords, float partialTicks, CallbackInfo info) {
    if (NoRender.getInstance().isOn() && (NoRender.getInstance()).fog.getValue() == NoRender.Fog.NOFOG)
      info.cancel(); 
  }
  
  @Redirect(method = {"setupFog"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
  public IBlockState getBlockStateAtEntityViewpointHook(World worldIn, Entity entityIn, float p_186703_2_) {
    if (NoRender.getInstance().isOn() && (NoRender.getInstance()).fog.getValue() == NoRender.Fog.AIR)
      return Blocks.AIR.defaultBlockState; 
    return ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn, entityIn, p_186703_2_);
  }
  
  @Inject(method = {"hurtCameraEffect"}, at = {@At("HEAD")}, cancellable = true)
  public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
    if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).hurtcam.getValue()).booleanValue())
      info.cancel(); 
  }
  
  @Redirect(method = {"getMouseOver"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
  public List<Entity> getEntitiesInAABBexcludingHook(WorldClient worldClient, @Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
    if (Speedmine.getInstance().isOn() && ((Boolean)(Speedmine.getInstance()).noTrace.getValue()).booleanValue() && (!((Boolean)(Speedmine.getInstance()).pickaxe.getValue()).booleanValue() || this.mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe))
      return new ArrayList<>(); 
    if (Speedmine.getInstance().isOn() && ((Boolean)(Speedmine.getInstance()).noTrace.getValue()).booleanValue() && ((Boolean)(Speedmine.getInstance()).noGapTrace.getValue()).booleanValue() && this.mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)
      return new ArrayList<>(); 
    return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
  }
  
  @ModifyVariable(method = {"orientCamera"}, ordinal = 3, at = @At(value = "STORE", ordinal = 0), require = 1)
  public double changeCameraDistanceHook(double range) {
    return (CameraClip.getInstance().isEnabled() && ((Boolean)(CameraClip.getInstance()).extend.getValue()).booleanValue()) ? ((Double)(CameraClip.getInstance()).distance.getValue()).doubleValue() : range;
  }
  
  @ModifyVariable(method = {"orientCamera"}, ordinal = 7, at = @At(value = "STORE", ordinal = 0), require = 1)
  public double orientCameraHook(double range) {
    return (CameraClip.getInstance().isEnabled() && ((Boolean)(CameraClip.getInstance()).extend.getValue()).booleanValue()) ? ((Double)(CameraClip.getInstance()).distance.getValue()).doubleValue() : ((CameraClip.getInstance().isEnabled() && !((Boolean)(CameraClip.getInstance()).extend.getValue()).booleanValue()) ? 4.0D : range);
  }
}
