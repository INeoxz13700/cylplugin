package fr.karmaowner.drogue;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.Data;
import fr.karmaowner.utils.FileUtils;
import fr.karmaowner.utils.RandomUtils;
import fr.karmaowner.utils.RegionUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Drogue implements Data {
  private FileUtils file;
  
  public static Drogue INSTANCE = new Drogue();
  
  public static ArrayList<ProtectedRegion> REGIONS = new ArrayList<>();
  
  public static Timestamp timer = new Timestamp(System.currentTimeMillis());
  
  public static int DURATION = 259200000;
  
  public static ProtectedRegion choosenRg;
  
  public static TaskCreator task;
  
  private Drogue() {
    if (INSTANCE == null) {
      this.file = new FileUtils("Drogue", "");
      if (!this.file.directoryExist())
        this.file.createFile(); 
      this.file.loadFileConfiguration();
    } 
  }
  
  public FileUtils getFile() {
    return this.file;
  }
  
  public boolean isRegionExist(String rgName) {
    for (ProtectedRegion r : REGIONS) {
      if (r.getId().equals(rgName))
        return true; 
    } 
    return false;
  }
  
  public ProtectedRegion getRegion(String rgName) {
    for (ProtectedRegion r : REGIONS) {
      if (r.getId().equals(rgName))
        return r; 
    } 
    return null;
  }
  
  public void addRegion(Player p, String rgName) {
    if (!isRegionExist(rgName)) {
      ProtectedRegion rg = Main.WG.getRegionManager(Main.INSTANCE.getServer().getWorld("cyl")).getRegion(rgName);
      if (rg != null) {
        REGIONS.add(rg);
        p.sendMessage(ChatColor.GREEN + "Région définie !");
      } else {
        p.sendMessage(ChatColor.RED + "Cette région n'existe pas !");
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Cette région existe déjà !");
    } 
  }
  
  public void deleteRegion(Player p, String rgName) {
    if (isRegionExist(rgName)) {
      ProtectedRegion r = getRegion(rgName);
      REGIONS.remove(r);
      p.sendMessage(ChatColor.GREEN + "Région supprimée !");
    } else {
      p.sendMessage(ChatColor.RED + "Cette région n'existe déjà !");
    } 
  }
  
  public void loadData() {
    String section = "Drogue";
    FileConfiguration f = this.file.getFileConfiguration();
    if (f.get(section) != null) {
      for (String rg : f.getStringList(section + ".list")) {
        if (RegionUtils.getRegionByName(rg, "cyl") != null)
          REGIONS.add(RegionUtils.getRegionByName(rg, "cyl")); 
      } 
      long time = Long.parseLong(f.getString(section + ".timer"));
      timer = new Timestamp(time);
      String choosenRg2 = f.getString(section + ".choosenRg");
      if(choosenRg2 != null)
      {
        if (RegionUtils.getRegionByName(choosenRg2, "cyl") != null) {
          choosenRg = RegionUtils.getRegionByName(f.getString(section + ".choosenRg"), "cyl");
        } else {
          timer = new Timestamp(0L);
        }
      }
      f.set(section, null);
    } 
    chooseRegion();
  }
  
  public void saveData() {
    Main.Log("Drogue Data saving...");
    String section = "Drogue";
    if (task != null)
      task.cancelTask();
    FileConfiguration f = this.file.getFileConfiguration();
    f.set(section + ".timer", timer.getTime());
    ArrayList<String> regions = new ArrayList<>();
    for (ProtectedRegion rg : REGIONS)
      regions.add(rg.getId()); 
    f.set(section + ".list", regions);
    if(choosenRg != null) f.set(section + ".choosenRg", choosenRg.getId());
    this.file.saveConfig();
    Main.Log("Drogue Data saved");
  }
  
  private void chooseRegion() {
    task = new TaskCreator(new CustomRunnable() {
          public void customRun() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - Drogue.timer.getTime() >= Drogue.DURATION) {
              RandomUtils<ProtectedRegion> random = new RandomUtils();
              for (ProtectedRegion r : Drogue.REGIONS) {
                if (Drogue.choosenRg == null || !r.getId().equals(Drogue.choosenRg.getId()))
                  random.addObj(r); 
              } 
              Drogue.choosenRg = (ProtectedRegion)random.getObj();
              Drogue.timer = new Timestamp(System.currentTimeMillis());
            } 
          }
        },  false, 0L, 20L);
  }
}
