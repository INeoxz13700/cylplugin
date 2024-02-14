package fr.karmaowner.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlockerEvents implements Listener {
  public enum BLOCKEDCOMMAND {
    CALC("//calc", 1),
    ASFIND("/as find", 2),
    ASFIND2("/areashop find", 2),
    PL("/pl", 1),
    PLUGINS("/plugins", 1),
    interrogative("/?", 1),
    ASTP("/as tp", 2),
    ASTP2("/areashop tp", 2),
    EVAL("//eval", 1),
    PING("/ping", 1),
    MSG("/msg", 1),
    MSG2("/m", 1),
    RGINFO("/Rg info", 2),
    ASINFO("/as info", 2),
    WHISPER("/whisper", 1),
    W("/w", 1),
    TELL("/tell", 1),
    T("/t", 1),
    EMSG("/emsg", 1),
    ETELL("/etell", 1),
    EWHISPER("/ewhisper", 1),
    HELPOP("/helpop", 1, "§cCommande remplacer par §4/helpme <message>"),
    STAFF("/staff", 1),
    PM("/pm", 1),
    SOLVE("//solve", 1),
    WECALC("/worldedit:calc", 1),
    WESOLVE("/worldedit:solve", 1),
    WEEVAL("/worldedit:eval", 1),
    WECALC2("/worldedit:/calc", 1),
    WEEVAL2("/worldedit:/eval", 1),
    WESOLVE2("/worldedit:/solve", 1),
    HALTACTIVITY("/halt-activity", 1, true),
    HALTACTIVITY2("/haltactivity", 1, true),
    VER("/ver", 1),
    BUKKITHELP("/bukkit:help", 1),
    MV("/mv", 1, true);
    
    private String cmd;
    
    private int args;
    
    private String msg;

    private boolean blockForEveryone;
    
    BLOCKEDCOMMAND(String cmd, int args) {
      this(cmd,args,false);
    }

    BLOCKEDCOMMAND(String cmd, int args, boolean blockForEveryone) {
      this.cmd = cmd;
      this.args = args;
      this.blockForEveryone = blockForEveryone;
    }
    
    BLOCKEDCOMMAND(String cmd, int args, String msg) {
      this.cmd = cmd;
      this.args = args;
      this.msg = msg;
    }
    
    public static boolean equalsIgnoreCase(String cmd) {
      String[] args = cmd.split(" ");
      if (args.length > 0)
        for (BLOCKEDCOMMAND bc : values()) {
          if (args.length >= bc.getArgsCount()) {
            boolean equal = true;
            for (int i = 0; i < (bc.cmd.split(" ")).length; i++) {
              if (!bc.cmd.split(" ")[i].equalsIgnoreCase(args[i])) {
                equal = false;
                break;
              } 
            } 
            if (equal)
              return true; 
          } 
        }  
      return false;
    }
    
    public static BLOCKEDCOMMAND getCommandBlocked(String cmd) {
      String[] args = cmd.split(" ");
      if (args.length > 0)
        for (BLOCKEDCOMMAND bc : values()) {
          if (args.length >= bc.getArgsCount()) {
            boolean equal = true;
            for (int i = 0; i < (bc.cmd.split(" ")).length; i++) {
              if (!bc.cmd.split(" ")[i].equalsIgnoreCase(args[i])) {
                equal = false;
                break;
              } 
            } 
            if (equal)
              return bc; 
          } 
        }  
      return null;
    }
    
    public int getArgsCount() {
      return this.args;
    }
    
    public String getMsg() {
      return this.msg;
    }
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommand(PlayerCommandPreprocessEvent e) {
    if (BLOCKEDCOMMAND.equalsIgnoreCase(e.getMessage())) {
      BLOCKEDCOMMAND bc = BLOCKEDCOMMAND.getCommandBlocked(e.getMessage());
      if(bc.blockForEveryone || !e.getPlayer().hasPermission("cylrp.staff"))
      {
          if (bc.getMsg() != null) {
            e.getPlayer().sendMessage(bc.getMsg());
          } else {
            e.getPlayer().sendMessage(ChatColor.RED + "L'accès à cette commande est bloqué !");
          }
          e.setCancelled(true);
      }
    }


    if (e.getMessage().equalsIgnoreCase("/heal") && e.getPlayer().hasPermission("cylrp.staff"))
      Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "api heal " + e.getPlayer().getName());
  }
}
