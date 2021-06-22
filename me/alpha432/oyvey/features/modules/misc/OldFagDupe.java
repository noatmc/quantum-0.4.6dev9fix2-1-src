package me.alpha432.oyvey.features.modules.misc;

import java.util.Comparator;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class OldFagDupe extends Module {
  Entity donkey;
  
  public OldFagDupe() {
    super("OldFagDupe", "", Module.Category.MISC, true, false, false);
  }
  
  public boolean setup() {
    return false;
  }
  
  public void onEnable() {
    if (findAirInHotbar() == -1) {
      disable();
      return;
    } 
    if (findChestInHotbar() == -1) {
      disable();
      return;
    } 
    this
      
      .donkey = mc.world.loadedEntityList.stream().filter(this::isValidEntity).min(Comparator.comparing(p_Entity -> Float.valueOf(mc.player.getDistance(p_Entity)))).orElse(null);
    if (this.donkey == null) {
      disable();
      return;
    } 
  }
  
  public void onUpdate() {
    if (findAirInHotbar() == -1) {
      disable();
      return;
    } 
    if (findChestInHotbar() == -1) {
      disable();
      return;
    } 
    this
      
      .donkey = mc.world.loadedEntityList.stream().filter(this::isValidEntity).min(Comparator.comparing(p_Entity -> Float.valueOf(mc.player.getDistance(p_Entity)))).orElse(null);
    if (this.donkey == null) {
      disable();
      return;
    } 
    putChestOn();
    Command.sendMessage("put chest on the donkey");
    toggle();
  }
  
  public void putChestOn() {
    mc.player.inventory.currentItem = findAirInHotbar();
    mc.player.inventory.currentItem = findChestInHotbar();
    mc.playerController.interactWithEntity((EntityPlayer)mc.player, this.donkey, EnumHand.MAIN_HAND);
  }
  
  private int findChestInHotbar() {
    int slot = -1;
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
        Block block = ((ItemBlock)stack.getItem()).getBlock();
        if (block instanceof net.minecraft.block.BlockChest) {
          slot = i;
          break;
        } 
      } 
    } 
    return slot;
  }
  
  private int findAirInHotbar() {
    int slot = -1;
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack.getItem() == Items.AIR)
        slot = i; 
    } 
    return slot;
  }
  
  private boolean isValidEntity(Entity entity) {
    if (entity instanceof AbstractChestHorse) {
      AbstractChestHorse donkey = (AbstractChestHorse)entity;
      return (!donkey.isChild() && donkey.isTame());
    } 
    return false;
  }
}
