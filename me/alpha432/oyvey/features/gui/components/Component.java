package me.alpha432.oyvey.features.gui.components;

import java.util.ArrayList;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.gui.components.items.Item;
import me.alpha432.oyvey.features.gui.components.items.buttons.Button;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Component extends Feature {
  public static int[] counter1 = new int[] { 1 };
  
  private final ArrayList<Item> items = new ArrayList<>();
  
  public boolean drag;
  
  private int x;
  
  private int y;
  
  private int x2;
  
  private int y2;
  
  private int width;
  
  private int height;
  
  private boolean open;
  
  private final int barHeight;
  
  private boolean hidden = false;
  
  private int startcolor;
  
  private int endcolor;
  
  public Component(String name, int x, int y, boolean open) {
    super(name);
    this.hidden = false;
    this.x = x;
    this.y = y;
    this.width = 88;
    this.height = 18;
    this.barHeight = 15;
    this.open = open;
    setupItems();
  }
  
  public void setupItems() {}
  
  private void drag(int mouseX, int mouseY) {
    if (!this.drag)
      return; 
    this.x = this.x2 + mouseX;
    this.y = this.y2 + mouseY;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drag(mouseX, mouseY);
    counter1 = new int[] { 1 };
    float totalItemHeight = this.open ? (getTotalItemHeight() - 2.0F) : 0.0F;
    if (((Boolean)(ClickGui.getInstance()).rainbowg.getValue()).booleanValue()) {
      if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
        this.startcolor = ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB();
        this.endcolor = ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB();
      } 
    } else {
      this.startcolor = ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).g_red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).g_green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).g_blue.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).g_alpha.getValue()).intValue());
    } 
    this.endcolor = ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).g_red1.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).g_green1.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).g_blue1.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).g_alpha1.getValue()).intValue());
    RenderUtil.drawRect(this.x, this.y, (this.x + this.width), (this.y + this.height - 5), ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue(), 255));
    RenderUtil.drawGradientSideways((this.x - 1), this.y, (this.x + this.width + 1), ((this.y + this.barHeight) - 2.0F), this.startcolor, this.endcolor);
    if (this.open) {
      RenderUtil.drawGradientSideways((this.x - 1), (this.y + 13.2F), (this.x + this.width + 1), (this.y + totalItemHeight + 19.0F), this.startcolor, this.endcolor);
      RenderUtil.drawRect(this.x, this.y + 13.2F, (this.x + this.width), (this.y + this.height) + totalItemHeight, ColorUtil.toRGBA(0, 0, 0, ((Integer)(ClickGui.getInstance()).alphaBox.getValue()).intValue()));
    } 
    OyVey.textManager.drawStringWithShadow(getName(), this.x + 3.0F, this.y - 4.0F - OyVeyGui.getClickGui().getTextOffset(), -1);
    if (this.open) {
      float y = (getY() + getHeight()) - 3.0F;
      for (Item item : getItems()) {
        counter1[0] = counter1[0] + 1;
        if (item.isHidden())
          continue; 
        item.setLocation(this.x + 2.0F, y);
        item.setWidth(getWidth() - 4);
        item.drawScreen(mouseX, mouseY, partialTicks);
        y += item.getHeight() + 1.5F;
      } 
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
      this.x2 = this.x - mouseX;
      this.y2 = this.y - mouseY;
      OyVeyGui.getClickGui().getComponents().forEach(component -> {
            if (component.drag)
              component.drag = false; 
          });
      this.drag = true;
      return;
    } 
    if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
      this.open = !this.open;
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      return;
    } 
    if (!this.open)
      return; 
    getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
  }
  
  public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    if (releaseButton == 0)
      this.drag = false; 
    if (!this.open)
      return; 
    getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    if (!this.open)
      return; 
    getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
  }
  
  public void addButton(Button button) {
    this.items.add(button);
  }
  
  public int getX() {
    return this.x;
  }
  
  public void setX(int x) {
    this.x = x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public void setY(int y) {
    this.y = y;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public boolean isHidden() {
    return this.hidden;
  }
  
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
  
  public boolean isOpen() {
    return this.open;
  }
  
  public final ArrayList<Item> getItems() {
    return this.items;
  }
  
  private boolean isHovering(int mouseX, int mouseY) {
    return (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (this.open ? 2 : 0));
  }
  
  private float getTotalItemHeight() {
    float height = 0.0F;
    for (Item item : getItems())
      height += item.getHeight() + 1.5F; 
    return height;
  }
  
  static {
    counter1 = new int[] { 1 };
  }
}
