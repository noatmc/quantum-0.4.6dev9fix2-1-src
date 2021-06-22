package me.alpha432.oyvey.features.modules.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatLogger extends Module {
  public Setting<Boolean> numbers = register(new Setting("OnlyNumbers", Boolean.valueOf(false)));
  
  File mainFolder = new File((Minecraft.getMinecraft()).gameDir + File.separator + "Quantum");
  
  File txt;
  
  File folder;
  
  BufferedWriter out;
  
  public ChatLogger() {
    super("ChatLogger", "cl", Module.Category.MISC, true, false, false);
  }
  
  public void onEnable() {
    try {
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
      Date date = new Date();
      this.folder = new File(this.mainFolder + File.separator + "logs");
      if (!this.folder.exists())
        this.folder.mkdirs(); 
      String fileName = formatter.format(date) + "-chatlogs.txt";
      this.txt = new File(this.folder + File.separator + fileName);
      this.txt.createNewFile();
      this.out = new BufferedWriter(new FileWriter(this.txt));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void onDisable() {
    if (this.out != null)
      try {
        this.out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }  
  }
  
  @SubscribeEvent
  public void onChatRecieved(ClientChatReceivedEvent event) {
    try {
      String message = event.getMessage().getUnformattedText();
      if (((Boolean)this.numbers.getValue()).booleanValue()) {
        if (message.matches(".*\\d.*")) {
          this.out.write(message);
          this.out.write(endLine());
          this.out.flush();
        } 
      } else {
        this.out.write(message);
        this.out.write(endLine());
        this.out.flush();
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static String endLine() {
    return "\r\n";
  }
}
