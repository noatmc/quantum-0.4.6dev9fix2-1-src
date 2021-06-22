package me.alpha432.oyvey.features.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.TestUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleFiller extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.Place));
  
  public Setting<PlaceMode> placeMode = register(new Setting("PlaceMode", PlaceMode.All, v -> (this.setting.getValue() == Settings.Place)));
  
  private final Setting<Double> smartRange = register(new Setting("SmartRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D), v -> (this.placeMode.getValue() == PlaceMode.Smart && this.setting.getValue() == Settings.Place)));
  
  private final Setting<Float> distance = register(new Setting("SuperSmartRange", Float.valueOf(2.0F), Float.valueOf(1.0F), Float.valueOf(7.0F), v -> (this.setting.getValue() == Settings.Place && this.placeMode.getValue() == PlaceMode.SuperSmart)));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250), v -> (this.setting.getValue() == Settings.Place)));
  
  private final Setting<Integer> blocksPerTick = register(new Setting("BlocksPerTick", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(20), v -> (this.setting.getValue() == Settings.Place)));
  
  private final Setting<Boolean> packet = register(new Setting("PacketPlace", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Place)));
  
  private final Setting<Boolean> autoDisable = register(new Setting("AutoDisable", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Misc)));
  
  private final Setting<Boolean> onlySafe = register(new Setting("OnlySafe", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Misc)));
  
  private final Setting<Double> range = register(new Setting("PlaceRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(6.0D), v -> (this.setting.getValue() == Settings.Misc)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Misc)));
  
  private static HoleFiller INSTANCE = new HoleFiller();
  
  static {
    surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets2(0, true));
  }
  
  private final Vec3d[] offsetsDefault = new Vec3d[] { new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D) };
  
  private int offsetStep = 0;
  
  private int oldSlot = -1;
  
  private boolean placing = false;
  
  private boolean hasOffhand = false;
  
  private EntityPlayer target;
  
  private static final BlockPos[] surroundOffset;
  
  private final Timer offTimer;
  
  private final Timer timer;
  
  private boolean isSneaking;
  
  private final Map<BlockPos, Integer> retries;
  
  private final Timer retryTimer;
  
  private int blocksThisTick;
  
  private ArrayList<BlockPos> holes;
  
  private int trie;
  
  public HoleFiller() {
    super("HoleFill", "hf", Module.Category.COMBAT, true, false, true);
    this.offTimer = new Timer();
    this.timer = new Timer();
    this.blocksThisTick = 0;
    this.retries = new HashMap<>();
    this.retryTimer = new Timer();
    this.holes = new ArrayList<>();
    setInstance();
  }
  
  public static HoleFiller getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HoleFiller(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onEnable() {
    if (fullNullCheck())
      disable(); 
    this.oldSlot = mc.player.inventory.currentItem;
    this.offTimer.reset();
    this.trie = 0;
  }
  
  public void onTick() {
    if (isOn() && (((Integer)this.blocksPerTick.getValue()).intValue() != 1 || !((Boolean)this.rotate.getValue()).booleanValue()))
      doHoleFill(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (isOn() && event.getStage() == 0 && ((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue())
      doHoleFill(); 
  }
  
  public void onDisable() {
    this.retries.clear();
  }
  
  private void place(BlockPos pos, int slot, int oldSlot) {
    mc.player.inventory.currentItem = slot;
    mc.playerController.updateController();
    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), mc.player.isSneaking());
    mc.player.inventory.currentItem = oldSlot;
    mc.playerController.updateController();
  }
  
  private void doHoleFill() {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial check : ()Z
    //   4: ifeq -> 8
    //   7: return
    //   8: aload_0
    //   9: new java/util/ArrayList
    //   12: dup
    //   13: invokespecial <init> : ()V
    //   16: putfield holes : Ljava/util/ArrayList;
    //   19: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   22: getfield player : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   25: invokevirtual getPosition : ()Lnet/minecraft/util/math/BlockPos;
    //   28: aload_0
    //   29: getfield range : Lme/alpha432/oyvey/features/setting/Setting;
    //   32: invokevirtual getValue : ()Ljava/lang/Object;
    //   35: checkcast java/lang/Double
    //   38: invokevirtual doubleValue : ()D
    //   41: dneg
    //   42: aload_0
    //   43: getfield range : Lme/alpha432/oyvey/features/setting/Setting;
    //   46: invokevirtual getValue : ()Ljava/lang/Object;
    //   49: checkcast java/lang/Double
    //   52: invokevirtual doubleValue : ()D
    //   55: dneg
    //   56: aload_0
    //   57: getfield range : Lme/alpha432/oyvey/features/setting/Setting;
    //   60: invokevirtual getValue : ()Ljava/lang/Object;
    //   63: checkcast java/lang/Double
    //   66: invokevirtual doubleValue : ()D
    //   69: dneg
    //   70: invokevirtual add : (DDD)Lnet/minecraft/util/math/BlockPos;
    //   73: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   76: getfield player : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   79: invokevirtual getPosition : ()Lnet/minecraft/util/math/BlockPos;
    //   82: aload_0
    //   83: getfield range : Lme/alpha432/oyvey/features/setting/Setting;
    //   86: invokevirtual getValue : ()Ljava/lang/Object;
    //   89: checkcast java/lang/Double
    //   92: invokevirtual doubleValue : ()D
    //   95: aload_0
    //   96: getfield range : Lme/alpha432/oyvey/features/setting/Setting;
    //   99: invokevirtual getValue : ()Ljava/lang/Object;
    //   102: checkcast java/lang/Double
    //   105: invokevirtual doubleValue : ()D
    //   108: aload_0
    //   109: getfield range : Lme/alpha432/oyvey/features/setting/Setting;
    //   112: invokevirtual getValue : ()Ljava/lang/Object;
    //   115: checkcast java/lang/Double
    //   118: invokevirtual doubleValue : ()D
    //   121: invokevirtual add : (DDD)Lnet/minecraft/util/math/BlockPos;
    //   124: invokestatic getAllInBox : (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;
    //   127: astore_3
    //   128: aload_3
    //   129: invokeinterface iterator : ()Ljava/util/Iterator;
    //   134: astore #4
    //   136: aload #4
    //   138: invokeinterface hasNext : ()Z
    //   143: ifeq -> 656
    //   146: aload #4
    //   148: invokeinterface next : ()Ljava/lang/Object;
    //   153: checkcast net/minecraft/util/math/BlockPos
    //   156: astore #5
    //   158: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   161: getfield player : Lnet/minecraft/client/entity/EntityPlayerSP;
    //   164: aload #5
    //   166: invokevirtual getDistanceSq : (Lnet/minecraft/util/math/BlockPos;)D
    //   169: aload_0
    //   170: getfield range : Lme/alpha432/oyvey/features/setting/Setting;
    //   173: invokevirtual getValue : ()Ljava/lang/Object;
    //   176: checkcast java/lang/Double
    //   179: invokevirtual doubleValue : ()D
    //   182: invokestatic square : (D)D
    //   185: dcmpl
    //   186: ifgt -> 136
    //   189: aload_0
    //   190: getfield placeMode : Lme/alpha432/oyvey/features/setting/Setting;
    //   193: invokevirtual getValue : ()Ljava/lang/Object;
    //   196: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller$PlaceMode.Smart : Lme/alpha432/oyvey/features/modules/combat/HoleFiller$PlaceMode;
    //   199: if_acmpne -> 214
    //   202: aload_0
    //   203: aload #5
    //   205: invokespecial isPlayerInRange : (Lnet/minecraft/util/math/BlockPos;)Z
    //   208: ifne -> 214
    //   211: goto -> 136
    //   214: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   217: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   220: aload #5
    //   222: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   225: invokeinterface getMaterial : ()Lnet/minecraft/block/material/Material;
    //   230: invokevirtual blocksMovement : ()Z
    //   233: ifne -> 653
    //   236: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   239: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   242: aload #5
    //   244: iconst_0
    //   245: iconst_1
    //   246: iconst_0
    //   247: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   250: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   253: invokeinterface getMaterial : ()Lnet/minecraft/block/material/Material;
    //   258: invokevirtual blocksMovement : ()Z
    //   261: ifne -> 653
    //   264: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   267: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   270: aload #5
    //   272: iconst_1
    //   273: iconst_0
    //   274: iconst_0
    //   275: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   278: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   281: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   286: getstatic net/minecraft/init/Blocks.BEDROCK : Lnet/minecraft/block/Block;
    //   289: if_acmpne -> 296
    //   292: iconst_1
    //   293: goto -> 297
    //   296: iconst_0
    //   297: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   300: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   303: aload #5
    //   305: iconst_1
    //   306: iconst_0
    //   307: iconst_0
    //   308: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   311: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   314: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   319: getstatic net/minecraft/init/Blocks.OBSIDIAN : Lnet/minecraft/block/Block;
    //   322: if_acmpne -> 329
    //   325: iconst_1
    //   326: goto -> 330
    //   329: iconst_0
    //   330: ior
    //   331: ifeq -> 632
    //   334: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   337: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   340: aload #5
    //   342: iconst_0
    //   343: iconst_0
    //   344: iconst_1
    //   345: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   348: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   351: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   356: getstatic net/minecraft/init/Blocks.BEDROCK : Lnet/minecraft/block/Block;
    //   359: if_acmpne -> 366
    //   362: iconst_1
    //   363: goto -> 367
    //   366: iconst_0
    //   367: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   370: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   373: aload #5
    //   375: iconst_0
    //   376: iconst_0
    //   377: iconst_1
    //   378: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   381: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   384: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   389: getstatic net/minecraft/init/Blocks.OBSIDIAN : Lnet/minecraft/block/Block;
    //   392: if_acmpne -> 399
    //   395: iconst_1
    //   396: goto -> 400
    //   399: iconst_0
    //   400: ior
    //   401: ifeq -> 632
    //   404: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   407: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   410: aload #5
    //   412: iconst_m1
    //   413: iconst_0
    //   414: iconst_0
    //   415: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   418: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   421: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   426: getstatic net/minecraft/init/Blocks.BEDROCK : Lnet/minecraft/block/Block;
    //   429: if_acmpne -> 436
    //   432: iconst_1
    //   433: goto -> 437
    //   436: iconst_0
    //   437: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   440: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   443: aload #5
    //   445: iconst_m1
    //   446: iconst_0
    //   447: iconst_0
    //   448: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   451: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   454: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   459: getstatic net/minecraft/init/Blocks.OBSIDIAN : Lnet/minecraft/block/Block;
    //   462: if_acmpne -> 469
    //   465: iconst_1
    //   466: goto -> 470
    //   469: iconst_0
    //   470: ior
    //   471: ifeq -> 632
    //   474: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   477: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   480: aload #5
    //   482: iconst_0
    //   483: iconst_0
    //   484: iconst_m1
    //   485: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   488: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   491: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   496: getstatic net/minecraft/init/Blocks.BEDROCK : Lnet/minecraft/block/Block;
    //   499: if_acmpne -> 506
    //   502: iconst_1
    //   503: goto -> 507
    //   506: iconst_0
    //   507: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   510: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   513: aload #5
    //   515: iconst_0
    //   516: iconst_0
    //   517: iconst_m1
    //   518: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   521: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   524: invokeinterface getBlock : ()Lnet/minecraft/block/Block;
    //   529: getstatic net/minecraft/init/Blocks.OBSIDIAN : Lnet/minecraft/block/Block;
    //   532: if_acmpne -> 539
    //   535: iconst_1
    //   536: goto -> 540
    //   539: iconst_0
    //   540: ior
    //   541: ifeq -> 632
    //   544: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   547: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   550: aload #5
    //   552: iconst_0
    //   553: iconst_0
    //   554: iconst_0
    //   555: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   558: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   561: invokeinterface getMaterial : ()Lnet/minecraft/block/material/Material;
    //   566: getstatic net/minecraft/block/material/Material.AIR : Lnet/minecraft/block/material/Material;
    //   569: if_acmpne -> 632
    //   572: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   575: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   578: aload #5
    //   580: iconst_0
    //   581: iconst_1
    //   582: iconst_0
    //   583: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   586: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   589: invokeinterface getMaterial : ()Lnet/minecraft/block/material/Material;
    //   594: getstatic net/minecraft/block/material/Material.AIR : Lnet/minecraft/block/material/Material;
    //   597: if_acmpne -> 632
    //   600: getstatic me/alpha432/oyvey/features/modules/combat/HoleFiller.mc : Lnet/minecraft/client/Minecraft;
    //   603: getfield world : Lnet/minecraft/client/multiplayer/WorldClient;
    //   606: aload #5
    //   608: iconst_0
    //   609: iconst_2
    //   610: iconst_0
    //   611: invokevirtual add : (III)Lnet/minecraft/util/math/BlockPos;
    //   614: invokevirtual getBlockState : (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
    //   617: invokeinterface getMaterial : ()Lnet/minecraft/block/material/Material;
    //   622: getstatic net/minecraft/block/material/Material.AIR : Lnet/minecraft/block/material/Material;
    //   625: if_acmpne -> 632
    //   628: iconst_1
    //   629: goto -> 633
    //   632: iconst_0
    //   633: istore #6
    //   635: iload #6
    //   637: ifne -> 643
    //   640: goto -> 136
    //   643: aload_0
    //   644: getfield holes : Ljava/util/ArrayList;
    //   647: aload #5
    //   649: invokevirtual add : (Ljava/lang/Object;)Z
    //   652: pop
    //   653: goto -> 136
    //   656: getstatic me/alpha432/oyvey/OyVey.holeManager : Lme/alpha432/oyvey/manager/HoleManager;
    //   659: invokevirtual getHoles : ()Ljava/util/List;
    //   662: astore_2
    //   663: aload_2
    //   664: dup
    //   665: astore #4
    //   667: monitorenter
    //   668: new java/util/ArrayList
    //   671: dup
    //   672: getstatic me/alpha432/oyvey/OyVey.holeManager : Lme/alpha432/oyvey/manager/HoleManager;
    //   675: invokevirtual getHoles : ()Ljava/util/List;
    //   678: invokespecial <init> : (Ljava/util/Collection;)V
    //   681: astore_1
    //   682: aload #4
    //   684: monitorexit
    //   685: goto -> 696
    //   688: astore #7
    //   690: aload #4
    //   692: monitorexit
    //   693: aload #7
    //   695: athrow
    //   696: aload_0
    //   697: getfield holes : Ljava/util/ArrayList;
    //   700: aload_0
    //   701: <illegal opcode> accept : (Lme/alpha432/oyvey/features/modules/combat/HoleFiller;)Ljava/util/function/Consumer;
    //   706: invokevirtual forEach : (Ljava/util/function/Consumer;)V
    //   709: aload_0
    //   710: invokevirtual toggle : ()V
    //   713: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #159	-> 0
    //   #160	-> 7
    //   #162	-> 8
    //   #163	-> 19
    //   #164	-> 128
    //   #165	-> 158
    //   #166	-> 211
    //   #167	-> 214
    //   #168	-> 264
    //   #169	-> 635
    //   #170	-> 640
    //   #172	-> 643
    //   #174	-> 653
    //   #175	-> 656
    //   #176	-> 663
    //   #177	-> 668
    //   #178	-> 682
    //   #179	-> 696
    //   #180	-> 709
    //   #181	-> 713
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   635	18	6	solidNeighbours	Z
    //   158	495	5	pos	Lnet/minecraft/util/math/BlockPos;
    //   682	6	1	targets	Ljava/util/ArrayList;
    //   0	714	0	this	Lme/alpha432/oyvey/features/modules/combat/HoleFiller;
    //   696	18	1	targets	Ljava/util/ArrayList;
    //   663	51	2	object	Ljava/lang/Object;
    //   128	586	3	blocks	Ljava/lang/Iterable;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   682	6	1	targets	Ljava/util/ArrayList<Lnet/minecraft/util/math/BlockPos;>;
    //   696	18	1	targets	Ljava/util/ArrayList<Lnet/minecraft/util/math/BlockPos;>;
    //   128	586	3	blocks	Ljava/lang/Iterable<Lnet/minecraft/util/math/BlockPos;>;
    // Exception table:
    //   from	to	target	type
    //   668	685	688	finally
    //   688	693	688	finally
  }
  
  private void placeBlock(BlockPos pos) {
    for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
      if (entity instanceof net.minecraft.entity.EntityLivingBase)
        return; 
    } 
    if (this.blocksThisTick < ((Integer)this.blocksPerTick.getValue()).intValue()) {
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
      if (obbySlot == -1 && eChestSot == -1)
        toggle(); 
      boolean smartRotate = (((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue());
      if (smartRotate) {
        this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      } else {
        this.isSneaking = BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      } 
      int originalSlot = mc.player.inventory.currentItem;
      mc.player.inventory.currentItem = (obbySlot == -1) ? eChestSot : obbySlot;
      mc.playerController.updateController();
      TestUtil.placeBlock(pos);
      if (mc.player.inventory.currentItem != originalSlot) {
        mc.player.inventory.currentItem = originalSlot;
        mc.playerController.updateController();
      } 
      this.timer.reset();
      this.blocksThisTick++;
    } 
  }
  
  private boolean isPlayerInRange(BlockPos pos) {
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, ((Double)this.smartRange.getValue()).doubleValue()))
        continue; 
      return true;
    } 
    return false;
  }
  
  private boolean check() {
    this.blocksThisTick = 0;
    if (this.retryTimer.passedMs(2000L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    if (((Boolean)this.onlySafe.getValue()).booleanValue() && !EntityUtil.isSafe((Entity)mc.player)) {
      disable();
      return true;
    } 
    return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
  }
  
  public enum PlaceMode {
    Smart, SuperSmart, All;
  }
  
  public enum Settings {
    Place, Misc;
  }
}
