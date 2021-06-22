package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Crosshair extends Module {
  private final Setting<Boolean> dynamic;
  
  private final Setting<Float> width;
  
  private final Setting<Float> gap;
  
  private final Setting<Float> length;
  
  private final Setting<Float> dynamicGap;
  
  private final Setting<Integer> red;
  
  private final Setting<Integer> green;
  
  private final Setting<Integer> blue;
  
  private final Setting<Integer> alpha;
  
  public static Crosshair INSTANCE;
  
  public Crosshair() {
    super("Crosshair", "", Module.Category.RENDER, true, false, false);
    this.dynamic = register(new Setting("Dynamic", Boolean.valueOf(true)));
    this.width = register(new Setting("Width", Float.valueOf(1.0F), Float.valueOf(0.5F), Float.valueOf(10.0F)));
    this.gap = register(new Setting("Gap", Float.valueOf(3.0F), Float.valueOf(0.5F), Float.valueOf(10.0F)));
    this.length = register(new Setting("Length", Float.valueOf(7.0F), Float.valueOf(0.5F), Float.valueOf(100.0F)));
    this.dynamicGap = register(new Setting("DynamicGap", Float.valueOf(1.5F), Float.valueOf(0.5F), Float.valueOf(10.0F)));
    this.red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    this.green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    this.blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    this.alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    INSTANCE = this;
  }
  
  @SubscribeEvent
  public void onRender2D(Render2DEvent event) {
    int color = (new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue())).getRGB();
    ScaledResolution resolution = new ScaledResolution(mc);
    float middlex = resolution.getScaledWidth() / 2.0F;
    float middley = resolution.getScaledHeight() / 2.0F;
    RenderUtil.drawBordered(middlex - ((Float)this.width.getValue()).floatValue(), middley - ((Float)this.gap.getValue()).floatValue() + ((Float)this.length.getValue()).floatValue() - ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), middlex + ((Float)this.width.getValue()).floatValue(), middley - ((Float)this.gap.getValue()).floatValue() - ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), 0.5F, color, -16777216);
    RenderUtil.drawBordered(middlex - ((Float)this.width.getValue()).floatValue(), middley + ((Float)this.gap.getValue()).floatValue() + ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), middlex + ((Float)this.width.getValue()).floatValue(), middley + ((Float)this.gap.getValue()).floatValue() + ((Float)this.length.getValue()).floatValue() + ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), 0.5F, color, -16777216);
    RenderUtil.drawBordered(middlex - ((Float)this.gap.getValue()).floatValue() + ((Float)this.length.getValue()).floatValue() - ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), middley - ((Float)this.width.getValue()).floatValue(), middlex - ((Float)this.gap.getValue()).floatValue() - ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), middley + ((Float)this.width.getValue()).floatValue(), 0.5F, color, -16777216);
    RenderUtil.drawBordered(middlex + ((Float)this.gap.getValue()).floatValue() + ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), middley - ((Float)this.width.getValue()).floatValue(), middlex + ((Float)this.gap.getValue()).floatValue() + ((Float)this.length.getValue()).floatValue() + ((isMoving() && ((Boolean)this.dynamic.getValue()).booleanValue()) ? ((Float)this.dynamicGap.getValue()).floatValue() : 0.0F), middley + ((Float)this.width.getValue()).floatValue(), 0.5F, color, -16777216);
  }
  
  public boolean isMoving() {
    return (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F || mc.player.moveVertical != 0.0F);
  }
  
  public int color(int index, int count) {
    float[] hsb = new float[3];
    Color.RGBtoHSB(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), hsb);
    float brightness = Math.abs((getOffset() + index / count * 2.0F) % 2.0F - 1.0F);
    brightness = 0.4F + 0.4F * brightness;
    hsb[2] = brightness % 1.0F;
    Color clr = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    return (new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), ((Integer)this.alpha.getValue()).intValue())).getRGB();
  }
  
  private static float getOffset() {
    return (float)(System.currentTimeMillis() % 2000L) / 1000.0F;
  }
}
