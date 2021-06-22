package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityDesync extends Module {
  private Entity Riding;
  
  public EntityDesync() {
    super("EntityDesync", "ed", Module.Category.PLAYER, true, false, false);
    this.Riding = null;
  }
  
  public void onEnable() {
    super.onEnable();
    if (mc.player == null) {
      this.Riding = null;
      toggle();
      return;
    } 
    if (!mc.player.isRiding()) {
      Command.sendMessage("You are not riding an entity");
      this.Riding = null;
      toggle();
      return;
    } 
    this.Riding = mc.player.getRidingEntity();
    mc.player.dismountRidingEntity();
    mc.world.removeEntity(this.Riding);
  }
  
  public void onDisable() {
    super.onDisable();
    if (this.Riding != null) {
      this.Riding.isDead = false;
      if (!mc.player.isRiding()) {
        mc.world.spawnEntity(this.Riding);
        mc.player.startRiding(this.Riding, true);
      } 
      this.Riding = null;
      Command.sendMessage("Forced a remount");
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (this.Riding == null)
      return; 
    if (mc.player.isRiding())
      return; 
    mc.player.onGround = true;
    this.Riding.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
    mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(this.Riding));
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketSetPassengers) {
      if (this.Riding == null)
        return; 
      SPacketSetPassengers l_Packet = (SPacketSetPassengers)event.getPacket();
      Entity en = mc.world.getEntityByID(l_Packet.getEntityId());
      if (en == this.Riding) {
        for (int i : l_Packet.getPassengerIds()) {
          Entity ent = mc.world.getEntityByID(i);
          if (ent == mc.player)
            return; 
        } 
        Command.sendMessage("You dismounted");
        toggle();
      } 
    } else if (event.getPacket() instanceof SPacketDestroyEntities) {
      SPacketDestroyEntities l_Packet = (SPacketDestroyEntities)event.getPacket();
      for (int l_EntityId : l_Packet.getEntityIDs()) {
        if (l_EntityId == this.Riding.getEntityId()) {
          Command.sendMessage("Entity is now null SPacketDestroyEntities");
          return;
        } 
      } 
    } 
  }
}
