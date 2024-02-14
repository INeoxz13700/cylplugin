package fr.karmaowner.jobs.missions;

import fr.karmaowner.data.Data;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.missions.type.AmendeTask;
import fr.karmaowner.jobs.missions.type.DefendTask;
import fr.karmaowner.jobs.missions.type.FouilleTask;
import fr.karmaowner.jobs.missions.type.GeneralType;
import fr.karmaowner.jobs.missions.type.KillTask;
import fr.karmaowner.jobs.missions.type.MenotteTask;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Mission implements Data {
  private String name;
  
  private int xp;
  
  private ItemStack item;
  
  private MissionType type;
  
  private String objective;
  
  private int price;
  
  private UUID id;
  
  public Mission(String name, ItemStack item, int xp, MissionType type, int price, UUID id, String objective) {
    this.name = name;
    this.item = item;
    this.objective = objective;
    this.id = id;
    ItemMeta meta = this.item.getItemMeta();
    ArrayList<String> lores = new ArrayList<>();
    GeneralType g = (GeneralType)type;
    lores.add(ChatColor.GOLD + "xp: " + xp);
    lores.add(ChatColor.RED + "Récompense: " + price + "€");
    if (type instanceof KillTask) {
      KillTask kt = (KillTask)type;
      lores.add(ChatColor.BLUE + "Type: Tuer");
      if (kt.getJob() != null) {
        StringBuilder jobs = new StringBuilder();
        for (Jobs.Job j : kt.getJob())
          jobs.append(j.getName()); 
        String t = StringUtils.join((Object[])new String[] { jobs.toString(), "," });
        lores.add(ChatColor.GREEN + "Cible: " + t);
      } else if (kt.getEntity() != null) {
        lores.add(ChatColor.GREEN + "Cible: " + kt.getEntity().getName());
      } 
      lores.add(ChatColor.YELLOW + "Nombre: " + kt.getCount());
    } else if (type instanceof DefendTask) {
      DefendTask dt = (DefendTask)type;
      lores.add(ChatColor.BLUE + "Type: Défendre");
      lores.add(ChatColor.GREEN + "Défendre: " + dt.getDesc());
    } else if (type instanceof FouilleTask) {
      FouilleTask dt = (FouilleTask)type;
      lores.add(ChatColor.BLUE + "Type: Fouiller");
      lores.add(ChatColor.GREEN + "Fouiller: " + dt.getDesc());
      StringBuilder jobs = new StringBuilder();
      for (Jobs.Job j : dt.getJob())
        jobs.append(j.getName()); 
      String t = StringUtils.join((Object[])new String[] { jobs.toString(), "," });
      lores.add(ChatColor.GREEN + "Cible: " + t);
      lores.add(ChatColor.YELLOW + "Nombre: " + dt.getCount());
    } else if (type instanceof MenotteTask) {
      MenotteTask dt = (MenotteTask)type;
      lores.add(ChatColor.BLUE + "Type: Menotter");
      lores.add(ChatColor.GREEN + "Menotter: " + dt.getDesc());
      StringBuilder jobs = new StringBuilder();
      for (Jobs.Job j : dt.getJob())
        jobs.append(j.getName()); 
      String t = StringUtils.join((Object[])new String[] { jobs.toString(), "," });
      lores.add(ChatColor.GREEN + "Cible: " + t);
      lores.add(ChatColor.YELLOW + "Nombre: " + dt.getCount());
    } else if (type instanceof AmendeTask) {
      AmendeTask dt = (AmendeTask)type;
      lores.add(ChatColor.BLUE + "Type: Amende");
      lores.add(ChatColor.GREEN + "Amende: " + dt.getDesc());
      StringBuilder jobs = new StringBuilder();
      for (Jobs.Job j : dt.getJob())
        jobs.append(j.getName()); 
      String t = StringUtils.join((Object[])new String[] { jobs.toString(), "," });
      lores.add(ChatColor.GREEN + "Cible: " + t);
      lores.add(ChatColor.YELLOW + "Nombre: " + dt.getCount());
    } 
    if (g.getDuration() > 0L)
      lores.add(ChatColor.DARK_RED + "Durée: " + (g.getDuration() / 1000L) + " secondes"); 
    lores.add("id: " + id.toString());
    if(meta != null)
    {
      meta.setLore(lores);
      this.item.setItemMeta(meta);
    }
    this.xp = xp;
    this.type = type;
    this.price = price;
  }
  
  public String getObjective() {
    return this.objective;
  }
  
  public int getPrice() {
    return this.price;
  }
  
  public UUID getUUID() {
    return this.id;
  }
  
  public void setUUID(UUID id) {
    this.id = id;
  }
  
  public MissionType getType() {
    return this.type;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getXp() {
    return this.xp;
  }
  
  public void setXp(int xp) {
    this.xp = xp;
  }
  
  public ItemStack getItem() {
    return this.item;
  }
  
  public void setItem(ItemStack item) {
    this.item = item;
  }
  
  public Mission copy() {
    return new Mission(this.name, this.item, this.xp, this.type, this.price, this.id, this.objective);
  }
  
  public void loadData() {}
  
  public void saveData() {}
}
