package fr.karmaowner.commands;

import fr.karmaowner.election.Vote;
import fr.karmaowner.utils.MessageUtils;
import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandElection implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("votemaire"))
      if (args[0].equalsIgnoreCase("add")) {
        if (sender.hasPermission("cylrp.maire.election")) {
          if (args.length >= 3) {
            ArrayList<String> candidats = new ArrayList<>();
            for (int i = 1; i < args.length; i++)
              candidats.add(args[i]); 
            Vote.startElection(candidats);
          } else {
            MessageUtils.sendMessage(sender, "Il doit y avoir au moins 2 candidats pour les élections.");
          } 
        } else {
          MessageUtils.sendMessage(sender, "Vous n'avez pas la permission d'exécuter cette commande.");
        } 
      } else if (args[0].equalsIgnoreCase("cancel")) {
        if (sender.hasPermission("cylrp.maire.election")) {
          Vote.cancelElection(sender.getName());
        } else {
          MessageUtils.sendMessage(sender, "Vous n'avez pas la permission d'exécuter cette commande.");
        } 
      } else if (args[0].equalsIgnoreCase("stop")) {
        if (sender.hasPermission("cylrp.maire.election")) {
          Vote.stopElection();
        } else {
          MessageUtils.sendMessage(sender, "Vous n'avez pas la permission d'exécuter cette commande.");
        } 
      } else if (args[0].equalsIgnoreCase("open")) {
        if (Vote.isStarted) {
          if (sender instanceof Player) {
            Player p = (Player)sender;
            p.openInventory(Vote.inv);
          } 
        } else {
          MessageUtils.sendMessage(sender, "Aucune élection en cours.");
        } 
      } else if (args[0].equalsIgnoreCase("list")) {
        if (sender.hasPermission("cylrp.maire.election")) {
          if (Vote.isStarted) {
            Vote.listCandidats(sender.getName());
          } else {
            MessageUtils.sendMessage(sender, "Aucune élection en cours.");
          } 
        } else {
          MessageUtils.sendMessage(sender, "Vous n'avez pas la permission d'exécuter cette commande.");
        } 
      }  
    return false;
  }
}
