package fr.karmaowner.common;

import fr.karmaowner.data.Data;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class EventRegistererData extends EventsRegisterer implements Data {
  private FileConfiguration fileConfig = Main.INSTANCE.getConfig();
  
  public FileConfiguration getFileConfig() {
    return this.fileConfig;
  }
}
