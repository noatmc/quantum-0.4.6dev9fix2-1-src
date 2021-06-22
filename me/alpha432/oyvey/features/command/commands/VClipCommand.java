package me.alpha432.oyvey.features.command.commands;

import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.movement.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

public class VClipCommand extends Command {
  public VClipCommand() {
    super("clip", new String[] { "0/1/2/3/4/5" });
  }
  
  public void execute(String[] commands) {
    Phase.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Phase.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.5346D, mc.player.posZ, false));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.2836D, mc.player.posZ, false));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.9231D, mc.player.posZ, false));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 2.3957D, mc.player.posZ, false));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 3.0121D, mc.player.posZ, false));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 4.9564D, mc.player.posZ, false));
    sendMessage("Tried clipping");
    switch (commands[0]) {
      case "0":
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.5346D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.2836D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.9231D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 2.3957D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 3.0121D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 4.9564D, mc.player.posZ, false));
        return;
      case "1":
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.5346D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 2.2836D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 2.9231D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 3.3957D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 4.0121D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 5.9564D, mc.player.posZ, false));
        return;
      case "2":
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 2.5346D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 3.2836D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 3.9231D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 4.3957D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 5.0121D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 6.9564D, mc.player.posZ, false));
        return;
      case "3":
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 3.5346D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 4.2836D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 4.9231D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 5.3957D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 6.0121D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 7.9564D, mc.player.posZ, false));
        return;
      case "4":
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 4.5346D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 5.2836D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 5.9231D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 6.3957D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 7.0121D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 8.9564D, mc.player.posZ, false));
        return;
      case "5":
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 5.5346D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 6.2836D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 6.9231D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 7.3957D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 8.0121D, mc.player.posZ, false));
        mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 9.9564D, mc.player.posZ, false));
        return;
    } 
  }
}
