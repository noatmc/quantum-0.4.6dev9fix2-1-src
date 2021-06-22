package me.alpha432.oyvey.features.modules.client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.DeathEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.combat.AutoCrystal;
import me.alpha432.oyvey.features.modules.combat.CrystalAura;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.manager.FileManager;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.TextUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Chat extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.Chat));
  
  private final Setting<String> suffix = register(new Setting("Mode", "Quantum", v -> (this.setting.getValue() == Settings.Chat)));
  
  public Setting<Boolean> killmsg = register(new Setting("AutoGG", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Chat)));
  
  private final Setting<Integer> targetResetTimer = register(new Setting("Reset", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(90), v -> ((Boolean)this.killmsg.getValue()).booleanValue()));
  
  private final Setting<Integer> delay = register(new Setting("KillDelay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(30), v -> ((Boolean)this.killmsg.getValue()).booleanValue()));
  
  public Map<EntityPlayer, Integer> targets = new ConcurrentHashMap<>();
  
  private static final String path = "Quantum/killsmg.txt";
  
  public List<String> messages = new ArrayList<>();
  
  private final Timer cooldownTimer = new Timer();
  
  public EntityPlayer cauraTarget;
  
  private boolean cooldown;
  
  private final Timer delayTimer = new Timer();
  
  public Setting<TextUtil.Color> timeStamps = register(new Setting("Time", TextUtil.Color.NONE, v -> (this.setting.getValue() == Settings.Visual)));
  
  public Setting<TextUtil.Color> bracket = register(new Setting("Bracket", TextUtil.Color.WHITE, v -> (this.setting.getValue() == Settings.Visual && this.timeStamps.getValue() != TextUtil.Color.NONE && this.setting.getValue() == Settings.Visual)));
  
  public Setting<Boolean> space = register(new Setting("Space", Boolean.valueOf(true), v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
  
  public Setting<Boolean> all = register(new Setting("All", Boolean.valueOf(false), v -> (this.timeStamps.getValue() != TextUtil.Color.NONE && this.setting.getValue() == Settings.Visual)));
  
  public Setting<Boolean> clean = register(new Setting("CleanChat", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Visual)));
  
  public Setting<Boolean> infinite = register(new Setting("Infinite", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Visual)));
  
  private final Timer timer = new Timer();
  
  public boolean check;
  
  private static Chat INSTANCE = new Chat();
  
  public Chat() {
    super("Chat", "c", Module.Category.CLIENT, true, false, false);
    setInstance();
    File file = new File("Quantum/killsmg.txt");
    if (!file.exists())
      try {
        file.createNewFile();
      } catch (Exception e) {
        e.printStackTrace();
      }  
  }
  
  public static Chat getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Chat(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  @SubscribeEvent
  public void onUpdate(UpdateEvent event) {
    if (mc.player == null)
      return; 
    if (this.delayTimer.passedMs(((Integer)this.delay.getValue()).intValue())) {
      mc.player.sendChatMessage("I just walked " + ThreadLocalRandom.current().nextInt(1, 31) + "!");
      this.delayTimer.reset();
    } 
  }
  
  public void onEnable() {
    loadMessages();
    this.timer.reset();
    this.cooldownTimer.reset();
  }
  
  public void onTick() {
    if (AutoCrystal.target != null && this.cauraTarget != AutoCrystal.target)
      this.cauraTarget = AutoCrystal.target; 
    if (CrystalAura.currentTarget != null && this.cauraTarget != CrystalAura.currentTarget)
      this.cauraTarget = CrystalAura.currentTarget; 
    if (!this.cooldown)
      this.cooldownTimer.reset(); 
    if (this.cooldownTimer.passedS(((Integer)this.delay.getValue()).intValue()) && this.cooldown) {
      this.cooldown = false;
      this.cooldownTimer.reset();
    } 
    if (AutoCrystal.target != null)
      this.targets.put(AutoCrystal.target, Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L))); 
    this.targets.replaceAll((p, v) -> Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L)));
    for (EntityPlayer player : this.targets.keySet()) {
      if (((Integer)this.targets.get(player)).intValue() <= ((Integer)this.targetResetTimer.getValue()).intValue())
        continue; 
      this.targets.remove(player);
      this.timer.reset();
    } 
  }
  
  @SubscribeEvent
  public void onEntityDeath(DeathEvent event) {
    if (((Boolean)this.killmsg.getValue()).booleanValue()) {
      if (this.targets.containsKey(event.player) && !this.cooldown) {
        announceDeath(event.player);
        this.cooldown = true;
        this.targets.remove(event.player);
      } 
      if (event.player == this.cauraTarget && !this.cooldown) {
        announceDeath(event.player);
        this.cooldown = true;
      } 
    } 
  }
  
  @SubscribeEvent
  public void onAttackEntity(AttackEntityEvent event) {
    if (event.getTarget() instanceof EntityPlayer && !OyVey.friendManager.isFriend(event.getEntityPlayer()))
      this.targets.put((EntityPlayer)event.getTarget(), Integer.valueOf(0)); 
  }
  
  @SubscribeEvent
  public void onSendAttackPacket(PacketEvent.Send event) {
    CPacketUseEntity packet;
    if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)mc.world) instanceof EntityPlayer && !OyVey.friendManager.isFriend((EntityPlayer)packet.getEntityFromWorld((World)mc.world)))
      this.targets.put((EntityPlayer)packet.getEntityFromWorld((World)mc.world), Integer.valueOf(0)); 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if ((((Boolean)this.clean.getValue()).booleanValue() || ((Boolean)this.infinite.getValue()).booleanValue()) && 
      event.getPacket() instanceof CPacketChatMessage) {
      String s = ((CPacketChatMessage)event.getPacket()).getMessage();
      this.check = !s.startsWith(OyVey.commandManager.getPrefix());
    } 
    if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
      CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
      String s = packet.getMessage();
      if (s.startsWith("/") || s.startsWith(".") || s.startsWith("#") || s.startsWith(",") || s.startsWith("-") || s.startsWith("+") || s.startsWith("$") || s.startsWith(";"))
        return; 
      String string = (String)this.suffix.getValue();
      ((CPacketChatMessage)event.getPacket()).message = ((CPacketChatMessage)event.getPacket()).getMessage() + " ⏐ " + TextUtil.toUnicode(string);
      if (s.length() >= 256)
        s = s.substring(0, 256); 
      packet.message = s;
    } 
  }
  
  @SubscribeEvent
  public void onChatPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() != 0 || event.getPacket() instanceof SPacketChat);
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
      if (!((SPacketChat)event.getPacket()).isSystem())
        return; 
      String originalMessage = ((SPacketChat)event.getPacket()).chatComponent.getFormattedText();
      String message = getTimeString(originalMessage) + originalMessage;
      ((SPacketChat)event.getPacket()).chatComponent = (ITextComponent)new TextComponentString(message);
    } 
  }
  
  public String getTimeString(String message) {
    String date = (new SimpleDateFormat("k:mm")).format(new Date());
    return ((this.bracket.getValue() == TextUtil.Color.NONE) ? "" : TextUtil.coloredString("<", (TextUtil.Color)this.bracket.getValue())) + TextUtil.coloredString(date, (TextUtil.Color)this.timeStamps.getValue()) + ((this.bracket.getValue() == TextUtil.Color.NONE) ? "" : TextUtil.coloredString(">", (TextUtil.Color)this.bracket.getValue())) + (((Boolean)this.space.getValue()).booleanValue() ? " " : "") + "§r";
  }
  
  private boolean shouldSendMessage(EntityPlayer player) {
    if (player.dimension != 1)
      return false; 
    return player.getPosition().equals(new Vec3i(0, 240, 0));
  }
  
  public void loadMessages() {
    this.messages = FileManager.readTextFileAllLines("Quantum/killsmg.txt");
  }
  
  public String getRandomMessage() {
    loadMessages();
    Random rand = new Random();
    if (this.messages.size() == 0)
      return "<player> just fucking died!"; 
    if (this.messages.size() == 1)
      return this.messages.get(0); 
    return this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1));
  }
  
  public void announceDeath(EntityPlayer target) {
    mc.player.connection.sendPacket((Packet)new CPacketChatMessage(getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
  }
  
  public enum Settings {
    Chat, Visual;
  }
  
  public enum SuffixMode {
    None, Custom;
  }
  
  public enum AnnouncerMode {
    DotGod, Simple;
  }
}
