package fr.karmaowner.commands;

import fr.karmaowner.colis.Coli;
import fr.karmaowner.common.Main;
import fr.karmaowner.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandColi implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("colis")) {
      if (args.length == 2)
        if (sender instanceof Player) {
          Player p = (Player)sender;
          if (args[0].equalsIgnoreCase("give")) {
            if (!p.hasPermission("cylrp.coli.give")) {
              MessageUtils.sendMessageFromConfig((CommandSender)p, "cylrp-not-permission");
              return false;
            } 
            if (args[1].isEmpty()) {
              MessageUtils.sendMessageFromConfig((CommandSender)p, "enter-username");
              return false;
            } 
            String name = args[1];
            if (Bukkit.getPlayerExact(name) == null) {
              MessageUtils.sendMessageFromConfig((CommandSender)p, "user-invalid");
              return false;
            } 
            Player dest = Main.INSTANCE.getServer().getPlayerExact(name);
            if (dest != null) {
              Coli.giveColi(dest);
              return true;
            } 
            MessageUtils.sendMessageFromConfig((CommandSender)p, "inexistant-user");
            return false;
          } 
          if (args[0].equalsIgnoreCase("help")) {
            int page = Integer.parseInt(args[1]);
            displayHelp(sender, page);
          } 
        } else if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
          if (args[0].equalsIgnoreCase("give")) {
            if (args[1].isEmpty()) {
              MessageUtils.sendMessageFromConfig(sender, "enter-username");
              return false;
            } 
            String name = args[1];
            if (Bukkit.getPlayerExact(name) == null) {
              MessageUtils.sendMessageFromConfig(sender, "user-invalid");
              return false;
            } 
            Player dest = Main.INSTANCE.getServer().getPlayerExact(name);
            if (dest != null) {
              Coli.giveColi(dest);
              return true;
            } 
            MessageUtils.sendMessageFromConfig(sender, "inexistant-user");
            return false;
          } 
          if (args[0].equalsIgnoreCase("help")) {
            int page = Integer.parseInt(args[1]);
            displayHelp(sender, page);
          } 
        } else {
          Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "colis " + args[0] + " " + args[1]);
        }  
      displayHelp(sender, 1);
      return false;
    } 
    return false;
  }
  
  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Coli commands 1/1 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/colis help <Page> §7- §aAffiche la liste des commandes");
      sender.sendMessage("● §c/colis give <Username> §7- §aDonne un coli au joueur indiqué");
    } 
  }
}
