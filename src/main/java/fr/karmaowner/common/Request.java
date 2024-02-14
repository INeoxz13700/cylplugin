package fr.karmaowner.common;

import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.MessageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Request {
  public static List<Request> requestList = new ArrayList<>();

  private RequestType type;

  private String sender;

  private String receiver;

  private String gangName;

  private long requestTime;

  private static final int requestExpireTimeInSeconds = 900;

  public enum RequestType {
    COMPANYINVITE, GANGINVITE, GANGALLY, BACMANDAT;
  }

  private Request(RequestType type, String sender, String receiver) {
    this(type, sender, receiver, "");
    if (type == RequestType.COMPANYINVITE) {
      PlayerData senderData = PlayerData.getPlayerData(sender);
      if (senderData == null)
        return;
      if (senderData.hasCompany()) {
        CompanyData companyData = CompanyData.getCompanyData(senderData.getCompanyName());
        String msg = MessageUtils.getMessageFromConfig("company-invite");
        msg = msg.replaceAll("%sender%", sender);
        msg = msg.replaceAll("%company%", companyData.getCompanyName());
        msg = msg.replaceAll("&", "§");
        Player pReceiver = Bukkit.getPlayerExact(receiver);
        if (pReceiver != null)
          pReceiver.sendMessage(msg);

        msg = MessageUtils.getMessageFromConfig("company-invite-sended");
        msg = msg.replaceAll("%receiver%", receiver);
        msg = msg.replaceAll("&", "§");

        Player pSender = Bukkit.getPlayerExact(sender);
        if (pSender != null)
          pSender.sendMessage(msg);
      }
    } else if (type == RequestType.GANGINVITE) {
      PlayerData senderData = PlayerData.getPlayerData(sender);
      if (senderData == null)
        return;
      if (senderData.hasGang()) {
        GangData gangData = GangData.getGangData(senderData.getGangName());
        Player pReceiver = Bukkit.getPlayerExact(receiver);
        if (pReceiver != null)
          pReceiver.sendMessage(ChatColor.DARK_AQUA + "Vous venez de recevoir une invitation du gang " + ChatColor.AQUA + gangData.getGangName());
        Player pSender = Bukkit.getPlayerExact(sender);
        if (pSender != null)
          pSender.sendMessage(ChatColor.DARK_AQUA + "Invitation envoyé à " + ChatColor.AQUA + pReceiver.getName());
      }
    } else if (type == RequestType.GANGALLY) {
      GangData gangDataReceiver = GangData.getGangData(receiver);
      gangDataReceiver.sendMessageAll("§aRequête d'alliance du gang §2" + sender);
    }
  }

  private Request(RequestType type, String sender, String receiver, String gangName) {
    this.type = type;
    this.sender = sender;
    this.receiver = receiver;
    this.gangName = gangName;
    this.requestTime = System.currentTimeMillis();
    if (type == RequestType.BACMANDAT) {
      PlayerData senderData = PlayerData.getPlayerData(sender);
      if (senderData == null)
        return;
      Player pSender = Bukkit.getPlayerExact(sender);
      Player pReceiver = Bukkit.getPlayerExact(receiver);
      PlayerData dataReceiver = PlayerData.getPlayerData(receiver);
      if (pSender != null)
        if (dataReceiver.selectedJob instanceof fr.karmaowner.jobs.Maire) {
          MessageUtils.sendMessage((CommandSender)pSender, "requde da envoyau Maire");
        } else if (dataReceiver.selectedJob instanceof fr.karmaowner.jobs.AssembleeNationale) {
          MessageUtils.sendMessage((CommandSender)pSender, "requde da envoyau Prde l'assemblnationale");
        }
      if (pReceiver != null)
        MessageUtils.sendMessage((CommandSender)pReceiver, "§aLe Major de la BAC a envoyer une requête de démantélement du gang §2" + gangName);
    }
  }

  public static void destroyExpiredRequest() {
    List<Request> toRemove = new ArrayList<>();
    for (Request request : requestList) {
      int elapsedTimeInSeconds = (int)((float)(System.currentTimeMillis() - request.requestTime) / 1000.0F);
      if (elapsedTimeInSeconds >= 900)
        toRemove.add(request);
    }
    toRemove.forEach(x -> x.destroy());
  }

  public static Request createRequest(RequestType type, String sender, String receiver) {
    destroyExpiredRequest();
    Request newRequest = new Request(type, sender, receiver);
    if (!requestList.contains(newRequest)) {
      requestList.add(newRequest);
      return newRequest;
    }
    return requestList.get(requestList.indexOf(newRequest));
  }

  public static Request createRequest(RequestType type, String sender, String receiver, String gangName) {
    destroyExpiredRequest();
    if (type != RequestType.BACMANDAT) {
      System.out.println("Cette fonction fonctionne seulement pour les requde mandat BAC");
      return null;
    }
    Request newRequest = new Request(type, sender, receiver, gangName);
    if (!requestList.contains(newRequest)) {
      requestList.add(newRequest);
      return newRequest;
    }
    return requestList.get(requestList.indexOf(newRequest));
  }

  public RequestType getType() {
    return this.type;
  }

  public String getSender() {
    return this.sender;
  }

  public String getReceiver() {
    return this.receiver;
  }

  public String getGangName() {
    return this.gangName;
  }

  public void destroy() {
    requestList.remove(this);
    Main.Log("Requête " + toString() + " détruite");
  }

  public static Request findRequest(RequestType type, String value, String receiver) {
    List<Request> requestFilter = (List<Request>)requestList.stream().filter(x -> (x.type == type)).collect(Collectors.toList());
    List<Request> toRemove = new ArrayList<>();
    if (requestFilter.size() > 0) {
      if (((Request)requestFilter.get(0)).type == RequestType.COMPANYINVITE) {
        for (Request request : requestFilter) {
          if (request.sender.equalsIgnoreCase(value) && request.receiver.equalsIgnoreCase(receiver))
            return request;
          PlayerData playerData = PlayerData.getPlayerData(request.sender);
          if (!playerData.hasCompany()) {
            toRemove.add(request);
            continue;
          }
          if (playerData.getCompanyName().equalsIgnoreCase(value) && request.receiver.equalsIgnoreCase(receiver))
            return request;
        }
      } else if (((Request)requestFilter.get(0)).type == RequestType.GANGINVITE) {
        for (Request request : requestFilter) {
          if (request.sender.equalsIgnoreCase(value) && request.receiver.equalsIgnoreCase(receiver))
            return request;
          PlayerData playerData = PlayerData.getPlayerData(request.sender);
          if (!playerData.hasGang()) {
            toRemove.add(request);
            continue;
          }
          if (playerData.getGangName().equalsIgnoreCase(value) && request.receiver.equalsIgnoreCase(receiver))
            return request;
        }
      } else if (((Request)requestFilter.get(0)).type == RequestType.GANGALLY) {
        for (Request request : requestFilter) {
          if (request.sender.equalsIgnoreCase(value) && request.receiver.equalsIgnoreCase(receiver))
            return request;
          GangData.getGangData(request.sender);
          GangData gangData = GangData.getGang(request.sender);
          if (gangData != null) {
            for (String username : gangData.getUsers()) {
              if (username.equalsIgnoreCase(value))
                return request;
            }
            continue;
          }
          toRemove.add(request);
        }
      } else if (((Request)requestFilter.get(0)).type == RequestType.BACMANDAT) {
        for (Request request : requestFilter) {
          if (request.sender.equalsIgnoreCase(value) && request.receiver.equalsIgnoreCase(receiver))
            return request;
        }
      }
      requestList.removeAll(toRemove);
    }
    return null;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Request) {
      Request request = (Request)obj;
      return (request.sender.equalsIgnoreCase(this.sender) && request.receiver.equalsIgnoreCase(this.receiver) && request.type == this.type && request.gangName.equalsIgnoreCase(this.gangName));
    }
    return false;
  }

  public int hashCode() {
    return this.sender.length() + this.receiver.length() + this.type.ordinal() + this.gangName.length();
  }
}
