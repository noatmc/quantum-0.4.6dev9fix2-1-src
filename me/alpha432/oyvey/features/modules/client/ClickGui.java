package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.Gui));
  
  private static ClickGui INSTANCE = new ClickGui();
  
  public Setting<String> prefix = register(new Setting("Prefix", ".", v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Boolean> customFov = register(new Setting("CustomFov", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Float> fov = register(new Setting("Fov", Float.valueOf(150.0F), Float.valueOf(-180.0F), Float.valueOf(180.0F), v -> (this.setting.getValue() == Settings.Gui && ((Boolean)this.customFov.getValue()).booleanValue())));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Integer> hoverAlpha = register(new Setting("Alpha", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Integer> alphaBox = register(new Setting("AlphaBox", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Integer> alpha = register(new Setting("HoverAlpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Gui)));
  
  public Setting<rainbowMode> rainbowModeHud = register(new Setting("HUD", rainbowMode.Static, v -> (((Boolean)this.rainbow.getValue()).booleanValue() && this.setting.getValue() == Settings.Gui)));
  
  public Setting<rainbowModeArray> rainbowModeA = register(new Setting("ArrayList", rainbowModeArray.Static, v -> (((Boolean)this.rainbow.getValue()).booleanValue() && this.setting.getValue() == Settings.Gui)));
  
  public Setting<Integer> rainbowHue = register(new Setting("Delay", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(600), v -> (((Boolean)this.rainbow.getValue()).booleanValue() && this.setting.getValue() == Settings.Gui)));
  
  public Setting<Float> rainbowBrightness = register(new Setting("Brightness ", Float.valueOf(255.0F), Float.valueOf(1.0F), Float.valueOf(255.0F), v -> (((Boolean)this.rainbow.getValue()).booleanValue() && this.setting.getValue() == Settings.Gui)));
  
  public Setting<Float> rainbowSaturation = register(new Setting("Saturation", Float.valueOf(100.0F), Float.valueOf(1.0F), Float.valueOf(255.0F), v -> (((Boolean)this.rainbow.getValue()).booleanValue() && this.setting.getValue() == Settings.Gui)));
  
  public Setting<Boolean> rainbowg = register(new Setting("Rainbow", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Boolean> guiComponent = register(new Setting("Gui Component", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_red = register(new Setting("RedL", Integer.valueOf(105), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_green = register(new Setting("GreenL", Integer.valueOf(162), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_blue = register(new Setting("BlueL", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_red1 = register(new Setting("RedR", Integer.valueOf(143), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_green1 = register(new Setting("GreenR", Integer.valueOf(140), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_blue1 = register(new Setting("BlueR", Integer.valueOf(213), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_alpha = register(new Setting("AlphaL", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Integer> g_alpha1 = register(new Setting("AlphaR", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Gradient)));
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.COLOR, v -> (this.setting.getValue() == Settings.Background)));
  
  public Setting<Integer> backgroundAlpha = register(new Setting("Background Alpha", Integer.valueOf(160), Integer.valueOf(0), Integer.valueOf(255), v -> (this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background)));
  
  public Setting<Integer> gb_red = register(new Setting("RedBG", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(255), v -> (this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background)));
  
  public Setting<Integer> gb_green = register(new Setting("GreenBG", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(255), v -> (this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background)));
  
  public Setting<Integer> gb_blue = register(new Setting("BlueBG", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(255), v -> (this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background)));
  
  private int color;
  
  public ClickGui() {
    super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
    setInstance();
  }
  
  public static ClickGui getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ClickGui(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onUpdate() {
    if (((Boolean)this.customFov.getValue()).booleanValue())
      mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, ((Float)this.fov.getValue()).floatValue()); 
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
      if (event.getSetting().equals(this.prefix)) {
        OyVey.commandManager.setPrefix((String)this.prefix.getPlannedValue());
        Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + OyVey.commandManager.getPrefix());
      } 
      OyVey.colorManager.setColor(((Integer)this.red.getPlannedValue()).intValue(), ((Integer)this.green.getPlannedValue()).intValue(), ((Integer)this.blue.getPlannedValue()).intValue(), ((Integer)this.hoverAlpha.getPlannedValue()).intValue());
    } 
  }
  
  public void onEnable() {
    mc.displayGuiScreen((GuiScreen)OyVeyGui.getClickGui());
  }
  
  public void onLoad() {
    OyVey.colorManager.setColor(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.hoverAlpha.getValue()).intValue());
    OyVey.commandManager.setPrefix((String)this.prefix.getValue());
  }
  
  public void onRender2D(Render2DEvent event) {
    drawBackground();
  }
  
  public void drawBackground() {
    if (this.mode.getValue() == Mode.COLOR)
      if (getInstance().isEnabled()) {
        RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(((Integer)this.gb_red.getValue()).intValue(), ((Integer)this.gb_green.getValue()).intValue(), ((Integer)this.gb_blue.getValue()).intValue(), ((Integer)this.backgroundAlpha.getValue()).intValue()));
      } else {
        RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(0, 0, 0, 0));
      }  
    if (this.mode.getValue() == Mode.NONE)
      if (getInstance().isEnabled()) {
        RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(((Integer)this.gb_red.getValue()).intValue(), ((Integer)this.gb_green.getValue()).intValue(), ((Integer)this.gb_blue.getValue()).intValue(), ((Integer)this.backgroundAlpha.getValue()).intValue()));
      } else {
        RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(0, 0, 0, 0));
      }  
  }
  
  public void onTick() {
    if (!(mc.currentScreen instanceof OyVeyGui))
      disable(); 
  }
  
  public void onDisable() {
    if (mc.currentScreen instanceof OyVeyGui)
      Util.mc.displayGuiScreen(null); 
  }
  
  public enum rainbowModeArray {
    Static, Up;
  }
  
  public enum rainbowMode {
    Static, Sideway;
  }
  
  public enum Settings {
    Gui, Gradient, Background;
  }
  
  public enum Mode {
    COLOR, BLUR, NONE;
  }
  
  public final int getColor() {
    return this.color;
  }
}
