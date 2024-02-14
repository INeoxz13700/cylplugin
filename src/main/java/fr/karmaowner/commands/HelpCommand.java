package fr.karmaowner.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HelpCommand {
  private HashMap<String, String> helpCommands = new HashMap<>();
  
  private HashMap<String, String> helpCommandsShortCut = new HashMap<>();
  
  private int nbCommandsPerPage = 6;
  
  private String command;
  
  private String shortcut;
  
  public HelpCommand(String command) {
    this.command = command;
  }
  
  public HelpCommand(String command, String shortcut) {
    this(command);
    this.shortcut = shortcut;
  }
  
  public void addCommand(String name, String description) {
    this.helpCommands.put(name, description);
  }
  
  public void addCommandShortCut(String name, String description) {
    this.helpCommandsShortCut.put(name, description);
  }
  
  public String getCommands() {
    StringJoiner builder = new StringJoiner("\n");
    int nbPage = (int)Math.floor((this.helpCommands.size() / this.nbCommandsPerPage));
    int i = 0;
    builder.add("§b【" + this.command + " commands 1/" + nbPage + "】");
    for (Map.Entry<String, String> c : this.helpCommands.entrySet()) {
      if (i > this.nbCommandsPerPage)
        break; 
      builder.add("§f● §c" + (String)c.getKey() + " §7- §a" + (String)c.getValue());
      i++;
    } 
    builder.add("§6Usage: /" + this.command + " " + ((this.shortcut != null) ? ("ou /" + this.shortcut) : ""));
    return builder.toString();
  }
  
  public String getCommandsShortCut() {
    if (this.shortcut != null) {
      StringJoiner builder = new StringJoiner("\n");
      int nbPage = (int)Math.floor((this.helpCommandsShortCut.size() / this.nbCommandsPerPage));
      int i = 0;
      builder.add("§b【" + this.shortcut + " commands 1/" + nbPage + "】");
      for (Map.Entry<String, String> c : this.helpCommandsShortCut.entrySet()) {
        if (i > this.nbCommandsPerPage)
          break; 
        builder.add("§f● §c/" + this.shortcut + " " + (String)c.getKey() + " §7- §a" + (String)c.getValue());
        i++;
      } 
      builder.add("§6Usage: /" + this.command + " " + ((this.shortcut != null) ? ("ou /" + this.shortcut) : ""));
      return builder.toString();
    } 
    return null;
  }
  
  public String getCommands(int page) {
    StringJoiner builder = new StringJoiner("\n");
    int nbPage = (int)Math.ceil(this.helpCommands.size() / this.nbCommandsPerPage);
    if (page >= 1 && page <= nbPage) {
      int x = this.nbCommandsPerPage * (page-1);
      int i = 0;
      builder.add("§b【" + this.command + " commands " + page + "/" + nbPage + "】");
      for (Map.Entry<String, String> c : this.helpCommands.entrySet()) {
        if (i > helpCommands.entrySet().size())
          break; 
        if (i >= x && i < x+nbCommandsPerPage)
          builder.add("● §c/" + this.command + " " + (String)c.getKey() + " §7- §a" + (String)c.getValue()); 
        i++;
      } 
      builder.add("§6Usage: /" + this.command + " " + ((this.shortcut != null) ? ("ou /" + this.shortcut) : ""));
    } else {
      builder.add("§cCette page n'existe pas");
    } 
    return builder.toString();
  }
  
  public String getCommandsShortcut(int page) {
    if (this.shortcut != null) {
      StringJoiner builder = new StringJoiner("\n");
      int nbPage = (int)Math.ceil(this.helpCommandsShortCut.size() / this.nbCommandsPerPage);
      if (page >= 1 && page <= nbPage) {
        int x = this.nbCommandsPerPage * (page-1);
        int i = 0;
        builder.add("§b【" + this.shortcut + " commands " + page + "/" + nbPage + "】");
        for (Map.Entry<String, String> c : this.helpCommandsShortCut.entrySet()) {
          if (i > helpCommands.entrySet().size())
            break; 
          if (i >= x && i < x+nbCommandsPerPage)
            builder.add("● §c/" + this.shortcut + " " + (String)c.getKey() + " §7- §a" + (String)c.getValue()); 
          i++;
        } 
        builder.add("§6Usage: /" + this.command + " " + ((this.shortcut != null) ? ("ou /" + this.shortcut) : ""));
      } else {
        builder.add("§cCette page n'existe pas");
      } 
      return builder.toString();
    } 
    return null;
  }
}
