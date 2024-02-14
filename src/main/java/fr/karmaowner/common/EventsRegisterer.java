package fr.karmaowner.common;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class EventsRegisterer implements Listener {
  public EventsRegisterer() {
    Main.INSTANCE.getServer().getPluginManager().registerEvents(this, (Plugin)Main.INSTANCE);
  }
}
