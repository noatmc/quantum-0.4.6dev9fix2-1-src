package me.alpha432.oyvey.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StashLogger extends Module {
  private final Setting<Integer> chestsToImportantNotify = register(new Setting("Chests", Integer.valueOf(15), Integer.valueOf(1), Integer.valueOf(30)));
  
  private final Setting<Boolean> chests = register(new Setting("Chests", Boolean.valueOf(true)));
  
  private final Setting<Boolean> Shulkers = register(new Setting("Shulkers", Boolean.valueOf(true)));
  
  private final Setting<Boolean> donkeys = register(new Setting("Donkeys", Boolean.valueOf(false)));
  
  private final Setting<Boolean> writeToFile = register(new Setting("CoordsSaver", Boolean.valueOf(true)));
  
  File mainFolder = new File((Minecraft.getMinecraft()).gameDir + File.separator + "Quantum");
  
  final Iterator<NBTTagCompound> iterator;
  
  public StashLogger() {
    super("StashLogger", "sl", Module.Category.MISC, true, false, false);
    this.iterator = null;
  }
  
  @SubscribeEvent
  public void onPacket(PacketEvent event) {
    if (nullCheck())
      return; 
    if (event.getPacket() instanceof SPacketChunkData) {
      SPacketChunkData l_Packet = (SPacketChunkData)event.getPacket();
      int l_ChestsCount = 0;
      int shulkers = 0;
      for (NBTTagCompound l_Tag : l_Packet.getTileEntityTags()) {
        String l_Id = l_Tag.getString("id");
        if (l_Id.equals("minecraft:chest") && ((Boolean)this.chests.getValue()).booleanValue()) {
          l_ChestsCount++;
          continue;
        } 
        if (l_Id.equals("minecraft:shulker_box") && ((Boolean)this.Shulkers.getValue()).booleanValue())
          shulkers++; 
      } 
      if (l_ChestsCount >= ((Integer)this.chestsToImportantNotify.getValue()).intValue())
        SendMessage(String.format("%s chests located at X: %s, Z: %s", new Object[] { Integer.valueOf(l_ChestsCount), Integer.valueOf(l_Packet.getChunkX() * 16), Integer.valueOf(l_Packet.getChunkZ() * 16) }), true); 
      if (shulkers > 0)
        SendMessage(String.format("%s shulker boxes at X: %s, Z: %s", new Object[] { Integer.valueOf(shulkers), Integer.valueOf(l_Packet.getChunkX() * 16), Integer.valueOf(l_Packet.getChunkZ() * 16) }), true); 
    } 
  }
  
  private void SendMessage(String message, boolean save) {
    String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toUpperCase() : (mc.getCurrentServerData()).serverIP;
    if (((Boolean)this.writeToFile.getValue()).booleanValue() && save)
      try {
        FileWriter writer = new FileWriter(this.mainFolder + "/stashes.txt", true);
        writer.write("[" + server + "]: " + message + "\n");
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }  
    mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F));
    mc.player.sendMessage((ITextComponent)new TextComponentString(ChatFormatting.RED + "[StashLogger] " + ChatFormatting.GREEN + message));
  }
}
