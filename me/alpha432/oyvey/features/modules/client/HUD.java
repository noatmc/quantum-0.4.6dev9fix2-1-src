package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.HudUtil;
import me.alpha432.oyvey.util.ItemUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.TextUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
  private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
  
  private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
  
  private static RenderItem itemRender;
  
  private static HUD INSTANCE = new HUD();
  
  private final Setting<Boolean> grayNess = register(new Setting("Gray", Boolean.valueOf(true)));
  
  private final Setting<Boolean> renderingUp = register(new Setting("RenderingUp", Boolean.valueOf(false)));
  
  private final Setting<Boolean> watermark = register(new Setting("Watermark", Boolean.valueOf(false)));
  
  public final Setting<Watermark> mode = register(new Setting("Mode", Watermark.None, v -> ((Boolean)this.watermark.getValue()).booleanValue()));
  
  private final Setting<String> customWatermark = register(new Setting("WatermarkName", "Quantum v0.4.6", v -> ((Boolean)this.watermark.getValue()).booleanValue()));
  
  private final Setting<Integer> wateX = register(new Setting("WPosX", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(600), v -> ((Boolean)this.watermark.getValue()).booleanValue()));
  
  private final Setting<Integer> wateY = register(new Setting("WPosY", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(530), v -> ((Boolean)this.watermark.getValue()).booleanValue()));
  
  private final Setting<Boolean> arrayList = register(new Setting("ActiveModules", Boolean.valueOf(true)));
  
  private final Setting<Float> arraylistx = register(new Setting("PosX", Float.valueOf(2.0F), Float.valueOf(0.0F), Float.valueOf(40.0F), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  private final Setting<Integer> arraylisty = register(new Setting("PosY", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(530), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  public Setting<Integer> animationHorizontalTime = register(new Setting("HorizontalTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  public Setting<Integer> animationVerticalTime = register(new Setting("VerticalTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  private final Setting<Boolean> pvpInfo = register(new Setting("PvpInfo", Boolean.valueOf(false)));
  
  private final Setting<Integer> pvpY = register(new Setting("PPosY", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(250), v -> ((Boolean)this.pvpInfo.getValue()).booleanValue()));
  
  private final Setting<Boolean> friendlist = register(new Setting("FriendList", Boolean.valueOf(false)));
  
  private final Setting<Integer> friendlistx = register(new Setting("FPosX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(600), v -> ((Boolean)this.friendlist.getValue()).booleanValue()));
  
  private final Setting<Integer> friendlisty = register(new Setting("FPosY", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(530), v -> ((Boolean)this.friendlist.getValue()).booleanValue()));
  
  private final Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(false)));
  
  private final Setting<Integer> coordsposy = register(new Setting("CPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(530), v -> ((Boolean)this.coords.getValue()).booleanValue()));
  
  private final Setting<Boolean> direction = register(new Setting("Direction", Boolean.valueOf(false)));
  
  private final Setting<Integer> directionposy = register(new Setting("DPosY", Integer.valueOf(4), Integer.valueOf(0), Integer.valueOf(530), v -> ((Boolean)this.direction.getValue()).booleanValue()));
  
  private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(false)));
  
  private final Setting<Boolean> totems = register(new Setting("Totems", Boolean.valueOf(false)));
  
  private final Setting<Greeter> greeter = register(new Setting("Greeter", Greeter.None));
  
  private final Setting<String> spoofGreeter = register(new Setting("GreeterName", "Welcome to Quantum", v -> (this.greeter.getValue() == Greeter.Custom)));
  
  private final Setting<Integer> greeterposy = register(new Setting("GPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), v -> (this.greeter.getValue() == Greeter.Custom)));
  
  private final Setting<Boolean> speed = register(new Setting("Speed", Boolean.valueOf(false)));
  
  private final Setting<Boolean> potions = register(new Setting("Potions", Boolean.valueOf(false)));
  
  private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(false)));
  
  private final Setting<Boolean> tps = register(new Setting("TPS", Boolean.valueOf(false)));
  
  private final Setting<Boolean> fps = register(new Setting("FPS", Boolean.valueOf(false)));
  
  private final Setting<Boolean> lag = register(new Setting("LagNotifier", Boolean.valueOf(false)));
  
  public Setting<String> command = register(new Setting("Command", "6ixGod+"));
  
  public Setting<TextUtil.Color> bracketColor = register(new Setting("BracketColor", TextUtil.Color.RED));
  
  public Setting<TextUtil.Color> commandColor = register(new Setting("NameColor", TextUtil.Color.GRAY));
  
  public Setting<String> commandBracket = register(new Setting("BracketL", "["));
  
  public Setting<String> commandBracket2 = register(new Setting("BracketR", "]"));
  
  public Setting<Boolean> notifyToggles = register(new Setting("ToggleNotifs", Boolean.valueOf(false)));
  
  public Setting<Boolean> magenDavid = register(new Setting("FutureGUI", Boolean.valueOf(false)));
  
  public Setting<RenderingMode> renderingMode = register(new Setting("Ordering", RenderingMode.Alphabet));
  
  public Setting<Boolean> time = register(new Setting("Time", Boolean.valueOf(false), "The time"));
  
  public Setting<Integer> lagTime = register(new Setting("LagTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(2000)));
  
  public static Entity target;
  
  private int color;
  
  public float hue;
  
  private boolean shouldIncrement;
  
  private int hitMarkerTimer;
  
  private final Timer timer = new Timer();
  
  private final Map<String, Integer> players = new HashMap<>();
  
  private int startcolor1;
  
  private int endcolor1;
  
  public enum Page {
    Default, SemiCustom, Custom;
  }
  
  public HUD() {
    super("HUD", "hud", Module.Category.CLIENT, true, false, false);
    setInstance();
  }
  
  public static HUD getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HUD(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onUpdate() {
    if (this.shouldIncrement)
      this.hitMarkerTimer++; 
    if (this.hitMarkerTimer == 10) {
      this.hitMarkerTimer = 0;
      this.shouldIncrement = false;
    } 
  }
  
  public void onRender2D(Render2DEvent event) {
    if (fullNullCheck())
      return; 
    if (((Boolean)this.friendlist.getValue()).booleanValue()) {
      this.color = ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue());
      renderFriends();
    } 
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    this.color = ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue());
    if (((Boolean)this.watermark.getValue()).booleanValue())
      if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue()) {
        if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
          this.renderer.drawString((String)this.customWatermark.getValue(), ((Integer)this.wateX.getValue()).intValue(), ((Integer)this.wateY.getValue()).intValue(), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
        } else {
          int[] arrayOfInt = { 1 };
          char[] stringToCharArray = ((String)this.customWatermark.getValue()).toCharArray();
          float f = 0.0F;
          for (char c : stringToCharArray) {
            this.renderer.drawString(String.valueOf(c), ((Integer)this.wateX.getValue()).intValue() + f, ((Integer)this.wateY.getValue()).intValue(), ColorUtil.rainbow(arrayOfInt[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
            f += this.renderer.getStringWidth(String.valueOf(c));
            arrayOfInt[0] = arrayOfInt[0] + 1;
          } 
        } 
      } else {
        this.renderer.drawString((String)this.customWatermark.getValue(), ((Integer)this.wateX.getValue()).intValue(), ((Integer)this.wateY.getValue()).intValue(), this.color, true);
      }  
    if (((Boolean)this.pvpInfo.getValue()).booleanValue()) {
      OyVey.textManager.drawStringWithShadow("Quantum", 1.0F, (((Integer)this.pvpY.getValue()).intValue() - 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color);
      String caOn = "CA" + ChatFormatting.GREEN + " ON";
      String caOff = "CA" + ChatFormatting.RED + " OFF";
      this.color = ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue());
      if (OyVey.moduleManager.getModuleByName("AutoCrystal").isEnabled() || OyVey.moduleManager.getModuleByName("CrystalAura").isEnabled())
        if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue()) {
          if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
            this.renderer.drawString(caOn, 2.0F, ((Integer)this.pvpY.getValue()).intValue(), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
          } else {
            int[] arrayOfInt = { 1 };
            char[] stringToCharArray = caOn.toCharArray();
            float f = 0.0F;
            for (char c : stringToCharArray) {
              this.renderer.drawString(String.valueOf(c), 2.0F + f, ((Integer)this.pvpY.getValue()).intValue(), ColorUtil.rainbow(arrayOfInt[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
              f += this.renderer.getStringWidth(String.valueOf(c));
              arrayOfInt[0] = arrayOfInt[0] + 1;
            } 
          } 
        } else {
          this.renderer.drawString(caOn, 2.0F, ((Integer)this.pvpY.getValue()).intValue(), this.color, true);
        }  
      if ((OyVey.moduleManager.getModuleByName("AutoCrystal").isDisabled() && !OyVey.moduleManager.getModuleByName("CrystalAura").isEnabled()) || (OyVey.moduleManager.getModuleByName("CrystalAura").isDisabled() && !OyVey.moduleManager.getModuleByName("AutoCrystal").isEnabled()))
        if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue()) {
          if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
            this.renderer.drawString(caOff, 2.0F, ((Integer)this.pvpY.getValue()).intValue(), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
          } else {
            int[] arrayOfInt = { 1 };
            char[] stringToCharArray = caOff.toCharArray();
            float f = 0.0F;
            for (char c : stringToCharArray) {
              this.renderer.drawString(String.valueOf(c), 2.0F + f, ((Integer)this.pvpY.getValue()).intValue(), ColorUtil.rainbow(arrayOfInt[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
              f += this.renderer.getStringWidth(String.valueOf(c));
              arrayOfInt[0] = arrayOfInt[0] + 1;
            } 
          } 
        } else {
          this.renderer.drawString(caOff, 2.0F, ((Integer)this.pvpY.getValue()).intValue(), this.color, true);
        }  
      String totnull = "" + ChatFormatting.RED + "0";
      String totslot = "" + ChatFormatting.GREEN + String.valueOf(ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING));
      if (ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) != 0) {
        if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static || (ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Sideway)
          this.renderer.drawString(totslot, 2.0F, (((Integer)this.pvpY.getValue()).intValue() + 10), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true); 
      } else if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static || (ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Sideway) {
        this.renderer.drawString(totnull, 2.0F, (((Integer)this.pvpY.getValue()).intValue() + 10), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
      } 
      String pingg = "" + ChatFormatting.GREEN + HudUtil.getPingSatus();
      if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
        this.renderer.drawString(pingg, 2.0F, (((Integer)this.pvpY.getValue()).intValue() + 20), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
      } else {
        this.renderer.drawString(pingg, 2.0F, (((Integer)this.pvpY.getValue()).intValue() + 20), this.color, true);
      } 
    } 
    int[] counter1 = { 1 };
    int j = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && !((Boolean)this.renderingUp.getValue()).booleanValue()) ? 14 : 0;
    if (((Boolean)this.arrayList.getValue()).booleanValue())
      if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
        if (this.renderingMode.getValue() == RenderingMode.Alphabet) {
          for (int k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); k++) {
            String str = OyVey.moduleManager.sortedModulesABC.get(k);
            this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (2 + j * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
            j++;
            counter1[0] = counter1[0] + 1;
          } 
        } else {
          for (int k = 0; k < OyVey.moduleManager.sortedModules.size(); k++) {
            Module module = OyVey.moduleManager.sortedModules.get(k);
            String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
            this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (2 + j * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
            j++;
            counter1[0] = counter1[0] + 1;
          } 
        } 
      } else if (this.renderingMode.getValue() == RenderingMode.Alphabet) {
        for (int k = 0; k < OyVey.moduleManager.sortedModulesABC.size(); k++) {
          String str = OyVey.moduleManager.sortedModulesABC.get(k);
          j += 10;
          this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (height - j), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
      } else {
        for (int k = 0; k < OyVey.moduleManager.sortedModules.size(); k++) {
          Module module = OyVey.moduleManager.sortedModules.get(k);
          String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
          j += 10;
          this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (height - j), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
      }  
    String grayString = ((Boolean)this.grayNess.getValue()).booleanValue() ? String.valueOf(ChatFormatting.GRAY) : "";
    int i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && ((Boolean)this.renderingUp.getValue()).booleanValue()) ? 13 : (((Boolean)this.renderingUp.getValue()).booleanValue() ? -2 : 0);
    if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
      if (((Boolean)this.potions.getValue()).booleanValue()) {
        List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
        for (PotionEffect potionEffect : effects) {
          String str = OyVey.potionManager.getColoredPotionString(potionEffect);
          i += 10;
          this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), potionEffect.getPotion().getLiquidColor(), true);
        } 
      } 
      if (((Boolean)this.speed.getValue()).booleanValue()) {
        String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
        i += 10;
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
        counter1[0] = counter1[0] + 1;
      } 
      if (((Boolean)this.time.getValue()).booleanValue()) {
        String str = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
        i += 10;
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
        counter1[0] = counter1[0] + 1;
      } 
      if (((Boolean)this.tps.getValue()).booleanValue()) {
        String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
        i += 10;
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
        counter1[0] = counter1[0] + 1;
      } 
      String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
      String str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
      if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          i += 10;
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          i += 10;
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
      } else {
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          i += 10;
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          i += 10;
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
      } 
    } else {
      if (((Boolean)this.potions.getValue()).booleanValue()) {
        List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
        for (PotionEffect potionEffect : effects) {
          String str = OyVey.potionManager.getColoredPotionString(potionEffect);
          this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), potionEffect.getPotion().getLiquidColor(), true);
        } 
      } 
      if (((Boolean)this.speed.getValue()).booleanValue()) {
        String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
        counter1[0] = counter1[0] + 1;
      } 
      if (((Boolean)this.time.getValue()).booleanValue()) {
        String str = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
        counter1[0] = counter1[0] + 1;
      } 
      if (((Boolean)this.tps.getValue()).booleanValue()) {
        String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
        counter1[0] = counter1[0] + 1;
      } 
      String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
      String str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing();
      if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
      } else {
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), ((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
          counter1[0] = counter1[0] + 1;
        } 
      } 
    } 
    boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
    int posX = (int)mc.player.posX;
    int posY = (int)mc.player.posY;
    int posZ = (int)mc.player.posZ;
    float nether = !inHell ? 0.125F : 8.0F;
    int hposX = (int)(mc.player.posX * nether);
    int hposZ = (int)(mc.player.posZ * nether);
    i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0;
    String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET) : (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]"));
    String direction = ((Boolean)this.direction.getValue()).booleanValue() ? OyVey.rotationManager.getDirection4D(false) : "";
    String coords = ((Boolean)this.coords.getValue()).booleanValue() ? coordinates : "";
    i += 10;
    if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue()) {
      String rainbowCoords = ((Boolean)this.coords.getValue()).booleanValue() ? ("XYZ " + (inHell ? (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]"))) : "";
      if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
        this.renderer.drawString(direction, 2.0F, (height - i - 11), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
        this.renderer.drawString(rainbowCoords, 2.0F, (height - i), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
      } else {
        int[] counter2 = { 1 };
        char[] stringToCharArray = direction.toCharArray();
        float s = 0.0F;
        for (char c : stringToCharArray) {
          this.renderer.drawString(String.valueOf(c), 2.0F + s, (height - i - 11), ColorUtil.rainbow(counter2[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
          s += this.renderer.getStringWidth(String.valueOf(c));
          counter2[0] = counter2[0] + 1;
        } 
        int[] counter3 = { 1 };
        char[] stringToCharArray2 = rainbowCoords.toCharArray();
        float u = 0.0F;
        for (char c : stringToCharArray2) {
          this.renderer.drawString(String.valueOf(c), 2.0F + u, (height - i), ColorUtil.rainbow(counter3[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
          u += this.renderer.getStringWidth(String.valueOf(c));
          counter3[0] = counter3[0] + 1;
        } 
      } 
    } else {
      this.renderer.drawString(direction, 2.0F, (height - i - 11), this.color, true);
      this.renderer.drawString(coords, 2.0F, (height - i), this.color, true);
    } 
    if (((Boolean)this.armor.getValue()).booleanValue())
      renderArmorHUD(true); 
    if (((Boolean)this.totems.getValue()).booleanValue())
      renderTotemHUD(); 
    if (this.greeter.getValue() != Greeter.None)
      renderGreeter(); 
    if (((Boolean)this.lag.getValue()).booleanValue())
      renderLag(); 
  }
  
  public Map<String, Integer> getTextRadarPlayers() {
    return EntityUtil.getTextRadarPlayers();
  }
  
  public void renderGreeter() {
    int width = this.renderer.scaledWidth;
    String text = "";
    this.color = ColorUtil.toRGBA(((Integer)(ClickGui.getInstance()).red.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).green.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).blue.getValue()).intValue());
    switch ((Greeter)this.greeter.getValue()) {
      case Custom:
        text = text + (String)this.spoofGreeter.getValue();
        break;
    } 
    if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue()) {
      if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Sideway)
        this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, ((Integer)this.greeterposy.getValue()).intValue(), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true); 
      if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
        this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, ((Integer)this.greeterposy.getValue()).intValue(), ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
      } else {
        int[] arrayOfInt = { 1 };
        char[] stringToCharArray = text.toCharArray();
        float f = 0.0F;
        for (char c : stringToCharArray) {
          this.renderer.drawString(String.valueOf(c), width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
          f += this.renderer.getStringWidth(String.valueOf(c));
          arrayOfInt[0] = arrayOfInt[0] + 1;
        } 
      } 
    } else {
      this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, this.color, true);
    } 
  }
  
  public void renderLag() {
    int width = this.renderer.scaledWidth;
    if (OyVey.serverManager.isServerNotResponding()) {
      String text = ChatFormatting.RED + "Server lagging for " + MathUtil.round((float)OyVey.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
      this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, this.color, true);
    } 
  }
  
  public void renderTotemHUD() {
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::func_190916_E).sum();
    if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
      totems += mc.player.getHeldItemOffhand().getCount(); 
    if (totems > 0) {
      GlStateManager.enableTexture2D();
      int i = width / 2;
      int iteration = 0;
      int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
      int x = i - 189 + 180 + 2;
      GlStateManager.enableDepth();
      RenderUtil.itemRender.zLevel = 200.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
      RenderUtil.itemRender.zLevel = 0.0F;
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      this.renderer.drawStringWithShadow(totems + "", (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (y + 9), 16777215);
      GlStateManager.enableDepth();
      GlStateManager.disableLighting();
    } 
  }
  
  public void renderArmorHUD(boolean percent) {
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    GlStateManager.enableTexture2D();
    int i = width / 2;
    int iteration = 0;
    int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
    for (ItemStack is : mc.player.inventory.armorInventory) {
      iteration++;
      if (is.isEmpty())
        continue; 
      int x = i - 90 + (9 - iteration) * 20 + 2;
      GlStateManager.enableDepth();
      RenderUtil.itemRender.zLevel = 200.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
      RenderUtil.itemRender.zLevel = 0.0F;
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
      this.renderer.drawStringWithShadow(s, (x + 19 - 2 - this.renderer.getStringWidth(s)), (y + 9), 16777215);
      if (percent) {
        int dmg = 0;
        int itemDurability = is.getMaxDamage() - is.getItemDamage();
        float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
        float red = 1.0F - green;
        if (percent) {
          dmg = 100 - (int)(red * 100.0F);
        } else {
          dmg = itemDurability;
        } 
        this.renderer.drawStringWithShadow(dmg + "", (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (y - 11), ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0));
      } 
    } 
    GlStateManager.enableDepth();
    GlStateManager.disableLighting();
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(AttackEntityEvent event) {
    this.shouldIncrement = true;
  }
  
  public void onLoad() {
    OyVey.commandManager.setClientMessage(getCommandMessage());
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      equals(event.getSetting().getFeature()))
      OyVey.commandManager.setClientMessage(getCommandMessage()); 
  }
  
  public String getCommandMessage() {
    return TextUtil.coloredString((String)this.commandBracket.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue()) + TextUtil.coloredString((String)this.command.getPlannedValue(), (TextUtil.Color)this.commandColor.getPlannedValue()) + TextUtil.coloredString((String)this.commandBracket2.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue());
  }
  
  public String getRainbowCommandMessage() {
    StringBuilder stringBuilder = new StringBuilder(getRawCommandMessage());
    stringBuilder.insert(0, "§+");
    stringBuilder.append("§r");
    return stringBuilder.toString();
  }
  
  public String getRawCommandMessage() {
    return (String)this.commandBracket.getValue() + (String)this.command.getValue() + (String)this.commandBracket2.getValue();
  }
  
  public void drawTextRadar(int yOffset) {
    if (!this.players.isEmpty()) {
      int y = this.renderer.getFontHeight() + 7 + yOffset;
      for (Map.Entry<String, Integer> player : this.players.entrySet()) {
        String text = (String)player.getKey() + " ";
        int textheight = this.renderer.getFontHeight() + 1;
        this.renderer.drawString(text, 2.0F, y, this.color, true);
        y += textheight;
      } 
    } 
  }
  
  private void renderFriends() {
    List<String> friends = new ArrayList<>();
    for (EntityPlayer player : mc.world.playerEntities) {
      if (OyVey.friendManager.isFriend(player.getName()))
        friends.add(player.getName()); 
    } 
    if (((Boolean)(ClickGui.getInstance()).rainbow.getValue()).booleanValue()) {
      int y = ((Integer)this.friendlisty.getValue()).intValue();
      int x = ((Integer)this.friendlistx.getValue()).intValue();
      if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static)
        if (friends.isEmpty()) {
          this.renderer.drawString("No friends online", x, y, ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
        } else {
          this.renderer.drawString("Friends:", x, y, ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
          y += 12;
          for (String friend : friends) {
            this.renderer.drawString(friend, x, y, ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
            y += 12;
          } 
        }  
    } else {
      int y = ((Integer)this.friendlisty.getValue()).intValue();
      int x = ((Integer)this.friendlistx.getValue()).intValue();
      if (friends.isEmpty()) {
        this.renderer.drawString("No friends online", x, y, this.color, true);
      } else {
        this.renderer.drawString("Friends:", x, y, this.color, true);
        y += 12;
        for (String friend : friends) {
          this.renderer.drawString(friend, x, y, this.color, true);
          y += 12;
        } 
      } 
    } 
  }
  
  public enum RenderingMode {
    Length, Alphabet;
  }
  
  public enum Greeter {
    None, Custom;
  }
  
  public enum Watermark {
    None, Custom;
  }
}
