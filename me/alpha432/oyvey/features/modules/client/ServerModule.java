package me.alpha432.oyvey.features.modules.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.mixin.mixins.accessors.IC00Handshake;
import me.alpha432.oyvey.util.TextUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerModule extends Module {
  private static ServerModule instance;
  
  private final AtomicBoolean connected = new AtomicBoolean(false);
  
  private final Timer pingTimer = new Timer();
  
  private final List<Long> pingList = new ArrayList<>();
  
  public Setting<String> ip = register(new Setting("PhobosIP", "0.0.0.0.0"));
  
  public Setting<String> port = register((new Setting("Port", "0")).setRenderName(true));
  
  public Setting<String> serverIP = register(new Setting("ServerIP", "AnarchyHvH.eu"));
  
  public Setting<Boolean> noFML = register(new Setting("RemoveFML", Boolean.valueOf(false)));
  
  public Setting<Boolean> getName = register(new Setting("GetName", Boolean.valueOf(false)));
  
  public Setting<Boolean> average = register(new Setting("Average", Boolean.valueOf(false)));
  
  public Setting<Boolean> clear = register(new Setting("ClearPings", Boolean.valueOf(false)));
  
  public Setting<Boolean> oneWay = register(new Setting("OneWay", Boolean.valueOf(false)));
  
  public Setting<Integer> delay = register(new Setting("KeepAlives", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(50)));
  
  private long currentPing = 0L;
  
  private long serverPing = 0L;
  
  private StringBuffer name = null;
  
  private long averagePing = 0L;
  
  private String serverPrefix = "idk";
  
  public ServerModule() {
    super("PingBypass", "Manages Phobos`s internal Server", Module.Category.CLIENT, false, false, true);
    instance = this;
  }
  
  public static ServerModule getInstance() {
    if (instance == null)
      instance = new ServerModule(); 
    return instance;
  }
  
  public String getPlayerName() {
    if (this.name == null)
      return null; 
    return this.name.toString();
  }
  
  public String getServerPrefix() {
    return this.serverPrefix;
  }
  
  public void onLogout() {
    this.averagePing = 0L;
    this.currentPing = 0L;
    this.serverPing = 0L;
    this.pingList.clear();
    this.connected.set(false);
    this.name = null;
  }
  
  @SubscribeEvent
  public void onReceivePacket(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketChat) {
      SPacketChat packet = (SPacketChat)event.getPacket();
      if (packet.chatComponent.getUnformattedText().startsWith("@Clientprefix"))
        String prefix = packet.chatComponent.getFormattedText().replace("@Clientprefix", ""); 
    } 
  }
  
  public void onTick() {
    if (mc.getConnection() != null && isConnected()) {
      if (((Boolean)this.getName.getValue()).booleanValue()) {
        mc.getConnection().sendPacket((Packet)new CPacketChatMessage("@Servername"));
        this.getName.setValue(Boolean.valueOf(false));
      } 
      if (this.serverPrefix.equalsIgnoreCase("idk") && mc.world != null)
        mc.getConnection().sendPacket((Packet)new CPacketChatMessage("@Servergetprefix")); 
      if (this.pingTimer.passedMs((((Integer)this.delay.getValue()).intValue() * 1000))) {
        mc.getConnection().sendPacket((Packet)new CPacketKeepAlive(100L));
        this.pingTimer.reset();
      } 
      if (((Boolean)this.clear.getValue()).booleanValue())
        this.pingList.clear(); 
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketChat) {
      SPacketChat packetChat = (SPacketChat)event.getPacket();
      if (packetChat.getChatComponent().getFormattedText().startsWith("@Client")) {
        this.name = new StringBuffer(TextUtil.stripColor(packetChat.getChatComponent().getFormattedText().replace("@Client", "")));
        event.setCanceled(true);
      } 
    } else {
      SPacketKeepAlive alive;
      if (event.getPacket() instanceof SPacketKeepAlive && (alive = (SPacketKeepAlive)event.getPacket()).getId() > 0L && alive.getId() < 1000L) {
        this.serverPing = alive.getId();
        this.currentPing = ((Boolean)this.oneWay.getValue()).booleanValue() ? (this.pingTimer.getPassedTimeMs() / 2L) : this.pingTimer.getPassedTimeMs();
        this.pingList.add(Long.valueOf(this.currentPing));
        this.averagePing = getAveragePing();
      } 
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    IC00Handshake packet;
    String ip;
    if (event.getPacket() instanceof net.minecraft.network.handshake.client.C00Handshake && (ip = (packet = (IC00Handshake)event.getPacket()).getIp()).equals(this.ip.getValue())) {
      packet.setIp((String)this.serverIP.getValue());
      System.out.println(packet.getIp());
      this.connected.set(true);
    } 
  }
  
  public String getDisplayInfo() {
    return this.averagePing + "ms";
  }
  
  private long getAveragePing() {
    if (!((Boolean)this.average.getValue()).booleanValue() || this.pingList.isEmpty())
      return this.currentPing; 
    int full = 0;
    for (Iterator<Long> iterator = this.pingList.iterator(); iterator.hasNext(); ) {
      long i = ((Long)iterator.next()).longValue();
      full = (int)(full + i);
    } 
    return (full / this.pingList.size());
  }
  
  public boolean isConnected() {
    return this.connected.get();
  }
  
  public int getPort() {
    int result;
    try {
      result = Integer.parseInt((String)this.port.getValue());
    } catch (NumberFormatException e) {
      return -1;
    } 
    return result;
  }
  
  public long getServerPing() {
    return this.serverPing;
  }
}
