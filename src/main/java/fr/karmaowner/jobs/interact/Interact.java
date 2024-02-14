package fr.karmaowner.jobs.interact;

import fr.karmaowner.data.GangData;
import fr.karmaowner.jobs.Jobs;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Interact {
  private Jobs.TypeInteract type;
  
  private Action action;
  
  private ArrayList<ItemStack> blocks;
  
  private ArrayList<ItemStack> items;
  
  private ArrayList<String> regions;
  
  private EntityType entity;
  
  private String entityName;
  
  private Jobs.Job job;
  
  private ArrayList<String> jobRank;
  
  private int Priority;
  
  private boolean isGang;
  
  private boolean isCompany;
  
  private String gangRank;
  
  private String companyRank;
  
  private Boolean outService = Boolean.FALSE;
  
  private String clothes;
  
  public static final byte ALLBLOCKS = 107;
  
  public Interact() {
    this.blocks = new ArrayList<>();
    this.items = new ArrayList<>();
    this.regions = new ArrayList<>();
    this.jobRank = new ArrayList<>();
  }
  
  public void setTypeInteract(Jobs.TypeInteract type) {
    this.type = type;
  }
  
  public void setAction(Action a) {
    this.action = a;
  }
  
  public Action getAction() {
    return this.action;
  }
  
  public ArrayList<ItemStack> getBlocks() {
    return this.blocks;
  }
  
  public void setEntityType(EntityType type) {
    this.entity = type;
  }
  
  public void setEntityName(String name) {
    this.entityName = name;
  }
  
  public boolean isEntityType(String name) {
    return (name != null && this.entityName != null && name.contains(this.entityName));
  }
  
  public boolean isEntityType(EntityType type) {
    return (this.entity == type);
  }
  
  public void setJob(Jobs.Job j) {
    this.job = j;
  }
  
  public boolean isJob(Jobs.Job j) {
    if (isEntityType(EntityType.PLAYER)) {
      if (this.job == null)
        return true; 
      if (this.job == j)
        return true; 
    } 
    return false;
  }
  
  public void setBlockToInteract(ItemStack item) {
    this.blocks.add(item);
  }
  
  public void setItem(ItemStack item) {
    this.items.add(item);
  }
  
  public void setOutService(Boolean b) {
    this.outService = b;
  }
  
  public Boolean IsOutService() {
    return this.outService;
  }
  
  public void setContainsClothesPlayerInteracting(String clothes) {
    this.clothes = clothes;
  }
  
  public String PlayerInteractingContainsClothes() {
    return this.clothes;
  }
  
  public ArrayList<ItemStack> getItems() {
    return this.items;
  }
  
  public boolean isInteractableBlock(Block b) {
    if (this.blocks.isEmpty())
      return true; 
    for (ItemStack item : this.blocks) {
      if (b != null &&
        b.getTypeId() == item.getTypeId() && (item.getData().getData() == 107 || b.getData() == item.getData().getData()))
        return true; 
    } 
    return false;
  }
  
  public void addRegion(String rgname) {
    this.regions.add(rgname);
  }
  
  public Jobs.TypeInteract getTypeInteract() {
    return this.type;
  }
  
  public void setPriority(int i) {
    this.Priority = i;
  }
  
  public int getPriority() {
    return this.Priority;
  }
  
  public ArrayList<String> getRegions() {
    return this.regions;
  }
  
  public boolean isGang() {
    return this.isGang;
  }
  
  public void setGang(boolean isGang) {
    this.isGang = isGang;
  }
  
  public boolean isCompany() {
    return this.isCompany;
  }
  
  public void setCompany(boolean isCompany) {
    this.isCompany = isCompany;
  }
  
  public String getGangRank() {
    return this.gangRank;
  }
  
  public void setGangRank(GangData.RANKS gangRank) {
    this.gangRank = gangRank.getRankName();
  }
  
  public String getCompanyRank() {
    return this.companyRank;
  }
  
  public void setCompanyRank(String companyRank) {
    this.companyRank = companyRank;
  }
  
  public void setJobRank(String rank) {
    this.jobRank.add(rank);
  }
  
  public ArrayList<String> getJobRank() {
    return this.jobRank;
  }
}
