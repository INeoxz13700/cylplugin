package fr.karmaowner.restore.regions;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.Data;
import fr.karmaowner.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Restore implements Data {
  private FileUtils file;
  
  public static Restore INSTANCE = new Restore();
  
  public static ArrayList<RegionState> REGIONS = new ArrayList<>();
  
  private TaskCreator taskSave;
  
  private Restore() {
    File file = new File("plugins/CYLRP-CORE/restores");
    if(!file.exists())
    {
      file.mkdir();
    }
  }
  
  public FileUtils getFile() {
    return this.file;
  }
  
  public boolean isRegionExist(String rgName) {
    for (RegionState rs : REGIONS) {
      if (rs.getRgName().equals(rgName))
        return true; 
    } 
    return false;
  }
  
  public RegionState getRegionState(String rgName) {
    for (RegionState rs : REGIONS) {
      if (rs.getRgName().equals(rgName))
        return rs; 
    } 
    return null;
  }
  
  public void addRegion(Player p, String rgName, int seconds) {
    if (!isRegionExist(rgName)) {
      ProtectedRegion rg = Main.WG.getRegionManager(Main.INSTANCE.getServer().getWorld("cyl")).getRegion(rgName);
      if (rg != null)
      {
        p.sendMessage("§aCréation du restorerg §b" + rgName + " §aen cours...");
        RegionState rgstate = new RegionState(p, rgName, seconds);
        REGIONS.add(rgstate);

        new TaskCreator(new CustomRunnable() {
          @Override
          public void customRun() {
            if(rgstate.isRegionInitialized())
            {
              saveRegionData(p, rgstate);
              cancel();
            }
          }
        },false, 0L, 20L);
      } else {
        p.sendMessage(ChatColor.RED + "Cette région n'existe pas !");
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Cette région existe déjà !");
    } 
  }


  public void deleteRegion(Player p, String rgName) {
    if (isRegionExist(rgName)) {
      p.sendMessage(ChatColor.GREEN + "Suppression de la région en cours...");
      RegionState rs = getRegionState(rgName);
      rs.getTask().cancelTask();
      File file = new File("plugins/CYLRP-CORE/restores/" + rgName + ".yml");
      file.delete();
      REGIONS.remove(rs);
      p.sendMessage(ChatColor.GREEN + "Région supprimée !");
    } else {
      p.sendMessage(ChatColor.RED + "Cette région n'existe déjà !");
    } 
  }
  
  public void loadData() {

    File regionsDirectory = new File("plugins/CYLRP-CORE/restores");
    if(regionsDirectory.exists())
    {
      File[] regionFiles = regionsDirectory.listFiles();
      Main.Log("Chargement des regions...");
      World world = Main.INSTANCE.getServer().getWorld("cyl");

      new TaskCreator(new CustomRunnable() {
        public void customRun() {
          for(int i = 0; i < regionFiles.length; i++)
          {
            File regionFile = regionFiles[i];
            FileConfiguration f = YamlConfiguration.loadConfiguration(regionFile);
            for (String key : f.getKeys(false))
            {

                ProtectedRegion rg = Main.WG.getRegionManager(world).getRegion(key);
                int seconds = f.getInt(key + ".state.seconds");
                List<String> contents = new ArrayList<>();
                if (rg != null && f.getConfigurationSection(key + ".contents") != null) {
                  for (String key2 : f.getConfigurationSection(key + ".contents").getKeys(false)) {
                    int id = f.getInt(key + ".contents." + key2 + ".id");
                    byte data = Byte.parseByte((f.getString(key + ".contents." + key2 + ".data") == null) ? "0" : f.getString(key + ".contents." + key2 + ".data"));
                    int direction = (f.get(key + ".contents." + key2 + ".direction") != null) ? f.getInt( key + ".contents." + key2 + ".direction") : -1;
                    if (direction != -1) {
                      contents.add(id + ":" + data + ":" + direction);
                      continue;
                    }
                    contents.add(id + ":" + data);
                  }

                  Main.Log(key + " chargé. Secondes : " + seconds);
                  REGIONS.add(new RegionState(key, seconds, contents));
                }
            }
            float percent = ((i+1) / (float)regionFiles.length) * 100;
            Main.Log("Chargement : " + percent + "%");
          }
          Main.Log("Tous les restorergs ont bien été chargés.");
        }
      },  true, 0L);
    }


  }

  @Override
  public void saveData() { }

  public void saveRegionData(Player p, RegionState rs) {
    File restoreDirectory = new File("plugins/CYLRP-CORE/restores");
    File restoreFile = new File(restoreDirectory.getPath() + "/" + rs.getRgName() + ".yml");
    try {
      restoreFile.createNewFile();
    } catch (IOException e) {
      Bukkit.broadcastMessage(e.getMessage());
      throw new RuntimeException(e);
    }

    new TaskCreator(new CustomRunnable() {
      public void customRun() {
        FileConfiguration f = YamlConfiguration.loadConfiguration(restoreFile);

        f.set(rs.getRgName() + ".state.seconds", rs.getSeconds());
        int i = 0;
        for (String s : rs.getContents()) {
          int id = Integer.parseInt(s.split(":")[0]);
          int data = Byte.parseByte(s.split(":")[1]);
          int direction = -1;
          if ((s.split(":")).length == 3)
            direction = Integer.parseInt(s.split(":")[2]);
          f.set(rs.getRgName() + ".contents." + i + ".id", id);
          f.set(rs.getRgName() + ".contents." + i + ".data", data);
          if (direction != -1)
            f.set(rs.getRgName() + ".contents." + i + ".direction", direction);
          i++;
        }

        try {
          f.save(restoreFile);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        p.sendMessage("§aLa création du restorerg est terminé!");
      }
    },true,0);

  }
  
}
