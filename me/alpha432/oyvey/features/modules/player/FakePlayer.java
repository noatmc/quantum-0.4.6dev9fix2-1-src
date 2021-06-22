package me.alpha432.oyvey.features.modules.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.IOUtils;

public class FakePlayer extends Module {
  public static final String[][] OyVeyInfo = new String[][] { { "8af022c8-b926-41a0-8b79-2b544ff00fcf", "3arthqu4ke", "3", "0" }, { "0aa3b04f-786a-49c8-bea9-025ee0dd1e85", "zb0b", "-3", "0" }, { "19bf3f1f-fe06-4c86-bea5-3dad5df89714", "3vt", "0", "-3" }, { "e47d6571-99c2-415b-955e-c4bc7b55941b", "Phobos_eu", "0", "3" }, { "b01f9bc1-cb7c-429a-b178-93d771f00926", "bakpotatisen", "6", "0" }, { "b232930c-c28a-4e10-8c90-f152235a65c5", "948", "-6", "0" }, { "ace08461-3db3-4579-98d3-390a67d5645b", "Browswer", "0", "-6" }, { "5bead5b0-3bab-460d-af1d-7929950f40c2", "fsck", "0", "6" }, { "78ee2bd6-64c4-45f0-96e5-0b6747ba7382", "Fit", "0", "9" }, { "78ee2bd6-64c4-45f0-96e5-0b6747ba7382", "deathcurz0", "0", "-9" } };
  
  private final String name = "BigNigger";
  
  public Setting<Boolean> multi = register(new Setting("Multi", Boolean.valueOf(false)));
  
  private final Setting<Integer> players = register(new Setting("Players", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(9), v -> ((Boolean)this.multi.getValue()).booleanValue(), "Amount of other players."));
  
  private EntityOtherPlayerMP _fakePlayer;
  
  private final List<EntityOtherPlayerMP> fakeEntities = new ArrayList<>();
  
  public List<Integer> fakePlayerIdList = new ArrayList<>();
  
  public FakePlayer() {
    super("FakePlayer", "fp", Module.Category.PLAYER, false, false, false);
  }
  
  public static String getUuid(String name) {
    JsonParser parser = new JsonParser();
    String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
    try {
      String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
      if (UUIDJson.isEmpty())
        return "invalid name"; 
      JsonObject UUIDObject = (JsonObject)parser.parse(UUIDJson);
      return reformatUuid(UUIDObject.get("id").toString());
    } catch (Exception e) {
      e.printStackTrace();
      return "error";
    } 
  }
  
  private static String reformatUuid(String uuid) {
    String longUuid = "";
    longUuid = longUuid + uuid.substring(1, 9) + "-";
    longUuid = longUuid + uuid.substring(9, 13) + "-";
    longUuid = longUuid + uuid.substring(13, 17) + "-";
    longUuid = longUuid + uuid.substring(17, 21) + "-";
    longUuid = longUuid + uuid.substring(21, 33);
    return longUuid;
  }
  
  public void onEnable() {
    if (fullNullCheck()) {
      disable();
      return;
    } 
    if (((Boolean)this.multi.getValue()).booleanValue()) {
      int amount = 0;
      int entityId = -101;
      for (String[] data : OyVeyInfo) {
        addFakePlayer(data[0], data[1], entityId, Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        if (++amount >= ((Integer)this.players.getValue()).intValue())
          return; 
        entityId -= amount;
      } 
    } else {
      this._fakePlayer = null;
      if (mc.player != null) {
        try {
          getClass();
          getClass();
          this._fakePlayer = new EntityOtherPlayerMP((World)mc.world, new GameProfile(UUID.fromString(getUuid("BigNigger")), "BigNigger"));
        } catch (Exception e) {
          getClass();
          this._fakePlayer = new EntityOtherPlayerMP((World)mc.world, new GameProfile(UUID.fromString("70ee432d-0a96-4137-a2c0-37cc9df67f03"), "BigNigger"));
          Command.sendMessage("Failed to load uuid, setting another one.");
        } 
        getClass();
        Command.sendMessage(String.format("%s has been spawned.", new Object[] { "BigNigger" }));
        this._fakePlayer.copyLocationAndAnglesFrom((Entity)mc.player);
        this._fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntityToWorld(-100, (Entity)this._fakePlayer);
      } 
    } 
  }
  
  private void addFakePlayer(String uuid, String name, int entityId, int offsetX, int offsetZ) {
    GameProfile profile = new GameProfile(UUID.fromString(uuid), name);
    EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP((World)mc.world, profile);
    fakePlayer.copyLocationAndAnglesFrom((Entity)mc.player);
    fakePlayer.posX += offsetX;
    fakePlayer.posZ += offsetZ;
    fakePlayer.setHealth(mc.player.getHealth() + mc.player.getAbsorptionAmount());
    this.fakeEntities.add(fakePlayer);
    mc.world.addEntityToWorld(entityId, (Entity)fakePlayer);
    this.fakePlayerIdList.add(Integer.valueOf(entityId));
  }
  
  public void onDisable() {
    if (mc.world != null && mc.player != null) {
      super.onDisable();
      mc.world.removeEntity((Entity)this._fakePlayer);
    } 
  }
  
  @SubscribeEvent
  public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    if (isEnabled())
      disable(); 
  }
}
