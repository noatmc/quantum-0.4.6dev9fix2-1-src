package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Auto extends Module {
  private static Auto INSTANCE = new Auto();
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.Reconnect));
  
  private final Setting<Boolean> logtrue = register(new Setting("Log", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Log)));
  
  private final Setting<Float> health = register(new Setting("Health", Float.valueOf(16.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.mode.getValue() == Mode.Log && ((Boolean)this.logtrue.getValue()).booleanValue())));
  
  private final Setting<Boolean> bed = register(new Setting("Beds", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Log && ((Boolean)this.logtrue.getValue()).booleanValue())));
  
  private final Setting<Float> range = register(new Setting("BedRange", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.mode.getValue() == Mode.Log && ((Boolean)this.logtrue.getValue()).booleanValue() && ((Boolean)this.bed.getValue()).booleanValue())));
  
  private final Setting<Boolean> logout = register(new Setting("LogoutOff", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Log && ((Boolean)this.logtrue.getValue()).booleanValue())));
  
  private final Setting<Boolean> reconnecttrue = register(new Setting("Reconnect", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Reconnect && !((Boolean)this.logtrue.getValue()).booleanValue())));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(15), v -> (this.mode.getValue() == Mode.Reconnect && ((Boolean)this.reconnecttrue.getValue()).booleanValue())));
  
  private static ServerData serverData;
  
  private final Setting<Boolean> respawntrue = register(new Setting("Respawn", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Respawn)));
  
  public Setting<Boolean> antiDeathScreen = register(new Setting("AntiDeathScreen", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Respawn && ((Boolean)this.respawntrue.getValue()).booleanValue())));
  
  public Setting<Boolean> deathCoords = register(new Setting("DeathCoords", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Respawn && ((Boolean)this.respawntrue.getValue()).booleanValue())));
  
  public Setting<Boolean> respawn = register(new Setting("Respawn", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Respawn && ((Boolean)this.respawntrue.getValue()).booleanValue())));
  
  public Auto() {
    super("Auto", "a", Module.Category.MISC, true, false, false);
    setInstance();
  }
  
  public static Auto getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Auto(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onTick() {
    if (!nullCheck() && mc.player.getHealth() <= ((Float)this.health.getValue()).floatValue()) {
      mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
      if (((Boolean)this.logout.getValue()).booleanValue())
        disable(); 
    } 
  }
  
  @SubscribeEvent
  public void onReceivePacket(PacketEvent.Receive event) {
    SPacketBlockChange packet;
    if (event.getPacket() instanceof SPacketBlockChange && ((Boolean)this.bed.getValue()).booleanValue() && (packet = (SPacketBlockChange)event.getPacket()).getBlockState().getBlock() == Blocks.BED && mc.player.getDistanceSqToCenter(packet.getBlockPosition()) <= MathUtil.square(((Float)this.range.getValue()).floatValue())) {
      mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("Logged")));
      if (((Boolean)this.logout.getValue()).booleanValue())
        disable(); 
    } 
  }
  
  @SubscribeEvent
  public void sendPacket(GuiOpenEvent event) {
    if (event.getGui() instanceof GuiDisconnected) {
      updateLastConnectedServer();
      GuiDisconnected disconnected = (GuiDisconnected)event.getGui();
      event.setGui((GuiScreen)new GuiDisconnectedHook(disconnected));
    } 
  }
  
  @SubscribeEvent
  public void onWorldUnload(WorldEvent.Unload event) {
    updateLastConnectedServer();
  }
  
  public void updateLastConnectedServer() {
    ServerData data = mc.getCurrentServerData();
    if (data != null)
      serverData = data; 
  }
  
  private class GuiDisconnectedHook extends GuiDisconnected {
    private final Timer timer;
    
    public GuiDisconnectedHook(GuiDisconnected disconnected) {
      super(disconnected.parentScreen, disconnected.reason, disconnected.message);
      this.timer = new Timer();
      this.timer.reset();
    }
    
    public void updateScreen() {
      if (this.timer.passedS(((Integer)Auto.this.delay.getValue()).intValue()))
        this.mc.displayGuiScreen((GuiScreen)new GuiConnecting(this.parentScreen, this.mc, (Auto.serverData == null) ? this.mc.currentServerData : Auto.serverData)); 
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      String s = "Reconnecting in " + MathUtil.round(((((Integer)Auto.this.delay.getValue()).intValue() * 1000) - this.timer.getPassedTimeMs()) / 1000.0D, 1);
      Auto.this.renderer.drawString(s, (this.width / 2 - Auto.this.renderer.getStringWidth(s) / 2), (this.height - 16), 16777215, true);
    }
  }
  
  @SubscribeEvent
  public void onDisplayDeathScreen(GuiOpenEvent event) {
    if (event.getGui() instanceof net.minecraft.client.gui.GuiGameOver) {
      if (((Boolean)this.deathCoords.getValue()).booleanValue() && event.getGui() instanceof net.minecraft.client.gui.GuiGameOver)
        Command.sendMessage(String.format("You died at x %d y %d z %d", new Object[] { Integer.valueOf((int)mc.player.posX), Integer.valueOf((int)mc.player.posY), Integer.valueOf((int)mc.player.posZ) })); 
      if ((((Boolean)this.respawn.getValue()).booleanValue() && mc.player.getHealth() <= 0.0F) || (((Boolean)this.antiDeathScreen.getValue()).booleanValue() && mc.player.getHealth() > 0.0F)) {
        event.setCanceled(true);
        mc.player.respawnPlayer();
      } 
    } 
  }
  
  public enum Mode {
    Log, Reconnect, Respawn;
  }
}
