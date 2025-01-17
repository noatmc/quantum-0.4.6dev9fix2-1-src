package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class HudComponents extends Module {
  private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulka_box.png");
  
  private static final double HALF_PI = 1.5707963267948966D;
  
  public static ResourceLocation logo = new ResourceLocation("textures/quantum.png");
  
  public Setting<Boolean> inventory = register(new Setting("Inventory", Boolean.valueOf(false)));
  
  public Setting<Integer> invX = register(new Setting("InvX", Integer.valueOf(564), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
  
  public Setting<Integer> invY = register(new Setting("InvY", Integer.valueOf(467), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
  
  public Setting<Integer> fineinvX = register(new Setting("InvFineX", Integer.valueOf(0), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
  
  public Setting<Integer> fineinvY = register(new Setting("InvFineY", Integer.valueOf(0), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
  
  public Setting<Boolean> renderXCarry = register(new Setting("RenderXCarry", Boolean.valueOf(false), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
  
  public Setting<Integer> invH = register(new Setting("InvH", Integer.valueOf(3), v -> ((Boolean)this.inventory.getValue()).booleanValue()));
  
  public Setting<Boolean> holeHud = register(new Setting("HoleHUD", Boolean.valueOf(false)));
  
  public Setting<Integer> holeX = register(new Setting("HoleX", Integer.valueOf(279), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.holeHud.getValue()).booleanValue()));
  
  public Setting<Integer> holeY = register(new Setting("HoleY", Integer.valueOf(485), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.holeHud.getValue()).booleanValue()));
  
  public Setting<Compass> compass = register(new Setting("Compass", Compass.NONE));
  
  public Setting<Integer> compassX = register(new Setting("CompX", Integer.valueOf(472), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.compass.getValue() != Compass.NONE)));
  
  public Setting<Integer> compassY = register(new Setting("CompY", Integer.valueOf(424), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.compass.getValue() != Compass.NONE)));
  
  public Setting<Integer> scale = register(new Setting("Scale", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(10), v -> (this.compass.getValue() != Compass.NONE)));
  
  public Setting<Boolean> playerViewer = register(new Setting("PlayerViewer", Boolean.valueOf(false)));
  
  public Setting<Integer> playerViewerX = register(new Setting("PlayerX", Integer.valueOf(752), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.playerViewer.getValue()).booleanValue()));
  
  public Setting<Integer> playerViewerY = register(new Setting("PlayerY", Integer.valueOf(497), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.playerViewer.getValue()).booleanValue()));
  
  public Setting<Float> playerScale = register(new Setting("PlayerScale", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(2.0F), v -> ((Boolean)this.playerViewer.getValue()).booleanValue()));
  
  public Setting<Boolean> imageLogo = register(new Setting("ImageLogo", Boolean.valueOf(false)));
  
  public Setting<Integer> imageX = register(new Setting("ImageX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
  
  public Setting<Integer> imageY = register(new Setting("ImageY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
  
  public Setting<Integer> imageWidth = register(new Setting("ImageWidth", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
  
  public Setting<Integer> imageHeight = register(new Setting("ImageHeight", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.imageLogo.getValue()).booleanValue()));
  
  public HudComponents() {
    super("HudComponents", "HudComponents", Module.Category.CLIENT, false, false, true);
  }
  
  public void onRender2D(Render2DEvent event) {
    if (fullNullCheck())
      return; 
    if (((Boolean)this.playerViewer.getValue()).booleanValue())
      drawPlayer(); 
    if (this.compass.getValue() != Compass.NONE)
      drawCompass(); 
    if (((Boolean)this.holeHud.getValue()).booleanValue())
      drawOverlay(event.partialTicks); 
    if (((Boolean)this.inventory.getValue()).booleanValue())
      renderInventory(); 
    if (((Boolean)this.imageLogo.getValue()).booleanValue())
      drawImageLogo(); 
  }
  
  public static EntityPlayer getClosestEnemy() {
    EntityPlayer closestPlayer = null;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (player == mc.player || 
        OyVey.friendManager.isFriend(player))
        continue; 
      if (closestPlayer == null) {
        closestPlayer = player;
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) < mc.player.getDistanceSq((Entity)closestPlayer))
        closestPlayer = player; 
    } 
    return closestPlayer;
  }
  
  public void drawImageLogo() {
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    mc.getTextureManager().bindTexture(logo);
    drawCompleteImage(((Integer)this.imageX.getValue()).intValue(), ((Integer)this.imageY.getValue()).intValue(), ((Integer)this.imageWidth.getValue()).intValue(), ((Integer)this.imageHeight.getValue()).intValue());
    mc.getTextureManager().deleteTexture(logo);
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
  }
  
  public void drawCompass() {
    ScaledResolution sr = new ScaledResolution(mc);
    if (this.compass.getValue() == Compass.LINE) {
      float playerYaw = mc.player.rotationYaw;
      float rotationYaw = MathUtil.wrap(playerYaw);
      RenderUtil.drawRect(((Integer)this.compassX.getValue()).intValue(), ((Integer)this.compassY.getValue()).intValue(), (((Integer)this.compassX.getValue()).intValue() + 100), (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight()), 1963986960);
      RenderUtil.glScissor(((Integer)this.compassX.getValue()).intValue(), ((Integer)this.compassY.getValue()).intValue(), (((Integer)this.compassX.getValue()).intValue() + 100), (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight()), sr);
      GL11.glEnable(3089);
      float zeroZeroYaw = MathUtil.wrap((float)(Math.atan2(0.0D - mc.player.posZ, 0.0D - mc.player.posX) * 180.0D / Math.PI) - 90.0F);
      RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + zeroZeroYaw, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + zeroZeroYaw, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -61424);
      RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 45.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 45.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
      RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 45.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 45.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
      RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 135.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 135.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
      RenderUtil.drawLine(((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 135.0F, (((Integer)this.compassY.getValue()).intValue() + 2), ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 135.0F, (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 2), 2.0F, -1);
      this.renderer.drawStringWithShadow("n", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 180.0F - this.renderer.getStringWidth("n") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
      this.renderer.drawStringWithShadow("n", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 180.0F - this.renderer.getStringWidth("n") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
      this.renderer.drawStringWithShadow("e", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - 90.0F - this.renderer.getStringWidth("e") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
      this.renderer.drawStringWithShadow("s", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F - this.renderer.getStringWidth("s") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
      this.renderer.drawStringWithShadow("w", ((Integer)this.compassX.getValue()).intValue() - rotationYaw + 50.0F + 90.0F - this.renderer.getStringWidth("w") / 2.0F, ((Integer)this.compassY.getValue()).intValue(), -1);
      RenderUtil.drawLine((((Integer)this.compassX.getValue()).intValue() + 50), (((Integer)this.compassY.getValue()).intValue() + 1), (((Integer)this.compassX.getValue()).intValue() + 50), (((Integer)this.compassY.getValue()).intValue() + this.renderer.getFontHeight() - 1), 2.0F, -7303024);
      GL11.glDisable(3089);
    } else {
      double centerX = ((Integer)this.compassX.getValue()).intValue();
      double centerY = ((Integer)this.compassY.getValue()).intValue();
      for (Direction dir : Direction.values()) {
        double rad = getPosOnCompass(dir);
        this.renderer.drawStringWithShadow(dir.name(), (float)(centerX + getX(rad)), (float)(centerY + getY(rad)), (dir == Direction.N) ? -65536 : -1);
      } 
    } 
  }
  
  public void drawPlayer(EntityPlayer player, int x, int y) {
    EntityPlayer ent = player;
    GlStateManager.pushMatrix();
    GlStateManager.color(1.0F, 1.0F, 1.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableAlpha();
    GlStateManager.shadeModel(7424);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GlStateManager.rotate(0.0F, 0.0F, 5.0F, 0.0F);
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate((((Integer)this.playerViewerX.getValue()).intValue() + 25), (((Integer)this.playerViewerY.getValue()).intValue() + 25), 50.0F);
    GlStateManager.scale(-50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue());
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-((float)Math.atan((((Integer)this.playerViewerY.getValue()).intValue() / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.translate(0.0F, 0.0F, 0.0F);
    RenderManager rendermanager = mc.getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
    try {
      rendermanager.renderEntity((Entity)ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    } catch (Exception exception) {}
    rendermanager.setRenderShadow(true);
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    GlStateManager.depthFunc(515);
    GlStateManager.resetColor();
    GlStateManager.disableDepth();
    GlStateManager.popMatrix();
  }
  
  public void drawPlayer() {
    EntityPlayerSP entityPlayerSP = mc.player;
    GlStateManager.pushMatrix();
    GlStateManager.color(1.0F, 1.0F, 1.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableAlpha();
    GlStateManager.shadeModel(7424);
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GlStateManager.rotate(0.0F, 0.0F, 5.0F, 0.0F);
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate((((Integer)this.playerViewerX.getValue()).intValue() + 25), (((Integer)this.playerViewerY.getValue()).intValue() + 25), 50.0F);
    GlStateManager.scale(-50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue(), 50.0F * ((Float)this.playerScale.getValue()).floatValue());
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-((float)Math.atan((((Integer)this.playerViewerY.getValue()).intValue() / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.translate(0.0F, 0.0F, 0.0F);
    RenderManager rendermanager = mc.getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
    try {
      rendermanager.renderEntity((Entity)entityPlayerSP, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    } catch (Exception exception) {}
    rendermanager.setRenderShadow(true);
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    GlStateManager.depthFunc(515);
    GlStateManager.resetColor();
    GlStateManager.disableDepth();
    GlStateManager.popMatrix();
  }
  
  private double getX(double rad) {
    return Math.sin(rad) * (((Integer)this.scale.getValue()).intValue() * 10);
  }
  
  private double getY(double rad) {
    double epicPitch = MathHelper.clamp(mc.player.rotationPitch + 30.0F, -90.0F, 90.0F);
    double pitchRadians = Math.toRadians(epicPitch);
    return Math.cos(rad) * Math.sin(pitchRadians) * (((Integer)this.scale.getValue()).intValue() * 10);
  }
  
  private enum Direction {
    N, W, S, E;
  }
  
  private static double getPosOnCompass(Direction dir) {
    double yaw = Math.toRadians(MathHelper.wrapDegrees(mc.player.rotationYaw));
    int index = dir.ordinal();
    return yaw + index * 1.5707963267948966D;
  }
  
  public enum Compass {
    NONE, CIRCLE, LINE;
  }
  
  public void drawOverlay(float partialTicks) {
    float yaw = 0.0F;
    int dir = MathHelper.floor((mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3;
    switch (dir) {
      case 1:
        yaw = 90.0F;
        break;
      case 2:
        yaw = -180.0F;
        break;
      case 3:
        yaw = -90.0F;
        break;
    } 
    BlockPos northPos = traceToBlock(partialTicks, yaw);
    Block north = getBlock(northPos);
    if (north != null && north != Blocks.AIR) {
      int damage = getBlockDamage(northPos);
      if (damage != 0)
        RenderUtil.drawRect((((Integer)this.holeX.getValue()).intValue() + 16), ((Integer)this.holeY.getValue()).intValue(), (((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 16), 1627324416); 
      drawBlock(north, (((Integer)this.holeX.getValue()).intValue() + 16), ((Integer)this.holeY.getValue()).intValue());
    } 
    BlockPos southPos = traceToBlock(partialTicks, yaw - 180.0F);
    Block south = getBlock(southPos);
    if (south != null && south != Blocks.AIR) {
      int damage = getBlockDamage(southPos);
      if (damage != 0)
        RenderUtil.drawRect((((Integer)this.holeX.getValue()).intValue() + 16), (((Integer)this.holeY.getValue()).intValue() + 32), (((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 48), 1627324416); 
      drawBlock(south, (((Integer)this.holeX.getValue()).intValue() + 16), (((Integer)this.holeY.getValue()).intValue() + 32));
    } 
    BlockPos eastPos = traceToBlock(partialTicks, yaw + 90.0F);
    Block east = getBlock(eastPos);
    if (east != null && east != Blocks.AIR) {
      int damage = getBlockDamage(eastPos);
      if (damage != 0)
        RenderUtil.drawRect((((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 16), (((Integer)this.holeX.getValue()).intValue() + 48), (((Integer)this.holeY.getValue()).intValue() + 32), 1627324416); 
      drawBlock(east, (((Integer)this.holeX.getValue()).intValue() + 32), (((Integer)this.holeY.getValue()).intValue() + 16));
    } 
    BlockPos westPos = traceToBlock(partialTicks, yaw - 90.0F);
    Block west = getBlock(westPos);
    if (west != null && west != Blocks.AIR) {
      int damage = getBlockDamage(westPos);
      if (damage != 0)
        RenderUtil.drawRect(((Integer)this.holeX.getValue()).intValue(), (((Integer)this.holeY.getValue()).intValue() + 16), (((Integer)this.holeX.getValue()).intValue() + 16), (((Integer)this.holeY.getValue()).intValue() + 32), 1627324416); 
      drawBlock(west, ((Integer)this.holeX.getValue()).intValue(), (((Integer)this.holeY.getValue()).intValue() + 16));
    } 
  }
  
  public void drawOverlay(float partialTicks, Entity player, int x, int y) {
    float yaw = 0.0F;
    int dir = MathHelper.floor((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3;
    switch (dir) {
      case 1:
        yaw = 90.0F;
        break;
      case 2:
        yaw = -180.0F;
        break;
      case 3:
        yaw = -90.0F;
        break;
    } 
    BlockPos northPos = traceToBlock(partialTicks, yaw, player);
    Block north = getBlock(northPos);
    if (north != null && north != Blocks.AIR) {
      int damage = getBlockDamage(northPos);
      if (damage != 0)
        RenderUtil.drawRect((x + 16), y, (x + 32), (y + 16), 1627324416); 
      drawBlock(north, (x + 16), y);
    } 
    BlockPos southPos = traceToBlock(partialTicks, yaw - 180.0F, player);
    Block south = getBlock(southPos);
    if (south != null && south != Blocks.AIR) {
      int damage = getBlockDamage(southPos);
      if (damage != 0)
        RenderUtil.drawRect((x + 16), (y + 32), (x + 32), (y + 48), 1627324416); 
      drawBlock(south, (x + 16), (y + 32));
    } 
    BlockPos eastPos = traceToBlock(partialTicks, yaw + 90.0F, player);
    Block east = getBlock(eastPos);
    if (east != null && east != Blocks.AIR) {
      int damage = getBlockDamage(eastPos);
      if (damage != 0)
        RenderUtil.drawRect((x + 32), (y + 16), (x + 48), (y + 32), 1627324416); 
      drawBlock(east, (x + 32), (y + 16));
    } 
    BlockPos westPos = traceToBlock(partialTicks, yaw - 90.0F, player);
    Block west = getBlock(westPos);
    if (west != null && west != Blocks.AIR) {
      int damage = getBlockDamage(westPos);
      if (damage != 0)
        RenderUtil.drawRect(x, (y + 16), (x + 16), (y + 32), 1627324416); 
      drawBlock(west, x, (y + 16));
    } 
  }
  
  private int getBlockDamage(BlockPos pos) {
    for (DestroyBlockProgress destBlockProgress : mc.renderGlobal.damagedBlocks.values()) {
      if (destBlockProgress.getPosition().getX() == pos.getX() && destBlockProgress.getPosition().getY() == pos.getY() && destBlockProgress.getPosition().getZ() == pos.getZ())
        return destBlockProgress.getPartialBlockDamage(); 
    } 
    return 0;
  }
  
  private BlockPos traceToBlock(float partialTicks, float yaw) {
    Vec3d pos = EntityUtil.interpolateEntity((Entity)mc.player, partialTicks);
    Vec3d dir = MathUtil.direction(yaw);
    return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
  }
  
  private BlockPos traceToBlock(float partialTicks, float yaw, Entity player) {
    Vec3d pos = EntityUtil.interpolateEntity(player, partialTicks);
    Vec3d dir = MathUtil.direction(yaw);
    return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
  }
  
  private Block getBlock(BlockPos pos) {
    Block block = mc.world.getBlockState(pos).getBlock();
    if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN)
      return block; 
    return Blocks.AIR;
  }
  
  private void drawBlock(Block block, float x, float y) {
    ItemStack stack = new ItemStack(block);
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.translate(x, y, 0.0F);
    (mc.getRenderItem()).zLevel = 501.0F;
    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
    (mc.getRenderItem()).zLevel = 0.0F;
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
  }
  
  public void renderInventory() {
    boxrender(((Integer)this.invX.getValue()).intValue() + ((Integer)this.fineinvX.getValue()).intValue(), ((Integer)this.invY.getValue()).intValue() + ((Integer)this.fineinvY.getValue()).intValue());
    itemrender(mc.player.inventory.mainInventory, ((Integer)this.invX.getValue()).intValue() + ((Integer)this.fineinvX.getValue()).intValue(), ((Integer)this.invY.getValue()).intValue() + ((Integer)this.fineinvY.getValue()).intValue());
  }
  
  private static void preboxrender() {
    GL11.glPushMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.disableAlpha();
    GlStateManager.clear(256);
    GlStateManager.enableBlend();
    GlStateManager.color(255.0F, 255.0F, 255.0F, 255.0F);
  }
  
  private static void postboxrender() {
    GlStateManager.disableBlend();
    GlStateManager.disableDepth();
    GlStateManager.disableLighting();
    GlStateManager.enableDepth();
    GlStateManager.enableAlpha();
    GlStateManager.popMatrix();
    GL11.glPopMatrix();
  }
  
  private static void preitemrender() {
    GL11.glPushMatrix();
    GL11.glDepthMask(true);
    GlStateManager.clear(256);
    GlStateManager.disableDepth();
    GlStateManager.enableDepth();
    RenderHelper.enableStandardItemLighting();
    GlStateManager.scale(1.0F, 1.0F, 0.01F);
  }
  
  private static void postitemrender() {
    GlStateManager.scale(1.0F, 1.0F, 1.0F);
    RenderHelper.disableStandardItemLighting();
    GlStateManager.enableAlpha();
    GlStateManager.disableBlend();
    GlStateManager.disableLighting();
    GlStateManager.scale(0.5D, 0.5D, 0.5D);
    GlStateManager.disableDepth();
    GlStateManager.enableDepth();
    GlStateManager.scale(2.0F, 2.0F, 2.0F);
    GL11.glPopMatrix();
  }
  
  private void boxrender(int x, int y) {
    preboxrender();
    mc.renderEngine.bindTexture(box);
    RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
    RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + ((Integer)this.invH.getValue()).intValue(), 500);
    RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
    postboxrender();
  }
  
  private void itemrender(NonNullList<ItemStack> items, int x, int y) {
    int i;
    for (i = 0; i < items.size() - 9; i++) {
      int iX = x + i % 9 * 18 + 8;
      int iY = y + i / 9 * 18 + 18;
      ItemStack itemStack = (ItemStack)items.get(i + 9);
      preitemrender();
      (mc.getRenderItem()).zLevel = 501.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, iX, iY, null);
      (mc.getRenderItem()).zLevel = 0.0F;
      postitemrender();
    } 
    if (((Boolean)this.renderXCarry.getValue()).booleanValue())
      for (i = 1; i < 5; i++) {
        int iX = x + (i + 4) % 9 * 18 + 8;
        ItemStack itemStack = ((Slot)mc.player.inventoryContainer.inventorySlots.get(i)).getStack();
        if (itemStack != null && !itemStack.isEmpty) {
          preitemrender();
          (mc.getRenderItem()).zLevel = 501.0F;
          RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, y + 1);
          RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, iX, y + 1, null);
          (mc.getRenderItem()).zLevel = 0.0F;
          postitemrender();
        } 
      }  
  }
  
  public static void drawCompleteImage(int posX, int posY, int width, int height) {
    GL11.glPushMatrix();
    GL11.glTranslatef(posX, posY, 0.0F);
    GL11.glBegin(7);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(0.0F, 0.0F, 0.0F);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(0.0F, height, 0.0F);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(width, height, 0.0F);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(width, 0.0F, 0.0F);
    GL11.glEnd();
    GL11.glPopMatrix();
  }
}
