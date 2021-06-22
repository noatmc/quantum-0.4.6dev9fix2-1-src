package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.features.modules.render.NoRender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelBiped.class})
public class MixinModelBiped {
  @Inject(method = {"render"}, at = {@At("HEAD")}, cancellable = true)
  public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
    if (entityIn instanceof net.minecraft.entity.monster.EntityPigZombie && ((Boolean)(NoRender.getInstance()).pigmen.getValue()).booleanValue())
      ci.cancel(); 
  }
}
