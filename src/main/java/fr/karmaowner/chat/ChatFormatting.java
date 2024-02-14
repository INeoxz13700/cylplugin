package fr.karmaowner.chat;

import fr.karmaowner.chat.events.ChatFormatEvent;
import fr.karmaowner.common.Main;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.grades.hasGrade;
import java.util.regex.Matcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ChatFormatting {
  private String pre;
  
  private String suff;
  
  private String format;
  
  private String message;
  
  public ChatFormatting(String format) {
    this.pre = "";
    this.suff = "";
    this.format = format;
    this.message = format;
  }
  
  public void setPrefix(String pre) {
    this.pre = pre;
  }
  
  public void setSuffix(String suff) {
    this.suff = suff;
  }
  
  public String getFormat() {
    return this.format;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void definePorcent(String porcent, String replacement) {
    this.message = this.message.replaceFirst(porcent, replacement.replaceAll("&", "§"));
  }
  
  public void replaceColor() {
    for (String porcents : this.message.split(" ")) {
      if (porcents.contains("color"))
        switch (porcents.split("_")[1]) {
          case "white":
            this.message = this.message.replaceFirst("%color_white", ChatColor.WHITE.toString());
          case "red":
            this.message = this.message.replaceFirst("%color_red", ChatColor.RED.toString());
          case "dred":
            this.message = this.message.replaceFirst("%color_dred", ChatColor.DARK_RED.toString());
          case "blue":
            this.message = this.message.replaceFirst("%color_blue", ChatColor.BLUE.toString());
          case "dblue":
            this.message = this.message.replaceFirst("%color_dblue", ChatColor.DARK_BLUE.toString());
          case "gray":
            this.message = this.message.replaceFirst("%color_gray", ChatColor.GRAY.toString());
          case "black":
            this.message = this.message.replaceFirst("%color_black", ChatColor.BLACK.toString());
          case "yellow":
            this.message = this.message.replaceFirst("%color_yellow", ChatColor.YELLOW.toString());
          case "gold":
            this.message = this.message.replaceFirst("%color_gold", ChatColor.GOLD.toString());
          case "green":
            this.message = this.message.replaceFirst("%color_green", ChatColor.GREEN.toString());
          case "dgreen":
            this.message = this.message.replaceFirst("%color_dgreen", ChatColor.DARK_GREEN.toString());
          case "aqua":
            this.message = this.message.replaceFirst("%color_aqua", ChatColor.AQUA.toString());
          case "daqua":
            this.message = this.message.replaceFirst("%color_daqua", ChatColor.DARK_AQUA.toString());
          case "purple":
            this.message = this.message.replaceFirst("%color_purple", ChatColor.LIGHT_PURPLE.toString());
          case "dpurple":
            this.message = this.message.replaceFirst("%color_dpurple", ChatColor.DARK_PURPLE.toString());
            break;
        }  
    } 
  }
  
  public String getPrefix() {
    return this.pre;
  }
  
  public String getSuffix() {
    return this.suff;
  }
  
  public String toString(Player p, String msg) {
    replaceColor();
    String m = Matcher.quoteReplacement(msg);
    if (p.hasPermission("cylrp.chat.color"))
      m = m.replaceAll("&", "§"); 
    m = ChatEmoji.toEmoji(m);
    ChatFormatEvent fe = new ChatFormatEvent(p, this.message, m);
    Main.INSTANCE.getServer().getPluginManager().callEvent((Event)fe);
    if (fe.getNewMsgFormat() != null)
      this.message = fe.getNewMsgFormat(); 
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data != null) {
      if (data.getIdentity() != null && (data.getIdentity()).length == 2)
        this.message = this.message.replaceFirst("%prp", data.getIdentity()[0] + " " + data.getIdentity()[1]); 
      String job = "";
      job = job + data.selectedJob.getFeatures().getDisplayName();
      if (data.selectedJob instanceof hasGrade) {
        hasGrade grade = (hasGrade)data.selectedJob;
        job = job + " - ";
        job = job + grade.getGrade().getGrade().getNom();
      } 
      this.message = this.message.replaceFirst("%job", job);
    } 
    String s = this.message.replaceFirst("%pre", this.pre).replaceFirst("%suff", this.suff).replaceFirst("%msg", m).replaceFirst("%p", p.getName());
    return s;
  }
  
  public void resetMessage() {
    this.message = this.format;
  }
}
