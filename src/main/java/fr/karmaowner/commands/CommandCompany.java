package fr.karmaowner.commands;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.chat.Chat;
import fr.karmaowner.common.Achievements;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.Request;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyElevage;
import fr.karmaowner.companies.eggs.EggsHatching;
import fr.karmaowner.companies.eggs.EntityEggsPnj;
import fr.karmaowner.companies.shop.ItemShop;
import fr.karmaowner.companies.shop.Npc;
import fr.karmaowner.companies.shop.Shop;
import fr.karmaowner.companies.shop.TempActionShop;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.data.PnjsEggsData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.jobs.parcelle.Champ;
import fr.karmaowner.jobs.parcelle.Enclo;
import fr.karmaowner.jobs.parcelle.Local;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import fr.karmaowner.utils.RecordBuilder;
import fr.karmaowner.utils.RegionUtils;
import fr.karmaowner.utils.TimerUtils;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandCompany implements CommandExecutor {
  PlayerData pData;
  
  Player pSender;
  
  ConsoleCommandSender cSender;

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      this.pSender = (Player)sender;
      this.pData = PlayerData.getPlayerData(this.pSender.getName());
    }
    if (label.equalsIgnoreCase("ent")) {
      if (args.length < 1) {
        displayHelpAlias((CommandSender)this.pSender, 1);
        return true;
      }
      if (args[0].equalsIgnoreCase("mbs")) {
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
        if (cData == null) {
          MessageUtils.sendMessageFromConfig(sender, "company-havent");
          return false;
        }
        String salaries = "";
        if (cData.getSalaries().size() > 0) {
          salaries = "§9Salaries :";
          for (String str : cData.getSalaries()) {
            if (cData.getSalaries().size() == 1) {
              salaries = salaries + " " + str;
              continue;
            }
            salaries = salaries + " " + str + ",";
          }
        }
        String stagiaires = "";
        if (cData.getStagiaires().size() > 0) {
          stagiaires = "§9Stagiaires :";
          for (String str : cData.getStagiaires()) {
            if (cData.getStagiaires().size() == 1) {
              stagiaires = stagiaires + " " + str;
              continue;
            }
            stagiaires = stagiaires + " " + str + ",";
          }
        }
        String secretaires = "";
        if (cData.getStagiaires().size() > 0) {
          secretaires = "§9Secretaires :";
          for (String str : cData.getSecretaires()) {
            if (cData.getSecretaires().size() == 1) {
              secretaires = secretaires + " " + str;
              continue;
            }
            secretaires = secretaires + " " + str + ",";
          }
        }
        String communityManager = "";
        if (cData.getStagiaires().size() > 0) {
          communityManager = "§9CommunityManager :";
          for (String str : cData.getCommunityManagers()) {
            if (cData.getCommunityManagers().size() == 1) {
              communityManager = communityManager + " " + str;
              continue;
            }
            communityManager = communityManager + " " + str + ",";
          }
        }
        String coGerant = "";
        if (cData.getCoGerant().size() > 0) {
          stagiaires = "§9CoGerant :";
          for (String str : cData.getCoGerant()) {
            if (cData.getCoGerant().size() == 1) {
              coGerant = coGerant + " " + str;
              continue;
            }
            coGerant = coGerant + " " + str + ",";
          }
        }
        String Gerant = "§9Gérant : " + cData.getGerant();
        sender.sendMessage("§bListe des membres :");
        if (!salaries.isEmpty())
          sender.sendMessage(salaries);
        if (!stagiaires.isEmpty())
          sender.sendMessage(stagiaires);
        if (!secretaires.isEmpty())
          sender.sendMessage(secretaires);
        if (!communityManager.isEmpty())
          sender.sendMessage(communityManager);
        if (!coGerant.isEmpty())
          sender.sendMessage(coGerant);
        if (!Gerant.isEmpty())
          sender.sendMessage(Gerant);
      }
      if (args[0].equalsIgnoreCase("h"))
        displayHelp((CommandSender)this.pSender, 1);
      if (args[0].equalsIgnoreCase("lv"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-owner");
            return true;
          }
          Chat.leftFromCanal(this.pSender.getName());
          cData.onLeft(cData.getExactUsernameFromUsername(this.pSender.getName()));
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
        }
      if (args[0].equalsIgnoreCase("info"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          sender.sendMessage("§6--------------------------------");
          sender.sendMessage("§6Nom: §e" + cData.getCompanyName());
          sender.sendMessage("§6Activité : §e" + cData.getCategoryName());
          sender.sendMessage("§6Revenues: §e" + cData.getRevenues());
          sender.sendMessage("§6Total de salariés: §e" + cData.getEffectif());
          sender.sendMessage("§6Produits achetés par vos clients: §e" + cData.getNbVenteTotal());
          sender.sendMessage("§6Succès débloqués: §e" + cData.getAchievements().size());
          sender.sendMessage("§6Salariés recrutable: §e" + cData.getNbSalaries());
          sender.sendMessage("§6Niveau : §e" + cData.getLevelReached());
          sender.sendMessage("§6--------------------------------");
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
        }
      if (args[0].equalsIgnoreCase("tc"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData.getCompany() instanceof fr.karmaowner.companies.CompanyAgriculture) {
            if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
              Champ.fillInventory();
              this.pSender.openInventory(Champ.getInv());
            } else {
              MessageUtils.sendMessageFromConfig(sender, "company-less-permission");
            }
          } else {
            MessageUtils.sendMessageFromConfig(sender, "company-not-agriculture");
          }
        } else {
          MessageUtils.sendMessageFromConfig(sender, "company-havent");
        }
      if (args[0].equalsIgnoreCase("tl"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
            Local.fillInventory();
            this.pSender.openInventory(Local.getInv());
          } else {
            MessageUtils.sendMessageFromConfig(sender, "company-less-permission");
          }
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
        }
      if (args[0].equalsIgnoreCase("h")) {
        int arg1 = 0;
        try {
          arg1 = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
          MessageUtils.getMessageFromConfig("not-integer");
        } finally {
          displayHelpAlias((CommandSender)this.pSender, arg1);
        }
      }
      if (args[0].equalsIgnoreCase("rl"))
        displayRankList((CommandSender)this.pSender);
      if (args[0].equalsIgnoreCase("sl")) {
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
          return false;
        }
        ((CompanyData)CompanyData.Companies.get(this.pData.companyName)).displayRepartition((CommandSender)this.pSender);
      }
      if (args[0].equalsIgnoreCase("inv")) {
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
        if (args[1].isEmpty() || Bukkit.getPlayerExact(args[1]) == null) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-user-invalid");
          return false;
        }
        String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());
        if (cData.getGerant().equalsIgnoreCase(senderUsername) || cData.getCoGerant().contains(senderUsername)) {
          for(String username : cData.getUsersList())
          {
            if(username.equalsIgnoreCase(args[1]))
            {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-user-exist");
              return false;
            }
          }
          if (cData.getUsersList().size() - 1 >= cData.getNbSalaries()) {
            this.pSender.sendMessage(ChatColor.RED + "Vous avez dépassé la limite de salarié qui s'élève à " + ChatColor.DARK_RED + cData.getNbSalaries());
            return false;
          }
          Request.createRequest(Request.RequestType.COMPANYINVITE, this.pSender.getName(), args[1]);
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
        }
      }
      if (args[0].equalsIgnoreCase("jn")) {

        if (args[1].equalsIgnoreCase(this.pSender.getName())) return false;
        if (args[1].isEmpty()) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-user-invalid");
          return false;
        }
        Request request = Request.findRequest(Request.RequestType.COMPANYINVITE,args[1], this.pSender.getName());
        if (request == null) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-request-invalid");
          return false;
        }

        CompanyData d = CompanyData.getCompanyData(PlayerData.getPlayerData(request.getSender()).getCompanyName());
        if (d.getUsersList().size() - 1 >= d.getNbSalaries()) {
          this.pSender.sendMessage(ChatColor.RED + "Vous ne pouvez pas rejoindre cette entreprise qui a atteint la limite de salariés autorisés.");
          return false;
        }
        d.getSalaries().add(this.pSender.getName());
        this.pData.companyName = d.getCompanyName();
        this.pData.companyCategory = d.getCategoryName();
        String msg = MessageUtils.getMessageFromConfig("company-join");
        msg = msg.replaceAll("%player%", this.pSender.getName());
        msg = msg.replaceAll("&", "§");
        d.broadcastCompany(msg);
        this.pSender.sendMessage(ChatColor.GREEN + "Vous venez de rejoindre l'entreprise !");
        request.destroy();
      }
      if (args[0].equalsIgnoreCase("kck")) {
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
        if (args[1].isEmpty()) {
          MessageUtils.sendMessage((CommandSender)this.pSender, "company-enter-username");
          return false;
        }

        if(args[1].equalsIgnoreCase(pSender.getName()))
        {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          return false;
        }

        String kickUsername = cData.getExactUsernameFromUsername(args[1]);
        String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());

        if(kickUsername == null)
        {
          MessageUtils.sendMessage((CommandSender)this.pSender, "company-not-contain-username");
          return false;
        }

        boolean canKickTarget = false;
        if(cData.getGerant().equalsIgnoreCase(senderUsername)) {
          canKickTarget = true;
        }
        else if(cData.getCoGerant().contains(senderUsername))
        {
          if(cData.getCoGerant().contains(kickUsername) || cData.getGerant().equalsIgnoreCase(kickUsername))
          {
            canKickTarget = false;
          }
          else
          {
            canKickTarget = true;
          }
        }

        if(!canKickTarget)
        {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
        }
        else
        {
          cData.onKick(this.pSender.getName(), kickUsername);
          if (Bukkit.getPlayerExact(args[1]) != null) Chat.leftFromCanal(args[1]);
        }

      }
      if (args[0].equalsIgnoreCase("setrk")) {
        if (args[1].isEmpty() || args[2].isEmpty()) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "command-invalid");
          displayHelp((CommandSender)this.pSender, 1);
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);

        String exactUsername = cData.getExactUsernameFromUsername(args[1]);
        String exactUsernameSender = cData.getExactUsernameFromUsername(pSender.getName());

        if(exactUsername == null)
        {
          MessageUtils.sendMessage(pSender,"§cCe joueur n'est pas dans l'entreprise");
          return false;
        }

        if (cData.getGerant().equalsIgnoreCase(exactUsername)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          return false;
        }
        if (args[2].equalsIgnoreCase("CoGerant")) {
          if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
            return false;
          }
          if (cData.getCoGerant().contains(exactUsername)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
            return false;
          }
          cData.setRank(exactUsername, args[2], this.pSender);
        } else if (args[2].equalsIgnoreCase("Secretaire")) {
          if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
            return false;
          }
          if (cData.getSecretaires().contains(exactUsername)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
            return false;
          }
          cData.setRank(exactUsername, args[2], this.pSender);
        } else if (args[2].equalsIgnoreCase("CommunityManager")) {
          if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
            return false;
          }
          if (cData.getCommunityManagers().contains(exactUsername)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
            return false;
          }
          cData.setRank(exactUsername, args[2], this.pSender);
        } else if (args[2].equalsIgnoreCase("Stagiaire")) {
          if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
            return false;
          }
          if (cData.getStagiaires().contains(exactUsername)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
            return false;
          }
          cData.setRank(exactUsername, args[2], this.pSender);
        } else if (args[2].equalsIgnoreCase("Salarie")) {
          if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
            return false;
          }
          if (cData.getSalaries().contains(exactUsername)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
            return false;
          }
          cData.setRank(exactUsername, args[2], this.pSender);
        } else {
          displayRankList((CommandSender)this.pSender);
        }
      }
      if (args[0].equalsIgnoreCase("s")) {
        if (args[1].isEmpty() || args[2].isEmpty()) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "command-invalid");
          return false;
        }
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender) this.pSender, "company-havent");
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);

        if(!cData.getGerant().equalsIgnoreCase(pSender.getName()))
        {
          MessageUtils.sendMessageFromConfig((CommandSender) this.pSender, "company-less-permission");
          return false;
        }

        if (CompanyData.rankExist(args[1])) {
          int arg2 = 0;
          try {
            arg2 = Integer.parseInt(args[2]);
          } catch (NumberFormatException e) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "bad-number");
            return false;
          }
          if (cData.setRepartition(args[1], arg2)) {
            MessageUtils.sendMessageFromConfig(sender, "repartition-attribuated");
            return true;
          }
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-repartition-max");
        } else {
          MessageUtils.sendMessage(sender, "§c Ce rang n'existe pas");
        }
      }
      if (args[0].equalsIgnoreCase("crte")) {
        Player p = Bukkit.getPlayerExact(args[1]);
        if (!sender.hasPermission("cylrp.company.create")) {
          MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
          return false;
        }
        if (!args[1].isEmpty() && !args[2].isEmpty() && !args[3].isEmpty()) {
          if ((PlayerData.getPlayerData(p.getName())).companyName == null || (PlayerData.getPlayerData(p.getName())).companyName.isEmpty()) {
            try {
              SqlCollection result = Main.Database.select(RecordBuilder.build().selectAll("company_data", new CustomEntry("Name", args[2]))
                  .toString());
              if (result.count() == 0) {
                CompanyData cData = new CompanyData(args[2], p, args[3]);
                if (cData.getCompany() instanceof CompanyElevage) {
                  CompanyElevage ce = (CompanyElevage)cData.getCompany();
                  for (CompanyElevage.XP_ELEVAGE x : CompanyElevage.XP_ELEVAGE.values()) {
                    ce.getEggsData().addEggs(new EggsHatching(x.getTime(), x.getTypeId(), x.getName()));
                    ce.getEggsData().addEggs(new EggsHatching(x.getTime(), x.getTypeId(), x.getName()));
                  }
                }
                String msg = MessageUtils.getMessageFromConfig("company-created");
                msg = msg.replaceAll("%company%", args[2]);
                msg = msg.replaceAll("&", "§");
                Bukkit.broadcastMessage(msg);
              } else {
                MessageUtils.sendMessage(sender, MessageUtils.getMessageFromConfig("company-exist"));
              }
            } catch (SQLException e) {
              e.printStackTrace();
            }
          } else {
            MessageUtils.sendMessage(sender, MessageUtils.getMessageFromConfig("company-owned"));
          }
        } else {
          MessageUtils.sendMessage(sender, MessageUtils.getMessageFromConfig("command-invalid"));
          displayHelp(sender, 1);
        }
      }
      if (args[0].equalsIgnoreCase("pub")) {
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "havent-company");
          return false;
        }
        if (args[1].isEmpty()) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "message-enter");
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
        String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());
        if (!cData.getGerant().equalsIgnoreCase(senderUsername) && !cData.getCoGerant().contains(senderUsername) && !cData.getCommunityManagers().contains(senderUsername)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          return false;
        }
        if (!this.pData.commandConfirmation) {
          MessageUtils.sendMessage((CommandSender)this.pSender, "§4Attention ! §cLa publicité coûtera §610$ §cpar joueurs connectés");
          MessageUtils.sendMessage((CommandSender)this.pSender, "§cRéutiliser la commande pour confirmer");
          this.pData.commandConfirmation = true;
          String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
          String message = StringUtils.join((Object[])msg, " ");
          this.pData.advertMessage = message;
          return false;
        }
        this.pData.commandConfirmation = false;
        double price = Bukkit.getServer().getOnlinePlayers().size() * 10;
        if (this.pData.getMoney().doubleValue() >= price) {
          MessageUtils.sendMessage((CommandSender)this.pSender, "§aTransaction comfirmé §6coût : " + price + " $");
          this.pData.setMoney(this.pData.getMoney().subtract(BigDecimal.valueOf(price)));
          this.pData.advertMessage = this.pData.advertMessage.replaceAll("&", "§");
          Bukkit.broadcastMessage("§6[Publicité-" + cData.getCompanyName() + "] " + this.pData.advertMessage);
        } else {
          String msg = MessageUtils.getMessageFromConfig("not-necessary-money");
          msg = msg.replaceAll("&", "§");
          MessageUtils.sendMessage(sender, msg);
        }
      }
      if (args[0].equalsIgnoreCase("ann")) {
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
          return false;
        }
        if (args[1].isEmpty()) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "message-enter");
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
        String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());
        if (cData.getGerant().equalsIgnoreCase(senderUsername) || cData.getCoGerant().contains(senderUsername) || cData.getCommunityManagers().contains(senderUsername) || cData.getSecretaires().contains(senderUsername)) {
          String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
          cData.broadcastCompany(msg);
        } else {
          MessageUtils.sendMessageFromConfig(sender, "company-less-permission");
        }
      }
      if (args[0].equals("disb"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
            cData.leaveAll();
            if (this.pSender.getWorld().getName().equalsIgnoreCase("world1"))
              PlayerUtils.teleportToSpawn(this.pSender);
            try {
              Main.Database.update(RecordBuilder.build().delete("company_data").where(new CustomEntry("Name", cData.getCompanyName())).toString());
            } catch (SQLException e) {
              e.printStackTrace();
            }
            CompanyData.Companies.remove(cData.getCompanyName());
            MessageUtils.sendMessageFromConfig(sender, "company-disband");
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          }
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
        }
      if (args[0].equals("remun"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - cData.lastAttributiontime.getTime() >= 86400000L) {
              cData.divideSalary();
            } else {
              int elapsedSc = 86400 - (int)((now.getTime() - cData.lastAttributiontime.getTime()) / 1000.0D);
              String timeleft = TimerUtils.formatString(elapsedSc);
              this.pSender.sendMessage("Vous devez attendre " + timeleft + " avant de rémunérer vos salariés.");
            }
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          }
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
        }
      if (args[0].equals("elvegg") && this.pSender.hasPermission("cylrp.company.eggspnj") &&
        args[1] != null && args[2] != null) {
        String name = args[1];
        String playername = args[2];
        if (Bukkit.getPlayerExact(playername) != null) {
          CompanyElevage.XP_ELEVAGE x = CompanyElevage.XP_ELEVAGE.getEnumByEnumName(name);
          if (x != null) {
            Bukkit.getPlayerExact(playername).getInventory().addItem(ItemUtils.getItem(x.getId(), x.getData().byteValue(), 1, x.getName(), null));
            this.pSender.sendMessage(ChatColor.GREEN + x.getName() + " Envoyé à " + ChatColor.DARK_GREEN + playername);
          }
        }
      }
      if (args[0].equals("achv")) {
        String name = args[1];
        if (CompanyData.Companies.containsKey(name)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(name);
          cData.printAchievements(this.pSender);
        } else {
          MessageUtils.sendMessageFromConfig(sender, "company-unknown");
        }
      }
      if (args[0].equalsIgnoreCase("eps")) {
        if (!sender.hasPermission("cylrp.company.eggspnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        new EntityEggsPnj(this.pSender);
      }
      if (args[0].equalsIgnoreCase("epd")) {
        if (!sender.hasPermission("cylrp.company.eggspnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        PnjsEggsData.removePnjLooking(this.pSender);
      }
      if (args[0].equalsIgnoreCase("eplp")) {
        if (!sender.hasPermission("cylrp.company.eggspnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        int page = Integer.parseInt(args[3]);
        PnjsEggsData.printPnjList(page);
      }
      if (args[0].equalsIgnoreCase("ept")) {
        if (!sender.hasPermission("cylrp.company.eggspnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        int id = Integer.parseInt(args[2]);
        this.pSender.teleport(PnjsEggsData.getEntities(id));
        this.pSender.sendMessage(ChatColor.GREEN + "Téléportation réussie !");
      }
      if (args[0].equalsIgnoreCase("ssi") &&
        !args[1].isEmpty()) {
        int quantite = Integer.parseInt(args[1]);
        TempActionShop.runAction(this.pSender, quantite);
      }
      if (args[0].equalsIgnoreCase("slp")) {
        if (!sender.hasPermission("cylrp.company.sellpnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        int page = Integer.parseInt(args[1]);
        Npc.printPnjList(page, this.pSender);
      }
      if (args[0].equalsIgnoreCase("sai")) {
        if (!sender.hasPermission("cylrp.company.sellpnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        if (args[1] != null && args[2] != null && args[3] != null && args[4] != null) {
          int id = Integer.parseInt(args[1].split(":")[0]);
          byte data = 0;
          if ((args[1].split(":")).length > 1)
            data = Byte.parseByte(args[1].split(":")[1]);
          double buy_price = Double.parseDouble(args[2]);
          double sell_price = Double.parseDouble(args[3]);
          int slot = Integer.parseInt(args[4]);
          Shop.addItem(this.pSender, new ItemShop(id, data, buy_price, sell_price, slot));
        }
      }
      if (args[0].equalsIgnoreCase("san")) {
        if (!sender.hasPermission("cylrp.company.sellpnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        if (!args[1].isEmpty() || !args[2].isEmpty() || !args[3].isEmpty() || !args[4].isEmpty()) {
          String npcName = args[1];
          String npcShop = args[2];
          String type = args[3];
          String Categorie = args[4];
          Npc.addNpc(this.pSender, npcName, npcShop, type, Categorie);
        }
      }
      if (args[0].equalsIgnoreCase("sdi")) {
        if (!sender.hasPermission("cylrp.company.sellpnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        if (!args[1].isEmpty()) {
          int slot = Integer.parseInt(args[1]);
          Shop.removeItem(this.pSender, slot);
        }
      }
      if (args[0].equalsIgnoreCase("sdn")) {
        if (!sender.hasPermission("cylrp.company.sellpnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        if (!args[1].isEmpty()) {
          String name = args[1];
          Npc.deleteNpc(this.pSender, name);
        }
      }
    } else if (cmd.getName().equalsIgnoreCase("entreprise")) {
      if (args.length == 0) {
          displayHelp(sender,1);
          return true;
      }
      if (args.length == 1) {
        if (args[0].equalsIgnoreCase("members")) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData == null) {
            MessageUtils.sendMessageFromConfig(sender, "company-havent");
            return false;
          }
          String salaries = "";
          if (cData.getSalaries().size() > 0) {
            salaries = "§9Salaries :";
            for (String str : cData.getSalaries()) {
              if (cData.getSalaries().size() == 1) {
                salaries = salaries + " " + str;
                continue;
              }
              salaries = salaries + " " + str + ",";
            }
          }
          String stagiaires = "";
          if (cData.getStagiaires().size() > 0) {
            stagiaires = "§9Stagiaires :";
            for (String str : cData.getStagiaires()) {
              if (cData.getStagiaires().size() == 1) {
                stagiaires = stagiaires + " " + str;
                continue;
              }
              stagiaires = stagiaires + " " + str + ",";
            }
          }
          String secretaires = "";
          if (cData.getStagiaires().size() > 0) {
            secretaires = "§9Secretaires :";
            for (String str : cData.getSecretaires()) {
              if (cData.getSecretaires().size() == 1) {
                secretaires = secretaires + " " + str;
                continue;
              }
              secretaires = secretaires + " " + str + ",";
            }
          }
          String communityManager = "";
          if (cData.getStagiaires().size() > 0) {
            communityManager = "§9CommunityManager :";
            for (String str : cData.getCommunityManagers()) {
              if (cData.getCommunityManagers().size() == 1) {
                communityManager = communityManager + " " + str;
                continue;
              }
              communityManager = communityManager + " " + str + ",";
            }
          }
          String coGerant = "";
          if (cData.getCoGerant().size() > 0) {
            stagiaires = "§9CoGerant :";
            for (String str : cData.getCoGerant()) {
              if (cData.getCoGerant().size() == 1) {
                coGerant = coGerant + " " + str;
                continue;
              }
              coGerant = coGerant + " " + str + ",";
            }
          }
          String Gerant = "§9Gérant : " + cData.getGerant();
          sender.sendMessage("§bListe des membres :");
          if (!salaries.isEmpty())
            sender.sendMessage(salaries);
          if (!stagiaires.isEmpty())
            sender.sendMessage(stagiaires);
          if (!secretaires.isEmpty())
            sender.sendMessage(secretaires);
          if (!communityManager.isEmpty())
            sender.sendMessage(communityManager);
          if (!coGerant.isEmpty())
            sender.sendMessage(coGerant);
          if (!Gerant.isEmpty())
            sender.sendMessage(Gerant);
        }
        if (args[0].equalsIgnoreCase("help")) {
          displayHelp((CommandSender)this.pSender, 1);
        } else if (args[0].equalsIgnoreCase("leave")) {
          if (CompanyData.Companies.containsKey(this.pData.companyName)) {
            CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
            if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-owner");
              return true;
            }
            if (this.pSender.getWorld().getName().equalsIgnoreCase("world1"))
              PlayerUtils.teleportToSpawn(this.pSender);
            this.pSender.sendMessage("§cVous venez de quitter l'entreprise §4" + this.pData.companyName);
            cData.onLeft(cData.getExactUsernameFromUsername(this.pSender.getName()));
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
          } 
        } else if (args[0].equalsIgnoreCase("info")) {
          if (CompanyData.Companies.containsKey(this.pData.companyName)) {
            CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
            sender.sendMessage("§6--------------------------------");
            sender.sendMessage("§6Nom: §e" + cData.getCompanyName());
            sender.sendMessage("§6Activité : §e" + cData.getCategoryName());
            sender.sendMessage("§6Revenues: §e" + cData.getRevenues());
            sender.sendMessage("§6Total de salariés: §e" + cData.getEffectif());
            sender.sendMessage("§6Produits achetés par vos clients: §e" + cData.getNbVenteTotal());
            sender.sendMessage("§6Succès débloqués: §e" + cData.getAchievements().size());
            sender.sendMessage("§6Salariés recrutable: §e" + cData.getNbSalaries());
            sender.sendMessage("§6Niveau : §e" + cData.getLevelReached());
            sender.sendMessage("§6--------------------------------");
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
          }
        }
      } else if (args.length == 2) {
        if (args[0].equalsIgnoreCase("help")) {
          int arg1 = 0;
          try {
            arg1 = Integer.parseInt(args[1]);
          } catch (NumberFormatException e) {
            MessageUtils.getMessageFromConfig("not-integer");
          } finally {
            displayHelp((CommandSender)this.pSender, arg1);
          }
        } else if (args[0].equalsIgnoreCase("rank")) {
          if (args[1].equalsIgnoreCase("list"))
            displayRankList((CommandSender)this.pSender);
        } else if (args[0].equalsIgnoreCase("salaire")) {
          if (args[1].equalsIgnoreCase("list")) {
            if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
              return false;
            }
            ((CompanyData)CompanyData.Companies.get(this.pData.companyName)).displayRepartition((CommandSender)this.pSender);
          }
        } else if (args[0].equalsIgnoreCase("invite")) {
          if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
            return false;
          }
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (args[1].isEmpty() || Bukkit.getPlayerExact(args[1]) == null) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-user-invalid");
            return false;
          }
          String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());
          if (cData.getGerant().equalsIgnoreCase(senderUsername) || cData.getCoGerant().contains(senderUsername)) {

            for(String username : cData.getUsersList())
            {
              if(username.equalsIgnoreCase(args[1]))
              {
                MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-user-exist");
                return false;
              }
            }

            if (cData.getUsersList().size() - 1 >= cData.getNbSalaries()) {
              this.pSender.sendMessage(ChatColor.RED + "Vous avez dépassé la limite de salarié qui s'élève à " + ChatColor.DARK_RED + cData.getNbSalaries());
              return false;
            }
            Request.createRequest(Request.RequestType.COMPANYINVITE, this.pSender.getName(), args[1]);
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          }
        } else if (args[0].equalsIgnoreCase("join")) {
            if (args[1].equalsIgnoreCase(this.pSender.getName())) return false;

            if (args[1].isEmpty()) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-user-invalid");
              return false;
            }
            Request request = Request.findRequest(Request.RequestType.COMPANYINVITE, args[1], this.pSender.getName());
            if (request == null) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-request-invalid");
              return false;
            }

            CompanyData d = CompanyData.getCompanyData(PlayerData.getPlayerData(request.getSender()).getCompanyName());
            if (d.getUsersList().size() - 1 >= d.getNbSalaries()) {
                this.pSender.sendMessage(ChatColor.RED + "Vous ne pouvez pas rejoindre cette entreprise qui a atteint la limite de salariés autorisés.");
                return false;
            }
            d.getSalaries().add(this.pSender.getName());
            this.pData.companyName = d.getCompanyName();
            this.pData.companyCategory = d.getCategoryName();
            String msg = MessageUtils.getMessageFromConfig("company-join");
            msg = msg.replaceAll("%player%", this.pSender.getName());
            msg = msg.replaceAll("&", "§");
            d.broadcastCompany(msg);
            this.pSender.sendMessage(ChatColor.GREEN + "Vous venez de rejoindre l'entreprise !");
            request.destroy();
        } else if (args[0].equalsIgnoreCase("kick")) {
          if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
            return false;
          }
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (args[1].isEmpty()) {
            MessageUtils.sendMessage((CommandSender)this.pSender, "company-enter-username");
            return false;
          }

          if(args[1].equalsIgnoreCase(pSender.getName()))
          {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
            return false;
          }

          String kickUsername = cData.getExactUsernameFromUsername(args[1]);
          String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());

          if(kickUsername == null)
          {
            MessageUtils.sendMessage((CommandSender)this.pSender, "company-not-contain-username");
            return false;
          }

          boolean canKickTarget = false;
          if(cData.getGerant().equalsIgnoreCase(senderUsername)) {
            canKickTarget = true;
          }
          else if(cData.getCoGerant().contains(senderUsername))
          {
            if(cData.getCoGerant().contains(kickUsername) || cData.getGerant().equalsIgnoreCase(kickUsername))
            {
              canKickTarget = false;
            }
            else
            {
              canKickTarget = true;
            }
          }

          if(!canKickTarget)
          {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          }
          else
          {
            cData.onKick(this.pSender.getName(), kickUsername);
            if (Bukkit.getPlayerExact(args[1]) != null) Chat.leftFromCanal(args[1]);
          }
        }
      } else if (args.length == 3) {

        if (!sender.hasPermission("cylrp.admin"))
          sender.sendMessage("§cPas de permission");

        if (args[0].equalsIgnoreCase("setlevel")) {
          int level;
          if (args[1].isEmpty()) {
            sender.sendMessage("§cEntrez pseudo du joueur");
            return true;
          }
          if (args[2].isEmpty()) {
            sender.sendMessage("§cEntrez le niveau à mettre");
            return true;
          }
          try {
            level = Integer.parseInt(args[2]);
          } catch (NumberFormatException e) {
            sender.sendMessage("§cLe niveau doit être un nombre entier");
            return true;
          }
          String name = args[1];
          PlayerData data = PlayerData.getPlayerData(name);
          if (data.companyName != null && !data.companyName.isEmpty()) {
            CompanyData cData = CompanyData.getCompanyData(data.companyName);
            cData.setLevel(level);
          } else {
            sender.sendMessage("§cEntreprise non existante");
          }
        }
        if (args[0].equalsIgnoreCase("setrank")) {
          if (args[1].isEmpty() || args[2].isEmpty()) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "command-invalid");
            displayHelp((CommandSender)this.pSender, 1);
            return false;
          }
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);

          String exactUsername = cData.getExactUsernameFromUsername(args[1]);
          String exactUsernameSender = cData.getExactUsernameFromUsername(pSender.getName());

          if(exactUsername == null)
          {
              MessageUtils.sendMessage(pSender,"§cCe joueur n'est pas dans l'entreprise");
              return false;
          }

          if (cData.getGerant().equalsIgnoreCase(exactUsername)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
            return false;
          }
          if (args[2].equalsIgnoreCase("CoGerant")) {
            if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
              return false;
            }
            if (cData.getCoGerant().contains(exactUsername)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
              return false;
            }
            cData.setRank(exactUsername, args[2], this.pSender);
          } else if (args[2].equalsIgnoreCase("Secretaire")) {
            if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
              return false;
            }
            if (cData.getSecretaires().contains(exactUsername)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
              return false;
            }
            cData.setRank(exactUsername, args[2], this.pSender);
          } else if (args[2].equalsIgnoreCase("CommunityManager")) {
            if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
              return false;
            }
            if (cData.getCommunityManagers().contains(exactUsername)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
              return false;
            }
            cData.setRank(exactUsername, args[2], this.pSender);
          } else if (args[2].equalsIgnoreCase("Stagiaire")) {
            if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
              return false;
            }
            if (cData.getStagiaires().contains(exactUsername)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
              return false;
            }
            cData.setRank(exactUsername, args[2], this.pSender);
          } else if (args[2].equalsIgnoreCase("Salarie")) {
            if (!cData.getGerant().equalsIgnoreCase(exactUsernameSender) && !cData.getCoGerant().contains(exactUsernameSender)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
              return false;
            }
            if (cData.getSalaries().contains(exactUsername)) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-already-rank");
              return false;
            }
            cData.setRank(exactUsername, args[2], this.pSender);
          } else {
            displayRankList((CommandSender)this.pSender);
          }
        } else if (args[0].equalsIgnoreCase("salaire")) {
          if (args[1].isEmpty() || args[2].isEmpty()) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "command-invalid");
            return false;
          }
          if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
            return false;
          }
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);

          if(!cData.getGerant().equalsIgnoreCase(pSender.getName()))
          {
            MessageUtils.sendMessage(pSender, "§cVous n'avez pas la permission");
            return false;
          }


          if (CompanyData.rankExist(args[1])) {
            int arg2 = 0;
            try {
              arg2 = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
              MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "bad-number");
              return false;
            }
            if (cData.setRepartition(args[1], arg2)) {
              MessageUtils.sendMessageFromConfig(sender, "repartition-attribuated");
              return true;
            }
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-repartition-max");
          } else {
            MessageUtils.sendMessage(sender, "§c Ce rang n'existe pas");
          }
        } else if (args[0].equalsIgnoreCase("givexp")) {
          if (this.pSender.hasPermission("cylrp.admin")) {
            String companyName = args[1];
            double xp = Double.parseDouble(args[2]);
            if (companyName != null) {
              CompanyData cd = CompanyData.getCompanyData(companyName);
              if (cd != null) {
                cd.addXp(xp);
                MessageUtils.sendMessage((CommandSender)this.pSender, "§aL'xp a été ajouté à l'entreprise");
              } else {
                MessageUtils.sendMessage((CommandSender)this.pSender, "Données de l'entreprise introuvable");
              }
            } else {
              MessageUtils.sendMessage((CommandSender)this.pSender, "Nom d'entreprise requis");
            }
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          }
        }
      } else if (args.length == 4) {
        if (args[0].equalsIgnoreCase("create")) {
          Player p = Bukkit.getPlayerExact(args[1]);
          if (!sender.hasPermission("cylrp.company.create")) {
            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
            return false;
          }
          if (!args[1].isEmpty() && !args[2].isEmpty() && !args[3].isEmpty()) {
            if ((PlayerData.getPlayerData(p.getName())).companyName == null || (PlayerData.getPlayerData(p.getName())).companyName.isEmpty()) {
              try {
                SqlCollection result = Main.Database.select(RecordBuilder.build().selectAll("company_data", new CustomEntry("Name", args[2]))
                    .toString());
                if (result.count() == 0) {
                  CompanyData cData = new CompanyData(args[2], p, args[3]);
                  if (cData.getCompany() instanceof CompanyElevage) {
                    CompanyElevage ce = (CompanyElevage)cData.getCompany();
                    for (CompanyElevage.XP_ELEVAGE x : CompanyElevage.XP_ELEVAGE.values()) {
                      ce.getEggsData().addEggs(new EggsHatching(x.getTime(), x.getTypeId(), x.getName()));
                      ce.getEggsData().addEggs(new EggsHatching(x.getTime(), x.getTypeId(), x.getName()));
                    }
                  }
                  String msg = MessageUtils.getMessageFromConfig("company-created");
                  msg = msg.replaceAll("%company%", args[2]);
                  msg = msg.replaceAll("&", "§");
                  Bukkit.broadcastMessage(msg);
                } else {
                  MessageUtils.sendMessage(sender, MessageUtils.getMessageFromConfig("company-exist"));
                }
              } catch (SQLException e) {
                e.printStackTrace();
              }
            } else {
              MessageUtils.sendMessage(sender, MessageUtils.getMessageFromConfig("company-owned"));
            }
          } else {
            MessageUtils.sendMessage(sender, MessageUtils.getMessageFromConfig("command-invalid"));
            displayHelp(sender, 1);
          }
        }
      }
      if (args[0].equalsIgnoreCase("publicite")) {
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "havent-company");
          return false;
        }
        if (args[1].isEmpty()) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "message-enter");
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
        String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());
        if (!cData.getGerant().equalsIgnoreCase(senderUsername) && !cData.getCoGerant().contains(senderUsername) && !cData.getCommunityManagers().contains(senderUsername)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          return false;
        }
        if (!this.pData.commandConfirmation) {
          MessageUtils.sendMessage((CommandSender)this.pSender, "§4Attention ! §cLa publicité coûtera §610$ §cpar joueurs connectés");
          MessageUtils.sendMessage((CommandSender)this.pSender, "§cRéutiliser la commande pour confirmer");
          this.pData.commandConfirmation = true;
          String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
          String message = StringUtils.join((Object[])msg, " ");
          this.pData.advertMessage = message;
          return false;
        }
        this.pData.commandConfirmation = false;
        double price = ((Bukkit.getServer().getOnlinePlayers()).size() * 10);
        if (this.pData.getMoney().doubleValue() >= price) {
          MessageUtils.sendMessage((CommandSender)this.pSender, "§aTransaction comfirmé §6coût : " + price + " $");
          this.pData.setMoney(this.pData.getMoney().subtract(BigDecimal.valueOf(price)));
          this.pData.advertMessage = this.pData.advertMessage.replaceAll("&", "§");
          Bukkit.broadcastMessage("§6[Publicité-" + cData.getCompanyName() + "] " + this.pData.advertMessage);
        } else {
          String msg = MessageUtils.getMessageFromConfig("not-necessary-money");
          msg = msg.replaceAll("&", "§");
          MessageUtils.sendMessage(sender, msg);
        }
      } else if (args[0].equalsIgnoreCase("announce")) {
        if (!CompanyData.Companies.containsKey(this.pData.companyName)) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
          return false;
        }
        if (args[1].isEmpty()) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "message-enter");
          return false;
        }
        CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
        String senderUsername = cData.getExactUsernameFromUsername(pSender.getName());

        if (cData.getGerant().equalsIgnoreCase(senderUsername) || cData.getCoGerant().contains(senderUsername) || cData.getCommunityManagers().contains(senderUsername) || cData.getSecretaires().contains(senderUsername)) {
          String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
          cData.broadcastCompany(msg);
        } else {
          MessageUtils.sendMessageFromConfig(sender, "company-less-permission");
        }
      }
      if (args[0].equals("disband"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
            cData.leaveAll();
            if (this.pSender.getWorld().getName().equalsIgnoreCase("world1"))
              PlayerUtils.teleportToSpawn(this.pSender);
            try {
              Main.Database.update(RecordBuilder.build().delete("company_data").where(new CustomEntry("Name", cData.getCompanyName())).toString());
            } catch (SQLException e) {
              e.printStackTrace();
            }
            CompanyData.Companies.remove(cData.getCompanyName());
            MessageUtils.sendMessageFromConfig(sender, "company-disband");
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          }
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
        }
      if (args[0].equals("remunerate"))
        if (CompanyData.Companies.containsKey(this.pData.companyName)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(this.pData.companyName);
          if (cData.getGerant().equalsIgnoreCase(this.pSender.getName())) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - cData.lastAttributiontime.getTime() >= 86400000L) {
              cData.divideSalary();
            } else {
              int elapsedSc = 86400 - (int)((now.getTime() - cData.lastAttributiontime.getTime()) / 1000.0D);
              String timeleft = TimerUtils.formatString(elapsedSc);
              this.pSender.sendMessage("Vous devez attendre " + timeleft + " avant de rémunérer vos salariés.");
            }
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-less-permission");
          }
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "company-havent");
        }
      if (args[0].equals("elevage") && this.pSender.hasPermission("cylrp.company.eggspnj") &&
        args[1].equals("egg") &&
        args[2] != null && args[3] != null) {
        String name = args[2];
        String playername = args[3];
        if (Bukkit.getPlayerExact(playername) != null) {
          CompanyElevage.XP_ELEVAGE x = CompanyElevage.XP_ELEVAGE.getEnumByEnumName(name);
          if (x != null) {
            Bukkit.getPlayerExact(playername).getInventory().addItem(ItemUtils.getItem(x.getId(), x.getData().byteValue(), 1, x.getName(), null));
            this.pSender.sendMessage(ChatColor.GREEN + x.getName() + " Envoyé à " + ChatColor.DARK_GREEN + playername);
          }
        }
      }
      if (args[0].equals("achievements")) {
        String name = args[1];
        if (CompanyData.Companies.containsKey(name)) {
          CompanyData cData = (CompanyData)CompanyData.Companies.get(name);
          cData.printAchievements(this.pSender);
        } else {
          MessageUtils.sendMessageFromConfig(sender, "company-unknown");
        }
      }
      if (args[0].equalsIgnoreCase("eggspnj")) {
        if (!sender.hasPermission("cylrp.company.eggspnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        if (args[1].equalsIgnoreCase("spawn")) {
          new EntityEggsPnj(this.pSender);
        } else if (args[1].equalsIgnoreCase("delete")) {
          PnjsEggsData.removePnjLooking(this.pSender);
        } else if (args[1].equalsIgnoreCase("list")) {
          if (args[2].equalsIgnoreCase("page")) {
            int page = Integer.parseInt(args[3]);
            PnjsEggsData.printPnjList(page);
          }
        } else if (args[1].equalsIgnoreCase("teleport")) {
          int id = Integer.parseInt(args[2]);
          this.pSender.teleport(PnjsEggsData.getEntities(id));
          this.pSender.sendMessage(ChatColor.GREEN + "Téléportation réussie !");
        }
      }
      if (args[0].equalsIgnoreCase("shop")) {
        if (args[1].equalsIgnoreCase("sell")) {
          if (args[2].equalsIgnoreCase("item") &&
            !args[3].isEmpty()) {
            int quantite = Integer.parseInt(args[3]);
            TempActionShop.runAction(this.pSender, quantite);
          }
          return true;
        }
        if (!sender.hasPermission("cylrp.company.sellpnj")) {
          MessageUtils.sendMessageFromConfig((CommandSender)this.pSender, "cylrp-not-permission");
          return false;
        }
        if (args[1].equalsIgnoreCase("list")) {
          if (args[2].equalsIgnoreCase("page")) {
            int page = Integer.parseInt(args[3]);
            Npc.printPnjList(page, this.pSender);
          }
        } else if (args[1].equalsIgnoreCase("add")) {
          if (args[2].equalsIgnoreCase("item")) {
            if (args[3] != null && args[4] != null && args[5] != null && args[6] != null) {
              int id = Integer.parseInt(args[3].split(":")[0]);
              byte data = 0;
              if ((args[3].split(":")).length > 1)
                data = Byte.parseByte(args[3].split(":")[1]);
              double buy_price = Double.parseDouble(args[4]);
              double sell_price = Double.parseDouble(args[5]);
              int slot = Integer.parseInt(args[6]);
              Shop.addItem(this.pSender, new ItemShop(id, data, buy_price, sell_price, slot));
            }
          } else if (args[2].equalsIgnoreCase("npc") && (
            !args[3].isEmpty() || !args[4].isEmpty() || !args[5].isEmpty() || !args[6].isEmpty())) {
            String npcName = args[3];
            String npcShop = args[4];
            String type = args[5];
            String Categorie = args[6];
            Npc.addNpc(this.pSender, npcName, npcShop, type, Categorie);
          }
        } else if (args[1].equalsIgnoreCase("delete")) {
          if (args[2].equalsIgnoreCase("item")) {
            if (!args[3].isEmpty()) {
              int slot = Integer.parseInt(args[3]);
              Shop.removeItem(this.pSender, slot);
            }
          } else if (args[2].equalsIgnoreCase("npc") &&
            !args[3].isEmpty()) {
            String name = args[3];
            Npc.deleteNpc(this.pSender, name);
          }
        }
      }
    }
    return false;
  }

  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Entreprise commands 1/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise help <Page> §7- §aAffiche la liste des commandes");
      sender.sendMessage("● §c/entreprise help §7- §aAffiche la liste des commandes");
      sender.sendMessage("● §c/entreprise leave §7- §aPermet de quitter son entreprise");
      sender.sendMessage("● §c/entreprise invite <Pseudo> §7- §aInviter un joueur dans son entreprise");
      sender.sendMessage("● §c/entreprise join <Nom Entreprise> §7- §aRejoindre un entreprise si la requête existe");
    } else if (page == 2) {
      sender.sendMessage("§b【Entreprise commands 2/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise join <Pseudo> §7- §aRejoindre un entreprise à partir du nom de l'inviteur si la requête existe");
      sender.sendMessage("● §c/entreprise kick <Pseudo> §7- §aVirer un joueur de son entreprise");
      sender.sendMessage("● §c/entreprise publicite <Message> §7- §aFaire une publicité dans le chat");
      sender.sendMessage("● §c/entreprise setrank <Pseudo> <Rang> §7- §aPermet d'attribuer un rang à un joueur dans son entreprise");
    } else if (page == 3) {
      sender.sendMessage("§b【Entreprise commands 3/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise salaire <Rang> <Repartition en pourcentage> §7- §aPermet de répartir le pourcentage du salaire total qui sera fournie en fin de semaine");
      sender.sendMessage("● §c/entreprise shop list page <page> §7- §aAffiche la liste des pnj marchand");
      sender.sendMessage("● §c/entreprise shop add item <id> <prix achat> <prix vente> <slot> §7- §aAjoute un item dans le pnj le plus proche de vous");
      sender.sendMessage("● §c/entreprise announce <message> §7- §aPermet de faire une annonce");
      sender.sendMessage("● §c/entreprise rank list §7- §aPermet d'avoir la liste des grades");
    } else if (page == 4) {
      sender.sendMessage("§b【Entreprise commands 4/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise salaire list §7- §aAffiche la répartition en pourcentage pour chaque grades");
      sender.sendMessage("● §c/entreprise create <pseudo> <nom entreprise> <categorie> §7- §aCrée une entreprise");
    } else if (page == 5) {
      sender.sendMessage("§b【Entreprise commands 5/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise shop add npc <name> <guiname> <job/company> <categorie> §7- §aPermet de placer un pnj de marché à votre location");
      sender.sendMessage("● §c/entreprise shop delete item <slot> §7- §aSupprime un item dans le pnj marchand au slot indiqué");
      sender.sendMessage("● §c/entreprise shop delete npc <name> §7- §aSupprime le pnj indiqué par le nom");
    } else if (page == 6) {
      sender.sendMessage("§b【Entreprise commands 6/8】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise shop sell item <quantité> §7- §amet l'item en main en vente dans le pnj le plus proche");
      sender.sendMessage("● §c/entreprise achievements <nomEntreprise> §7- §aAffiche les succès de l'entreprise indiqué");
      sender.sendMessage("● §c/entreprise eggspnj spawn §7- §aCrée le pnj pour voir les oeufs disponible (Eleveur)");
      sender.sendMessage("● §c/entreprise eggspnj delete §7- §aSupprime le pnj pour voir les oeufs disponible (Etre à proximité)");
      sender.sendMessage("● §c/entreprise eggspnj teleport <id> §7- §aTéléporte au pnj");
    } else if (page == 7) {
      sender.sendMessage("§b【Entreprise commands 7/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise eggspnj list page <page> §7- §aAffiche la liste des pnj éleveur");
      sender.sendMessage("● §c/entreprise disband §7- §aPermet de dissoudre son entreprise");
      sender.sendMessage("● §c/entreprise members §7- §aAffiche la liste des membres");
      sender.sendMessage("● §c/entreprise info §7- §aAfficher les données de l'entreprise");
    } else if (page == 8) {
      sender.sendMessage("§b【Entreprise commands 8/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/entreprise elevage egg <nom de l'oeuf> <pseudo> §7- §aGive un oeuf au joueur en question");
      sender.sendMessage("● §c/entreprise remunerate  §7- §aRépartir le revenu de l'entreprise à vos salariés.");
    }
  }
  
  public void displayHelpAlias(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Entreprise commands 1/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent h <Page> §7- §aAffiche la liste des commandes");
      sender.sendMessage("● §c/ent lv §7- §aPermet de quitter son entreprise");
      sender.sendMessage("● §c/ent inv <Pseudo> §7- §aInviter un joueur dans son entreprise");
      sender.sendMessage("● §c/ent jn <Nom Entreprise> §7- §aRejoindre un entreprise si la requête existe");
    } else if (page == 2) {
      sender.sendMessage("§b【Entreprise commands 2/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent jn <Pseudo> §7- §aRejoindre un entreprise à partir du nom de l'inviteur si la requête existe");
      sender.sendMessage("● §c/ent kck <Pseudo> §7- §aVirer un joueur de son entreprise");
      sender.sendMessage("● §c/ent pub <Message> §7- §aFaire une publicité dans le chat");
      sender.sendMessage("● §c/ent setrk <Pseudo> <Rang> §7- §aPermet d'attribuer un rang à un joueur dans son entreprise");
    } else if (page == 3) {
      sender.sendMessage("§b【Entreprise commands 3/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent slp <page> §7- §aAffiche la liste des pnj marchand");
      sender.sendMessage("● §c/ent sai <id> <prix achat> <prix vente> <slot> §7- §aAjoute un item dans le pnj le plus proche de vous");
      sender.sendMessage("● §c/ent ann <message> §7- §aPermet de faire une annonce");
      sender.sendMessage("● §c/ent rl §7- §aPermet d'avoir la liste des grades");
    } else if (page == 4) {
      sender.sendMessage("§b【Entreprise commands 4/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent sl §7- §aAffiche la répartition en pourcentage pour chaque grades");
      sender.sendMessage("● §c/ent crte <pseudo> <nom entreprise> <categorie> §7- §aCrée une entreprise");
    } else if (page == 5) {
      sender.sendMessage("§b【Entreprise commands 5/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent san <name> <guiname> <job/company> <categorie> §7- §aPermet de placer un pnj de marché à votre location");
      sender.sendMessage("● §c/ent sdi <slot> §7- §aSupprime un item dans le pnj marchand au slot indiqué");
      sender.sendMessage("● §c/ent sdn <name> §7- §aSupprime le pnj indiqué par le nom");
    } else if (page == 6) {
      sender.sendMessage("§b【Entreprise commands 6/8】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent ssi <quantité> §7- §amet l'item en main en vente dans le pnj le plus proche");
      sender.sendMessage("● §c/ent achv <nomEntreprise> §7- §aAffiche les succès de l'entreprise indiqué");
      sender.sendMessage("● §c/ent eps §7- §aCrée le pnj pour voir les oeufs disponible (Eleveur)");
      sender.sendMessage("● §c/ent epd §7- §aSupprime le pnj pour voir les oeufs disponible (Etre à proximité)");
      sender.sendMessage("● §c/ent ept <id> §7- §aTéléporte au pnj");
    } else if (page == 7) {
      sender.sendMessage("§b【Entreprise commands 7/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent eplp <page> §7- §aAffiche la liste des pnj éleveur");
      sender.sendMessage("● §c/ent disb §7- §aPermet de dissoudre son entreprise");
      sender.sendMessage("● §c/ent mbs §7- §aAffiche la liste des membres");
      sender.sendMessage("● §c/ent info §7- §aAfficher les données de l'entreprise");
    } else if (page == 8) {
      sender.sendMessage("§b【Entreprise commands 8/8 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/ent elvegg <nom de l'oeuf> <pseudo> §7- §aGive un oeuf au joueur en question");
      sender.sendMessage("● §c/ent remun §7- §aRépartir le revenu de l'entreprise à vos salariés.");
    } 
  }
  
  public void displayRankList(CommandSender sender) {
    sender.sendMessage("§7► §bEntreprise Rangs §7◄");
    sender.sendMessage("§fCoGerant ◊ CommunityManager ◊ Secretaire ◊ Stagiaire ◊ Salarie");
  }
}
