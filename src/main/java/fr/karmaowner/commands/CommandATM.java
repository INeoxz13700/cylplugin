package fr.karmaowner.commands;

import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.MoneyUtils;
import java.math.BigDecimal;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandATM implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("atm")) {
      if (args.length >= 4) {
        int taxes = 0;
        if(args.length == 5)
        {
          taxes = Integer.parseInt(args[4]);
        }
        Player victim = null;
        if (args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty() || args[3].isEmpty()) {
          MessageUtils.sendMessageFromConfig(sender, "command-invalid");
          return false;
        } 
        if (args[0].equalsIgnoreCase("admin")) {
          victim = Bukkit.getPlayerExact(args[2]);
          if (victim == null) {
            MessageUtils.sendMessageFromConfig(sender, "user-invalid");
            return false;
          } 
          PlayerData pData = PlayerData.getPlayerData(victim.getName());
          if (args[1].equalsIgnoreCase("put") && sender.hasPermission("cylrp.atm.put")) {
            int value;
            try {
              value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
              MessageUtils.sendMessageFromConfig(sender, "not-integer");
              return false;
            }

            if(value < 0){
              return false;
            }

            int moneyToAdd = MoneyUtils.addMoneyToBank(value, victim);

            if (moneyToAdd > 0) {
              MessageUtils.sendMessageFromConfig((CommandSender)victim, "atm-put-successfuly");
              pData.setMoney(pData.getMoney().add(BigDecimal.valueOf(moneyToAdd)));

              if(taxes > 0)
              {
                int taxeToPay = (int)(value * (taxes / 100F));
                MessageUtils.sendMessage(victim, "§cVous avez payé 20% de taxes pour avoir utilisé l'application");
                pData.setMoney(pData.getMoney().subtract(BigDecimal.valueOf(taxeToPay)));
              }

              return true;
            } 
            MessageUtils.sendMessageFromConfig((CommandSender)victim, "atm-enough-tickets");
            return false;
          } 
          if (args[1].equalsIgnoreCase("take") && sender.hasPermission("cylrp.atm.take")) {
            int value;
            try {
              value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
              MessageUtils.sendMessageFromConfig(sender, "not-integer");
              return false;
            }

            if(value < 0)
            {
              return false;
            }

            if (pData.getMoney().intValue() >= value)
            {
              int moneyToTake = MoneyUtils.convertValueToTickets(value, victim);

              if(taxes > 0)
              {
                int taxeToPay = (int)(value * (taxes / 100F));
                pData.setMoney(pData.getMoney().subtract(BigDecimal.valueOf(taxeToPay)));
                MessageUtils.sendMessage(victim, "§cVous avez payé 20% de taxes pour avoir utilisé l'application");
              }

              pData.setMoney(pData.getMoney().subtract(BigDecimal.valueOf(moneyToTake)));
              MessageUtils.sendMessageFromConfig((CommandSender)victim, "atm-take-successfuly");

              if (moneyToTake != value)
                if (moneyToTake > 0) {
                  MessageUtils.sendMessage((CommandSender)victim, "§cSeulement " + moneyToTake + " ont étaient retiré de votre compte bancaire car votre inventaire est plein!");
                } else {
                  MessageUtils.sendMessage((CommandSender)victim, "§cVotre inventaire est plein l'argent n'a pas été retiré de votre compte bancaire!");
                }  
              return true;
            } 
            MessageUtils.sendMessageFromConfig((CommandSender)victim, "atm-enough-money");
            return false;
          } 
          if (!sender.hasPermission("cylrp.atm.take") && !sender.hasPermission("cylrp.atm.put")) {
            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
            return false;
          } 
        } 
      } else if (args.length == 2) {
        if (args[0].equalsIgnoreCase("help")) {
          int page = Integer.parseInt(args[1]);
          displayHelp(sender, page);
          return true;
        } 
      } 
      displayHelp(sender, 1);
      return false;
    } 
    return false;
  }
  
  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【ATM commands 1/1 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/atm admin put <Username> <money> §7- §aAjoute de l'argent au joueur");
      sender.sendMessage("● §c/atm admin take <Username> <money> §7- §aRetire de l'argent du compte bancaire du joueur");
      sender.sendMessage("● §c/atm help <Page> §7- §aAffiche l'aide");
    } 
  }
}
