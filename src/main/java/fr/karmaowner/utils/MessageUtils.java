package fr.karmaowner.utils;

import fr.karmaowner.common.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MessageUtils {
  public static String getMessageFromConfig(String name) {
    if (Main.INSTANCE.getConfig().isSet("messages." + name))
      return Main.INSTANCE.getConfig().getString("messages." + name); 
    return "null";
  }
  
  public static void sendMessage(CommandSender p, String msg) {
    p.sendMessage(Main.prefix + " " + msg);
  }
  
  public static void sendMessageFromConfig(CommandSender p, String path) {
    p.sendMessage(Main.prefix + " " + getMessageFromConfig(path).replaceAll("&", "§"));
  }
  
  public static void broadcastFromConfig(String path) {
    Bukkit.broadcastMessage(Main.prefix + " " + getMessageFromConfig(path).replaceAll("&", "§"));
  }
  
  public static void broadcast(String msg) {
    Bukkit.broadcastMessage(Main.prefix + " " + msg);
  }
  
  public static void sendLogFromConfig(String path) {
    Main.Log(getMessageFromConfig(path));
  }
}
