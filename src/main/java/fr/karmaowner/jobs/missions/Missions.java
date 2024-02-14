package fr.karmaowner.jobs.missions;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.grades.Grade;
import fr.karmaowner.jobs.grades.JobGrades;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.missions.type.GeneralType;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.RecordBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class Missions extends Jobs implements hasGrade {
  public static final String MISSIONSINVNAME = ChatColor.DARK_RED + "Missions";
  
  public static final int ITEMPROGRESS = 5502;
  
  private Inventory MissionsInventory;
  
  protected JobGrades grade;
  
  public ArrayList<Mission> randomMissions;
  
  public ArrayList<Mission> missions = new ArrayList<>();
  
  public ArrayList<UUID> finishedMissions = new ArrayList<>();
  
  public static HashMap<Player, String[]> creatingMissions = (HashMap)new HashMap<>();
  
  public static HashMap<Jobs.Job, ArrayList<Mission>> allMissions = new HashMap<>();
  
  private Mission inProgress;
  
  private Timestamp startMission;
  
  public Missions(String player) {
    super(player);
    this.randomMissions = new ArrayList<>();
    this.startMission = new Timestamp(System.currentTimeMillis());
    this.MissionsInventory = Main.INSTANCE.getServer().createInventory(null, 27, MISSIONSINVNAME);
  }
  
  public int hasRandomMission(Mission m) {
    int i = 0;
    for (Mission m2 : this.randomMissions) {
      if (m2.getUUID().toString().equals(m.getUUID().toString()))
        return i; 
      i++;
    } 
    return -1;
  }
  
  public Inventory getMissionsInventory() {
    return this.MissionsInventory;
  }
  
  public JobGrades getGrade() {
    return this.grade;
  }
  
  public static boolean copy(String uuid, String job) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    Mission searched = null;
    for (ArrayList<Mission> m : allMissions.values()) {
      for (Mission mi : m) {
        if (mi.getUUID().toString().contains(uuid)) {
          searched = mi.copy();
          break;
        } 
      } 
      if (searched != null)
        break; 
    } 
    if (searched == null)
      return false; 
    if (!Jobs.Job.isJob(job))
      return false; 
    int size = 0;
    if (allMissions.get(Jobs.Job.getJobByName(job)) != null) {
      size = ((ArrayList)allMissions.get(Jobs.Job.getJobByName(job))).size();
    } else {
      allMissions.put(Jobs.Job.getJobByName(job), new ArrayList<>());
    } 
    String key = "Missions." + job + "." + size;
    f.set(key + ".name", searched.getName());
    f.set(key + ".displayName", searched.getItem().getItemMeta().getDisplayName());
    f.set(key + ".xp", Integer.valueOf(searched.getXp()));
    f.set(key + ".id", Integer.valueOf(searched.getItem().getTypeId()));
    f.set(key + ".byte", Byte.valueOf(searched.getItem().getData().getData()));
    f.set(key + ".price", Integer.valueOf(searched.getPrice()));
    UUID id = UUID.randomUUID();
    searched.setUUID(id);
    f.set(key + ".uuid", id.toString());
    if (searched.getType() instanceof GeneralType) {
      GeneralType gt = (GeneralType)searched.getType();
      gt.saveData(key);
    } 
    Main.INSTANCE.saveConfig();
    ((ArrayList<Mission>)allMissions.get(Jobs.Job.getJobByName(job))).add(searched);
    for (Player ply : Bukkit.getOnlinePlayers()) {
      PlayerData data = PlayerData.getPlayerData(ply.getName());
      if (data.selectedJob.getFeatures().getName().equalsIgnoreCase(job)) {
        Missions m = (Missions)data.selectedJob;
        m.missions.add(searched.copy());
        ply.sendMessage(ChatColor.GREEN + "Nouvelle mission disponible: " + ChatColor.DARK_GREEN + searched.getName());
        ply.sendMessage(ChatColor.BLUE + "Pour plus d'info: /jobs missions open");
      } 
    } 
    return true;
  }
  
  private void defineRandomMissions() {
    Timestamp now = new Timestamp(System.currentTimeMillis());
    if (now.getTime() - this.startMission.getTime() >= 43200000L) {
      this.startMission = new Timestamp(System.currentTimeMillis());
      Jobs.Job j = Jobs.Job.getFeatures(getClass());
      ArrayList<Integer> added = new ArrayList<>();
      if (!allMissions.get(j).isEmpty())
        for (int i = 0; i < 4; i++) {
          int rand = generateRandomNumberWithExcepts(0, allMissions.size(), added);
          this.randomMissions.add(((Mission)((ArrayList<Mission>)allMissions.get(j)).get(rand)).copy());
          added.add(rand);
        }  
    } 
  }
  
  public int generateRandomNumberWithExcepts(int start, int end, List<Integer> excepts) {
    int size = excepts.size();
    int range = end - start + 1 - size;
    Random r = new Random();
    int randNum = r.nextInt(range) + start;
    excepts.sort(null);
    int i = 0;
    for (Iterator<Integer> iterator = excepts.iterator(); iterator.hasNext(); ) {
      int except = (Integer) iterator.next();
      if (randNum < except - i)
        return randNum + i; 
      i++;
    } 
    return randNum + i;
  }
  
  public void fillMissionsInventory() {
    this.MissionsInventory.clear();
    int i = 0;
    for (Mission m : this.missions) {
      if (i >= 25)
        break; 
      if (!isFinished(m.getUUID().toString())) {
        this.MissionsInventory.setItem(i, m.getItem());
        i++;
      } 
    } 
    defineRandomMissions();
    for (Mission m : this.randomMissions) {
      if (i >= 25)
        break; 
      this.MissionsInventory.setItem(i, m.getItem());
      i++;
    } 
    Grade next = this.grade.getGrades().nextGrade(this.grade.getPoints());
    ArrayList<String> lores = new ArrayList<>();
    lores.add(ChatColor.GOLD + "Grade: " + this.grade.getGrade().getNom());
    lores.add(ChatColor.YELLOW + "points: " + this.grade.getPoints());
    if (next != null) {
      lores.add(ChatColor.DARK_RED + "grade suivant: " + next.getNom());
      lores.add(ChatColor.DARK_RED + "points requis: " + next.getPoints());
    } 
    lores.add(ChatColor.BLUE + "missions accomplies: " + this.grade.getNbMissions());
    ItemStack aboutProgression = ItemUtils.getItem(5502, (byte)0, 1, ChatColor.RED + "Votre progression", lores);
    this.MissionsInventory.setItem(26, aboutProgression);
  }
  
  public boolean isFinished(String uuid) {
    for (UUID id : this.finishedMissions) {
      if (id.toString().equals(uuid))
        return true; 
    } 
    return false;
  }
  
  public Mission getMissionByItem(ItemStack item) {
    if (item != null && item.getItemMeta() != null)
      for (Mission m : this.missions) {
        ItemStack i = m.getItem();
        if (i != null && 
          i.getItemMeta() != null) {
          String id = ((String)i.getItemMeta().getLore().get(i.getItemMeta().getLore().size() - 1)).split(" ")[1];
          String id2 = ((String)item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)).split(" ")[1];
          if (id.equals(id2))
            return m; 
        } 
      }  
    return null;
  }
  
  public Mission getMissionByName(String name) {
    for (Mission m : this.missions) {
      if (m.getName().equals(name))
        return m; 
    } 
    return null;
  }
  
  public Mission getInProgress() {
    return this.inProgress;
  }
  
  public void setInProgress(Mission inProgress) {
    this.inProgress = inProgress;
  }
  
  public static Mission getMissionByUUID(String uuid, Jobs.Job j) {
    for (Mission m : allMissions.get(j)) {
      if (m.getUUID().toString().contains(uuid))
        return m; 
    } 
    return null;
  }
  
  public Mission getMissionByUUIDInPlayer(String uuid) {
    for (Mission m : this.missions) {
      if (m.getUUID().toString().contains(uuid))
        return m; 
    } 
    return null;
  }
  
  public static Mission getMissionByName(String name, Jobs.Job j) {
    for (Mission m : allMissions.get(j)) {
      if (m.getName().toString().equals(name))
        return m; 
    } 
    return null;
  }
  
  public static int getMissionPositionInFile(String uuid, Jobs.Job j) {
    int i = 0;
    String name = "Missions";
    if (Main.INSTANCE.getConfig().getConfigurationSection(name + "." + j.getName()) != null)
      for (String key : Main.INSTANCE.getConfig().getConfigurationSection(name + "." + j.getName()).getKeys(false)) {
        String uid = Main.INSTANCE.getConfig().getString(name + "." + j.getName() + "." + key + ".uuid");
        if (uid.equals(uuid))
          return Integer.parseInt(key); 
      }  
    return -1;
  }
  
  public static void deleteMission(String uuid, Jobs.Job j, Player p) {
    if (j != null) {
      Mission mi = getMissionByUUID(uuid, j);
      if (mi != null) {
        int index = getMissionPositionInFile(mi.getUUID().toString(), j);
        FileConfiguration f = Main.INSTANCE.getConfig();
        f.set("Missions." + j.getName() + "." + index, null);
        Main.INSTANCE.saveConfig();
        for (Player ply : Bukkit.getOnlinePlayers()) {
          PlayerData data = PlayerData.getPlayerData(ply.getName());
          if (data.selectedJob instanceof Missions) {
            Missions job = (Missions)data.selectedJob;
            if (job.getInProgress() != null && job.getInProgress().getUUID().toString().contains(uuid)) {
              GeneralType gt = (GeneralType)job.getInProgress().getType();
              gt.failed(ply);
            } 
            Mission mp = job.getMissionByUUIDInPlayer(uuid);
            job.randomMissions.remove(mp);
            job.missions.remove(mp);
          } 
        } 
        allMissions.get(j).remove(mi);
        p.sendMessage(ChatColor.GREEN + "Mission supprimée avec succès !");
      } else {
        p.sendMessage(ChatColor.RED + "Cette mission n'existe pas !");
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Ce métier n'existe pas !");
    } 
  }
  
  public void loadData() {
    super.loadData();
    PlayerData data = PlayerData.getPlayerData(getPlayer());
    Jobs.Job j = Jobs.Job.getFeatures(getClass());
    allMissions.computeIfAbsent(j, k -> new ArrayList<>());
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("jobs_missions_data")
          .where(new CustomEntry("pseudo", data.getPlayerName()))
          .where(new CustomEntry("jobname", j.getName().toLowerCase()), RecordBuilder.LINK.AND).toString());
      if (results.count() == 1) {
        ResultSet rs = results.getActualResult();
        this.startMission = new Timestamp(rs.getLong("startMission"));
        List<String> finished = Arrays.asList(rs.getString("MissionsFinished").split(";"));
        finished = ((String)finished.get(0)).equals("") ? new ArrayList<>() : finished;
        for (String elt : finished)
          this.finishedMissions.add(UUID.fromString(elt)); 
        List<String> random = Arrays.asList(rs.getString("randomMissionsAvailable").split(";"));
        random = ((String)random.get(0)).equals("") ? new ArrayList<>() : random;
        for (String elt : random) {
          Mission m = getMissionByUUID(elt, j);
          if (m != null)
            this.randomMissions.add(m.copy()); 
        } 
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    for (Mission m : allMissions.get(j))
      this.missions.add(m.copy()); 
  }
  
  public void saveData() {
    super.saveData();
    PlayerData data = PlayerData.getPlayerData(getPlayer());
    Jobs.Job j = Jobs.Job.getFeatures(getClass());
    List<String> stringUUID = new ArrayList<>();
    for (UUID uuid : this.finishedMissions)
      stringUUID.add(uuid.toString()); 
    List<String> randomUUID = new ArrayList<>();
    for (Mission m : this.randomMissions)
      randomUUID.add(m.getUUID().toString()); 
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("jobs_missions_data")
          .where(new CustomEntry("pseudo", data.getPlayerName()))
          .where(new CustomEntry("jobname", j.getName().toLowerCase()), RecordBuilder.LINK.AND).toString());
      if (results.count() == 1) {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("MissionsFinished", StringUtils.join(stringUUID, ';'));
        fields.put("randomMissionsAvailable", StringUtils.join(randomUUID, ';'));
        if (this.startMission != null)
          fields.put("startMission", this.startMission.getTime());
        try {
          Main.Database.update(RecordBuilder.build().update(fields, "jobs_missions_data")
              .where(new CustomEntry("pseudo", data.getPlayerName()))
              .where(new CustomEntry("jobname", j.getName().toLowerCase()), RecordBuilder.LINK.AND).toString());
        } catch (SQLException e) {
          e.printStackTrace();
        } 
      } else {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("MissionsFinished", StringUtils.join(stringUUID, ";"));
        fields.put("randomMissionsAvailable", StringUtils.join(randomUUID, ";"));
        fields.put("jobname", j.getName().toLowerCase());
        fields.put("pseudo", data.getPlayerName());
        if (this.startMission != null)
          fields.put("startMission", this.startMission.getTime());
        try {
          Main.Database.update(RecordBuilder.build().insert(fields, "jobs_missions_data").toString());
        } catch (SQLException e) {
          e.printStackTrace();
        } 
      } 
    } catch (SQLException e1) {
      e1.printStackTrace();
    } 
  }
  
  public static void open(Player p) {
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.selectedJob instanceof Missions) {
      Missions job = (Missions)data.selectedJob;
      job.fillMissionsInventory();
      p.openInventory(job.getMissionsInventory());
    } else {
      p.sendMessage(ChatColor.RED + "Vous ne pouvez pas effectuer cette action, votre métier ne vous le permet pas !");
    } 
  }
  
  public static boolean isPlayerCreatingMissions(Player p) {
    for (Player p1 : creatingMissions.keySet()) {
      if (p1 == p)
        return true; 
    } 
    return false;
  }
  
  public static String[] getArgsThread(Player p) {
    for (Map.Entry<Player, String[]> data : creatingMissions.entrySet()) {
      if (data.getKey() == p)
        return data.getValue(); 
    } 
    return null;
  }
  
  public static void loadDataMission() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    if (f.getConfigurationSection("Missions") != null)
      for (String key : f.getConfigurationSection("Missions").getKeys(false)) {
        Jobs.Job j = Jobs.Job.getJobByName(key);
        allMissions.put(j, new ArrayList<>());
        for (String key1 : f.getConfigurationSection("Missions." + key).getKeys(false)) {
          String token = "Missions." + key + "." + key1;
          String Name = f.getString(token + ".name");
          String displayName = f.getString(token + ".displayName");
          int xp = f.getInt(token + ".xp");
          int id = f.getInt(token + ".id");
          byte Data = Byte.parseByte(f.getString(token + ".byte"));
          int price = f.getInt(token + ".price");
          String objective = f.getString(token + ".objective");
          UUID uuid = UUID.fromString(f.getString(token + ".uuid"));
          ItemStack item = ItemUtils.getItem(id, Data, 1, displayName, null);
          ((ArrayList<Mission>)allMissions.get(j)).add(new Mission(Name, item, xp, (MissionType)GeneralType.loadData(token), price, uuid, objective));
        } 
      }  
  }
  
  public static boolean isMissionExist(String MissionName, Jobs.Job j) {
    for (Map.Entry<Jobs.Job, ArrayList<Mission>> mission : allMissions.entrySet()) {
      if (((Jobs.Job)mission.getKey()).getName().equals(j.getName()))
        for (Mission m : mission.getValue()) {
          if (m.getName().equals(MissionName))
            return true; 
        }  
    } 
    return false;
  }
  
  public static Mission saveDataMission(HashMap<String, Object> infos) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String job = (String)infos.get("whois");
    int size = 0;
    if (allMissions.get(Jobs.Job.getJobByName(job)) != null) {
      size = allMissions.get(Job.getJobByName(job)).size();
    } else {
      allMissions.put(Jobs.Job.getJobByName(job), new ArrayList<>());
    } 
    String key = "Missions." + job + "." + size;
    f.set(key + ".name", infos.get("name"));
    f.set(key + ".objective", infos.get("objective"));
    f.set(key + ".displayName", infos.get("displayname"));
    f.set(key + ".xp", (Integer) infos.get("xp"));
    ItemStack item = (ItemStack)infos.get("item");
    f.set(key + ".id", item.getTypeId());
    f.set(key + ".byte", item.getData().getData());
    f.set(key + ".price", ((Integer) infos.get("price")).intValue());
    UUID id = UUID.randomUUID();
    f.set(key + ".uuid", id.toString());
    GeneralType.saveData(key, infos);
    Main.INSTANCE.saveConfig();
    Mission m = new Mission((String)infos.get("name"), item, (Integer) infos.get("xp"), (MissionType)GeneralType.loadData(key), (Integer) infos.get("price"), id, (String)infos.get("objective"));
    ((ArrayList<Mission>)allMissions.get(Jobs.Job.getJobByName(job))).add(m);
    return m;
  }
  
  public static void createThread(final Player p) {
    final HashMap<String, Object> infos = new HashMap<>();
    if (!isPlayerCreatingMissions(p)) {
      p.sendMessage(ChatColor.GREEN + "Création d'une mission acceptée !");
      creatingMissions.put(p, null);
      new TaskCreator(new CustomRunnable() {
            private int i = 0;
            
            private int counter = 0;
            
            public void customRun() {
              if (!p.isOnline()) {
                cancel();
                return;
              } 
              if (--this.counter <= 0) {
                Missions.MessageSendingThread(this.i, p, infos.get("task"));
                this.counter = 15;
              } 
              ArrayList<Object> state = Missions.commandsForThread(this.i, p, infos, this);
              if (state != null && !state.isEmpty()) {
                if (this.i != (Integer) state.get(1)) {
                  this.counter = 0;
                  this.i = (Integer) state.get(1);
                } 
                if ((Boolean) state.get(0)) {
                  Mission mData = Missions.saveDataMission(infos);
                  for (Player ply : Bukkit.getOnlinePlayers()) {
                    PlayerData data = PlayerData.getPlayerData(ply.getName());
                    if (data.selectedJob.getFeatures().getName().equalsIgnoreCase((String)infos.get("whois"))) {
                      Missions m = (Missions)data.selectedJob;
                      m.missions.add(mData.copy());
                      ply.sendMessage(ChatColor.GREEN + "Nouvelle mission disponible: " + ChatColor.DARK_GREEN + mData.getName());
                      ply.sendMessage(ChatColor.BLUE + "Pour plus d'info: /jobs missions open");
                    } 
                  } 
                  p.sendMessage(ChatColor.GREEN + "La Mission " + ChatColor.DARK_GREEN + (String)infos.get("name") + ChatColor.GREEN + " vient d'être créée");
                  Missions.creatingMissions.remove(p);
                  cancel();
                } 
              } 
            }
          }, false, 0L, 20L);
    } else {
      p.sendMessage(ChatColor.RED + "Vous êtes déjà en train de créer une mission !");
    } 
  }
  
  private static void MessageSendingThread(int step, Player p, Object task) {
    String taskName = (String)task;
    switch (step) {
      case 0:
        p.sendMessage(ChatColor.DARK_PURPLE + "Veuillez donner le nom de la mission en tapant la commande /jobs missions name <le nom>");
        break;
      case 1:
        p.sendMessage(ChatColor.LIGHT_PURPLE + "Veuillez désormais indiquer le nom d'affichage de la mission en tapant la commande /jobs missions displayname <le nom>");
        break;
      case 2:
        p.sendMessage(ChatColor.BLUE + "Veuillez maintenant donner le nombre d'xp de la mission en tapant la commande /jobs missions xp <nombre>");
        break;
      case 3:
        p.sendMessage(ChatColor.DARK_BLUE + "Veuillez maintenant donner l'id de l'item qui sera affiché dans l'inventaire de missions en tapant la commande /jobs missions item <id>:[byte]");
        break;
      case 4:
        p.sendMessage(ChatColor.AQUA + "Veuillez désormais donner la somme d'argent à remporter en tapant la commande /jobs missions price <montant>");
        break;
      case 5:
        p.sendMessage(ChatColor.DARK_AQUA + "Veuillez indiquer une mini-description de la mission en tapant la commande /jobs missions desc <description>");
        break;
      case 6:
        p.sendMessage(ChatColor.GREEN + "Veuillez indiquer la durée de la mission en secondes en tapant la commande /jobs missions duration <durée en secondes>");
        break;
      case 7:
        p.sendMessage(ChatColor.DARK_GREEN + "Veuillez désormais indiquer le type de mission en tapant la commande /jobs missions task <nom>");
        p.sendMessage(ChatColor.DARK_RED + "****** TYPE ******");
        p.sendMessage(ChatColor.RED + "KillTask - Consiste à tuer une ou plusieurs entités prédéfinies.");
        p.sendMessage(ChatColor.RED + "DefendTask - Le but étant de défendre pendant un certain temps défini une région.");
        p.sendMessage(ChatColor.RED + "FouilleTask - Consiste à fouiller une ou plusieurs joueurs.");
        p.sendMessage(ChatColor.RED + "MenotteTask - Consiste à menotter une ou plusieurs joueurs.");
        p.sendMessage(ChatColor.RED + "AmendeTask - Mettre une amende à un joueur ou plusieurs.");
        p.sendMessage(ChatColor.DARK_RED + "******************");
        break;
      case 8:
        if (taskName.equalsIgnoreCase("KillTask")) {
          p.sendMessage(ChatColor.GREEN + "Veuillez indiquer la catégorie de personne à éliminer en tapant la commande /jobs missions type <nom>");
          p.sendMessage(ChatColor.DARK_RED + "****** CATEGORIES ******");
          p.sendMessage(ChatColor.RED + "Entity - à la prochaine étape vous allez devoir choisir une entité.");
          p.sendMessage(ChatColor.RED + "Jobs - à la prochaine étape vous devrez choisir un ou plusieurs métiers.");
          p.sendMessage(ChatColor.DARK_RED + "******************");
          break;
        } 
        if (taskName.equalsIgnoreCase("DefendTask"))
          p.sendMessage(ChatColor.GREEN + "Veuillez indiquer le premier point (le point minimum) en tapant la commande /jobs missions firstPoint <x> <y> <z>"); 
        break;
      case 9:
        if (taskName.equalsIgnoreCase("KillTask")) {
          p.sendMessage(ChatColor.YELLOW + "Veuillez désormais donner le nom de l'entité (https://minecraft-ids.grahamedgecombe.com/entities) à éliminer en tapant la commande /jobs missions entity <nom>");
          break;
        } 
        if (taskName.equalsIgnoreCase("DefendTask"))
          p.sendMessage(ChatColor.YELLOW + "Veuillez indiquer le second point (le point maximum) en tapant la commande /jobs missions secondPoint <x> <y> <z>"); 
        break;
      case 10:
        if (taskName.equalsIgnoreCase("KillTask") || taskName.equalsIgnoreCase("MenotteTask") || taskName.equalsIgnoreCase("FouilleTask") || taskName
          .equalsIgnoreCase("AmendeTask")) {
          p.sendMessage(ChatColor.GOLD + "Veuillez indiquer le ou les métiers à éliminer en tapant la commande /jobs missions jobs <nom>[espace entre chaque métier]");
          p.sendMessage(ChatColor.DARK_RED + "****** METIERS ******");
          for (Jobs.Job j : Jobs.Job.values())
            p.sendMessage(ChatColor.RED + j.getName()); 
          p.sendMessage(ChatColor.DARK_RED + "******************");
          break;
        } 
        if (taskName.equalsIgnoreCase("DefendTask"))
          p.sendMessage(ChatColor.GOLD + "Veuillez indiquer la durée de défense en secondes en tapant la commande /jobs missions timeDefend <durée en secondes>"); 
        break;
      case 11:
        p.sendMessage(ChatColor.RED + "Veuillez maintenant indiquer le nombre d'individu à éliminer en tapant la commande /jobs missions count <nombre>");
        break;
      case 12:
        p.sendMessage(ChatColor.DARK_RED + "Veuillez maintenant indiquer le métier pour lequel la mission sera appliquée en tapant la commande /jobs missions whois <nom>");
        break;
      case 13:
        p.sendMessage(ChatColor.GRAY + "Veuillez désormais indiquer l'objectif de la mission en tapant la commande /jobs missions objective <l'objectif>");
        break;
    } 
  }
  
  private static ArrayList<Object> commandsForThread(int step, Player p, HashMap<String, Object> infos, CustomRunnable bukkitRunnable) {
    String[] args = getArgsThread(p);
    ArrayList<Object> about = new ArrayList<>();
    boolean finished = false;
    if (args != null) {
      if (args[0].equalsIgnoreCase("missions")) {
        if (args[1].equalsIgnoreCase("stop")) {
          bukkitRunnable.cancel();
          creatingMissions.remove(p);
          p.sendMessage(ChatColor.DARK_RED + "Création de la mission annulée !");
          return null;
        } 
        if (args[1].equalsIgnoreCase("name") && step == 0) {
          StringBuilder s = new StringBuilder();
          for (int i = 2; i < args.length; i++) {
            if (args[i] != null)
              s.append(args[i]); 
          } 
          if (!s.toString().isEmpty()) {
            infos.put("name", s.toString());
            step++;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez saisir le nom de la mission !");
          } 
        } else if (args[1].equalsIgnoreCase("displayname") && step == 1) {
          StringBuilder s = new StringBuilder();
          for (int i = 2; i < args.length; i++) {
            if (args[i] != null)
              s.append(args[i]); 
          } 
          if (!s.toString().isEmpty()) {
            infos.put("displayname", s.toString());
            step = 13;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez saisir le nom d'affichage de la mission !");
          } 
        } else if (args[1].equalsIgnoreCase("objective") && step == 13) {
          StringBuilder s = new StringBuilder();
          for (int i = 2; i < args.length; i++) {
            if (args[i] != null)
              s.append(args[i]); 
          } 
          if (!s.toString().isEmpty()) {
            infos.put("objective", s.toString());
            step = 2;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez saisir l'objectif de la mission !");
          } 
        } else if (args[1].equalsIgnoreCase("xp") && step == 2) {
          String s = args[2];
          if (s != null) {
            int xp = Integer.parseInt(s);
            infos.put("xp", xp);
            step++;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez saisir le nombre d'xp de la mission !");
          } 
        } else if (args[1].equalsIgnoreCase("item") && step == 3) {
          String s = args[2];
          if (s != null) {
            int id = Integer.parseInt(s.split(":")[0]);
            byte data = 0;
            if ((s.split(":")).length > 1)
              data = Byte.parseByte(s.split(":")[1]); 
            infos.put("item", ItemUtils.getItem(id, data, 1, (String)infos.get("displayname"), null));
            step++;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez saisir l'id de l'item de la mission !");
          } 
        } else if (args[1].equalsIgnoreCase("price") && step == 4) {
          String s = args[2];
          if (s != null) {
            int price = Integer.parseInt(s);
            infos.put("price", price);
            step++;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez saisir la somme d'argent à remporter à la fin de la mission !");
          } 
        } else if (args[1].equalsIgnoreCase("desc") && step == 5) {
          StringBuilder s = new StringBuilder();
          for (int i = 2; i < args.length; i++) {
            if (args[i] != null)
              s.append(args[i]); 
          } 
          if (!s.toString().isEmpty()) {
            infos.put("desc", s.toString());
            step++;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez indiquer une mini-description de la mission !");
          } 
        } else if (args[1].equalsIgnoreCase("duration") && step == 6) {
          String s = args[2];
          if (s != null) {
            long duration = (Integer.parseInt(s) * 1000L);
            infos.put("duration", duration);
            step++;
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez indiquer la durée de la mission en secondes !");
          } 
        } else if (args[1].equalsIgnoreCase("task") && step == 7) {
          String s = args[2];
          if (s != null) {
            infos.put("task", s);
            if (s.equalsIgnoreCase("FouilleTask") || s.equalsIgnoreCase("MenotteTask") || s.equalsIgnoreCase("AmendeTask")) {
              infos.put("type", "Jobs");
              step = 10;
            } else {
              step++;
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez indiquer le nom de la tâche en secondes !");
          } 
        } else if (args[1].equalsIgnoreCase("whois") && step == 12) {
          String s = args[2];
          if (s != null) {
            if (!isMissionExist(s, Jobs.Job.getJobByName(s))) {
              infos.put("whois", s);
              finished = true;
            } else {
              bukkitRunnable.cancel();
              creatingMissions.remove(p);
              p.sendMessage(ChatColor.DARK_RED + "Cette mission existe déjà: Création de la mission annulée !");
              return null;
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez saisir le nom du métier pour lequel la mission sera appliquée !");
          } 
        } else if (infos.get("task") != null) {
          if (((String)infos.get("task")).equalsIgnoreCase("KillTask")) {
            if (args[1].equalsIgnoreCase("type") && step == 8) {
              String s = args[2];
              if (s != null) {
                infos.put("type", s);
                if (s.equalsIgnoreCase("Entity")) {
                  step = 9;
                } else if (s.equalsIgnoreCase("Jobs")) {
                  step = 10;
                } 
              } else {
                p.sendMessage(ChatColor.RED + "Vous devez indiquer la catégorie de personne à éliminer !");
              } 
            } else if (args[1].equalsIgnoreCase("count") && step == 11) {
              String s = args[2];
              if (s != null) {
                int count = Integer.parseInt(s);
                infos.put("count", count);
                step++;
              } else {
                p.sendMessage(ChatColor.RED + "Vous devez saisir le nombre d'individu à éliminer !");
              } 
            } else if (infos.get("type") != null) {
              if (((String)infos.get("type")).equalsIgnoreCase("Entity")) {
                if (args[1].equalsIgnoreCase("entity") && step == 9) {
                  String s = args[2];
                  if (s != null) {
                    if (EntityType.fromName(s) != null) {
                      infos.put("entity", s);
                      step = 11;
                    } else {
                      p.sendMessage(ChatColor.RED + "Cette entité n'existe pas !");
                    } 
                  } else {
                    p.sendMessage(ChatColor.RED + "Vous devez indiquer le nom de l'entité !");
                  } 
                } 
              } else if (((String)infos.get("type")).equalsIgnoreCase("Jobs") && 
                args[1].equalsIgnoreCase("jobs") && step == 10) {
                List<String> jobs = new ArrayList<>();
                for (int i = 2; i < args.length; i++) {
                  if (args[i] != null)
                    if (Jobs.Job.isJob(args[i])) {
                      jobs.add(args[i]);
                    } else {
                      jobs = new ArrayList<>();
                      break;
                    }  
                } 
                if (!jobs.isEmpty()) {
                  infos.put("jobs", jobs);
                  step = 11;
                } else {
                  p.sendMessage(ChatColor.RED + "Vous devez saisir le ou les noms de métiers !");
                } 
              } 
            } 
          } else if (((String)infos.get("task")).equalsIgnoreCase("DefendTask")) {
            if (args[1].equalsIgnoreCase("firstPoint") && step == 8) {
              String x = args[2];
              String y = args[3];
              String z = args[4];
              if (x != null && y != null && z != null) {
                double x_ = Double.parseDouble(x);
                double y_ = Double.parseDouble(y);
                double z_ = Double.parseDouble(z);
                infos.put("firstPoint", new Vector(x_, y_, z_));
                step++;
              } else {
                p.sendMessage(ChatColor.RED + "Saisissez les coordonnées x, y, z du premier point (point minimum) !");
              } 
            } else if (args[1].equalsIgnoreCase("secondPoint") && step == 9) {
              String x = args[2];
              String y = args[3];
              String z = args[4];
              if (x != null && y != null && z != null) {
                double x_ = Double.parseDouble(x);
                double y_ = Double.parseDouble(y);
                double z_ = Double.parseDouble(z);
                infos.put("secondPoint", new Vector(x_, y_, z_));
                step++;
              } else {
                p.sendMessage(ChatColor.RED + "Saisissez les coordonnées x, y, z du second point (point maximum) !");
              } 
            } else if (args[1].equalsIgnoreCase("timeDefend") && step == 10) {
              String s = args[2];
              if (s != null) {
                long timeDefend = (Integer.parseInt(s) * 1000L);
                infos.put("timeDefend", timeDefend);
                step = 12;
              } else {
                p.sendMessage(ChatColor.RED + "Saisissez la durée de défense !");
              } 
            } 
          } else if (((String)infos.get("task")).equalsIgnoreCase("MenotteTask") || ((String)infos.get("task")).equalsIgnoreCase("FouilleTask") || ((String)infos
            .get("task")).equalsIgnoreCase("AmendeTask")) {
            if (args[1].equalsIgnoreCase("count") && step == 11) {
              String s = args[2];
              if (s != null) {
                int count = Integer.parseInt(s);
                infos.put("count", count);
                step++;
              } else {
                p.sendMessage(ChatColor.RED + "Vous devez saisir le nombre d'individu à éliminer !");
              } 
            } else if (((String)infos.get("type")).equalsIgnoreCase("Jobs") && 
              args[1].equalsIgnoreCase("jobs") && step == 10) {
              List<String> jobs = new ArrayList<>();
              for (int i = 2; i < args.length; i++) {
                if (args[i] != null)
                  if (Jobs.Job.isJob(args[i])) {
                    jobs.add(args[i]);
                  } else {
                    jobs = new ArrayList<>();
                    break;
                  }  
              } 
              if (!jobs.isEmpty()) {
                infos.put("jobs", jobs);
                step = 11;
              } else {
                p.sendMessage(ChatColor.RED + "Vous devez saisir le ou les noms de métiers !");
              } 
            } 
          } 
        } 
      } 
      about.add(finished);
      about.add(step);
      creatingMissions.put(p, null);
    } 
    return about;
  }
}
