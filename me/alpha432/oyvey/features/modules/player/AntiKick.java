package me.alpha432.oyvey.features.modules.player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.NoSuchElementException;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ClientEvent;
import me.alpha432.oyvey.event.events.NettyChannelEvent;
import me.alpha432.oyvey.event.events.PlayerUpdateEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiKick extends Module {
  private final Setting<Integer> timeout;
  
  private final Setting<Mode> mode;
  
  private boolean handlerRemoved;
  
  private Channel nettyChannel;
  
  private Integer timeoutLast;
  
  private Timer changeThrottle;
  
  public AntiKick() {
    super("AntiKick", "ak", Module.Category.PLAYER, true, false, true);
    this.timeout = register(new Setting("Timeout", Integer.valueOf(240), Integer.valueOf(30), Integer.valueOf(600)));
    this.mode = register(new Setting("Mode", Mode.Change));
    this.handlerRemoved = false;
    this.nettyChannel = null;
    this.timeoutLast = Integer.valueOf(240);
    this.changeThrottle = new Timer();
    this.timeoutLast = (Integer)this.timeout.getValue();
  }
  
  public String getDisplayInfo() {
    return ((Mode)this.mode.getValue()).name() + ((this.mode.getValue() == Mode.Change) ? (" " + String.valueOf(this.timeoutLast)) : "") + " | " + ((this.nettyChannel == null) ? "NC" : "OK");
  }
  
  @SubscribeEvent
  public void onNettyChannelSet(NettyChannelEvent event) {
    this.nettyChannel = event.getChannel();
    this.handlerRemoved = false;
    if (isEnabled())
      updateTimeout(this.timeoutLast.intValue(), (this.mode.getValue() == Mode.Change)); 
  }
  
  @SubscribeEvent
  public void onPlayerUpdate(PlayerUpdateEvent event) {
    if (isEnabled() && this.changeThrottle.passedMs(1000L) && this.timeout.getValue() != this.timeoutLast && this.nettyChannel != null) {
      this.timeoutLast = (Integer)this.timeout.getValue();
      this.changeThrottle.reset();
      updateTimeout(this.timeoutLast.intValue(), (this.mode.getValue() == Mode.Change));
    } 
  }
  
  @SubscribeEvent
  public void onSettingsUpdate(ClientEvent event) {
    if (event.getStage() == 2 && event.getSetting().getFeature().equals(this) && event.getSetting().equals(this.mode)) {
      this.timeoutLast = (Integer)this.timeout.getValue();
      updateTimeout(this.timeoutLast.intValue(), (this.mode.getPlannedValue() == Mode.Change));
    } 
  }
  
  private void updateTimeout(int seconds, boolean addBack) {
    if (this.nettyChannel != null) {
      try {
        if (!this.handlerRemoved)
          this.nettyChannel.pipeline().remove("timeout"); 
      } catch (NoSuchElementException e) {
        OyVey.LOGGER.info("AntiLagKick: catched NSEE trying to remove timeout");
      } 
      if (addBack)
        this.nettyChannel.pipeline().addFirst("timeout", (ChannelHandler)new ReadTimeoutHandler(seconds)); 
      this.handlerRemoved = !addBack;
    } 
  }
  
  public enum Mode {
    Change, Remove;
  }
}
