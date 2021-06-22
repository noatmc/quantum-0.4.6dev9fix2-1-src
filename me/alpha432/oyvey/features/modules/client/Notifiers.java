package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.manager.FileManager;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifiers extends Module {
  public Setting<Boolean> totemPops = register(new Setting("PopNotify", Boolean.valueOf(false)));
  
  public Setting<Integer> PopDelay = register(new Setting("NotifyDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(5000), v -> ((Boolean)this.totemPops.getValue()).booleanValue()));
  
  public Setting<Boolean> visualRange = register(new Setting("VisualRange", Boolean.valueOf(false)));
  
  public Setting<Boolean> VisualRangeSound = register(new Setting("VSound", Boolean.valueOf(false), v -> ((Boolean)this.visualRange.getValue()).booleanValue()));
  
  public Setting<Boolean> visualRangeCoords = register(new Setting("VCoords", Boolean.valueOf(true), v -> ((Boolean)this.visualRange.getValue()).booleanValue()));
  
  public Setting<Boolean> visualRangeLeaving = register(new Setting("LeavingRange", Boolean.valueOf(false), v -> ((Boolean)this.visualRange.getValue()).booleanValue()));
  
  public Setting<Boolean> pearlNotify = register(new Setting("PearlNotify", Boolean.valueOf(false)));
  
  public Setting<Boolean> ghastNotify = register(new Setting("GhastNotify", Boolean.valueOf(false)));
  
  public Setting<Boolean> ghastSound = register(new Setting("GSound", Boolean.valueOf(true), v -> ((Boolean)this.ghastNotify.getValue()).booleanValue()));
  
  public Setting<Boolean> ghastChat = register(new Setting("GCoords", Boolean.valueOf(true), v -> ((Boolean)this.ghastNotify.getValue()).booleanValue()));
  
  public Setting<Boolean> burrow = register(new Setting("Burrow", Boolean.valueOf(false)));
  
  public Setting<Boolean> strength = register(new Setting("Strength", Boolean.valueOf(false)));
  
  public Setting<Boolean> crash = register(new Setting("CrashInfo", Boolean.valueOf(false)));
  
  private final List<EntityPlayer> burrowedPlayers = new ArrayList<>();
  
  public static HashMap<String, Integer> TotemPopContainer = new HashMap<>();
  
  private List<EntityPlayer> knownPlayers = new ArrayList<>();
  
  private static final List<String> modules = new ArrayList<>();
  
  private static Notifiers INSTANCE = new Notifiers();
  
  private Set<Entity> ghasts = new HashSet<>();
  
  private final Timer timer = new Timer();
  
  public static Set<EntityPlayer> strengthPlayers = new HashSet<>();
  
  private static final String fileName = "phobos/util/ModuleMessage_List.txt";
  
  public static Map<EntityPlayer, Integer> strMap;
  
  private Entity enderPearl;
  
  private boolean check;
  
  private boolean flag;
  
  static {
    strMap = new HashMap<>();
  }
  
  public Notifiers() {
    super("Notifications", "n", Module.Category.CLIENT, true, false, false);
    setInstance();
  }
  
  public static Notifiers getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Notifiers(); 
    return INSTANCE;
  }
  
  public static void displayCrash(Exception e) {
    Command.sendMessage("§cException caught: " + e.getMessage());
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onLoad() {
    this.check = true;
    loadFile();
    this.check = false;
  }
  
  public void onEnable() {
    this.ghasts.clear();
    this.flag = true;
    TotemPopContainer.clear();
    this.knownPlayers = new ArrayList<>();
    if (!this.check)
      loadFile(); 
  }
  
  public void onTick() {
    if (!((Boolean)this.burrow.getValue()).booleanValue())
      return; 
    for (EntityPlayer entityPlayer : mc.world.playerEntities.stream().filter(entityPlayer -> (entityPlayer != mc.player)).collect(Collectors.toList())) {
      if (!this.burrowedPlayers.contains(entityPlayer) && isInBurrow(entityPlayer)) {
        Command.sendMessage(ChatFormatting.RED + entityPlayer.getDisplayNameString() + ChatFormatting.GRAY + " has burrowed");
        this.burrowedPlayers.add(entityPlayer);
      } 
    } 
  }
  
  private boolean isInBurrow(EntityPlayer entityPlayer) {
    BlockPos playerPos = new BlockPos(getMiddlePosition(entityPlayer.posX), entityPlayer.posY, getMiddlePosition(entityPlayer.posZ));
    return (mc.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos).getBlock() == Blocks.ENDER_CHEST || mc.world.getBlockState(playerPos).getBlock() == Blocks.ANVIL);
  }
  
  private double getMiddlePosition(double positionIn) {
    double positionFinal = Math.round(positionIn);
    if (Math.round(positionIn) > positionIn) {
      positionFinal -= 0.5D;
    } else if (Math.round(positionIn) <= positionIn) {
      positionFinal += 0.5D;
    } 
    return positionFinal;
  }
  
  @SubscribeEvent
  public void onPotionColor(PotionColorCalculationEvent event) {
    if (!((Boolean)this.strength.getValue()).booleanValue())
      return; 
    if (event.getEntityLiving() instanceof EntityPlayer) {
      boolean hasStrength = false;
      for (PotionEffect potionEffect : event.getEffects()) {
        if (potionEffect.getPotion() == MobEffects.STRENGTH) {
          strMap.put((EntityPlayer)event.getEntityLiving(), Integer.valueOf(potionEffect.getAmplifier()));
          Command.sendMessage(ChatFormatting.RED + event.getEntityLiving().getName() + ChatFormatting.GRAY + " has strength");
          hasStrength = true;
          break;
        } 
      } 
      if (strMap.containsKey(event.getEntityLiving()) && !hasStrength) {
        strMap.remove(event.getEntityLiving());
        Command.sendMessage(ChatFormatting.RED + event.getEntityLiving().getName() + ChatFormatting.GRAY + " no longer has strength");
      } 
    } 
  }
  
  public void onDeath(EntityPlayer player) {
    if (((Boolean)this.totemPops.getValue()).booleanValue() && 
      TotemPopContainer.containsKey(player.getName())) {
      int l_Count = ((Integer)TotemPopContainer.get(player.getName())).intValue();
      TotemPopContainer.remove(player.getName());
      if (l_Count == 1) {
        Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totem");
      } else {
        Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totems");
      } 
    } 
  }
  
  public void onTotemPop(EntityPlayer player) {
    if (((Boolean)this.totemPops.getValue()).booleanValue()) {
      if (fullNullCheck())
        return; 
      if (mc.player.equals(player))
        return; 
      int l_Count = 1;
      if (TotemPopContainer.containsKey(player.getName())) {
        l_Count = ((Integer)TotemPopContainer.get(player.getName())).intValue();
        TotemPopContainer.put(player.getName(), Integer.valueOf(++l_Count));
      } else {
        TotemPopContainer.put(player.getName(), Integer.valueOf(l_Count));
      } 
      if (l_Count == 1) {
        Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " popped " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totem");
      } else {
        Command.sendSilentMessage(ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " popped " + ChatFormatting.RED + l_Count + ChatFormatting.GRAY + " totems");
      } 
    } 
  }
  
  public void onUpdate() {
    if (this.check && this.timer.passedMs(750L))
      this.check = false; 
    if (((Boolean)this.visualRange.getValue()).booleanValue()) {
      ArrayList<EntityPlayer> tickPlayerList = new ArrayList<>(mc.world.playerEntities);
      if (tickPlayerList.size() > 0)
        for (EntityPlayer player : tickPlayerList) {
          if (player.getName().equals(mc.player.getName()) || this.knownPlayers.contains(player))
            continue; 
          this.knownPlayers.add(player);
          if (OyVey.friendManager.isFriend(player)) {
            Command.sendMessage(ChatFormatting.GRAY + "Player " + ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " entered your visual range" + (((Boolean)this.visualRangeCoords.getValue()).booleanValue() ? (" at (" + ChatFormatting.RED + (int)player.posX + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posY + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posZ + ChatFormatting.GRAY + ")!") : "!"));
          } else {
            Command.sendMessage(ChatFormatting.GRAY + "Player " + ChatFormatting.RED + player.getName() + ChatFormatting.GRAY + " entered your visual range" + (((Boolean)this.visualRangeCoords.getValue()).booleanValue() ? (" at (" + ChatFormatting.RED + (int)player.posX + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posY + ChatFormatting.GRAY + ", " + ChatFormatting.RED + (int)player.posZ + ChatFormatting.GRAY + ")!") : "!"));
          } 
          if (((Boolean)this.VisualRangeSound.getValue()).booleanValue())
            mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F); 
          return;
        }  
      if (this.knownPlayers.size() > 0)
        for (EntityPlayer player : this.knownPlayers) {
          if (tickPlayerList.contains(player))
            continue; 
          this.knownPlayers.remove(player);
          if (((Boolean)this.visualRangeLeaving.getValue()).booleanValue())
            if (OyVey.friendManager.isFriend(player)) {
              Command.sendMessage("Player §a" + player.getName() + "§r left your visual range" + (((Boolean)this.visualRangeCoords.getValue()).booleanValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"));
            } else {
              Command.sendMessage("Player §c" + player.getName() + "§r left your visual range" + (((Boolean)this.visualRangeCoords.getValue()).booleanValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"));
            }  
          return;
        }  
    } 
    if (((Boolean)this.pearlNotify.getValue()).booleanValue()) {
      if (mc.world == null || mc.player == null)
        return; 
      this.enderPearl = null;
      for (Entity e : mc.world.loadedEntityList) {
        if (e instanceof net.minecraft.entity.item.EntityEnderPearl) {
          this.enderPearl = e;
          break;
        } 
      } 
      if (this.enderPearl == null) {
        this.flag = true;
        return;
      } 
      EntityPlayer closestPlayer = null;
      for (EntityPlayer entity : mc.world.playerEntities) {
        if (closestPlayer == null) {
          closestPlayer = entity;
          continue;
        } 
        if (closestPlayer.getDistance(this.enderPearl) <= entity.getDistance(this.enderPearl))
          continue; 
        closestPlayer = entity;
      } 
      if (closestPlayer == mc.player)
        this.flag = false; 
      if (closestPlayer != null && this.flag) {
        String faceing = this.enderPearl.getHorizontalFacing().toString();
        if (faceing.equals("west")) {
          faceing = "east";
        } else if (faceing.equals("east")) {
          faceing = "west";
        } 
        Command.sendSilentMessage(OyVey.friendManager.isFriend(closestPlayer.getName()) ? (ChatFormatting.AQUA + closestPlayer.getName() + ChatFormatting.GRAY + " has just thrown a pearl heading " + ChatFormatting.RED + faceing + ChatFormatting.GRAY + "!") : (ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.GRAY + " has just thrown a pearl heading " + ChatFormatting.RED + faceing + ChatFormatting.GRAY + "!"));
        this.flag = false;
      } 
    } 
    if (((Boolean)this.ghastNotify.getValue()).booleanValue())
      for (Entity entity : mc.world.getLoadedEntityList()) {
        if (!(entity instanceof net.minecraft.entity.monster.EntityGhast) || this.ghasts.contains(entity))
          continue; 
        if (((Boolean)this.ghastChat.getValue()).booleanValue())
          Command.sendMessage(ChatFormatting.GRAY + "Ghast Detected at: " + ChatFormatting.RED + entity.getPosition().getX() + "x" + ChatFormatting.GRAY + ", " + ChatFormatting.RED + entity.getPosition().getY() + "y" + ChatFormatting.GRAY + ", " + ChatFormatting.RED + entity.getPosition().getZ() + "z" + ChatFormatting.GRAY + "."); 
        this.ghasts.add(entity);
        if (!((Boolean)this.ghastSound.getValue()).booleanValue())
          continue; 
        mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
      }  
  }
  
  public void loadFile() {
    List<String> fileInput = FileManager.readTextFileAllLines("phobos/util/ModuleMessage_List.txt");
    Iterator<String> i = fileInput.iterator();
    modules.clear();
    while (i.hasNext()) {
      String s = i.next();
      if (s.replaceAll("\\s", "").isEmpty())
        continue; 
      modules.add(s);
    } 
  }
}
