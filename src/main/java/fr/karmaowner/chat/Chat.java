package fr.karmaowner.chat;

import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;

public class Chat {
  private static HashMap<String, ChatGroup> GROUPS = new HashMap<>();
  
  public static final String DEFAULT = "HRP";
  
  private Chat(ChatGroup group) {
    GROUPS.put(group.getName(), group);
  }
  
  public static void createGroup(ChatGroup group) {
    new Chat(group);
  }
  
  public static ChatGroup getGroup(String name) {
    return (GROUPS.get(name) != null) ? GROUPS.get(name) : null;
  }
  
  public static ChatGroup getDefaultGroup() {
    return GROUPS.get("HRP");
  }
  
  public static void switchCanal(String playername, String groupName) {
    ChatGroup group = getGroup(groupName);
    if (group != null) {
      ChatGroup oldGroup = getPlayerChatGroup(playername);
      if (oldGroup != null)
        if (!oldGroup.getName().equalsIgnoreCase(group.getName())) {
          if (group.addPlayer(playername))
            oldGroup.deletePlayer(playername); 
        } else {
          usage(MessageUtils.getMessageFromConfig("already-in-canal"), playername);
        }  
    } else {
      usage(ErrorGroups(), playername);
    } 
  }
  
  public static void leftFromCanal(String playername) {
    ChatGroup group = getPlayerChatGroup(playername);
    if (group != null && 
      getDefaultGroup().addPlayer(playername))
      group.deletePlayer(playername); 
  }
  
  public static ChatGroup getPlayerChatGroup(String playername) {
    for (ChatGroup group : GROUPS.values()) {
      if (group.isPlayerOnCanal(playername))
        return group; 
    } 
    return null;
  }
  
  public static void usage(String error, String playername) {
    PlayerUtils.sendMessagePlayer(playername, ChatColor.BOLD.toString() + ChatColor.RED + error);
    HashMap<String, String> matching = new HashMap<>();
    matching.put("Entreprise", "chatentreprise");
    matching.put("RP", "rp");
    matching.put("HRP", "hrp");
    for (ChatGroup group : GROUPS.values()) {
      String match = matching.get(group.getName());
      if (match != null) {
        PlayerUtils.sendMessagePlayer(playername, ChatColor.GOLD + "/chat " + ChatColor.YELLOW + ChatColor.BOLD + group.getName() + " ou " + ChatColor.YELLOW + "/" + match);
        continue;
      } 
      PlayerUtils.sendMessagePlayer(playername, ChatColor.GOLD + "/chat " + ChatColor.YELLOW + ChatColor.BOLD + group.getName());
    } 
  }
  
  public static String ErrorGroups() {
    return MessageUtils.getMessageFromConfig("chat-type-incorrect");
  }
}
