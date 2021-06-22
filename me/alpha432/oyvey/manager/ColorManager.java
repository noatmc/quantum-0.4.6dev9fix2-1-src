package me.alpha432.oyvey.manager;

import java.awt.Color;
import me.alpha432.oyvey.features.gui.components.Component;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.util.ColorUtil;

public class ColorManager {
  private float red = 1.0F;
  
  private float green = 1.0F;
  
  private float blue = 1.0F;
  
  private float alpha = 1.0F;
  
  private Color color = new Color(this.red, this.green, this.blue, this.alpha);
  
  private static Color colorRealth = new Color(255, 255, 255);
  
  public Color getColor() {
    return this.color;
  }
  
  public void setColor(Color color) {
    this.color = color;
  }
  
  public int getColorAsInt() {
    return ColorUtil.toRGBA(this.color);
  }
  
  public int getColorAsIntFullAlpha() {
    return ColorUtil.toRGBA(new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 255));
  }
  
  public int getColorWithAlpha(int alpha) {
    if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue())
      return ColorUtil.rainbow(Component.counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(); 
    return ColorUtil.toRGBA(new Color(this.red, this.green, this.blue, alpha / 255.0F));
  }
  
  public void setColor(float red, float green, float blue, float alpha) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
    updateColor();
  }
  
  public void updateColor() {
    setColor(new Color(this.red, this.green, this.blue, this.alpha));
  }
  
  public void setColor(int red, int green, int blue, int alpha) {
    this.red = red / 255.0F;
    this.green = green / 255.0F;
    this.blue = blue / 255.0F;
    this.alpha = alpha / 255.0F;
    updateColor();
  }
  
  public void setRed(float red) {
    this.red = red;
    updateColor();
  }
  
  public void setGreen(float green) {
    this.green = green;
    updateColor();
  }
  
  public void setBlue(float blue) {
    this.blue = blue;
    updateColor();
  }
  
  public void setAlpha(float alpha) {
    this.alpha = alpha;
    updateColor();
  }
  
  public static int toRGBA(Color color) {
    return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }
  
  public static int toARGB(int r, int g, int b, int a) {
    return (new Color(r, g, b, a)).getRGB();
  }
  
  public static int toRGBA(double r, double g, double b, double a) {
    return toRGBA((float)r, (float)g, (float)b, (float)a);
  }
  
  public static int toRGBA(float r, float g, float b, float a) {
    return toRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
  }
  
  public static int toRGBA(float[] colors) {
    if (colors.length != 4)
      throw new IllegalArgumentException("colors[] must have a length of 4!"); 
    return toRGBA(colors[0], colors[1], colors[2], colors[3]);
  }
  
  public static int toRGBA(double[] colors) {
    if (colors.length != 4)
      throw new IllegalArgumentException("colors[] must have a length of 4!"); 
    return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
  }
  
  public static int toRGBA(int r, int g, int b) {
    return toRGBA(r, g, b, 255);
  }
  
  public static int toRGBA(int r, int g, int b, int a) {
    return (r << 16) + (g << 8) + b + (a << 24);
  }
  
  public static Color getColorRealth() {
    return colorRealth;
  }
}
