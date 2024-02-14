package fr.karmaowner.chat.group;

import fr.karmaowner.chat.ChatFormatBuilder;
import fr.karmaowner.chat.ChatGroup;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Gang extends ChatGroup {
  public Gang() {
    super("Gang", ChatFormatBuilder.build("%color_daqua %pre - %name/%g - %prp %color_white : %color_gray %msg").setPrefix("Gang").create());
  }
  
  public boolean addPlayer(String playername) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      if (data.gangName != null && !data.gangName.isEmpty())
        return super.addPlayer(p.getName()); 
      p.sendMessage(ChatColor.RED + "[Chat] Vous n'avez pas les privilèges pour accéder à ce canal !");
      return false;
    } 
    return false;
  }
  
  public void sendMessage(Player sender, String msg) {
    PlayerData dataSender = PlayerData.getPlayerData(sender.getName());
    String rank = GangData.getGang(dataSender.gangName).rankNameUser(sender.getName());
    for (String p : getPlayers()) {
      PlayerData data = PlayerData.getPlayerData(p);
      if (data != null && data.gangName != null && data.gangName.equals(dataSender.gangName)) {
        getFormat().definePorcent("%name", dataSender.gangName);
        getFormat().definePorcent("%g", rank);
        PlayerUtils.sendMessagePlayer(p, getFormat().toString(sender, msg));
      } 
    } 
    getFormat().resetMessage();
  }
}
