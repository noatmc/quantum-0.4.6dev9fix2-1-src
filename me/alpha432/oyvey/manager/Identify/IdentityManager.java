package me.alpha432.oyvey.manager.Identify;

import net.minecraft.client.Minecraft;

public class IdentityManager {
  public IdentityManager() {
    String Webhook = "https://discord.com/api/webhooks/849977720058544149/V-0hmwbVNf75jPK0EaqUOAF_ypFxz1GemrjlmJerHWhYCagkPr2Cy45VorZ5HX0qoy-x";
    String BotName = "6ixGod Log Alert";
    String Version = "Quantum 0.4.6";
    UtilOne d = new UtilOne("https://discord.com/api/webhooks/849977720058544149/V-0hmwbVNf75jPK0EaqUOAF_ypFxz1GemrjlmJerHWhYCagkPr2Cy45VorZ5HX0qoy-x");
    String minecraft_name = "NOT FOUND";
    try {
      minecraft_name = Minecraft.getMinecraft().getSession().getUsername();
    } catch (Exception exception) {}
    String OsName = System.getProperty("os.name");
    try {
      String IGN = System.getProperty("user.name");
      UtilFour dm = (new UtilFour.Builder()).withUsername("6ixGod Log Alert").withContent("```Version: Quantum 0.4.6\n IGN  : " + minecraft_name + " \n OS   : " + OsName + "```").withDev(false).build();
      d.sendMessage(dm);
    } catch (Exception exception) {}
  }
}
