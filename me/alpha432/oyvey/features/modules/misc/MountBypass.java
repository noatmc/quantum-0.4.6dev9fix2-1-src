package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.combat.AutoWeb;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MountBypass extends Module {
  public Setting<Type> type;
  
  private int lastHotbarSlot;
  
  public MountBypass() {
    super("MountBypass", "mb", Module.Category.MISC, true, false, false);
    this.type = register(new Setting("Type", Type.Old));
  }
  
  public void onEnable() {
    if (this.type.getValue() == Type.New) {
      if (fullNullCheck())
        return; 
      this.lastHotbarSlot = AutoWeb.mc.player.inventory.currentItem;
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (this.type.getValue() == Type.Old && 
      event.getPacket() instanceof CPacketUseEntity) {
      CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
      if (packet.getEntityFromWorld((World)mc.world) instanceof net.minecraft.entity.passive.AbstractChestHorse && 
        packet.getAction() == CPacketUseEntity.Action.INTERACT_AT) {
        event.setCanceled(true);
        Command.sendMessage("<" + getDisplayName() + "> attempted a mountbypass");
      } 
    } 
    if (this.type.getValue() == Type.New && 
      event.getPacket() instanceof CPacketUseEntity) {
      CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
      int chestSlot = InventoryUtil.findHotbarBlock(BlockChest.class);
      if (chestSlot == -1) {
        Command.sendMessage("<" + getDisplayName() + "> you are out of chests");
        disable();
        return;
      } 
      if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != chestSlot)
        this.lastHotbarSlot = mc.player.inventory.currentItem; 
      int originalSlot = AutoWeb.mc.player.inventory.currentItem;
      int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
      if (webSlot == -1)
        toggle(); 
      mc.player.inventory.currentItem = (webSlot == -1) ? webSlot : webSlot;
      mc.playerController.updateController();
      mc.player.inventory.currentItem = originalSlot;
      mc.playerController.updateController();
      Command.sendMessage("<" + getDisplayName() + "> attempted a mountbypass");
    } 
  }
  
  public enum Type {
    Old, New;
  }
}
