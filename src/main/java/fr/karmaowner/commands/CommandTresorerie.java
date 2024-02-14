package fr.karmaowner.commands;

import fr.karmaowner.data.PlayerData;
import fr.karmaowner.tresorerie.Tresorerie;
import fr.karmaowner.utils.MessageUtils;
import java.math.BigDecimal;

import fr.karmaowner.utils.Permissions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTresorerie implements CommandExecutor {
  private HelpCommand commands = new HelpCommand("tresorerie");
  
  public CommandTresorerie() {
    help();
  }
  
  public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
    Player p = null;
    PlayerData pData = null;
    if (arg0 instanceof Player) {
      p = (Player)arg0;
      pData = PlayerData.getPlayerData(p.getName());
    } 
    if (arg3.length == 0) {
      MessageUtils.sendMessage((CommandSender)p, this.commands.getCommands());
      return true;
    } 
    if (arg3.length == 2 && arg3[0].equalsIgnoreCase("help")) {
      int page = Integer.parseInt(arg3[1]);
      MessageUtils.sendMessage((CommandSender)p, this.commands.getCommands(page));
      return true;
    } 
    if (arg3.length == 3 && arg3[0].equalsIgnoreCase("subvention")) {
      String tresorerieName = arg3[1];
      double money = Double.parseDouble(arg3[2]);
      Tresorerie to = Tresorerie.getTresorerie(tresorerieName);
      Tresorerie from = Tresorerie.getTresorerie(pData.selectedJob.getFeatures().getName());
      if (from == null) {
        MessageUtils.sendMessage((CommandSender)p, "Votre métier ne dispose d'aucune trésorerie");
        return false;
      } 
      if (to != null) {
        if (from.hasPrivilege(p)) {
          if (from.hasMoney(money)) {
            from.substMoney(money);
            to.addMoney(money);
            MessageUtils.sendMessage((CommandSender)p, "§aL'argent a été transféré à la trésorerie §2" + to.getTresorerieName());
          } else {
            MessageUtils.sendMessage((CommandSender)p, "La trésorerie ne dispose pas d'assez d'argent");
          } 
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Vous ne pouvez pas intéragir avec la trésorerie: §4" + from.getNeededPrivilege());
        } 
      } else {
        MessageUtils.sendMessage((CommandSender)p, "Cette trésorerie n'existe pas !");
      } 
    } else if (arg3.length == 3 && arg3[0].equalsIgnoreCase("subventionstaff")) {
      String tresorerieName = arg3[1];
      double money = Double.parseDouble(arg3[2]);
      Tresorerie to = Tresorerie.getTresorerie(tresorerieName);
      Tresorerie from = Tresorerie.getTresorerie("staff");
      if (from == null) {
        MessageUtils.sendMessage((CommandSender)p, "Votre métier ne dispose d'aucune trésorerie");
        return false;
      } 
      if (to != null) {
        if (from.hasPrivilege(p)) {
          if (from.hasMoney(money)) {
            from.substMoney(money);
            to.addMoney(money);
            MessageUtils.sendMessage((CommandSender)p, "§aL'argent a été transféré à la trésorerie §2" + to.getTresorerieName());
          } else {
            MessageUtils.sendMessage((CommandSender)p, "La trésorerie ne dispose pas d'assez d'argent");
          } 
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Vous ne pouvez pas intéragir avec la trésorerie: §4" + from.getNeededPrivilege());
        } 
      } else {
        MessageUtils.sendMessage((CommandSender)p, "Cette trésorerie n'existe pas !");
      } 
    } else if (arg3.length == 1 && arg3[0].equalsIgnoreCase("list")) {
      MessageUtils.sendMessage((CommandSender)p, "§6------- §eListe des trésoreries §6-------");
      for (Tresorerie t : Tresorerie.TresorerieList)
        MessageUtils.sendMessage((CommandSender)p, "§6- Trésorerie §e" + StringUtils.capitalize(t.getTresorerieName()) + " §6(total = §e" + t.getMoney() + "§6 )"); 
      MessageUtils.sendMessage((CommandSender)p, "§6-------------------------------------");
    } else if (arg3.length == 2 && arg3[0].equalsIgnoreCase("pay")) {
      double money = Double.parseDouble(arg3[1]);
      Tresorerie from = Tresorerie.getTresorerie(pData.selectedJob.getFeatures().getName());
      if (from == null) {
        MessageUtils.sendMessage((CommandSender)p, "Votre métier ne dispose d'aucune trésorerie");
        return false;
      } 
      BigDecimal subBigDecimal = pData.getMoney().subtract(new BigDecimal(money));
      double substractedMoney = subBigDecimal.doubleValue();
      if (substractedMoney >= 0.0D) {
        pData.setMoney(subBigDecimal);
        from.addMoney(money);
        MessageUtils.sendMessage((CommandSender)p, "§aL'argent a été transféré à la trésorerie");
      } else {
        MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas assez d'argent");
      } 
    } else if (arg3.length == 2 && arg3[0].equalsIgnoreCase("pick")) {
      double money = Double.parseDouble(arg3[1]);
      Tresorerie from = Tresorerie.getTresorerie(pData.selectedJob.getFeatures().getName());
      if (from == null) {
        MessageUtils.sendMessage((CommandSender)p, "Votre métier ne dispose d'aucune trésorerie");
        return false;
      } 
      if (from instanceof fr.karmaowner.tresorerie.TresoreriePickable) {
        if (from.hasPrivilege(p)) {
          if (from.hasMoney(money)) {
            BigDecimal addBigDecimal = pData.getMoney().add(new BigDecimal(money));
            pData.setMoney(addBigDecimal);
            from.substMoney(money);
            MessageUtils.sendMessage((CommandSender)p, "§aL'argent a été transféré sur votre compte");
          } else {
            MessageUtils.sendMessage((CommandSender)p, "La trésorerie ne dispose pas d'assez d'argent");
          } 
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Vous ne pouvez pas intéragir avec la trésorerie: §4" + from.getNeededPrivilege());
        } 
      } else {
        MessageUtils.sendMessage((CommandSender)p, "Cette trésorerie ne dispose pas de la fonctionnalité pour récupérer de l'argent");
      } 
    } else if (arg3.length == 2 && arg3[0].equalsIgnoreCase("pickstaff")) {
      double money = Double.parseDouble(arg3[1]);
      Tresorerie from = Tresorerie.getTresorerie("staff");
      if (from == null) {
        MessageUtils.sendMessage((CommandSender)p, "Votre métier ne dispose d'aucune trésorerie");
        return false;
      } 
      if (from instanceof fr.karmaowner.tresorerie.TresoreriePickable) {
        if (from.hasPrivilege(p)) {
          if (from.hasMoney(money)) {
            BigDecimal addBigDecimal = pData.getMoney().add(new BigDecimal(money));
            pData.setMoney(addBigDecimal);
            from.substMoney(money);
            MessageUtils.sendMessage((CommandSender)p, "§aL'argent a été transféré sur votre compte");
          } else {
            MessageUtils.sendMessage((CommandSender)p, "La trésorerie ne dispose pas d'assez d'argent");
          } 
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Vous ne pouvez pas intéragir avec la trésorerie: §4" + from.getNeededPrivilege());
        } 
      } else {
        MessageUtils.sendMessage((CommandSender)p, "Cette trésorerie ne dispose pas de la fonctionnalité pour récupérer de l'argent");
      } 
    }
    else if(arg3.length == 3 && arg3[0].matches("add"))
    {
      if(arg0.hasPermission(Permissions.Admin))
      {
        Tresorerie from = Tresorerie.getTresorerie(arg3[1]);
        double money = Double.parseDouble(arg3[2]);
        if(from != null)
        {
          MessageUtils.sendMessage(arg0, "§a" + money + "$ §ba été ajouté à la trésorerie");
          from.addMoney(money);
        }
      }


    }
    return false;
  }
  
  public void help() {
    this.commands.addCommand("subvention <nom trésorerie> <money>", "Transfert d'argent d'une trésorerie à une autre trésorerie donné en paramètre");
    this.commands.addCommand("subventionstaff <nom trésorerie> <money>", "Transfert d'argent de la trésorerie du staff à une autre trésorerie donné en paramètre");
    this.commands.addCommand("list", "Liste des trésoreries disponibles");
    this.commands.addCommand("pay <money>", "Léguer de l'argent ç la trésorerie rattachée");
    this.commands.addCommand("pick <money>", "Récupérer de l'argent de la trésorerie rattachée");
    this.commands.addCommand("pickstaff <money>", "Récupérer de l'argent de la trésorerie du staff");
  }
}
