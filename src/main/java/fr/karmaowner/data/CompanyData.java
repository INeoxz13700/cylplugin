package fr.karmaowner.data;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.cylapi.core.ICompany;
import fr.cylapi.core.ICompanyAchievements;
import fr.cylapi.core.ICompanyData;
import fr.karmaowner.chat.Chat;
import fr.karmaowner.common.Achievements;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.companies.Company;
import fr.karmaowner.utils.CustomConcurrentHashMap;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import fr.karmaowner.utils.RecordBuilder;
import fr.karmaowner.utils.RegionUtils;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompanyData implements DataAchievements, ICompanyData {
  public static CustomConcurrentHashMap<String, CompanyData> Companies = new CustomConcurrentHashMap();
  
  private Company company = null;
  
  private String CompanyName = "";
  
  private String Categorie = "";
  
  private String Gerant = "";
  
  private List<String> Secretaires;
  
  private List<String> CommunityManagers;
  
  private List<String> Stagiaires;
  
  private List<String> CoGerant;
  
  private List<String> Salaries;
  
  private List<Achievements> achievements = new ArrayList<>();
  
  private HashMap<String, String> parcellesowned = new HashMap<>();
  
  public Timestamp lastAttributiontime = null;
  
  private double wonMoneyPerDay = 0.0D;
  
  public static final double QUOTAMONEY = 100000.0D;
  
  public Timestamp elapsedTimeQuota = null;
  
  private final double ONELEVELXP = 500.0D;
  
  private double Revenues;
  
  private double xpToReachForLevelUp = 500.0D;
  
  private static final int MAXLEVEL = 250;
  
  private int NbVenteTotal = 0;
  
  private double xpActually;
  
  private int Level = 1;
  
  private int nbSalaries = 5;
  
  public ResultSet results;
  
  private int UsersRepartition = 5;
  
  private int StagiairesRepartition = 15;
  
  private int SecretairesRepartition = 10;
  
  private int CommunityManagersRepartition = 20;
  
  private int CoGerantRepartition = 20;
  
  private int GerantRepartition = 30;
  
  public boolean isCreation = false;
  
  public TaskCreator save;
  
  public CompanyData(String CompanyName, Player p, String categorie) {
    if (Company.getClass(categorie) == null) {
      p.sendMessage(ChatColor.RED + "Catégorie inexistante !");
      return;
    } 
    if (Companies.containsKey(CompanyName))
      return; 
    this.Secretaires = new ArrayList<>();
    this.CommunityManagers = new ArrayList<>();
    this.Stagiaires = new ArrayList<>();
    this.CoGerant = new ArrayList<>();
    this.Salaries = new ArrayList<>();
    this.CompanyName = CompanyName;
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("company_data")
          .where(new CustomEntry("Name", CompanyName)).toString());
      Main.Log("Checking count of data");
      if (results.count() == 0) {
        Main.Log("condition gone ");
        HashMap<String, Object> data = new HashMap<>();
        this.isCreation = true;
        setGerant(p.getName());
        this.lastAttributiontime = new Timestamp(System.currentTimeMillis());
        this.elapsedTimeQuota = new Timestamp(System.currentTimeMillis());
        PlayerData pData = PlayerData.getPlayerData(getGerant());
        this.CompanyName = CompanyName;
        this.Categorie = categorie;
        pData.companyName = CompanyName;
        pData.companyCategory = categorie;
        Companies.put(getCompanyName(), this);
        this.company = Company.getInstanceByCategorie(this.Categorie, this);
        data.put("Name", CompanyName);
        data.put("Categorie", this.Categorie);
        data.put("Gerant", this.Gerant);
        Main.Log("creating data on db");
        try {
          Main.Database.update(RecordBuilder.build()
              .insert(data, "company_data").toString());
        } catch (SQLException e) {
          e.printStackTrace();
        } 
        Main.Log("Company data created on db");
        results = null;
      } else if (results.count() == 1) {
        this.results = results.getActualResult();
        Companies.put(getCompanyName(), this);
      } 
      if (results != null && !this.isCreation && 
        this.Categorie != null && this.Gerant != null) {
        loadData();
        this.company = Company.getInstanceByCategorie(this.Categorie, this);
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public double getXpToLevelUp() {
    return this.xpToReachForLevelUp;
  }
  
  public CompanyData(String CompanyName) {
    if (Companies.containsKey(CompanyName))
      return; 
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("company_data").where(new CustomEntry("Name", CompanyName)).toString());
      if (results.count() == 1) {
        this.results = results.getActualResult();
        loadData();
        Companies.put(getCompanyName(), this);
        if (this.Categorie != null && this.Gerant != null)
          this.company = Company.getInstanceByCategorie(this.Categorie, this); 
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public static CompanyData getCompanyData(String CompanyName) {
    return Companies.containsKey(CompanyName) ? (CompanyData)Companies.get(CompanyName) : new CompanyData(CompanyName);
  }
  
  public List<String> getSalaries() {
    return this.Salaries;
  }
  
  public void divideSalary() {
    this.lastAttributiontime = new Timestamp(System.currentTimeMillis());
    for (String user : getUsersList()) {
      PlayerData data = PlayerData.getPlayerData(user);
      double percent = getRepartitionByPlayer(user) / 100.0D;
      double money = getRevenues() * percent;
      if (data != null) {
        double taxe = 9.0D;
        money *= (100.0D - taxe) / 100.0D;
        data.setMoney(data.getMoney().add(BigDecimal.valueOf(money)));
        setRevenues(getRevenues() - money);
        if (Bukkit.getPlayerExact(user) != null) {
          Bukkit.getPlayerExact(user).sendMessage(ChatColor.LIGHT_PURPLE + "Vous venez de recevoir votre salaire de " + ChatColor.DARK_PURPLE + money + "€ net avec taxe de 9% appliquée");
          continue;
        } 
        data.saveData();
      } 
    } 
    broadcastCompany(ChatColor.YELLOW + "Le revenu de l'entreprise a été réparti à un total de " + ChatColor.GOLD + getUsersList().size() + ChatColor.YELLOW + " membres !");
  }
  
  public void setOwnedParcelle(String entree, String sortie) {
    this.parcellesowned.put(entree, sortie);
  }
  
  public HashMap<String, String> getOwnedParcelle() {
    return this.parcellesowned;
  }
  
  public boolean isOwnedParcelle(String sortie) {
    for (String s : this.parcellesowned.values()) {
      if (s.equals(sortie))
        return true; 
    } 
    return false;
  }
  
  public String getOwnedParcelleOut(String entree) {
    for (Map.Entry<String, String> s : this.parcellesowned.entrySet()) {
      if (s.getKey().equals(entree))
        return s.getValue(); 
    } 
    return null;
  }
  
  public String getOwnedParcelleIn(String sortie) {
    for (Map.Entry<String, String> s : this.parcellesowned.entrySet()) {
      if (s.getValue().equals(sortie))
        return s.getKey(); 
    } 
    return null;
  }
  
  public Company getCompany() {
    return this.company;
  }
  
  public int getNbSalaries() {
    return this.nbSalaries;
  }
  
  public void loadData() {
    try {
      this.Gerant = this.results.getString("Gerant");
      this.CoGerant = new ArrayList<>();
      if (this.results.getString("CoGerant") != null && !this.results.getString("CoGerant").isEmpty())
        this.CoGerant.addAll(Arrays.asList(this.results.getString("CoGerant").split(";"))); 
      this.Secretaires = new ArrayList<>();
      if (this.results.getString("Secretaires") != null && !this.results.getString("Secretaires").isEmpty())
        this.Secretaires.addAll(Arrays.asList(this.results.getString("Secretaires").split(";"))); 
      this.CommunityManagers = new ArrayList<>();
      if (this.results.getString("CommunityManagers") != null && !this.results.getString("CommunityManagers").isEmpty())
        this.CommunityManagers.addAll(Arrays.asList(this.results.getString("CommunityManagers").split(";"))); 
      this.Stagiaires = new ArrayList<>();
      if (this.results.getString("Stagiaires") != null && !this.results.getString("Stagiaires").isEmpty())
        this.Stagiaires.addAll(Arrays.asList(this.results.getString("Stagiaires").split(";"))); 
      this.Salaries = new ArrayList<>();
      if (this.results.getString("Salaries") != null && !this.results.getString("Salaries").isEmpty())
        this.Salaries.addAll(Arrays.asList(this.results.getString("Salaries").split(";"))); 
      this.UsersRepartition = this.results.getInt("UsersRepartition");
      this.StagiairesRepartition = this.results.getInt("StagiairesRepartition");
      this.SecretairesRepartition = this.results.getInt("SecretairesRepartition");
      this.CommunityManagersRepartition = this.results.getInt("CommunityManagersRepartition");
      this.CoGerantRepartition = this.results.getInt("CoGerantRepartition");
      this.GerantRepartition = this.results.getInt("GerantRepartition");
      this.NbVenteTotal = this.results.getInt("NbVenteTotal");
      this.nbSalaries = this.results.getInt("nbSalaries");
      this.Revenues = this.results.getInt("Revenues");
      this.Level = this.results.getInt("Level");
      this.xpToReachForLevelUp = this.results.getDouble("xpToReachForLevelUp");
      this.xpActually = this.results.getDouble("xpActually");
      this.Categorie = this.results.getString("Categorie");
      this.CompanyName = this.results.getString("Name");
      this.lastAttributiontime = new Timestamp(this.results.getLong("lastAttributiontime"));
      this.wonMoneyPerDay = this.results.getDouble("wonMoneyPerDay");
      this.elapsedTimeQuota = new Timestamp(this.results.getLong("elapsedTimeQuota"));
      List<String> parcelles = new ArrayList<>();
      if (this.results.getString("parcelles") != null && !this.results.getString("parcelles").isEmpty())
        parcelles.addAll(Arrays.asList(this.results.getString("parcelles").split(";"))); 
      if (parcelles.size() > 0)
        for (String parcelle : parcelles) {
          String[] splitted = parcelle.split("=");
          if (splitted.length > 1) {
            String parcelle_name = splitted[0].split("\\.")[0];
            String parcelle_id = splitted[1];
            this.parcellesowned.put(parcelle_name, parcelle_id);
          } 
        }  
      if (this.results.getString("achievements") != null && !this.results.getString("achievements").isEmpty())
        this.achievements.addAll(Achievements.parseToAchievements(Arrays.asList(this.results.getString("achievements").split(";")))); 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void saveData() {
    Main.Log("company " + getCompanyName());
    HashMap<String, Object> data = new HashMap<>();
    data.put("Gerant", getGerant());
    data.put("CoGerant", StringUtils.join(this.CoGerant, ';'));
    data.put("Secretaires", StringUtils.join(this.Secretaires, ';'));
    data.put("CommunityManagers", StringUtils.join(this.CommunityManagers, ';'));
    data.put("Stagiaires", StringUtils.join(this.Stagiaires, ';'));
    data.put("Salaries", StringUtils.join(this.Salaries, ';'));
    data.put("nbSalaries", this.nbSalaries);
    data.put("UsersRepartition", this.UsersRepartition);
    data.put("StagiairesRepartition", this.StagiairesRepartition);
    data.put("SecretairesRepartition", this.SecretairesRepartition);
    data.put("CoGerantRepartition", this.CoGerantRepartition);
    data.put("GerantRepartition", this.GerantRepartition);
    data.put("NbVenteTotal", this.NbVenteTotal);
    data.put("wonMoneyPerDay", this.wonMoneyPerDay);
    if (this.elapsedTimeQuota != null) data.put("elapsedTimeQuota", this.elapsedTimeQuota.getTime());
    data.put("wonMoneyPerDay", this.wonMoneyPerDay);
    if (this.lastAttributiontime != null) data.put("lastAttributiontime", this.lastAttributiontime.getTime());
    data.put("Revenues", this.Revenues);
    data.put("Level", this.Level);
    data.put("xpToReachForLevelUp", this.xpToReachForLevelUp);
    data.put("xpActually", this.xpActually);
    data.put("Categorie", this.Categorie);
    data.put("Name", this.CompanyName);
    StringJoiner joiner = new StringJoiner(";");
    for (Achievements a : this.achievements)
      joiner.add(a.toString()); 
    data.put("achievements", joiner.toString());
    StringJoiner parcelles = new StringJoiner(";");
    for (Map.Entry<String, String> parcelle : this.parcellesowned.entrySet())
      parcelles.add(parcelle.getKey() + ".name=" + parcelle.getValue());
    data.put("parcelles", parcelles.toString());
    try {
      Main.Database.update(RecordBuilder.build().update(data, "company_data")
          .where(new CustomEntry("Name", this.CompanyName)).toString());
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    if (this.company != null)
      this.company.saveData(); 
  }
  
  public boolean isWhichOneConnected() {
    for (String name : getUsersList()) {
      if (PlayerUtils.isConnected(name))
        return true; 
    } 
    return false;
  }
  
  public double getXp() {
    return this.xpActually;
  }
  
  public void addXp(double xp) {
    if (this.xpActually + xp >= this.xpToReachForLevelUp && this.Level < 250) {
      this.Level++;
      increaseNbSalary();
      broadcastCompany("§6l'entreprise vient d'augmenter d'un niveau §e-> §c" + this.Level);
      this.xpToReachForLevelUp += getCoeff() * 500.0D;
    } 
    this.xpActually += xp;
  }
  
  public void setLevel(int level) {
    if (level > 250)
      level = 250; 
    if (level <= 0)
      level = 1; 
    this.xpActually = getXpByLevel(level);
    this.xpToReachForLevelUp = getXpByLevel(level + 1);
    this.Level = level;
    increaseNbSalary();
    broadcastCompany("§6l'entreprise vient passer au niveau §e-> §c" + this.Level);
  }
  
  public double getXpByLevel(int level) {
    double xp = 0.0D;
    for (int i = 1; i <= level; i++)
      xp += (500 * i); 
    return xp;
  }
  
  public double getWonMoneyPerDay() {
    return this.wonMoneyPerDay;
  }
  
  public void addWonMoneyPerDay(double money) {
    this.wonMoneyPerDay += money;
  }
  
  public void setWonMoneyPerDay(double money) {
    this.wonMoneyPerDay = money;
  }
  
  public void increaseNbSalary() {
    if (this.Level < 15) {
      this.nbSalaries = 5;
    } else if (this.Level >= 15 && this.Level < 30) {
      this.nbSalaries = 10;
    } else if (this.Level >= 30 && this.Level < 60) {
      this.nbSalaries = 15;
    } else if (this.Level >= 60 && this.Level < 120) {
      this.nbSalaries = 20;
    } else if (this.Level >= 120 && this.Level < 200) {
      this.nbSalaries = 25;
    } else if (this.Level >= 200) {
      this.nbSalaries = 30;
    } 
  }
  
  public double getCoeff() {
    return this.Level;
  }
  
  public List<Achievements> getAchievements() {
    return this.achievements;
  }
  
  public void setAchievements(Achievements a) {
    this.achievements.add(a);
  }
  
  public double getRepartitionByPlayer(String username) {
    String rank = getRankName(username);
    if (rank.equalsIgnoreCase("CoGerant"))
      return (this.CoGerantRepartition / getCoGerant().size()); 
    if (rank.equalsIgnoreCase("CommunityManager"))
      return (this.CommunityManagersRepartition / getCommunityManagers().size()); 
    if (rank.equalsIgnoreCase("Stagiaire"))
      return (this.StagiairesRepartition / getStagiaires().size()); 
    if (rank.equalsIgnoreCase("Secretaire"))
      return (this.SecretairesRepartition / getSecretaires().size()); 
    if (rank.equalsIgnoreCase("Salarie"))
      return (this.UsersRepartition / getSalaries().size()); 
    if (rank.equalsIgnoreCase("Gerant"))
      return this.GerantRepartition; 
    return 0.0D;
  }
  
  public int[] getRepartition() {
    return new int[] { this.UsersRepartition, this.StagiairesRepartition, this.SecretairesRepartition, this.CommunityManagersRepartition, this.CoGerantRepartition, this.GerantRepartition };
  }
  
  public int getLevelReached() {
    return this.Level;
  }
  
  public String getGerant() {
    return this.Gerant;
  }
  
  public void setGerant(String playerName) {
    this.Gerant = playerName;
  }
  
  public List<String> getSecretaires() {
    return this.Secretaires;
  }
  
  public double getRevenues() {
    return this.Revenues;
  }
  
  public void setRevenues(double money) {
    this.Revenues = money;
  }
  
  public List<String> getStagiaires() {
    return this.Stagiaires;
  }
  
  public List<String> getCoGerant() {
    return this.CoGerant;
  }
  
  public List<String> getCommunityManagers() {
    return this.CommunityManagers;
  }
  
  public List<String> getUsersList() {
    ArrayList<String> users = new ArrayList<>();
    users.add(getGerant());
    if (getCoGerant() != null)
      users.addAll(getCoGerant());
    users.addAll(getCommunityManagers());
    users.addAll(getStagiaires());
    users.addAll(getSecretaires());
    users.addAll(getSalaries());
    return users;
  }

  public String getExactUsernameFromUsername(String username)
  {
    for(String username1 : getUsersList())
    {
      if(username1.equalsIgnoreCase(username))
      {
        return username1;
      }
    }
    return null;
  }
  
  public String getCompanyName() {
    return this.CompanyName;
  }
  
  public String getCategoryName() {
    return this.Categorie;
  }
  
  public void broadcastCompany(String[] msg) {
    for (String user : getUsersList()) {
      Player pUser = Bukkit.getPlayerExact(user);
      String message = StringUtils.join((Object[])msg, " ");
      if (pUser != null)
        pUser.sendMessage("§7[§c" + getCompanyName() + "§7] " + message); 
    } 
  }
  
  public void broadcastCompany(String msg) {
    for (String user : getUsersList()) {
      Player pUser = Bukkit.getPlayerExact(user);
      if (pUser != null)
        pUser.sendMessage("§7[§c" + getCompanyName() + "§7] " + msg); 
    } 
  }
  
  public void setRank(String username, String rank, Player p) {
    if (rank.equalsIgnoreCase("CoGerant")) {
      resetRank(username);
      getCoGerant().add(username);
    } else if (rank.equalsIgnoreCase("CommunityManager")) {
      resetRank(username);
      getCommunityManagers().add(username);
    } else if (rank.equalsIgnoreCase("Stagiaire")) {
      resetRank(username);
      getStagiaires().add(username);
    } else if (rank.equalsIgnoreCase("Secretaire")) {
      resetRank(username);
      getSecretaires().add(username);
    } else if (rank.equalsIgnoreCase("Salarie")) {
      resetRank(username);
      getSalaries().add(username);
    } else {
      p.sendMessage("§cCe rang n'existe pas!");
      return;
    } 
    broadcastCompany("§6Rang : §e" + rank + " §6attribué à §e" + username);
  }
  
  public void leaveAll() {
    for (String player : getUsersList())
      onLeft(player); 
  }
  
  public void onLeft(String p) {
    PlayerData d = PlayerData.getPlayerData(p);

    if (d != null) {
      d.companyName = "";
      d.companyCategory = null;
    }
    else
    {
      Main.Log(p + " a voulu leave son entreprise mais son playerData est null");
      return;
    }

    getRank(p).remove(p);
    Chat.leftFromCanal(p);
    String msg = MessageUtils.getMessageFromConfig("company-left");
    msg = msg.replaceAll("%player%", p);
    msg = msg.replaceAll("&", "§");
    broadcastCompany(msg);
  }
  
  public void onKick(String kicker, String victim) {
    getRank(victim).remove(victim);
    if (Bukkit.getPlayerExact(victim) != null) {
      String str = MessageUtils.getMessageFromConfig("kicked-from-company");
      str = str.replaceAll("%kicker%", kicker);
      str = str.replaceAll("%company%", getCompanyName());
      MessageUtils.sendMessage((CommandSender)Bukkit.getPlayerExact(victim), str);
    } 
    String msg = MessageUtils.getMessageFromConfig("kicked-successfuly");
    msg = msg.replaceAll("%victim%", victim);
    MessageUtils.sendMessage((CommandSender)Bukkit.getPlayerExact(kicker), msg);
   }
  
  public void resetRank(String victim) {
    getRank(victim).remove(victim);
  }
  
  public List<String> getRank(String username) {
    if (getGerant().equalsIgnoreCase(username)) {
      List<String> Gerant = new ArrayList<>();
      Gerant.add(username);
      return Gerant;
    } 
    if (getCoGerant().contains(username))
      return getCoGerant(); 
    if (getCommunityManagers().contains(username))
      return getCommunityManagers(); 
    if (getSecretaires().contains(username))
      return getSecretaires(); 
    if (getStagiaires().contains(username))
      return getStagiaires(); 
    return getSalaries();
  }
  
  public String getRankName(String username) {
    if (getGerant().equalsIgnoreCase(username))
      return "Gerant"; 
    if (getCoGerant().contains(username))
      return "CoGerant"; 
    if (getCommunityManagers().contains(username))
      return "CommunityManager"; 
    if (getSecretaires().contains(username))
      return "Secretaire"; 
    if (getStagiaires().contains(username))
      return "Stagiaire"; 
    return "Salarie";
  }
  
  public int totalRepartition() {
    return this.UsersRepartition + this.StagiairesRepartition + this.SecretairesRepartition + this.CommunityManagersRepartition + this.CoGerantRepartition + this.GerantRepartition;
  }
  
  public boolean setRepartition(String rank, int repartition) {
    if (rank.equalsIgnoreCase("Gerant")) {
      int temp = this.GerantRepartition;
      this.GerantRepartition = 0;
      if (totalRepartition() + repartition > 100) {
        this.GerantRepartition = temp;
        return false;
      } 
      this.GerantRepartition = repartition;
      return true;
    } 
    if (rank.equalsIgnoreCase("CoGerant")) {
      int temp = this.CoGerantRepartition;
      this.CoGerantRepartition = 0;
      if (totalRepartition() + repartition > 100) {
        this.CoGerantRepartition = temp;
        return false;
      } 
      this.CoGerantRepartition = repartition;
      return true;
    } 
    if (rank.equalsIgnoreCase("CommunityManager")) {
      int temp = this.CommunityManagersRepartition;
      this.CommunityManagersRepartition = 0;
      if (totalRepartition() + repartition > 100) {
        this.CommunityManagersRepartition = temp;
        return false;
      } 
      this.CommunityManagersRepartition = repartition;
      return true;
    } 
    if (rank.equalsIgnoreCase("Stagiaire")) {
      int temp = this.StagiairesRepartition;
      this.StagiairesRepartition = 0;
      if (totalRepartition() + repartition > 100) {
        this.StagiairesRepartition = temp;
        return false;
      } 
      this.StagiairesRepartition = repartition;
      return true;
    } 
    if (rank.equalsIgnoreCase("Secretaire")) {
      int temp = this.SecretairesRepartition;
      this.SecretairesRepartition = 0;
      if (totalRepartition() + repartition > 100) {
        this.SecretairesRepartition = temp;
        return false;
      } 
      this.SecretairesRepartition = repartition;
      return true;
    } 
    if (rank.equalsIgnoreCase("Salarie")) {
      int temp = this.UsersRepartition;
      this.UsersRepartition = 0;
      if (totalRepartition() + repartition > 100) {
        this.UsersRepartition = temp;
        return false;
      } 
      this.UsersRepartition = repartition;
      return true;
    } 
    return false;
  }
  
  public void printAchievements(Player p) {
    p.sendMessage(ChatColor.DARK_PURPLE + "------ Succès débloqués ------");
    for (Achievements a : this.achievements)
      p.sendMessage("~" + a.getNameAchievements()); 
    if (this.achievements.size() == 0)
      p.sendMessage(ChatColor.RED + "Aucun succès débloqué"); 
    p.sendMessage(ChatColor.DARK_PURPLE + "------------------------------");
  }
  
  public boolean hasParcelle(String name) {
    for (Map.Entry<String, String> parcelle : getOwnedParcelle().entrySet()) {
      if (((String)parcelle.getKey()).equals(name))
        return true; 
    } 
    return false;
  }
  
  public void displayRepartition(CommandSender sender) {
    sender.sendMessage("§7► §bEntreprise Repartition §7◄");
    sender.sendMessage("§4- Gerant : §c" + this.GerantRepartition + "%");
    sender.sendMessage("§c- CoGerant : §c" + this.CoGerantRepartition + "%");
    sender.sendMessage("§d- CommunityManager : §c" + this.CommunityManagersRepartition + "%");
    sender.sendMessage("§a- Secretaire : §c" + this.SecretairesRepartition + "%");
    sender.sendMessage("§b- Stagiaire : §c" + this.StagiairesRepartition + "%");
    sender.sendMessage("§b- Salarie : §c" + this.UsersRepartition + "%");
  }
  
  public void displayOwnedParcellesName(CommandSender sender) {
    sender.sendMessage("§7► §2Nom des Parcelles §7◄");
    for (Map.Entry<String, String> owned : getOwnedParcelle().entrySet())
      sender.sendMessage("§a Entrée: " + (String)owned.getKey() + " §3 Sortie: " + (String)owned.getValue()); 
    if (getOwnedParcelle().values().size() == 0)
      sender.sendMessage("§c Aucune parcelle"); 
  }
  
  public boolean isMember(String name) {
    for (String user : getUsersList()) {
      if (user.equals(name))
        return true; 
    } 
    return false;
  }
  
  public ProtectedRegion getParcelle(String parcelle) {
    if (getOwnedParcelleIn(parcelle) != null) {
      ProtectedRegion r = RegionUtils.getRegionByName(parcelle, "world1");
      if (r != null)
        return r; 
    } 
    return null;
  }
  
  public boolean isRegionMember(String parcelle, String name) {
    if (isMember(name)) {
      ProtectedRegion r = getParcelle(parcelle);
      if (r != null) {
        DefaultDomain d = r.getMembers();
        for (String nameP : d.getPlayers()) {
          if (nameP.equalsIgnoreCase(name))
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  public boolean isParcelleIn(String entree) {
    for (String in : this.parcellesowned.keySet()) {
      if (in.contains(entree))
        return true; 
    } 
    return false;
  }
  
  public void setPlayerRegionMember(CommandSender sender, String name, String parcelle) {
    if (isMember(name)) {
      if (!isRegionMember(parcelle, name)) {
        ProtectedRegion r = getParcelle(parcelle);
        if (r != null) {
          DefaultDomain d = r.getMembers();
          d.addPlayer(name);
          r.setMembers(d);
          broadcastCompany(ChatColor.DARK_GREEN + name + ChatColor.GREEN + " est désormais membre de la parcelle " + ChatColor.DARK_GREEN + parcelle);
        } else {
          sender.sendMessage(ChatColor.RED + "Votre entreprise n'a pas de parcelle qui se nomme ainsi !");
        } 
      } else {
        sender.sendMessage(ChatColor.RED + "Ce joueur est déjà membre de la parcelle !");
      } 
    } else {
      sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas un membre de l'entreprise");
    } 
  }
  
  public static boolean rankExist(String rank) {
    return (rank.equalsIgnoreCase("Gerant") || rank.equalsIgnoreCase("CoGerant") || rank.equalsIgnoreCase("CommunityManager") || rank.equalsIgnoreCase("Secretaire") || rank.equalsIgnoreCase("Stagiaire") || rank.equalsIgnoreCase("Salarie"));
  }
  
  public static void saveDatas() {
    Main.Log("Company Data saving...");
    List<String> removeCompany = new ArrayList<>();
    for (Map.Entry<String, CompanyData> entry : Companies.entrySet()) {
      if (!entry.getValue().getCompanyName().isEmpty())
        entry.getValue().saveData();
      int connected = entry.getValue().getConnectedUsers();
      if (connected == 0)
        removeCompany.add(entry.getKey()); 
    } 
    Companies.keySet().removeAll(removeCompany);
    Main.Log("Company Data saved");
  }
  
  public ArrayList<String> getParcelles() {
    return new ArrayList<>(getOwnedParcelle().keySet());
  }
  
  public boolean isParcelle(String id) {
    for (String entree : getOwnedParcelle().keySet()) {
      if (entree.equals(id))
        return true; 
    } 
    return false;
  }
  
  public int getConnectedUsers() {
    int count = 0;
    for (String user : getUsersList()) {
      if (user != null && PlayerUtils.isConnected(user))
        count++; 
    } 
    return count;
  }
  
  public int getEffectif() {
    int size = getStagiaires().size() + getSalaries().size() + getSecretaires().size() + getCommunityManagers().size() + getCoGerant().size() + 1;
    return size;
  }
  
  public int getNbVenteTotal() {
    return this.NbVenteTotal;
  }
  
  public void setNbVenteTotal(int val) {
    this.NbVenteTotal = val;
  }
  
  public List<String> getCoGerants() {
    return this.CoGerant;
  }
  
  public int getCoGerantsRepartition() {
    return this.CoGerantRepartition;
  }
  
  public int getCommunityManagersRepartition() {
    return this.CommunityManagersRepartition;
  }
  
  public ICompany getCompanyActivity() {
    return new ICompany() {
        public String getType() {
          return CompanyData.this.Categorie;
        }
        
        public String getCompteurName() {
          return CompanyData.this.company.getCompteurName();
        }
        
        public int getCompteur() {
          return CompanyData.this.company.getCompteur();
        }
      };
  }
  
  public String getCompanyCategorie() {
    return this.Categorie;
  }
  
  public int getGerantRepartition() {
    return this.GerantRepartition;
  }
  
  public int getLevel() {
    return this.Level;
  }
  
  public int getMaxLevel() {
    return 250;
  }
  
  public int getMaxMembersCount() {
    return this.nbSalaries;
  }
  
  public HashMap<String, String> getParcellesOwned() {
    return this.parcellesowned;
  }
  
  public double getRevenue() {
    return this.Revenues;
  }
  
  public int getSalariesRepartition() {
    return this.UsersRepartition;
  }
  
  public int getStagiairesRepartition() {
    return this.StagiairesRepartition;
  }
  
  public double getXpToReachForLevelUp() {
    return this.xpToReachForLevelUp;
  }
  
  public List<ICompanyAchievements> getCompanyAchievements() {
    List<ICompanyAchievements> achievements = new ArrayList<>();
    for (Achievements a : this.achievements) {
      achievements.add(new ICompanyAchievements() {
            public String getName() {
              return a.getNameAchievements();
            }
          });
    } 
    return achievements;
  }
}
