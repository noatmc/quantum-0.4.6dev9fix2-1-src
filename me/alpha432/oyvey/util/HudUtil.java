package me.alpha432.oyvey.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import net.minecraft.client.Minecraft;

public class HudUtil implements Util {
  public static String getPingSatus() {
    String line = "";
    int ping = OyVey.serverManager.getPing();
    if (ping > 100) {
      line = line + ChatFormatting.RED;
    } else if (ping > 50) {
      line = line + ChatFormatting.YELLOW;
    } else {
      line = line + ChatFormatting.GREEN;
    } 
    return line + " " + ping;
  }
  
  public static String getTpsStatus() {
    String line = "";
    double tps = Math.ceil(OyVey.serverManager.getTPS());
    if (tps > 16.0D) {
      line = line + ChatFormatting.GREEN;
    } else if (tps > 10.0D) {
      line = line + ChatFormatting.YELLOW;
    } else {
      line = line + ChatFormatting.RED;
    } 
    return line + " " + tps;
  }
  
  public static String getFpsStatus() {
    String line = "";
    int fps = Minecraft.getDebugFPS();
    if (fps > 120) {
      line = line + ChatFormatting.GREEN;
    } else if (fps > 60) {
      line = line + ChatFormatting.YELLOW;
    } else {
      line = line + ChatFormatting.RED;
    } 
    return line + " " + fps;
  }
}
