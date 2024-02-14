package fr.karmaowner.chat;

import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ChatGroup {
  private ArrayList<String> players = new ArrayList<>();
  
  private ChatFormatting format;
  
  private String name;
  
  private boolean isEnable;
  
  public ChatGroup(String name, ChatFormatting format) {
    this.name = name;
    this.format = format;
    this.isEnable = true;
  }
  
  public boolean isPlayerOnCanal(String playername) {
    for (String ply : this.players) {
      if (ply.equals(playername))
        return true; 
    } 
    return false;
  }
  
  public boolean addPlayer(String playername) {
    if (!isPlayerOnCanal(playername)) {
      this.players.add(playername);
      PlayerUtils.sendMessagePlayer(playername, switchingMessage());
      return true;
    } 
    PlayerUtils.sendMessagePlayer(playername, ChatColor.RED + "[Chat] Vous êtes déjà dans ce canal !");
    return false;
  }
  
  public String switchingMessage() {
    return ChatColor.BLUE + "[Chat] Vous êtes désormais dans le canal " + ChatColor.AQUA + this.name;
  }
  
  public void deletePlayer(String playername) {
    if (isPlayerOnCanal(playername)) {
      this.players.remove(playername);
      PlayerUtils.sendMessagePlayer(playername, ChatColor.GREEN + "[Chat] Vous venez de quitter le canal " + ChatColor.DARK_GREEN + getName());
    } 
  }
  
  public ChatFormatting getFormat() {
    return this.format;
  }
  
  public ArrayList<String> getPlayers() {
    return this.players;
  }
  
  public ArrayList<String> getClosestPlayers(int radius, Player sender) {
    ArrayList<String> closest = new ArrayList<>();
    for (String pname : this.players) {
      Player p = Bukkit.getPlayerExact(pname);
      if (p != null && sender.getLocation().distance(p.getLocation()) <= radius)
        closest.add(pname); 
    } 
    return closest;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void sendMessage(Player sender, String msg) {
    if (this.isEnable || sender.hasPermission("staffchat")) {
      for (String ply : this.players)
        PlayerUtils.sendMessagePlayer(ply, this.format.toString(sender, msg)); 
      getFormat().resetMessage();
    } else {
      MessageUtils.sendMessage((CommandSender)sender, "le chat est désactivé. Vous ne pouvez pas envoyer de message.");
    } 
  }
  
  public void sendMessage(int radius, Player sender, String msg) {
    if (this.isEnable || sender.hasPermission("staffchat")) {
      for (String ply : getClosestPlayers(radius, sender))
        PlayerUtils.sendMessagePlayer(ply, msg); 
      getFormat().resetMessage();
    } else {
      MessageUtils.sendMessage((CommandSender)sender, "le chat est désactivé. Vous ne pouvez pas envoyer de message.");
    } 
  }
  
  public boolean isEnable() {
    return this.isEnable;
  }
  
  public void setEnable(boolean b) {
    this.isEnable = b;
  }
  
  public ArrayList<String> getPlayersOnMessage(String msg) {
    ArrayList<String> players = new ArrayList<>();
    for (String word : msg.split(" ")) {
      for (Player p : Bukkit.getOnlinePlayers()) {
        if (word.toLowerCase().contains(p.getName().toLowerCase()))
          players.add(p.getName()); 
      } 
    } 
    return players;
  }
}
