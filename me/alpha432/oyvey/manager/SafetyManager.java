package me.alpha432.oyvey.manager;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.client.Managers;
import me.alpha432.oyvey.features.modules.combat.AutoCrystal;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.DamageUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class SafetyManager extends Feature implements Runnable {
  private final Timer syncTimer = new Timer();
  
  private final AtomicBoolean SAFE = new AtomicBoolean(false);
  
  private ScheduledExecutorService service;
  
  public void run() {
    if (AutoCrystal.getInstance().isOff() || (AutoCrystal.getInstance()).threadMode.getValue() == AutoCrystal.ThreadMode.NONE)
      doSafetyCheck(); 
  }
  
  public void doSafetyCheck() {
    if (!fullNullCheck()) {
      boolean safe = true;
      EntityPlayer closest = ((Boolean)(Managers.getInstance()).safety.getValue()).booleanValue() ? EntityUtil.getClosestEnemy(18.0D) : null, entityPlayer = closest;
      if (((Boolean)(Managers.getInstance()).safety.getValue()).booleanValue() && closest == null) {
        this.SAFE.set(true);
        return;
      } 
      ArrayList<Entity> crystals = new ArrayList<>(mc.world.loadedEntityList);
      for (Entity crystal : crystals) {
        if (!(crystal instanceof net.minecraft.entity.item.EntityEnderCrystal) || DamageUtil.calculateDamage(crystal, (Entity)mc.player) <= 4.0D || (closest != null && closest.getDistanceSq(crystal) >= 40.0D))
          continue; 
        safe = false;
      } 
      if (safe)
        for (BlockPos pos : BlockUtil.possiblePlacePositionsCa(4.0F, false, ((Boolean)(Managers.getInstance()).oneDot15.getValue()).booleanValue())) {
          if (DamageUtil.calculateDamage(pos, (Entity)mc.player) <= 4.0D || (closest != null && closest.getDistanceSq(pos) >= 40.0D))
            continue; 
          safe = false;
        }  
      this.SAFE.set(safe);
    } 
  }
  
  public void onUpdate() {
    run();
  }
  
  public String getSafetyString() {
    if (this.SAFE.get())
      return "§aSecure"; 
    return "§cUnsafe";
  }
  
  public boolean isSafe() {
    return this.SAFE.get();
  }
  
  public ScheduledExecutorService getService() {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(this, 0L, ((Integer)(Managers.getInstance()).safetyCheck.getValue()).intValue(), TimeUnit.MILLISECONDS);
    return service;
  }
}
