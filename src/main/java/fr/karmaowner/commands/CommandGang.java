package fr.karmaowner.commands;

import fr.karmaowner.chat.Chat;
import fr.karmaowner.chat.group.Gang;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.Request;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.gangs.Capture;
import fr.karmaowner.utils.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGang implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
    final Player p = (Player)sender;
    final PlayerData pData = PlayerData.getPlayerData(p.getName());
    GangData g = null;
    if (pData.gangName != null)
      g = GangData.getGang(pData.gangName); 
    if (label.equalsIgnoreCase("g")) {
      if (args.length == 0) {
        displayHelpAlias(sender, 1);
        return false;
      } 
      if (args[0].equalsIgnoreCase("addc")) {
        if (!sender.hasPermission("cylrp.gang.createcapture")) {
          MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
          return false;
        } 
        String rgName = args[2];
        Capture.captures.put(rgName, new Capture(p, rgName));
      } 
      if (args[0].equalsIgnoreCase("aly"))
        if (args[1].equalsIgnoreCase("ad")) {
          if (g != null) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              String gangname = args[2];
              if (gangname != null) {
                GangData gdata = GangData.getGang(gangname);
                if (gdata != null) {
                  if (!g.getAllies().contains(gangname)) {
                    if (!g.getEnnemies().contains(gangname)) {
                      if (gdata.getConnectedUsers() > 0) {
                        if (Request.findRequest(Request.RequestType.GANGALLY,g.getGangName(), gangname) == null) {
                          MessageUtils.sendMessage(p, "§aRequête d'alliance envoyé au gang §4" + gangname);
                          Request.createRequest(Request.RequestType.GANGALLY, g.getGangName(), gangname);
                        } else {
                          MessageUtils.sendMessage((CommandSender)p, "§cUne requête existe déjà");
                        } 
                      } else {
                        MessageUtils.sendMessage((CommandSender)p, "§cAucun membre de ce gang n'est connecté inutile d'envoyer la requête");
                      } 
                    } else {
                      MessageUtils.sendMessage((CommandSender)p, "§cCe Gang est votre ennemie");
                    } 
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cCe Gang est déjà votre allié");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
                } 
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("accpt")) {
          if (g != null) {
            String requestname = args[2];
            if (requestname != null) {
              Request request = Request.findRequest(Request.RequestType.GANGALLY, requestname, g.getGangName());
              if (request != null) {
                if (g.isHighRank(g.getExactUsername(p.getName()))) {
                  GangData senderGang = GangData.getGangData(request.getSender());
                  if (senderGang != null) {
                    g.addAlly(senderGang.getGangName());
                    senderGang.addAlly(g.getGangName());
                    g.sendMessageAll("§6Le gang §2" + senderGang.getGangName() + "§6 est désormais allié");
                    senderGang.sendMessageAll("§6Le gang §2" + g.getGangName() + "§6 a changé son alignement: §2Allié");
                    request.destroy();
                  }
                  else
                  {
                    MessageUtils.sendMessage(sender,"§cRequête défaillante veuillez réessayer.");
                  }
                } else {
                  MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
                }
              } else {
                MessageUtils.sendMessageFromConfig(sender, "unknown-request");
              }
            } else {
              MessageUtils.sendMessage((CommandSender)p, "§cVeuillez indiqué l'émetteur de la requête (nom du gang ou joueur)");
            }
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          }
        } else if (args[1].equalsIgnoreCase("rm")) {
          if (g != null) {
            String gangname = args[2];
            if (gangname != null) {
              if (g.isHighRank(g.getExactUsername(p.getName())) &&
                g.getAllies().contains(gangname)) {
                GangData gData = GangData.getGangData(gangname);
                if (gData != null) {
                  if (g.getAllies().contains(gangname)) {
                    g.deleteAlly(gangname);
                    gData.deleteAlly(g.getGangName());
                    g.sendMessageAll("§6Le gang §f" + gangname + "§6 n'est plus allié");
                    gData.sendMessageAll("§6Le gang §f" + g.getGangName() + "§6 a changé son alignement: §fNeutre");
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'est pas allié");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
                } 
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("ls")) {
          if (g != null) {
            p.sendMessage("§a-------Gangs alliés-------");
            if (g.getAllies().isEmpty())
              p.sendMessage("§2Aucun"); 
            for (String gangname : g.getAllies())
              p.sendMessage("§2" + gangname); 
            p.sendMessage("§a------------------------");
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        }  
      if (args[0].equalsIgnoreCase("enmy"))
        if (args[1].equalsIgnoreCase("ad")) {
          if (g != null) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              String gangname = args[2];
              if (gangname != null) {
                GangData gData = GangData.getGangData(gangname);
                if (gData != null) {
                  if (!g.getAllies().contains(gangname)) {
                    if (!g.getEnnemies().contains(gangname)) {
                      g.addEnemy(gangname);
                      gData.addEnemy(g.getGangName());
                      g.sendMessageAll("§6Le gang §4" + gangname + "§6 est désormais un ennemi juré");
                      gData.sendMessageAll("§6Le gang §4" + g.getGangName() + "§6 a changé son alignement: §4Ennemi");
                    } else {
                      MessageUtils.sendMessage((CommandSender)p, "§cCe Gang est déjà un ennemi juré");
                    } 
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cImpossible Ce Gang est allié");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
                } 
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("rm")) {
          if (g != null) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              String gangname = args[2];
              if (gangname != null) {
                GangData gData = GangData.getGangData(gangname);
                if (gData != null) {
                  if (g.getEnnemies().contains(gangname)) {
                    g.deleteEnemy(gangname);
                    g.sendMessageAll("§6Le gang §f" + gangname + "§6 n'est plus ennemi");
                    gData.sendMessageAll("§6Le gang §f" + g.getGangName() + "§6 a changé son alignement: §fNeutre");
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'est pas ennemi");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
                } 
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("ls")) {
          if (g != null) {
            p.sendMessage("§c------Gangs ennemies------");
            if (g.getEnnemies().isEmpty())
              p.sendMessage("§4Aucun"); 
            for (String gangname : g.getEnnemies())
              p.sendMessage("§4" + gangname); 
            p.sendMessage("§c-------------------------");
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        }  
      if (args[0].equalsIgnoreCase("s"))
        if (g != null) {
          if (args.length == 3) {
            if (args[1].equalsIgnoreCase("c")) {
              String targetPlayer = args[2];
              if (targetPlayer.isEmpty()) {
                MessageUtils.sendMessage(sender, "§cEntrez un pseudo");
                return false;
              } 
              if (targetPlayer.equals(sender.getName())) {
                MessageUtils.sendMessage(sender, "§cVous ne pouvez pas vous mettre chef");
                return false;
              }
              targetPlayer = g.getExactUsername(targetPlayer);
              if (g.getUsers().contains(targetPlayer)) {
                String oldChef = g.getChef();
                if (!oldChef.equalsIgnoreCase(sender.getName())) {
                  sender.sendMessage("§cVous n'avez pas la permission");
                  return false;
                } 
                g.sendMessageAll("§c" + oldChef + " §4donne le lead à " + targetPlayer);
                g.removeUser(targetPlayer);
                g.setChef(targetPlayer);
                g.setMembre(oldChef);
              } else {
                sender.sendMessage("§cCe joueur n'est pas membre de votre gang");
              } 
            } 
          } else {
            MessageUtils.sendMessage(sender, "§c/g s c [<pseudo>]");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        }  
      if (args[0].equalsIgnoreCase("clsmt"))
        new TaskCreator(new CustomRunnable() {
              public void customRun() {
                if (Bukkit.getPlayerExact(p.getName()) == null) {
                  cancel();
                  return;
                } 
                int page = 1;
                if (args.length == 2)
                  page = (Integer.parseInt(args[1]) == 0) ? 1 : Integer.parseInt(args[1]); 
                p.sendMessage("§6Chargement du classement...");
                ArrayList<String> list = new ArrayList<>();
                try {
                  list = GangData.loadRanking();
                } catch (SQLException e) {
                  e.printStackTrace();
                } 
                int perPage = 10;
                int pageTotal = (int)Math.floor(list.size() / (float) perPage);
                if (page < 0 || page > pageTotal) {
                  p.sendMessage("§4Page incorrect. Nombre total de page §c" + pageTotal);
                } else {
                  p.sendMessage("§5§ka §6Résultat: §5§ka");
                  p.sendMessage("§4Filtre: Points de capture");
                  p.sendMessage("§c---------------------------");
                  p.sendMessage("§4page §6" + page + "/" + pageTotal);
                  int i;
                  for (i = (page - 1) * perPage; i < page * perPage; i++) {
                    String[] gang = ((String)list.get(i)).split("§");
                    String gangname = gang[0];
                    String points = gang[1];
                    p.sendMessage("§6" + (i + 1) + "- " + gangname + " : §4" + points + " points");
                  } 
                  p.sendMessage("§4page §6" + page + "/" + pageTotal);
                  p.sendMessage("§c---------------------------");
                  if (pData.gangName != null && !pData.gangName.equals("")) {
                    p.sendMessage("§2Votre gang:");
                    i = 0;
                    for (String g : list) {
                      String[] gang = g.split("§");
                      String gangname = gang[0];
                      String points = gang[1];
                      if (gangname.equals(pData.gangName)) {
                        p.sendMessage("§a" + (i + 1) + "- " + gangname + " : §2" + points + " points");
                        break;
                      } 
                      i++;
                    } 
                  } 
                } 
              }
            }, true, 0L); 
      if (args[0].equalsIgnoreCase("addschef")) {
        String name = args[1];
        if (g != null) {
          name = g.getExactUsername(name);
          if (g.isHighRank(g.getExactUsername(p.getName()))) {
            if (g.isUserMember(name)) {
              if (g.setSousChef(name)) {
                p.sendMessage(ChatColor.GREEN + "Vous venez de l'ajouter sous-chef");
                Bukkit.getPlayerExact(name).sendMessage(ChatColor.GREEN + "Vous avez été promu Sous-chef !");
              } else {
                p.sendMessage(ChatColor.RED + "Impossible de le promouvoir Sous-Chef en raison de son rang ou parcequ'il l'est déjà.");
              } 
              MessageUtils.sendMessageFromConfig(sender, "player-now-sous-chef");
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-player-not-gang");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } 
      if (args[0].equalsIgnoreCase("info"))
        if (GangData.GANGS.containsKey(pData.gangName)) {
          GangData gData = GangData.getGangData(pData.gangName);
          sender.sendMessage("§6--------------------------------");
          sender.sendMessage("§6Nom: §e" + gData.getGangName());
          sender.sendMessage("§6Nombre de points de rang: §e" + gData.getRankingPoints());
          sender.sendMessage("§6Nombre de membres: §e" + gData.getUsers().size());
          sender.sendMessage("§6--------------------------------");
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        }  
      if (args[0].equalsIgnoreCase("addm")) {
        String name = args[1];
        if (g != null) {
          if (g.isHighRank(g.getExactUsername(p.getName()))) {
            if (g.isUserMember(name)) {
              if (g.setMembre(name)) {
                p.sendMessage(ChatColor.GREEN + "Vous venez de l'ajouter Membre");
                if (Bukkit.getPlayerExact(name) != null)
                  Bukkit.getPlayerExact(name).sendMessage(ChatColor.GREEN + "Vous avez été promu Membre !"); 
              } else {
                p.sendMessage(ChatColor.RED + "Impossible de le promouvoir Membre en raison de son rang ou parcequ'il l'est déjà.");
              } 
              MessageUtils.sendMessageFromConfig((CommandSender)p, "gang-player-now-member");
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-player-not-gang");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } 
      if (args[0].equalsIgnoreCase("delc")) {
        if (!sender.hasPermission("cylrp.gang.deletecapture")) {
          MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
          return false;
        } 
        String rgName = args[1];
        Capture.deleteCapture(p, rgName);
      } 
      if (args[0].equalsIgnoreCase("crte")) {

        String gangName = args[1];

        PlayerData targetData = PlayerData.getPlayerData(sender.getName());

        if (targetData.gangName == null) {
          try {
            SqlCollection result = Main.Database.select(RecordBuilder.build().selectAll("Gang_data", new CustomEntry("Name", gangName)).toString());
            if (result.count() == 0) {
              if (targetData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste) {
                GangData gang = new GangData(gangName, p);
                Bukkit.broadcastMessage(Main.prefix + ChatColor.RED + " Le gang " + ChatColor.DARK_RED + gangName + ChatColor.RED + " vient d'être créé");
                MessageUtils.sendMessageFromConfig(p, "gang-create-successfuly");
              } else {
                MessageUtils.sendMessageFromConfig(p, "gang-bad-rank");
              }
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-already-exist");
            }
          } catch (SQLException e) {
            e.printStackTrace();
          }
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-already-in");
        }

      } 
      if (args[0].equalsIgnoreCase("inv"))
        if (g != null) {
          String name = args[1];
          name = g.getExactUsername(name);
          if (!g.isUserMember(name)) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              if (PlayerUtils.getPlayer(args[1]) != null) {
                GangData gdata = GangData.getGang(pData.gangName);
                if (gdata.getUsers().size() >= 10) {
                  p.sendMessage(ChatColor.RED + "Vous avez dépassé la limite de membres autorisé qui s'élève à " + ChatColor.DARK_RED + '\n');
                  return false;
                } 
                Request.createRequest(Request.RequestType.GANGINVITE, p.getName(), args[1]);
              } else {
                MessageUtils.sendMessageFromConfig(sender, "user-invalid");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-player-already-in-gang");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        }  
      if (args[0].equalsIgnoreCase("disb"))
        if (args.length == 2) {
          if (p.hasPermission("cylrp.admin")) {
            String GangName = args[1];
            GangData gData = GangData.getGangData(GangName);
            if (gData != null) {
              try {
                gData.destroy();
              } catch (Exception e) {
                e.printStackTrace();
              } 
              MessageUtils.sendMessage((CommandSender)p, "§aLe gang §2" + GangName + "§a vient d'être dissous");
            } else {
              MessageUtils.sendMessage((CommandSender)p, "Ce gang n'existe pas");
            } 
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas la permission d'exécuter cette commande");
          } 
        } else if (g != null) {
          if (g.getChef() != null) {
            if (g.getChef().equalsIgnoreCase(p.getName())) {
              if (!Capture.isCaptureExist((pData.getActuallyRegion() == null) ? null : pData.getActuallyRegion().getId())) {
                try {
                  g.destroy();
                  MessageUtils.sendMessageFromConfig(sender, "gang-disbanded");
                } catch (Exception e) {
                  e.printStackTrace();
                } 
              } else {
                p.sendMessage(ChatColor.RED + "Vous ne pouvez pas dissoudre votre gang car vous êtes dans une zone de capture");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Il n'y a aucun chef dans votre gang, il se pourrait qu'un bug se soit manifesté.Vous pouvez forcer pour quitter le gang en executant la commande /gang leave");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        }  
      if (args[0].equalsIgnoreCase("kck")) {
        String name = args[1];
        if (g != null) {
          name = g.getExactUsername(name);
          if (g.isHighRank(g.getExactUsername(p.getName()))) {
            if (g.isUserMember(name)) {
              if (!g.getChef().equals(name)) {
                PlayerData data = PlayerData.getPlayerData(name);
                if (g.getChef().equalsIgnoreCase(p.getName()) || !g.isHighRank(name)) {
                  if (!Capture.isCaptureExist((pData.getActuallyRegion() == null) ? null : pData.getActuallyRegion().getId()) && 
                    !Capture.isCaptureExist((data.getActuallyRegion() == null) ? null : data.getActuallyRegion().getId())) {
                    if (Bukkit.getPlayerExact(args[1]) != null)
                      Chat.leftFromCanal(name); 
                    g.removeUser(name);
                    data.gangName = null;
                    if (Bukkit.getPlayerExact(args[1]) != null)
                      Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.GREEN + "Vous avez été kick du gang");
                    MessageUtils.sendMessageFromConfig(sender, "gang-player-kicked");
                  } else {
                    p.sendMessage(ChatColor.RED + "Vous ne pouvez pas kick ce joueur car vous êtes dans une zone de capture ou votremembre l'est.");
                  } 
                } else {
                  MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
                } 
              } else {
                MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-player-not-in");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } 
      if (args[0].equalsIgnoreCase("lv"))
        if (g != null) {
          if (g.getChef() != null) {
            if (!g.getChef().equalsIgnoreCase(p.getName())) {
              if (!Capture.isCaptureExist((pData.getActuallyRegion() == null) ? null : pData.getActuallyRegion().getId())) {
                g.removeUser(g.getExactUsername(p.getName()));
                Chat.leftFromCanal(p.getName());
                pData.gangName = null;
                MessageUtils.sendMessageFromConfig(sender, "gang-not-appartenance");
              } else {
                p.sendMessage(ChatColor.RED + "Vous ne pouvez pas leave ce gang car vous êtes dans une zone de capture");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-cannot-quit");
            } 
          } else {
            pData.gangName = null;
            Chat.leftFromCanal(p.getName());
            MessageUtils.sendMessageFromConfig(sender, "gang-not-appartenance");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        }  
      if (args[0].equalsIgnoreCase("jn"))
        if (g == null) {
          Request request = Request.findRequest(Request.RequestType.GANGINVITE,args[1],sender.getName());
          if (request != null) {
            if (pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste || pData.selectedJob instanceof fr.karmaowner.jobs.BAC) {
              if (request.getType() == Request.RequestType.GANGINVITE) {
                GangData data = GangData.getGangData(PlayerData.getPlayerData(request.getSender()).getGangName());
                if (data.getUsers().size() >= 10) {
                  p.sendMessage(ChatColor.RED + "Ce gang à trop de membre. Vous ne pouvez pas le rejoindre. ");
                  return false;
                } 
                data.getMembres().add(p.getName());
                pData.gangName = data.getGangName();
                request.destroy();
                String msg = MessageUtils.getMessageFromConfig("gang-joined");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%gang%", pData.gangName);
                MessageUtils.sendMessage(sender, msg);
              } else {
                MessageUtils.sendMessageFromConfig(sender, "gang-not-request");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-bad-rank");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "unknown-request");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-already-in");
        }  
      if (args[0].equalsIgnoreCase("mbs"))
        if (g != null) {
          g.printMembers(p);
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-not-appartenance");
        }  
      if (args[0].equalsIgnoreCase("h")) {
        if(args.length == 2){
          int page = Integer.parseInt(args[1]);
          displayHelpAlias(sender, page);
        }
        else
        {
          displayHelpAlias(sender, 1);
        }
      } else {
        displayHelpAlias(sender, 1);
      }
    } else if (cmd.getName().equalsIgnoreCase("gang")) {
      if (args.length == 0) {
        displayHelp(sender, 1);
        return false;
      } 
      if (args[0].equalsIgnoreCase("add")) {
        if (args[1].equalsIgnoreCase("capture")) {
          if (!sender.hasPermission("cylrp.gang.createcapture")) {
            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
            return false;
          } 
          String rgName = args[2];
          Capture.captures.put(rgName, new Capture(p, rgName));
        } else if (args[1].equalsIgnoreCase("sousChef")) {
          String name = args[2];
          if (g != null) {
            name = g.getExactUsername(name);
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              if (g.isUserMember(name)) {
                if (g.setSousChef(name)) {
                  p.sendMessage(ChatColor.GREEN + "Vous venez de l'ajouter sous-chef");
                  Bukkit.getPlayerExact(name).sendMessage(ChatColor.GREEN + "Vous avez été promu Sous-chef !");
                } else {
                  p.sendMessage(ChatColor.RED + "Impossible de le promouvoir Sous-Chef en raison de son rang ou parcequ'il l'est déjà.");
                } 
                MessageUtils.sendMessageFromConfig(sender, "player-now-sous-chef");
              } else {
                MessageUtils.sendMessageFromConfig(sender, "gang-player-not-gang");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("Membre")) {
          String name = args[2];
          if (g != null) {
            name = g.getExactUsername(name);
            String senderExactName = g.getExactUsername(p.getName());
            if (g.isHighRank(senderExactName)) {
              if (g.isUserMember(name)) {
                if (!g.rankNameUser(senderExactName).equals(GangData.RANKS.SOUSCHEF.getRankName()) || !g.rankNameUser(name).equals(GangData.RANKS.SOUSCHEF.getRankName())) {
                  if (g.setMembre(name)) {
                    p.sendMessage(ChatColor.GREEN + "Vous venez de l'ajouter Membre");
                    Bukkit.getPlayerExact(name).sendMessage(ChatColor.GREEN + "Vous avez été promu Membre !");
                  } else {
                    p.sendMessage(ChatColor.RED + "Impossible de le promouvoir Membre en raison de son rang ou parcequ'il l'est déjà.");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "Vous ne pouvez pas promouvoir un sous-Chef avec votre grade");
                } 
              } else {
                MessageUtils.sendMessageFromConfig(sender, "gang-player-not-gang");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } 
      } else if (args[0].equalsIgnoreCase("set")) {
        if (g != null) {
          if (args.length == 3) {
            if (args[1].equalsIgnoreCase("chef")) {
              String targetPlayer = args[2];
              if (targetPlayer.isEmpty()) {
                MessageUtils.sendMessage(sender, "§cEntrez un pseudo");
                return false;
              } 
              if (targetPlayer.equals(sender.getName())) {
                MessageUtils.sendMessage(sender, "§cVous ne pouvez pas vous mettre chef");
                return false;
              }
              targetPlayer = g.getExactUsername(targetPlayer);
              if (g.getUsers().contains(targetPlayer)) {
                String oldChef = g.getChef();
                if (!oldChef.equalsIgnoreCase(sender.getName())) {
                  sender.sendMessage("§cVous n'avez pas la permission");
                  return false;
                } 
                g.sendMessageAll("§c" + oldChef + " §4donne le lead à " + targetPlayer);
                g.removeUser(targetPlayer);
                g.setChef(targetPlayer);
                g.setMembre(oldChef);
              } else {
                sender.sendMessage("§cCe joueur n'est pas membre de votre gang");
              } 
            } 
          } else {
            MessageUtils.sendMessage(sender, "§c/gang set chef [<pseudo>]");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } else if (args[0].equalsIgnoreCase("delete")) {
        if (!sender.hasPermission("cylrp.gang.deletecapture")) {
          MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
          return false;
        } 
        if (args[1].equalsIgnoreCase("capture")) {
          String rgName = args[2];
          Capture.deleteCapture(p, rgName);
        } 
      } else if (args[0].equalsIgnoreCase("create")) {

        String gangName = args[1];

        PlayerData targetData = PlayerData.getPlayerData(sender.getName());

        if (targetData.gangName == null) {
            try {
              SqlCollection result = Main.Database.select(RecordBuilder.build().selectAll("Gang_data", new CustomEntry("Name", gangName)).toString());
              if (result.count() == 0) {
                if (targetData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste) {
                  GangData gang = new GangData(gangName, p);
                  Bukkit.broadcastMessage(Main.prefix + ChatColor.RED + " Le gang " + ChatColor.DARK_RED + gangName + ChatColor.RED + " vient d'être créé");
                  MessageUtils.sendMessageFromConfig(p, "gang-create-successfuly");
                } else {
                  MessageUtils.sendMessageFromConfig(p, "gang-bad-rank");
                } 
              } else {
                MessageUtils.sendMessageFromConfig(sender, "gang-already-exist");
              } 
            } catch (SQLException e) {
              e.printStackTrace();
            } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-already-in");
        }
      } else if (args[0].equalsIgnoreCase("ally")) {
        if (args[1].equalsIgnoreCase("add")) {
          if (g != null) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              String gangname = args[2];
              GangData gdata = GangData.getGang(gangname);
              if (gdata != null) {
                if (!g.getAllies().contains(gangname)) {

                  if (!g.getEnnemies().contains(gangname)) {

                    if (gdata.getConnectedUsers() > 0) {
                      if (Request.findRequest(Request.RequestType.GANGALLY, g.getGangName(), gangname) == null) {
                        MessageUtils.sendMessage(p, "§aRequête d'alliance envoyé au gang §4" + gangname);
                        Request.createRequest(Request.RequestType.GANGALLY, g.getGangName(), gangname);
                      } else {
                        MessageUtils.sendMessage((CommandSender)p, "§cUne requête existe déjà");
                      }
                    } else {
                      MessageUtils.sendMessage((CommandSender)p, "§cAucun membre de ce gang n'est connecté inutile d'envoyer la requête");
                    }
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cCe Gang est votre ennemie");
                  }
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang est déjà votre allié");
                }
              } else {
                MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
              }
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("accept")) {
          if (g != null) {
            String requestname = args[2];
            if (requestname != null) {
              Request request = Request.findRequest(Request.RequestType.GANGALLY, requestname, g.getGangName());
              if (request != null) {
                if (g.isHighRank(g.getExactUsername(p.getName()))) {
                  GangData senderGang = GangData.getGangData(request.getSender());
                  if (senderGang != null) {
                     g.addAlly(senderGang.getGangName());
                     senderGang.addAlly(g.getGangName());
                     g.sendMessageAll("§6Le gang §2" + senderGang.getGangName() + "§6 est désormais allié");
                     senderGang.sendMessageAll("§6Le gang §2" + g.getGangName() + "§6 a changé son alignement: §2Allié");
                     request.destroy();
                  }
                  else
                  {
                    MessageUtils.sendMessage(sender,"§cRequête défaillante veuillez réessayer.");
                  }
                } else {
                  MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
                }
              } else {
                MessageUtils.sendMessageFromConfig(sender, "unknown-request");
              } 
            } else {
              MessageUtils.sendMessage((CommandSender)p, "§cVeuillez indiqué l'émetteur de la requête (nom du gang ou joueur)");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("remove")) {
          if (g != null) {
            String gangname = args[2];
            if (gangname != null) {
              if (g.isHighRank(g.getExactUsername(p.getName())) &&
                g.getAllies().contains(gangname)) {
                GangData gData = GangData.getGangData(gangname);
                if (gData != null) {
                  if (g.getAllies().contains(gangname)) {
                    g.deleteAlly(gangname);
                    gData.deleteAlly(g.getGangName());
                    g.sendMessageAll("§6Le gang §f" + gangname + "§6 n'est plus allié");
                    gData.sendMessageAll("§6Le gang §f" + g.getGangName() + "§6 a changé son alignement: §fNeutre");
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'est pas allié");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
                } 
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("list")) {
          if (g != null) {
            p.sendMessage("§a-------Gangs alliés-------");
            if (g.getAllies().isEmpty())
              p.sendMessage("§2Aucun"); 
            for (String gangname : g.getAllies())
              p.sendMessage("§2" + gangname); 
            p.sendMessage("§a------------------------");
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } 
      } else if (args[0].equalsIgnoreCase("enemy")) {
        if (args[1].equalsIgnoreCase("add")) {
          if (g != null) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              String gangname = args[2];
              if (gangname != null) {
                GangData gData = GangData.getGangData(gangname);
                if (gData != null) {
                  if (!g.getAllies().contains(gangname)) {
                    if (!g.getEnnemies().contains(gangname)) {
                      g.addEnemy(gangname);
                      g.sendMessageAll("§6Le gang §4" + gangname + "§6 est désormais un ennemi juré");
                      gData.sendMessageAll("§6Le gang §4" + g.getGangName() + "§6 a changé son alignement: §4Ennemi");
                    } else {
                      MessageUtils.sendMessage((CommandSender)p, "§cCe Gang est déjà un ennemi juré");
                    } 
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cImpossible Ce Gang est allié");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
                } 
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("remove")) {
          if (g != null) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              String gangname = args[2];
              if (gangname != null) {
                GangData gData = GangData.getGangData(gangname);
                if (gData != null) {
                  if (g.getEnnemies().contains(gangname)) {
                    g.deleteEnemy(gangname);
                    g.sendMessageAll("§6Le gang §f" + gangname + "§6 n'est plus ennemi");
                    gData.sendMessageAll("§6Le gang §f" + g.getGangName() + "§6 a changé son alignement: §fNeutre");
                  } else {
                    MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'est pas ennemi");
                  } 
                } else {
                  MessageUtils.sendMessage((CommandSender)p, "§cCe Gang n'existe pas");
                } 
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } else if (args[1].equalsIgnoreCase("list")) {
          if (g != null) {
            p.sendMessage("§c------Gangs ennemies------");
            if (g.getEnnemies().isEmpty())
              p.sendMessage("§4Aucun"); 
            for (String gangname : g.getEnnemies())
              p.sendMessage("§4" + gangname); 
            p.sendMessage("§c-------------------------");
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-havent");
          } 
        } 
      } else if (args[0].equalsIgnoreCase("info")) {
        if (GangData.GANGS.containsKey(pData.gangName)) {
          GangData gData = GangData.getGangData(pData.gangName);
          sender.sendMessage("§6--------------------------------");
          sender.sendMessage("§6Nom: §e" + gData.getGangName());
          sender.sendMessage("§6Nombre de points de rang: §e" + gData.getRankingPoints());
          sender.sendMessage("§6Nombre de membres: §e" + gData.getUsers().size());
          sender.sendMessage("§6--------------------------------");
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } else if (args[0].equalsIgnoreCase("debug")) {
        if (p.hasPermission("cylrp.admin")) {
          String gangname = args[1];
          GangData data = GangData.getGangData(gangname);
          p.sendMessage("§c-----------------------------");
          p.sendMessage("§4Dans la hashmap = " + (GangData.GANGS.containsKey(gangname) ? "oui" : "non"));
          if (data != null) {
            p.sendMessage("§4Nom du gang = " + data.getGangName());
            p.sendMessage("§4Nombre de membres = " + data.getUsers().size());
            p.sendMessage("§4Nombre de membres connectés = " + data.getConnectedUsers());
            p.sendMessage("§4Nombre de points de rang = " + data.getRankingPoints());
            p.sendMessage("§4Chef du gang = " + data.getChef());
            StringJoiner joiner = new StringJoiner(", ");
            if (data.getMembres() != null) {
              for (String member : data.getMembres())
                joiner.add(member); 
              p.sendMessage("§4Membres du gang = " + joiner.toString());
            } else {
              p.sendMessage("§4Membres du gang = aucun");
            } 
            if (data.getSousChefs() != null) {
              joiner = new StringJoiner(", ");
              for (String schef : data.getSousChefs())
                joiner.add(schef); 
              p.sendMessage("§4Sous-chefs du gang = " + joiner.toString());
            } else {
              p.sendMessage("§4Sous-chefs du gang = aucun");
            } 
            if (data.getAllies() != null) {
              joiner = new StringJoiner(", ");
              for (String ally : data.getAllies())
                joiner.add(ally); 
              p.sendMessage("§4Les gangs alliés = " + joiner.toString());
            } else {
              p.sendMessage("§4Les gangs alliés = aucun");
            } 
            if (data.getEnnemies() != null) {
              joiner = new StringJoiner(", ");
              for (String enemy : data.getEnnemies())
                joiner.add(enemy); 
              p.sendMessage("§4Les gangs ennemies = " + joiner.toString());
            } else {
              p.sendMessage("§4Les gangs ennemies = aucun");
            } 
          } else {
            p.sendMessage("§4Les données de ce gang n'existe pas");
          } 
          p.sendMessage("§c-----------------------------");
        } 
      } else if (args[0].equalsIgnoreCase("setPoint")) {
        String gangName = args[1];
        int points = Integer.parseInt(args[2]);
        if (!p.hasPermission("cylrp.admin")) {
          MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas la permission d'exécuter cette commande");
          return true;
        } 
        GangData gData = GangData.getGangData(gangName);
        if (gData != null) {
          if (points > 0) {
            gData.setRankingPoints(points);
            MessageUtils.sendMessage((CommandSender)p, "§aLe gang §2" + gangName + "§a à désormais §2" + points + " points de rang");
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Vous devez renseigner un nombre de points > 0");
          } 
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Ce gang n'existe pas");
        } 
      } else if (args[0].equalsIgnoreCase("classement")) {
        if (args.length == 2 && args[1].equalsIgnoreCase("reset")) {
          if (sender.hasPermission("cylrp.admin")) {
            Main.resetRanking = true;
            sender.sendMessage("§aLe classement est entrain de se reset");
          } 
        } else {
          new TaskCreator(new CustomRunnable() {
                public void customRun() {
                  if (Bukkit.getPlayerExact(p.getName()) == null) {
                    cancel();
                    return;
                  } 
                  int page = 1;
                  if (args.length == 2) {
                    try {
                      page = (Integer.parseInt(args[1]) == 0) ? 1 : Integer.parseInt(args[1]);
                    } catch (Exception e) {
                      p.sendMessage("Un chiffre est attendu");
                    }
                  }
                  p.sendMessage("§6Chargement du classement...");
                  ArrayList<String> list = new ArrayList<>();
                  try {
                    list = GangData.loadRanking();
                  } catch (SQLException e) {
                    e.printStackTrace();
                  } 
                  int perPage = 10;
                  int pageTotal = (int)Math.ceil(list.size() / (float)perPage);
                  if (page > pageTotal) {
                    p.sendMessage("§4Page incorrect. Nombre total de page §c" + pageTotal);
                  } else {
                    p.sendMessage("§5§ka §6Résultat: §5§ka");
                    p.sendMessage("§4Filtre: Points de capture");
                    p.sendMessage("§c---------------------------");
                    p.sendMessage("§4page §6" + page + "/" + pageTotal);
                    int i;
                    for (i = (page - 1) * perPage; i < page * perPage; i++) {
                      if (i < list.size() && list.get(i) != null) {
                        String[] gang = ((String)list.get(i)).split("§");
                        String gangname = gang[0];
                        String points = gang[1];
                        p.sendMessage("§6" + (i + 1) + "- " + gangname + " : §4" + points + " points");
                      } 
                    } 
                    p.sendMessage("§4page §6" + page + "/" + pageTotal);
                    p.sendMessage("§c---------------------------");
                    if (pData.gangName != null && !pData.gangName.equals("")) {
                      p.sendMessage("§2Votre gang:");
                      i = 0;
                      for (String g : list) {
                        String[] gang = g.split("§");
                        String gangname = gang[0];
                        String points = gang[1];
                        if (gangname.equals(pData.gangName)) {
                          p.sendMessage("§a" + (i + 1) + "- " + gangname + " : §2" + points + " points");
                          break;
                        } 
                        i++;
                      } 
                    } 
                  } 
                }
              }, true, 0L);
        } 
      } else if (args[0].equalsIgnoreCase("invite")) {
        if (g != null) {
          String name = args[1];
          name = g.getExactUsername(name);
          if (!g.isUserMember(name)) {
            if (g.isHighRank(g.getExactUsername(p.getName()))) {
              if (PlayerUtils.getPlayer(args[1]) != null) {
                GangData gdata = GangData.getGang(pData.gangName);
                if (gdata.getUsers().size() >= 10) {
                  p.sendMessage(ChatColor.RED + "Vous avez dépassé la limite de membres autorisé qui s'élève à " + ChatColor.DARK_RED + '\n');
                  return false;
                } 
                Request.createRequest(Request.RequestType.GANGINVITE, p.getName(), args[1]);
              } else {
                MessageUtils.sendMessageFromConfig(sender, "user-invalid");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-player-already-in-gang");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } else if (args[0].equalsIgnoreCase("disband")) {
        if (args.length == 2) {
          if (p.hasPermission("cylrp.admin")) {
            String GangName = args[1];
            GangData gData = GangData.getGangData(GangName);
            if (gData != null) {
              try {
                gData.destroy();
              } catch (Exception e) {
                e.printStackTrace();
              } 
              MessageUtils.sendMessage((CommandSender)p, "§aLe gang §2" + GangName + "§a vient d'être dissous");
            } else {
              MessageUtils.sendMessage((CommandSender)p, "Ce gang n'existe pas");
            } 
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas la permission d'exécuter cette commande");
          } 
        } else if (g != null) {
          if (g.getChef() != null) {
            if (g.getChef().equalsIgnoreCase(p.getName())) {
              if (!Capture.isCaptureExist((pData.getActuallyRegion() == null) ? null : pData.getActuallyRegion().getId())) {
                try {
                  g.destroy();
                  MessageUtils.sendMessageFromConfig(sender, "gang-disbanded");
                } catch (Exception e) {
                  e.printStackTrace();
                } 
              } else {
                p.sendMessage(ChatColor.RED + "Vous ne pouvez pas dissoudre votre gang car vous êtes dans une zone de capture");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Il n'y a aucun chef dans votre gang, il se pourrait qu'un bug se soit manifesté.Vous pouvez forcer pour quitter le gang en executant la commande /gang leave");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } else if (args[0].equalsIgnoreCase("kick")) {
        String name = args[1];
        if (g != null) {
          name = g.getExactUsername(name);
          if (g.isHighRank(g.getExactUsername(p.getName()))) {
            if (g.isUserMember(name)) {
              if (!g.getChef().equals(name)) {
                PlayerData data = PlayerData.getPlayerData(name);
                if (g.getChef().equalsIgnoreCase(p.getName()) || !g.isHighRank(name)) {
                  if (!Capture.isCaptureExist((pData.getActuallyRegion() == null) ? null : pData.getActuallyRegion().getId()) && 
                    !Capture.isCaptureExist((data.getActuallyRegion() == null) ? null : data.getActuallyRegion().getId())) {
                    if (Bukkit.getPlayerExact(name) != null)
                      Chat.leftFromCanal(name); 
                    g.removeUser(name);
                    data.gangName = null;
                    if (Bukkit.getPlayerExact(name) != null)
                      Bukkit.getPlayerExact(name).sendMessage(ChatColor.GREEN + "Vous avez été kick du gang"); 
                    MessageUtils.sendMessageFromConfig(sender, "gang-player-kicked");
                  } else {
                    p.sendMessage(ChatColor.RED + "Vous ne pouvez pas kick ce joueur car vous êtes dans une zone de capture ou votremembre l'est.");
                  } 
                } else {
                  MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
                } 
              } else {
                MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-player-not-in");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "gang-less-permission");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } else if (args[0].equalsIgnoreCase("leave")) {
        if (g != null) {
          if (g.getChef() != null) {
            if (!g.getChef().equalsIgnoreCase(p.getName())) {
              if (!Capture.isCaptureExist((pData.getActuallyRegion() == null) ? null : pData.getActuallyRegion().getId())) {
                g.removeUser(g.getExactUsername(p.getName()));
                Chat.leftFromCanal(p.getName());
                pData.gangName = null;
                MessageUtils.sendMessageFromConfig(sender, "gang-not-appartenance");
              } else {
                p.sendMessage(ChatColor.RED + "Vous ne pouvez pas leave ce gang car vous êtes dans une zone de capture");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-cannot-quit");
            } 
          } else {
            pData.gangName = null;
            Chat.leftFromCanal(p.getName());
            MessageUtils.sendMessageFromConfig(sender, "gang-not-appartenance");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-havent");
        } 
      } else if (args[0].equalsIgnoreCase("join")) {
        if (g == null) {
          Request request = Request.findRequest(Request.RequestType.GANGINVITE,args[1], sender.getName());
          if (request != null) {
            if (pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste || pData.selectedJob instanceof fr.karmaowner.jobs.BAC) {
              if (request.getType() == Request.RequestType.GANGINVITE) {
                GangData data = GangData.getGangData(PlayerData.getPlayerData(request.getSender()).getGangName());
                if (data.getUsers().size() >= 10) {
                  p.sendMessage(ChatColor.RED + "Ce gang à trop de membre. Vous ne pouvez pas le rejoindre. ");
                  return false;
                } 
                data.getMembres().add(p.getName());
                pData.gangName = data.getGangName();
                request.destroy();
                String msg = MessageUtils.getMessageFromConfig("gang-joined");
                msg = msg.replaceAll("&", "§");
                msg = msg.replaceAll("%gang%", pData.gangName);
                MessageUtils.sendMessage(sender, msg);
              } else {
                MessageUtils.sendMessageFromConfig(sender, "gang-not-request");
              } 
            } else {
              MessageUtils.sendMessageFromConfig(sender, "gang-bad-rank");
            } 
          } else {
            MessageUtils.sendMessageFromConfig(sender, "unknown-request");
          } 
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-already-in");
        } 
      } else if (args[0].equalsIgnoreCase("members")) {
        if (g != null) {
          g.printMembers(p);
        } else {
          MessageUtils.sendMessageFromConfig(sender, "gang-not-appartenance");
        } 
      } else if (args[0].equalsIgnoreCase("help")) {
        if(args.length == 1)
        {
          displayHelp(sender, 1);
        }
        else
        {
          int page = Integer.parseInt(args[1]);
          displayHelp(sender, page);
        }
      } else {
        displayHelp(sender, 1);
      } 
    } 
    return false;
  }
  
  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Gang commands 1/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/gang help <Page> §7- §aAffiche l'aide");
      sender.sendMessage("● §c/gang add capture <nom> §7- §aAjoute la region dans la liste des captures");
      sender.sendMessage("● §c/gang set chef <pseudo> §7- §aDonner le lead à un membre du gang");
      sender.sendMessage("● §c/gang add sousChef <pseudo> §7- §aProuvemoir un joueur au rang sous-chef");
      sender.sendMessage("● §c/gang add membre <pseudo> §7- §aProuvevoir un joueur au rang membre");
      sender.sendMessage("● §c/gang delete capture <nom> §7- §aRetire la region de la liste des captures");
    } 
    if (page == 2) {
      sender.sendMessage("§b【Gang commands 2/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/gang create <nom> <pseudo> §7- §aCrée un gang");
      sender.sendMessage("● §c/gang invite <pseudo> §7- §aInvite un joueur dans le gang");
      sender.sendMessage("● §c/gang disband §7- §aPermet de dissoudre son gang");
      sender.sendMessage("● §c/gang kick <pseudo> §7- §aKick le joueur de son gang");
    } 
    if (page == 3) {
      sender.sendMessage("§b【Gang commands 3/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/gang leave §7- §aPermet de quitter un gang");
      sender.sendMessage("● §c/gang join <nom> §7- §aPermet de rejoindre un gang");
      sender.sendMessage("● §c/gang join <pseudo> §7- §aPermet de rejoindre un gang");
      sender.sendMessage("● §c/gang members §7- §aAffiche la liste des membres du gang");
      sender.sendMessage("● §c/gang help <page> §7- §aAffiche la liste des commmandes");
    } 
    if (page == 4) {
      sender.sendMessage("§b【Gang commands 4/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/gang ally add <Nom du gang> §7- §aConstruire une alliance avec un gang");
      sender.sendMessage("● §c/gang ally remove <Nom du gang> §7- §aRompre une alliance de gang");
      sender.sendMessage("● §c/gang ally list §7- §aListe des gangs alliés");
      sender.sendMessage("● §c/gang ally accept <Nom du gang|Emetteur de la requête> §7- §aAccepte une requête d'alliance");
    } 
    if (page == 5) {
      sender.sendMessage("§b【Gang commands 5/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/gang enemy add <Nom du gang> §7- §aDéclarer un gang comme ennemi");
      sender.sendMessage("● §c/gang enemy remove <Nom du gang> §7- §aAppliquer la neutralité à un gang");
      sender.sendMessage("● §c/gang enemy list §7- §aListe des gangs ennemis");
      sender.sendMessage("● §c/gang classement <page> §7- §aClassement des gangs");
      sender.sendMessage("● §c/gang info <page> §7- §aInformations sur votre gang");
    } 
  }
  
  public void displayHelpAlias(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Gang commands 1/5】");
      sender.sendMessage("");
      sender.sendMessage("● §c/g h <Page> §7- §aAffiche l'aide");
      sender.sendMessage("● §c/g addc <nom> §7- §aAjoute la region dans la liste des captures");
      sender.sendMessage("● §c/g addschef <pseudo> §7- §aPromouvoir un joueur au rang sous-chef");
      sender.sendMessage("● §c/g s c <pseudo> §7- §aDonner le lead à un membre du gang");
      sender.sendMessage("● §c/g addm <pseudo> §7- §aPromouvoir un joueur au rang membre");
      sender.sendMessage("● §c/g delc <nom> §7- §aRetire la region de la liste des captures");
    } 
    if (page == 2) {
      sender.sendMessage("§b【Gang commands 2/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/g h <Page> §7- §aAffiche l'aide");
      sender.sendMessage("● §c/g crte <nom> <pseudo> §7- §aCrée un gang");
      sender.sendMessage("● §c/g rname <nom> §7- §aRenommer son gang");
      sender.sendMessage("● §c/g inv <pseudo> §7- §aInvite un joueur dans le gang");
      sender.sendMessage("● §c/g disb §7- §aPermet de dissoudre son gang");
      sender.sendMessage("● §c/g kck <pseudo> §7- §aKick le joueur de son gang");
    } 
    if (page == 3) {
      sender.sendMessage("§b【Gang commands 3/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/g h <Page> §7- §aAffiche l'aide");
      sender.sendMessage("● §c/g lv §7- §aPermet de quitter un gang");
      sender.sendMessage("● §c/g jn <nom> §7- §aPermet de rejoindre un gang");
      sender.sendMessage("● §c/g mbs §7- §aAffiche la liste des membres du gang");
    } 
    if (page == 4) {
      sender.sendMessage("§b【Gang commands 4/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/g aly ad <Nom du gang> §7- §aConstruire une alliance avec un gang");
      sender.sendMessage("● §c/g aly rm <Nom du gang> §7- §aRompre une alliance de gang");
      sender.sendMessage("● §c/g aly ls §7- §aListe des gangs alliés");
      sender.sendMessage("● §c/g aly accpt <Nom du gang|Emetteur de la requête> §7- §aAccepte une requête d'alliance");
    } 
    if (page == 5) {
      sender.sendMessage("§b【Gang commands 5/5 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/g enmy ad <Nom du gang> §7- §aDéclarer un gang comme ennemi");
      sender.sendMessage("● §c/g enmy rm <Nom du gang> §7- §aAppliquer la neutralité à un gang");
      sender.sendMessage("● §c/g enmy ls §7- §aListe des gangs ennemis");
      sender.sendMessage("● §c/g clsmt <page> §7- §aClassement des gangs");
      sender.sendMessage("● §c/g info <page> §7- §aInformations sur votre gang");
    } 
  }
}
