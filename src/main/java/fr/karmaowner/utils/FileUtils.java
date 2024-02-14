package fr.karmaowner.utils;

import fr.karmaowner.common.Main;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileUtils {
  protected File folderdirectory;
  
  protected File filedirectory;
  
  protected FileConfiguration fileConfiguration;
  
  public FileUtils(String playername) {
    this.folderdirectory = new File(Main.INSTANCE.getDataFolder() + File.separator + "UserData");
    this.filedirectory = new File(Main.INSTANCE.getDataFolder() + File.separator + "UserData" + File.separator + playername + ".yml");
  }
  
  public FileConfiguration getFileConfiguration() {
    return this.fileConfiguration;
  }
  
  public FileUtils(String fileName, String directoryName) {
    this.folderdirectory = new File(Main.INSTANCE.getDataFolder() + File.separator + directoryName);
    this.filedirectory = new File(Main.INSTANCE.getDataFolder() + File.separator + directoryName + File.separator + fileName + ".yml");
  }
  
  public void createFile() {
    if (directoryExist())
      return; 
    try {
      try {
        this.folderdirectory.mkdir();
        this.filedirectory.createNewFile();
      } catch (IOException e) {
        Main.Log("Erreur lors de la cr�ation du fichier");
        e.printStackTrace();
      } 
    } catch (SecurityException e) {
      Main.Log("Erreur lors de la cr�ation de " + this.filedirectory.getName() + ".yml d�sactivation du plugin");
      Main.Log(e.toString());
      Main.INSTANCE.getServer().getPluginManager().disablePlugin((Plugin)Main.INSTANCE);
    } 
  }
  
  public void deleteFile() {
    if (directoryExist()) {
      Main.Log("Fiché" + this.filedirectory.getName() + "supprimé");
      this.filedirectory.delete();
    } 
  }
  
  public void loadFileConfiguration() {
    this.fileConfiguration = (FileConfiguration)YamlConfiguration.loadConfiguration(this.filedirectory);
  }
  
  public boolean directoryExist() {
    if (this.filedirectory.exists())
      return true; 
    return false;
  }
  
  public File getDirectory() {
    return this.folderdirectory;
  }
  
  public File getFile() {
    return this.filedirectory;
  }
  
  public void saveConfig() {
    try {
      this.fileConfiguration.save(this.filedirectory);
    } catch (IOException e) {
      Main.Log("Erreur lors de la sauvegarde du fichier " + this.filedirectory.getName());
      e.printStackTrace();
    } 
  }
}
