package fr.karmaowner.commands;

import fr.karmaowner.drogue.Drogue;
import fr.karmaowner.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDrogue implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("drogue")) {
      Player p = (Player)sender;
      if (!sender.hasPermission("cylrp.drogue")) {
        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
        return false;
      } 
      if (args.length == 0) {
        displayHelp(sender, 1);
        return false;
      } 
      if (args[0].equalsIgnoreCase("add")) {
        if (args[1].equalsIgnoreCase("region")) {
          String rgName = args[2];
          Drogue.INSTANCE.addRegion(p, rgName);
        } 
      } else if (args[0].equalsIgnoreCase("delete")) {
        if (args[1].equalsIgnoreCase("region")) {
          String rgName = args[2];
          Drogue.INSTANCE.deleteRegion(p, rgName);
        } 
      } else if (args[0].equalsIgnoreCase("help")) {
        int page = Integer.parseInt(args[1]);
        displayHelp(sender, page);
      } 
    } 
    return false;
  }
  
  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Restoreg commands 1/3 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/drogue add region <rg name> §7- §aAjoute une region");
      sender.sendMessage("● §c/drogue delete region <rg name> §7- §aRetire une region");
      sender.sendMessage("● §c/drogue help <page> §7- §aAffiche la liste des commandes");
    } 
  }
}
