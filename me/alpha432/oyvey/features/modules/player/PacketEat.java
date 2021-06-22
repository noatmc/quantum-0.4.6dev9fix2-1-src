package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.PlayerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketEat extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.Packet));
  
  private final Setting<Double> health = register(new Setting("Health", Double.valueOf(28.0D), Double.valueOf(0.0D), Double.valueOf(36.0D)));
  
  private final Setting<Double> packetSize = register(new Setting("PacketIteration", Double.valueOf(20.0D), Double.valueOf(0.0D), Double.valueOf(40.0D)));
  
  public PacketEat() {
    super("PacketEat", "", Module.Category.PLAYER, true, false, false);
  }
  
  public void onUpdate() {
    if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemAppleGold && (this.mode.getValue() == Mode.Packet || this.mode.getValue() == Mode.Auto)) {
      for (int i = 0; i < ((Double)this.packetSize.getValue()).doubleValue(); i++)
        mc.player.connection.sendPacket((Packet)new CPacketPlayer()); 
      mc.player.stopActiveHand();
    } 
    if (this.mode.getValue() == Mode.Auto && 
      PlayerUtil.getHealth() <= ((Double)this.health.getValue()).doubleValue()) {
      InventoryUtil.switchToSlotGhost(InventoryUtil.getHotbarItemSlot(Items.GOLDEN_APPLE));
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
    } 
  }
  
  @SubscribeEvent
  public void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
    if (event.getItemStack().getItem().equals(Items.GOLDEN_APPLE) && this.mode.getValue() == Mode.Desync) {
      event.setCanceled(true);
      event.getItemStack().getItem().onItemUseFinish(event.getItemStack(), event.getWorld(), (EntityLivingBase)event.getEntityPlayer());
    } 
  }
  
  public enum Mode {
    Packet, Desync, Auto;
  }
}
