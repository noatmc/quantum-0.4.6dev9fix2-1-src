package me.alpha432.oyvey.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoFallOldfag extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.Predict));
  
  private final Setting<Boolean> disconnect = register(new Setting("Disconnect", Boolean.valueOf(false)));
  
  private final Setting<Integer> fallDist = register(new Setting("FallDistance", Integer.valueOf(4), Integer.valueOf(3), Integer.valueOf(30), v -> (this.mode.getValue() == Mode.Old)));
  
  BlockPos n1;
  
  public enum Mode {
    Predict, Old;
  }
  
  public NoFallOldfag() {
    super("NoFallBypass", "nf", Module.Category.MOVEMENT, true, false, false);
  }
  
  @SubscribeEvent
  public void onUpdate(TickEvent.ClientTickEvent event) {
    if (nullCheck())
      return; 
    if (((Mode)this.mode.getValue()).equals("Predict") && 
      mc.player.fallDistance > ((Integer)this.fallDist.getValue()).intValue() && predict(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
      mc.player.motionY = 0.0D;
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, this.n1.getY(), mc.player.posZ, false));
      mc.player.fallDistance = 0.0F;
      if (((Boolean)this.disconnect.getValue()).booleanValue())
        mc.player.connection.getNetworkManager().closeChannel((ITextComponent)new TextComponentString(ChatFormatting.GOLD + "NoFall")); 
    } 
    if (((Mode)this.mode.getValue()).equals("Old") && 
      mc.player.fallDistance > ((Integer)this.fallDist.getValue()).intValue()) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, 0.0D, mc.player.posZ, false));
      mc.player.fallDistance = 0.0F;
    } 
  }
  
  private boolean predict(BlockPos blockPos) {
    Minecraft mc = Minecraft.getMinecraft();
    this.n1 = blockPos.add(0, -((Integer)this.fallDist.getValue()).intValue(), 0);
    if (mc.world.getBlockState(this.n1).getBlock() != Blocks.AIR)
      return true; 
    return false;
  }
}
