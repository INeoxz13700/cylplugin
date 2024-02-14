package fr.karmaowner.commands;

import fr.karmaowner.casino.Bet;
import fr.karmaowner.casino.Casino;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCasino implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      PlayerData pData = PlayerData.getPlayerData(p.getName());
      if (pData.getActuallyRegion() == null) {
        MessageUtils.sendMessageFromConfig((CommandSender)p, "location-not-casino");
        return false;
      } 
      if (!pData.getActuallyRegion().getId().equalsIgnoreCase(Casino.RGNAME)) {
        MessageUtils.sendMessageFromConfig((CommandSender)p, "location-not-casino");
        return false;
      } 
      if (label.equalsIgnoreCase("cas")) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("tictactoc")) {
          Casino.joinParty("Tictactoc", p);
          return true;
        } 
        if (args.length == 1 && args[0].equalsIgnoreCase("sa")) {
          Casino.StopWaiting(p);
          return true;
        } 
        if (args.length == 2 && args[0].equalsIgnoreCase("m") && !args[1].isEmpty()) {
          Bet.addBet(p, Double.parseDouble(args[1]));
          return true;
        } 
        if (args.length == 1 && args[0].equals("jackpot")) {
          Casino.joinParty("Jackpot", p);
          return true;
        } 
        if (args.length == 1 && args[0].equals("roulette")) {
          Casino.joinParty("Roulette", p);
          return true;
        } 
        if (args.length == 1 && args[0].equals("p")) {
          Casino.listParty(p);
          return true;
        } 
        if (args.length == 2 && args[0].equals("h")) {
          int page = Integer.parseInt(args[1]);
          displayHelpalias(sender, page);
          return true;
        } 
        displayHelpalias(sender, 1);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length >= 1 && args[0].equalsIgnoreCase("tictactoc")) {
        Casino.joinParty("Tictactoc", p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length == 1 && args[0].equalsIgnoreCase("stopAttendre")) {
        Casino.StopWaiting(p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length == 2 && args[0].equalsIgnoreCase("miser") && !args[1].isEmpty()) {
        Bet.addBet(p, Double.parseDouble(args[1]));
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length == 1 && args[0].equals("jackpot")) {
        Casino.joinParty("Jackpot", p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length == 1 && args[0].equals("roulette")) {
        Casino.joinParty("Roulette", p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length == 1 && args[0].equals("partie")) {
        Casino.listParty(p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length == 2 && args[0].equals("help")) {
        int page = Integer.parseInt(args[1]);
        displayHelp(sender, page);
        return true;
      } 
      displayHelp(sender, 1);
    } else {
      if (cmd.getName().equalsIgnoreCase("casino") && args.length >= 2 && args[0].equalsIgnoreCase("tictactoc")) {
        Player p = Bukkit.getPlayerExact(args[1]);
        if (p == null)
          return false; 
        Casino.joinParty("Tictactoc", p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length >= 2 && args[0].equals("jackpot")) {
        Player p = Bukkit.getPlayerExact(args[1]);
        if (p == null)
          return false; 
        Casino.joinParty("Jackpot", p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length >= 2 && args[0].equals("roulette")) {
        Player p = Bukkit.getPlayerExact(args[1]);
        if (p == null)
          return false; 
        Casino.joinParty("Roulette", p);
        return true;
      } 
      if (cmd.getName().equalsIgnoreCase("casino") && args.length == 2 && args[0].equals("help")) {
        int page = Integer.parseInt(args[1]);
        displayHelp(sender, page);
        return true;
      } 
    } 
    return false;
  }
  
  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Casino commands 1/1 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/casino <jeux> §7- §aJouer à un des jeux du casino");
      sender.sendMessage("● §c/casino stopAttendre §7- §aQuitter la file d'attente");
      sender.sendMessage("● §c/casino miser <valeur> §7- §aMiser");
      sender.sendMessage("● §c/casino partie §7- §aAffiche la liste des parties");
      sender.sendMessage("● §c/casino help <Page> §7- §aAffiche l'aide");
    } 
  }
  
  public void displayHelpalias(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Casino commands 1/1 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/cas <jeux> §7- §aJouer à un des jeux du casino");
      sender.sendMessage("● §c/cas sa §7- §aQuitter la file d'attente");
      sender.sendMessage("● §c/cas m <valeur> §7- §aMiser");
      sender.sendMessage("● §c/cas p §7- §aAffiche la liste des parties");
      sender.sendMessage("● §c/cas h <Page> §7- §aAffiche l'aide");
    } 
  }
}
