package fr.karmaowner.data;

import fr.cylapi.core.IGangData;
import fr.karmaowner.chat.Chat;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.gangs.Capture;
import fr.karmaowner.utils.CustomConcurrentHashMap;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.PlayerUtils;
import fr.karmaowner.utils.RecordBuilder;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GangData implements Data, IGangData {
  public static final String INDEX = "Gang";
  
  public static CustomConcurrentHashMap<String, GangData> GANGS = new CustomConcurrentHashMap();
  
  public TaskCreator save;
  
  public ResultSet results;
  
  private String gangName;
  
  private List<String> allies = new ArrayList<>();
  
  private List<String> ennemies = new ArrayList<>();
  
  private int RankingPoints;
  
  private String Chef;
  
  private List<String> SousChefs;
  
  private List<String> Membres;
  
  public static final int NBMEMBRES = 10;
  
  public enum RANKS {
    CHEF("Chef"),
    SOUSCHEF("Sous-Chef"),
    MEMBRE("Membre");
    
    private String name;
    
    RANKS(String name) {
      this.name = name;
    }
    
    public String getRankName() {
      return this.name;
    }
  }
  
  public GangData(String gangName, Player p) {
    setGangName(gangName);
    if (GANGS.containsKey(gangName))
      return; 
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("Gang_data", new CustomEntry("Name", gangName))
          .toString());
      if (results.count() == 0) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Chef", p.getName());
        data.put("Name", getGangName());
        this.RankingPoints = 0;
        PlayerData dataPlayer = PlayerData.getPlayerData(p.getName());
        this.Chef = p.getName();
        dataPlayer.gangName = gangName;
        this.SousChefs = new ArrayList<>();
        this.Membres = new ArrayList<>();
        this.results = null;
        try {
          Main.Database.update(RecordBuilder.build().insert(data, "Gang_data").toString());
        } catch (SQLException e) {
          e.printStackTrace();
        } 
        GANGS.put(gangName, this);
        return;
      } 
      if (results.count() == 1) {
        this.results = results.getActualResult();
        GANGS.put(gangName, this);
        loadData();
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public GangData(String gangName) {
    setGangName(gangName);
    if (GANGS.containsKey(gangName))
      return; 
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("Gang_data", new CustomEntry("Name", gangName))
          .toString());
      if (results.count() == 1) {
        this.results = results.getActualResult();
        GANGS.put(gangName, this);
        loadData();
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public static GangData getGangData(String gangName) {
    return GANGS.containsKey(gangName) ? (GangData)GANGS.get(gangName) : new GangData(gangName);
  }
  
  public static ArrayList<String> loadRanking() throws SQLException {
    SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("Gang_data").toString());
    ArrayList<String> list = new ArrayList<>();
    if (results.count() > 0)
      for (ResultSet rs : results) {
        GangData data = getGangData(rs.getString("Name"));
        if (data != null && data.gangName != null && !data.gangName.isEmpty())
          list.add(data.gangName + "§" + data.getRankingPoints()); 
      }  
    Collections.sort(list, new Comparator<String>() {
          public int compare(String o1, String o2) {
            return Integer.parseInt(o2.split("§")[1]) - Integer.parseInt(o1.split("§")[1]);
          }
        });
    return list;
  }
  
  public static void resetRanking() throws SQLException {
    TopRankedGangRewards();
    for (GangData data : GANGS.values())
      data.RankingPoints = 0; 
    HashMap<String, Object> fields = new HashMap<>();
    fields.put("RankingPoints", 0);
    String request = "UPDATE Gang_data SET RankingPoints = 0 WHERE RankingPoints != 0";
    Statement stmt = Main.Database.getConnection().createStatement();
    stmt.executeUpdate(request);
    for (Capture capture : Capture.captures.values())
      capture.resetCapture(); 
  }
  
  private static void TopRankedGangRewards() throws SQLException {
    ArrayList<String> bestGang = loadRanking();
    String gangName = ((String)bestGang.get(0)).split("§")[0];
    GangData data = getGang(gangName);
    if (data != null) {
      PlayerData chefData = PlayerData.getPlayerData(data.getChef());
      chefData.setMoney(chefData.getMoney().add(BigDecimal.valueOf(125000L)));
      for (String schef : data.getSousChefs()) {
        PlayerData sChefData = PlayerData.getPlayerData(schef);
        sChefData.setMoney(sChefData.getMoney().add(BigDecimal.valueOf(62500L)));
      } 
      for (String member : data.getMembres()) {
        PlayerData mData = PlayerData.getPlayerData(member);
        mData.setMoney(mData.getMoney().add(BigDecimal.valueOf(62500L)));
      } 
    } 
  }
  
  public static GangData getGang(String name) {
    return (GangData)GANGS.get(name);
  }
  
  public static GangData getOfflineGang(String name) {
    return new GangData(name);
  }
  
  public void setChef(String name) {
    this.Chef = name;
  }
  
  public List<String> getAllies() {
    return this.allies;
  }
  
  public List<String> getEnnemies() {
    return this.ennemies;
  }
  
  public void addAlly(String gangname) {
    this.allies.add(gangname);
  }
  
  public void addEnemy(String gangname) {
    this.ennemies.add(gangname);
  }
  
  public void deleteAlly(String gangname) {
    List<String> tmpList = new ArrayList<>();
    for (String ally : this.allies) {
      if (!ally.equals(gangname))
        tmpList.add(ally); 
    } 
    this.allies = tmpList;
  }
  
  public void deleteEnemy(String gangname) {
    List<String> tmpList = new ArrayList<>();
    for (String enemy : this.ennemies) {
      if (!enemy.equals(gangname))
        tmpList.add(enemy); 
    } 
    this.ennemies = tmpList;
  }
  
  public String getChef() {
    return this.Chef;
  }
  
  public boolean isHighRank(String name) {
    return (rankNameUser(name).equals(RANKS.CHEF.getRankName()) || 
      rankNameUser(name).equals(RANKS.SOUSCHEF.getRankName()));
  }
  
  public boolean setSousChef(String name) {
    if (!this.SousChefs.contains(name) && !this.Chef.equals(name)) {
      PlayerData data = PlayerData.getPlayerData(name);
      if (data != null)
        data.gangName = this.gangName; 
      removeUser(name);
      this.SousChefs.add(name);
      return true;
    } 
    return false;
  }
  
  public boolean setMembre(String name) {
    if (!this.Membres.contains(name) && !this.Chef.equals(name)) {
      PlayerData data = PlayerData.getPlayerData(name);
      if (data != null)
        data.gangName = this.gangName; 
      removeUser(name);
      this.Membres.add(name);
      return true;
    } 
    return false;
  }
  
  public List<String> getSousChefs() {
    return this.SousChefs;
  }
  
  public List<String> getMembres() {
    return this.Membres;
  }

  public String getExactUsername(String username)
  {
      for(String user : getUsers())
      {
        if(user.equalsIgnoreCase(username)) return user;
      }
      return null;
  }
  
  public boolean isUserMember(String player) {
    return getUsers().contains(player);
  }
  
  public String rankNameUser(String player) {
    if (isUserMember(player)) {
      if (this.Chef != null && 
        this.Chef.equals(player))
        return RANKS.CHEF.getRankName(); 
      if (this.SousChefs != null && 
        this.SousChefs.contains(player))
        return RANKS.SOUSCHEF.getRankName(); 
      if (this.Membres != null && 
        this.Membres.contains(player))
        return RANKS.MEMBRE.getRankName(); 
    } 
    return null;
  }
  
  public void sendMessageAll(String msg) {
    for (String plyer : getUsers()) {
      Player p = PlayerUtils.getPlayer(plyer);
      if (p != null)
        p.sendMessage(msg); 
    } 
  }
  
  public List<String> getUsers() {
    List<String> users = new ArrayList<>();
    users.add(this.Chef);
    if (this.SousChefs != null)
      users.addAll(this.SousChefs); 
    if (this.Membres != null)
      users.addAll(this.Membres); 
    return users;
  }
  
  public void removeUser(String player) {
    if (isUserMember(player) && 
      rankNameUser(player) != null)
      if (rankNameUser(player).equals(RANKS.CHEF.getRankName())) {
        setChef(null);
      } else if (rankNameUser(player).equals(RANKS.SOUSCHEF.getRankName())) {
        getSousChefs().remove(player);
      } else {
        List<String> tmpList = new ArrayList<>();
        for (String m : this.Membres) {
          if (!m.equals(player))
            tmpList.add(m); 
        } 
        this.Membres = tmpList;
      }  
  }
  
  public void destroy() throws Exception {
    sendMessageAll(ChatColor.DARK_RED + "Le gang " + ChatColor.RED + this.gangName + ChatColor.DARK_RED + " vient d'être dissout par le chef " + ChatColor.RED + getChef());
    for (Capture c : Capture.captures.values()) {
      if (c.getCaptureOwner() != null && c.getCaptureOwner().equals(this.gangName))
        c.resetCapture(); 
    } 
    for (String gName : this.allies) {
      GangData data = getGangData(gName);
      if (data != null)
        data.deleteAlly(this.gangName); 
    } 
    for (String gName : this.ennemies) {
      GangData data = getGangData(gName);
      if (data != null && data.getEnnemies().contains(this.gangName))
        data.deleteEnemy(this.gangName); 
    } 
    for (String player : getUsers()) {
      PlayerData data = PlayerData.getPlayerData(player);
      if (data != null) {
        data.gangName = null;
        removeUser(player);
        if (!PlayerData.getHashMap().containsKey(player)) {
          Main.Database.update(RecordBuilder.build().update(new CustomEntry("gangName", null), "players_data")
              
              .where(new CustomEntry("pseudo", player)).toString());
        } else {
          (PlayerData.getPlayerData(player)).gangName = null;
        } 
        Chat.leftFromCanal(player);
      } 
    } 
    GANGS.remove(this.gangName);
    Main.Database.update(RecordBuilder.build().delete("Gang_data")
        .where(new CustomEntry("Name", this.gangName)).toString());
  }
  
  public void loadData() {
    try {
      this.RankingPoints = this.results.getInt("RankingPoints");
      this.Chef = this.results.getString("Chef");
      setGangName(this.results.getString("Name"));
      this.SousChefs = new ArrayList<>();
      if (!this.results.getString("SousChefs").isEmpty())
        this.SousChefs.addAll(Arrays.asList(this.results.getString("SousChefs").split(";"))); 
      this.Membres = new ArrayList<>();
      if (!this.results.getString("Membres").isEmpty())
        this.Membres.addAll(Arrays.asList(this.results.getString("Membres").split(";"))); 
      this.allies = new ArrayList<>();
      if (!this.results.getString("allies").isEmpty())
        this.allies.addAll(Arrays.asList(this.results.getString("allies").split(";"))); 
      this.ennemies = new ArrayList<>();
      if (!this.results.getString("ennemies").isEmpty())
        this.ennemies.addAll(Arrays.asList(this.results.getString("ennemies").split(";"))); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void saveData() {
    HashMap<String, Object> data = new HashMap<>();
    data.put("RankingPoints", this.RankingPoints);
    data.put("Chef", this.Chef);
    data.put("SousChefs", StringUtils.join(this.SousChefs, ';'));
    data.put("Membres", StringUtils.join(this.Membres, ';'));
    data.put("ennemies", StringUtils.join(this.ennemies, ';'));
    data.put("allies", StringUtils.join(this.allies, ';'));
    try {
      Main.Database.update(RecordBuilder.build().update(data, "Gang_data")
          .where(new CustomEntry("Name", this.gangName)).toString());
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public int getConnectedUsers() {
    int count = 0;
    for (String user : getUsers()) {
      if (user != null && 
        Bukkit.getPlayerExact(user) != null)
        count++; 
    } 
    return count;
  }
  
  public static void saveDatas() {
    Main.Log("Gang Data saving...");
    List<String> removeGang = new ArrayList<>();
    for (Map.Entry<String, GangData> data : (Iterable<Map.Entry<String, GangData>>)GANGS.entrySet()) {
      if (!((GangData)data.getValue()).getGangName().isEmpty())
        ((GangData)data.getValue()).saveData(); 
      int connected = ((GangData)data.getValue()).getConnectedUsers();
      if (connected == 0)
        removeGang.add(data.getKey()); 
    } 
    GANGS.keySet().removeAll(removeGang);
    Main.Log("Gang Data saved");
  }
  
  public String getGangName() {
    return this.gangName;
  }
  
  public void setGangName(String gangName) {
    this.gangName = gangName;
  }
  
  public void renameGang(Player sender, String gangName) {
    try {
      SqlCollection result = Main.Database.select(RecordBuilder.build().selectAll("Gang_data", new CustomEntry("Name", gangName)).toString());
      if (result.count() == 1) {
        sender.sendMessage("§cUn gang possède déjà ce nom");
        return;
      } 
      GANGS.remove(this.gangName);
      setGangName(gangName);
      GANGS.put(gangName, this);
      for (String user : getUsers())
        (PlayerData.getPlayerData(user)).gangName = gangName; 
      sendMessageAll("§aVotre gang se nomme maintenant : §b" + gangName);
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public int getRankingPoints() {
    return this.RankingPoints;
  }
  
  public void setRankingPoints(int rankingPoints) {
    this.RankingPoints = rankingPoints;
  }
  
  public boolean isEnemyWith(String gangName) {
    return this.ennemies.contains(gangName);
  }
  
  public void printMembers(Player p) {
    p.sendMessage(ChatColor.RED.toString() + ChatColor.MAGIC + "&" + ChatColor.DARK_RED + "------ Membres du gang ------" + ChatColor.RED + ChatColor.MAGIC + "&");
    p.sendMessage(ChatColor.GOLD + "Chef => " + ChatColor.YELLOW + getChef());
    for (String name : getSousChefs())
      p.sendMessage(ChatColor.GOLD + "Sous-Chef => " + ChatColor.YELLOW + name); 
    for (String name : getMembres())
      p.sendMessage(ChatColor.GOLD + "Membre => " + ChatColor.YELLOW + name); 
    p.sendMessage(ChatColor.DARK_RED + "-----------------------------");
  }
  
  public String getChefName() {
    return this.Chef;
  }
  
  public int getMaxTotalMembres() {
    return 10;
  }
}
