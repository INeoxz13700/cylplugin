package fr.karmaowner.chat.group;

import fr.karmaowner.chat.ChatFormatBuilder;
import fr.karmaowner.chat.ChatGroup;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Entreprise extends ChatGroup {
  public Entreprise() {
    super("Entreprise", ChatFormatBuilder.build("%color_gold %pre - %name/%g - %prp %color_white : %color_gray %msg").setPrefix("Entreprise").create());
  }
  
  public boolean addPlayer(String playername) {
    Player p = Bukkit.getPlayerExact(playername);
    PlayerData data = PlayerData.getPlayerData(playername);
    PlayerUtils.sendMessagePlayer(playername, data.companyName);
    if (data.companyName != null && !data.companyName.isEmpty())
      return super.addPlayer(p.getName()); 
    if (p != null)
      p.sendMessage(ChatColor.RED + "[Chat] Vous n'avez pas les privilèges pour accéder à ce canal !"); 
    return false;
  }
  
  public void sendMessage(Player sender, String msg) {
    PlayerData dataSender = PlayerData.getPlayerData(sender.getName());
    String rank = ((CompanyData)CompanyData.Companies.get(dataSender.companyName)).getRankName(sender.getName());
    for (String p : getPlayers()) {
      PlayerData data = PlayerData.getPlayerData(p);
      if (data != null && data.companyName != null && data.companyName.equals(dataSender.companyName)) {
        getFormat().definePorcent("%name", dataSender.companyName);
        getFormat().definePorcent("%g", rank);
        PlayerUtils.sendMessagePlayer(p, getFormat().toString(sender, msg));
      } 
    } 
    getFormat().resetMessage();
  }
}
