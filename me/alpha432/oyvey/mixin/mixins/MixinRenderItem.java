package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.render.GlintModify;
import me.alpha432.oyvey.features.modules.render.ViewModChanger;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderItem.class})
public class MixinRenderItem {
  @Shadow
  private void renderModel(IBakedModel model, int color, ItemStack stack) {}
  
  @ModifyArg(method = {"renderEffect"}, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index = 1)
  private int renderEffect(int oldValue) {
    return OyVey.moduleManager.getModuleByName("GlintModify").isEnabled() ? GlintModify.getColor(1L, 1.0F).getRGB() : oldValue;
  }
  
  @Inject(method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"}, at = {@At("INVOKE")})
  public void renderItem(ItemStack stack, EntityLivingBase entitylivingbaseIn, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
    if (ViewModChanger.getInstance().isEnabled() && (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)) {
      ViewModChanger changer = ViewModChanger.getInstance();
      float size = ((Float)changer.size.getValue()).floatValue() / 10.0F;
      GL11.glScalef(size, size, size);
      if (transform.equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND)) {
        GL11.glTranslated((((Float)changer.offhandX.getValue()).floatValue() / 4.0F), (((Float)changer.offhandY.getValue()).floatValue() / 4.0F), (((Float)changer.offhandZ.getValue()).floatValue() / 4.0F));
      } else {
        GL11.glTranslated((((Float)changer.offsetX.getValue()).floatValue() / 4.0F), (((Float)changer.offsetY.getValue()).floatValue() / 4.0F), (((Float)changer.offsetZ.getValue()).floatValue() / 4.0F));
      } 
    } 
  }
}