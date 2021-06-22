package me.alpha432.oyvey.features.modules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

public class MobOwner extends Module {
  private final Map<Entity, String> owners = new HashMap<>();
  
  private final Map<Entity, UUID> toLookUp = new ConcurrentHashMap<>();
  
  private final List<Entity> lookedUp = new ArrayList<>();
  
  public MobOwner() {
    super("MobOwner", "Shows you who owns mobs.", Module.Category.MISC, false, false, false);
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (PlayerUtil.timer.passedS(5.0D))
      for (Map.Entry<Entity, UUID> entry : this.toLookUp.entrySet()) {
        Entity entity = entry.getKey();
        UUID uuid = entry.getValue();
        if (uuid != null) {
          EntityPlayer owner = mc.world.getPlayerEntityByUUID(uuid);
          if (owner == null) {
            try {
              String name = PlayerUtil.getNameFromUUID(uuid);
              if (name != null) {
                this.owners.put(entity, name);
                this.lookedUp.add(entity);
              } 
            } catch (Exception e) {
              this.lookedUp.add(entity);
              this.toLookUp.remove(entry);
            } 
            PlayerUtil.timer.reset();
            break;
          } 
          this.owners.put(entity, owner.getName());
          this.lookedUp.add(entity);
          continue;
        } 
        this.lookedUp.add(entity);
        this.toLookUp.remove(entry);
      }  
    for (Entity entity2 : mc.world.getLoadedEntityList()) {
      if (!entity2.getAlwaysRenderNameTag()) {
        if (entity2 instanceof EntityTameable) {
          EntityTameable tameableEntity = (EntityTameable)entity2;
          if (!tameableEntity.isTamed() || tameableEntity.getOwnerId() == null)
            continue; 
          if (this.owners.get(tameableEntity) != null) {
            tameableEntity.setAlwaysRenderNameTag(true);
            tameableEntity.setCustomNameTag(this.owners.get(tameableEntity));
            continue;
          } 
          if (this.lookedUp.contains(entity2))
            continue; 
          this.toLookUp.put(tameableEntity, tameableEntity.getOwnerId());
          continue;
        } 
        if (!(entity2 instanceof AbstractHorse))
          continue; 
        AbstractHorse tameableEntity2 = (AbstractHorse)entity2;
        if (!tameableEntity2.isTame() || tameableEntity2.getOwnerUniqueId() == null)
          continue; 
        if (this.owners.get(tameableEntity2) != null) {
          tameableEntity2.setAlwaysRenderNameTag(true);
          tameableEntity2.setCustomNameTag(this.owners.get(tameableEntity2));
          continue;
        } 
        if (this.lookedUp.contains(entity2))
          continue; 
        this.toLookUp.put(tameableEntity2, tameableEntity2.getOwnerUniqueId());
      } 
    } 
  }
  
  public void onDisable() {
    for (Entity entity : mc.world.loadedEntityList) {
      if (!(entity instanceof EntityTameable) && 
        !(entity instanceof AbstractHorse))
        continue; 
      try {
        entity.setAlwaysRenderNameTag(false);
      } catch (Exception exception) {}
    } 
  }
}
