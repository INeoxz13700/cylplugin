package fr.karmaowner.jobs.interact;

import fr.karmaowner.data.GangData;
import fr.karmaowner.jobs.Jobs;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class InteractBuilder {
  private Interact interact = new Interact();
  
  public static InteractBuilder build() {
    return new InteractBuilder();
  }
  
  public InteractBuilder interact(Jobs.TypeInteract type) {
    this.interact.setTypeInteract(type);
    return this;
  }
  
  public InteractBuilder action(Action a) {
    this.interact.setAction(a);
    return this;
  }
  
  public InteractBuilder job(Jobs.Job j) {
    this.interact.setJob(j);
    return this;
  }
  
  public InteractBuilder job(Jobs.Job j, String rank) {
    this.interact.setJob(j);
    this.interact.setJobRank(rank);
    return this;
  }
  
  public InteractBuilder gang(GangData.RANKS rank) {
    this.interact.setGang(true);
    this.interact.setGangRank(rank);
    return this;
  }
  
  public InteractBuilder company(String rank) {
    this.interact.setCompany(true);
    this.interact.setCompanyRank(rank);
    return this;
  }
  
  public InteractBuilder entity(EntityType e) {
    this.interact.setEntityType(e);
    return this;
  }
  
  public InteractBuilder entity(String name) {
    this.interact.setEntityName(name);
    return this;
  }
  
  public InteractBuilder OutService(Boolean b) {
    this.interact.setOutService(b);
    return this;
  }
  
  public InteractBuilder setClothes(String clothes) {
    this.interact.setContainsClothesPlayerInteracting(clothes);
    return this;
  }
  
  public InteractBuilder item(ItemStack item) {
    this.interact.setItem(item);
    return this;
  }
  
  public InteractBuilder region(String rgname) {
    this.interact.addRegion(rgname);
    return this;
  }
  
  public InteractBuilder block(ItemStack item) {
    this.interact.setBlockToInteract(item);
    return this;
  }
  
  public Interact create() {
    return this.interact;
  }
  
  public InteractBuilder priority(int i) {
    this.interact.setPriority(i);
    return this;
  }
}
