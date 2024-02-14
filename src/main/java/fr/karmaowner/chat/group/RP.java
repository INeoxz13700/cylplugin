package fr.karmaowner.chat.group;

import fr.karmaowner.chat.ChatFormatBuilder;
import fr.karmaowner.chat.ChatGroup;
import fr.karmaowner.common.Main;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RP extends ChatGroup {
  public RP() {
    super("RP", ChatFormatBuilder.build("%g%color_red%prp%sp %color_white : %color_gray %staff%msg").create());
  }
  
  public void sendMessage(Player sender, String msg) {
    if (isEnable() || sender.hasPermission("staffchat")) {
      PlayerData dataSender = PlayerData.getPlayerData(sender.getName());
      if (dataSender.companyName != null && !dataSender.companyName.equals("") && !Main.chat.getPlayerPrefix(sender).contains("[STAFF]")) {
        getFormat().definePorcent("%ent", "E");
      } else {
        getFormat().definePorcent("%ent", "");
      } 
      if (dataSender.gangName != null && !dataSender.gangName.equals("") && !Main.chat.getPlayerPrefix(sender).contains("[STAFF]")) {
        if (dataSender.companyName != null && !dataSender.companyName.equals("")) {
          getFormat().definePorcent("%gang", "§f/§4G");
        } else {
          getFormat().definePorcent("%gang", "§4G");
        } 
      } else {
        getFormat().definePorcent("%gang", "");
      } 
      if (Main.chat.getPlayerPrefix(sender) != null && Main.chat.getPlayerPrefix(sender).contains("[STAFF]")) {
        getFormat().definePorcent("%staff", "&f");
        getFormat().definePorcent("%g", Main.chat.getPlayerPrefix(sender));
      } else {
        getFormat().definePorcent("%staff", "");
        getFormat().definePorcent("%g", "");
      } 
      ArrayList<String> rp_players = new ArrayList<>();
      ArrayList<String> players = getPlayersOnMessage(msg);
      for (String pl : getPlayersOnMessage(msg)) {
        PlayerData pData = PlayerData.getPlayerData(pl);
        rp_players.add(pData.getIdentity()[0] + " " + pData.getIdentity()[1]);
      } 
      for (String p : getClosestPlayers(10, sender)) {
        String m = getFormat().toString(sender, msg);
        String regex = "x=([\\-0-9]{1,4}(\\.[0-9]+)?);y=([\\-0-9]{1,4}(\\.[0-9]+)?);z=([\\-0-9]{1,4}(\\.[0-9]+)?)";
        Pattern pp = Pattern.compile(regex);
        String m3 = Matcher.quoteReplacement(msg);
        Matcher mm = pp.matcher(m3);
        if (mm.lookingAt()) {
          double x = 0.0D, y = 0.0D, z = 0.0D;
          String s = "";
          s = mm.group();
          x = Double.parseDouble(s.split("x=")[1].split(";")[0]);
          y = Double.parseDouble(s.split("y=")[1].split(";")[0]);
          z = Double.parseDouble(s.split("z=")[1]);
          TextComponent t = new TextComponent("x=" + x + ";y=" + y + ";z=" + z);
          t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/destination start " + x + " " + y + " " + z + ""));
          t.setColor(ChatColor.AQUA);
          t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (BaseComponent[])new TextComponent[] { new TextComponent("mettre en marche le gps") }));
          t.setUnderlined(Boolean.TRUE);
          TextComponent base = new TextComponent("Coordonnées détectées: ");
          base.setColor(ChatColor.RED);
          base.addExtra((BaseComponent)t);
          Bukkit.getPlayerExact(p).spigot().sendMessage((BaseComponent)base);
        } 
        Player pl = Bukkit.getPlayerExact(p);
        PlayerData da = PlayerData.getPlayerData(p);
        if (pl.hasPermission("cylrp.staff")) {
          m = m.replace("%sp", " (" + sender.getName() + ")");
        } else {
          m = m.replace("%sp", "");
        } 
        if (rp_players.contains(da.getIdentity()[0]) || rp_players.contains(da.getIdentity()[1])) {
          String m2 = ChatColor.RED + "[" + ChatColor.DARK_RED + dataSender.getIdentity()[0] + " " + dataSender.getIdentity()[1] + ChatColor.RED + "->" + ChatColor.GOLD + "Vous" + ChatColor.RED + "] " + m;
          PlayerUtils.sendMessagePlayer(p, m2);
          continue;
        } 
        if (players.contains(p)) {
          String m2 = ChatColor.RED + "[" + ChatColor.DARK_RED + sender.getName() + ChatColor.RED + "->" + ChatColor.GOLD + "Vous" + ChatColor.RED + "] " + m;
          PlayerUtils.sendMessagePlayer(p, m2);
          continue;
        } 
        PlayerUtils.sendMessagePlayer(p, m);
      } 
      getFormat().resetMessage();
    } else {
      MessageUtils.sendMessage((CommandSender)sender, "le chat est désactivé. Vous ne pouvez pas envoyer de message.");
    } 
  }
}
