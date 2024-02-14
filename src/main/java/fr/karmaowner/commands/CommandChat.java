package fr.karmaowner.commands;

import fr.karmaowner.chat.Chat;
import fr.karmaowner.chat.ChatGroup;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChat implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (label.equalsIgnoreCase("cht")) {
        if (args.length == 2) {
          String GroupName = args[0];
          if (args[1].equalsIgnoreCase("j")) {
            Chat.switchCanal(p.getName(), GroupName);
          } else if (args[1].equalsIgnoreCase("l")) {
            Chat.leftFromCanal(p.getName());
          } 
        } else if (args.length == 1) {
          if (args[0].equalsIgnoreCase("h")) {
            displayHelpalias(sender, 1);
          } else if (args[0].equalsIgnoreCase("l")) {
            Chat.usage("Commandes disponibles", p.getName());
          } else if (args[0].equalsIgnoreCase("on")) {
            if (p.hasPermission("staffchat")) {
              ChatGroup g = Chat.getDefaultGroup();
              if (!g.isEnable()) {
                g.setEnable(true);
                Bukkit.broadcastMessage("§a[CHAT GLOBAL] Activé");
              } else {
                p.sendMessage("§4[CHAT GLOBAL] Le chat est déjà activé");
              } 
            } else {
              p.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            } 
          } else if (args[0].equalsIgnoreCase("off")) {
            if (p.hasPermission("staffchat")) {
              ChatGroup g = Chat.getDefaultGroup();
              if (g.isEnable()) {
                g.setEnable(false);
                Bukkit.broadcastMessage("§a[CHAT GLOBAL] Désactivé");
              } else {
                p.sendMessage("§4[CHAT GLOBAL] Le chat est déjà désactivé");
              } 
            } else {
              p.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            } 
          } 
        } else {
          displayHelpalias(sender, 1);
        } 
      } else if (cmd.getName().equals("chat")) {
        if (args.length == 2) {
          String GroupName = args[0];
          if (args[1].equalsIgnoreCase("join")) {
            Chat.switchCanal(p.getName(), GroupName);
          } else if (args[1].equalsIgnoreCase("leave")) {
            Chat.leftFromCanal(p.getName());
          } 
        } else if (args.length == 1) {
          if (args[0].equalsIgnoreCase("help")) {
            displayHelp(sender, 1);
          } else if (args[0].equalsIgnoreCase("list")) {
            Chat.usage("Commandes disponibles", p.getName());
          } else if (args[0].equalsIgnoreCase("enable")) {
            if (p.hasPermission("staffchat")) {
              ChatGroup g = Chat.getDefaultGroup();
              if (!g.isEnable()) {
                g.setEnable(true);
                Bukkit.broadcastMessage("§a[CHAT GLOBAL] Activé");
              } else {
                p.sendMessage("§4[CHAT GLOBAL] Le chat est déjà activé");
              } 
            } else {
              p.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            } 
          } else if (args[0].equalsIgnoreCase("disable")) {
            if (p.hasPermission("staffchat")) {
              ChatGroup g = Chat.getDefaultGroup();
              if (g.isEnable()) {
                g.setEnable(false);
                Bukkit.broadcastMessage("§a[CHAT GLOBAL] Désactivé");
              } else {
                p.sendMessage("§4[CHAT GLOBAL] Le chat est déjà désactivé");
              } 
            } else {
              p.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            } 
          } 
        } else {
          displayHelp(sender, 1);
        } 
      } 
    } 
    return false;
  }
  
  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Chat commands 1/1 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/chat help <Page> §7- §aAffiche la liste des commandes");
      sender.sendMessage("● §c/chat <ChatName> join §7- §aPermet de rejoindre un chat");
      sender.sendMessage("● §c/chat <ChatName> leave §7- §aPermet de quitter le chat actuelle");
      sender.sendMessage("● §c/chat list §7- §aAffiche la liste des chats");
      sender.sendMessage("● §c/chat enable §7- §aActiver le chat");
      sender.sendMessage("● §c/chat disable §7- §aDésactiver le chat");
    } 
  }
  
  public void displayHelpalias(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Chat commands 1/1 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/cht h <Page> §7- §aAffiche la liste des commandes");
      sender.sendMessage("● §c/cht <ChatName> j §7- §aPermet de rejoindre un chat");
      sender.sendMessage("● §c/cht <ChatName> l §7- §aPermet de quitter le chat actuelle");
      sender.sendMessage("● §c/cht l §7- §aAffiche la liste des chats");
      sender.sendMessage("● §c/cht on §7- §aActiver le chat");
      sender.sendMessage("● §c/cht off §7- §aDésactiver le chat");
    } 
  }
}
