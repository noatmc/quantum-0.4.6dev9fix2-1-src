package me.alpha432.oyvey.features.modules.player;

import java.awt.Color;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.BlockEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speedmine extends Module {
  private static Speedmine INSTANCE = new Speedmine();
  
  private final Setting<Float> range = register(new Setting("Range", Float.valueOf(10.0F), Float.valueOf(0.0F), Float.valueOf(50.0F)));
  
  private final Timer timer = new Timer();
  
  public Setting<Boolean> tweaks = register(new Setting("Tweaks", Boolean.valueOf(true)));
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.PACKET, v -> ((Boolean)this.tweaks.getValue()).booleanValue()));
  
  public Setting<Boolean> reset = register(new Setting("Reset", Boolean.valueOf(true)));
  
  public Setting<Float> damage = register(new Setting("Damage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> (this.mode.getValue() == Mode.DAMAGE && ((Boolean)this.tweaks.getValue()).booleanValue())));
  
  public Setting<Boolean> noBreakAnim = register(new Setting("NoBreakAnim", Boolean.valueOf(false)));
  
  public Setting<Boolean> noDelay = register(new Setting("NoDelay", Boolean.valueOf(false)));
  
  public Setting<Boolean> noSwing = register(new Setting("NoSwing", Boolean.valueOf(false)));
  
  public Setting<Boolean> noTrace = register(new Setting("NoTrace", Boolean.valueOf(false)));
  
  public Setting<Boolean> noGapTrace = register(new Setting("NoGapTrace", Boolean.valueOf(false), v -> ((Boolean)this.noTrace.getValue()).booleanValue()));
  
  public Setting<Boolean> allow = register(new Setting("AllowMultiTask", Boolean.valueOf(false)));
  
  public Setting<Boolean> pickaxe = register(new Setting("Pickaxe", Boolean.valueOf(true), v -> ((Boolean)this.noTrace.getValue()).booleanValue()));
  
  public Setting<Boolean> doubleBreak = register(new Setting("DoubleBreak", Boolean.valueOf(false)));
  
  public Setting<Boolean> webSwitch = register(new Setting("WebSwitch", Boolean.valueOf(false)));
  
  public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", Boolean.valueOf(false)));
  
  public Setting<Boolean> illegal = register(new Setting("IllegalMine", Boolean.valueOf(false)));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(false)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  public BlockPos currentPos;
  
  public IBlockState currentBlockState;
  
  private boolean isMining = false;
  
  private BlockPos lastPos = null;
  
  private EnumFacing lastFacing = null;
  
  public Speedmine() {
    super("Mine", "m", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  public static Speedmine getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Speedmine(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onTick() {
    if (this.currentPos != null) {
      if (mc.player != null && mc.player.getDistanceSq(this.currentPos) > MathUtil.square(((Float)this.range.getValue()).floatValue())) {
        this.currentPos = null;
        this.currentBlockState = null;
        return;
      } 
      if (mc.player != null && ((Boolean)this.silentSwitch.getValue()).booleanValue() && this.timer.passedMs((int)(2000.0F * OyVey.serverManager.getTpsFactor())) && getPickSlot() != -1)
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(getPickSlot())); 
      if (!mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
        this.currentPos = null;
        this.currentBlockState = null;
      } else if (((Boolean)this.webSwitch.getValue()).booleanValue() && this.currentBlockState.getBlock() == Blocks.WEB && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe) {
        InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
      } 
    } 
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (((Boolean)this.noDelay.getValue()).booleanValue())
      mc.playerController.blockHitDelay = 0; 
    if (this.isMining && this.lastPos != null && this.lastFacing != null && ((Boolean)this.noBreakAnim.getValue()).booleanValue())
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing)); 
    if (((Boolean)this.reset.getValue()).booleanValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && !((Boolean)this.allow.getValue()).booleanValue())
      mc.playerController.isHittingBlock = false; 
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.render.getValue()).booleanValue() && this.currentPos != null) {
      Color color = new Color(this.timer.passedMs((int)(2000.0F * OyVey.serverManager.getTpsFactor())) ? 0 : 255, this.timer.passedMs((int)(2000.0F * OyVey.serverManager.getTpsFactor())) ? 255 : 0, 0, 255);
      RenderUtil.drawBoxESP(this.currentPos, color, false, color, ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (fullNullCheck())
      return; 
    if (event.getStage() == 0) {
      if (((Boolean)this.noSwing.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation)
        event.setCanceled(true); 
      CPacketPlayerDigging packet;
      if (((Boolean)this.noBreakAnim.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)event.getPacket()) != null && packet.getPosition() != null) {
        try {
          for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(packet.getPosition()))) {
            if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
              continue; 
            showAnimation();
            return;
          } 
        } catch (Exception exception) {}
        if (packet.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK))
          showAnimation(true, packet.getPosition(), packet.getFacing()); 
        if (packet.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK))
          showAnimation(); 
      } 
    } 
  }
  
  @SubscribeEvent
  public void onBlockEvent(BlockEvent event) {
    if (fullNullCheck())
      return; 
    if (event.getStage() == 3 && mc.world.getBlockState(event.pos).getBlock() instanceof net.minecraft.block.BlockEndPortalFrame)
      mc.world.getBlockState(event.pos).getBlock().setHardness(50.0F); 
    if (event.getStage() == 3 && ((Boolean)this.reset.getValue()).booleanValue() && mc.playerController.curBlockDamageMP > 0.1F)
      mc.playerController.isHittingBlock = true; 
    if (event.getStage() == 4 && ((Boolean)this.tweaks.getValue()).booleanValue()) {
      if (BlockUtil.canBreak(event.pos)) {
        if (((Boolean)this.reset.getValue()).booleanValue())
          mc.playerController.isHittingBlock = false; 
        switch ((Mode)this.mode.getValue()) {
          case PACKET:
            if (this.currentPos == null) {
              this.currentPos = event.pos;
              this.currentBlockState = mc.world.getBlockState(this.currentPos);
              this.timer.reset();
            } 
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            event.setCanceled(true);
            break;
          case DAMAGE:
            if (mc.playerController.curBlockDamageMP < ((Float)this.damage.getValue()).floatValue())
              break; 
            mc.playerController.curBlockDamageMP = 1.0F;
            break;
          case INSTANT:
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            mc.playerController.onPlayerDestroyBlock(event.pos);
            mc.world.setBlockToAir(event.pos);
            break;
        } 
      } 
      BlockPos above;
      if (((Boolean)this.doubleBreak.getValue()).booleanValue() && BlockUtil.canBreak(above = event.pos.add(0, 1, 0)) && mc.player.getDistance(above.getX(), above.getY(), above.getZ()) <= 5.0D) {
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
        mc.playerController.onPlayerDestroyBlock(above);
        mc.world.setBlockToAir(above);
      } 
    } 
  }
  
  private int getPickSlot() {
    for (int i = 0; i < 9; ) {
      if (mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) {
        i++;
        continue;
      } 
      return i;
    } 
    return -1;
  }
  
  private void showAnimation(boolean isMining, BlockPos lastPos, EnumFacing lastFacing) {
    this.isMining = isMining;
    this.lastPos = lastPos;
    this.lastFacing = lastFacing;
  }
  
  public void showAnimation() {
    showAnimation(false, (BlockPos)null, (EnumFacing)null);
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public enum Mode {
    PACKET, DAMAGE, INSTANT;
  }
}
