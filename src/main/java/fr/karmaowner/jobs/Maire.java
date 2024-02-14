package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.events.JobsEvents;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.FileUtils;
import fr.karmaowner.utils.ItemUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Maire extends Jobs implements Legal, CanChangeJob, Radio, JobsMairie {
  public static ArrayList<String> laws = new ArrayList<>();
  
  public static FileUtils fileLaws = new FileUtils("lois", "");
  
  public static ArrayList<String> gardes = new ArrayList<>();
  
  public static String maire;
  
  public static TaskCreator couvrefeu;
  
  public static String prefix = "§a[§d§lMaire§a]";
  
  public static ArrayList<String> mandatGang = new ArrayList<>();
  
  public enum Action {
    CADAVRE("§cSe promener avec le cadavre", 4281, (byte)0, null, null);
    
    private byte dataItem;
    
    private int idItem;
    
    private Byte data;
    
    private Integer id;
    
    private String displayName;
    
    Action(String displayName, int idItem, byte dataItem, Integer id, Byte data) {
      this.displayName = displayName;
      this.idItem = idItem;
      this.dataItem = dataItem;
      this.id = id;
      this.data = data;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.idItem, this.dataItem, 1, this.displayName, null);
    }
    
    public Integer getId() {
      return this.id;
    }
    
    public int getIdItem() {
      return this.idItem;
    }
    
    public Byte getData() {
      return this.data;
    }
    
    public String getDisplayName() {
      return this.displayName;
    }
    
    public Action getAction(String displayName) {
      for (Action a : values()) {
        if (a.getDisplayName().equals(displayName))
          return a; 
      } 
      return null;
    }
  }
  
  public Maire(String player) {
    super(player);
    maire = player;
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public static void loadJobData() {
    Jobs.Job.MAIRE.loadJobClothes();
    loadMandat();
    loadLaws();
    loadGardes();
  }
  
  public static boolean isGarde(String player) {
    for (String g : gardes) {
      if (g.equalsIgnoreCase(player))
        return true; 
    } 
    return false;
  }
  
  public static void addLaw(String law) {
    laws.add(law);
    broadcastMaire("§a Le Maire vient de rajouter une nouvelle loi §4/lois");
  }

  public static void broadcastMaire(String msg) {
    Bukkit.broadcastMessage(prefix + " " + msg);
  }
  
  public static boolean deleteLaw(int numLaw) {
    if (numLaw >= 0 && numLaw < laws.size()) {
      laws.remove(numLaw);
      return true;
    } 
    return false;
  }
  
  public static void listLaws(String playername) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      int i = 0;
      p.sendMessage("§6§k-§c-------------§4Lois§c--------------§6§k-");
      for (String l : laws) {
        p.sendMessage("§fLoi " + i + ":");
        p.sendMessage("§7-" + l);
        i++;
      } 
      if (laws.isEmpty())
        p.sendMessage("§4Aucune loi instaurée pour le moment."); 
      p.sendMessage("§6§k-§c------------------------------§6§k-");
    } 
  }
  
  public static void loadLaws() {
    if (!fileLaws.directoryExist())
      fileLaws.createFile(); 
    fileLaws.loadFileConfiguration();
    if (fileLaws.getFileConfiguration() != null) {
      FileConfiguration f = fileLaws.getFileConfiguration();
      if (f.getStringList("lois") != null)
        laws.addAll(f.getStringList("lois")); 
    } 
  }
  
  public static void saveLaws() {
    FileConfiguration f = fileLaws.getFileConfiguration();
    f.set("lois", laws);
    fileLaws.saveConfig();
  }
  
  public static void loadGardes() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String key = "gardes";
    if (f.get(key) != null)
      gardes.addAll(f.getStringList(key)); 
  }
  
  public static void loadMandat() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String key = "mandat";
    if (f.get(key) != null)
      mandatGang.addAll(f.getStringList(key)); 
  }
  
  public static void saveMandat() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String key = "mandat";
    f.set(key, mandatGang);
  }
  
  public static void saveGardes() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String key = "gardes";
    f.set(key, gardes);
  }
  
  public static void sendMessageGardes(String message) {
    for (String g : gardes) {
      Player p = Bukkit.getPlayerExact(g);
      if (p != null)
        p.sendMessage(prefix + " §c" + message); 
    }

    for(Player p : Bukkit.getOnlinePlayers())
    {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      if(data.selectedJob instanceof ChefGarde) p.sendMessage(prefix + " §c" + message);
    }
  }
  
  public static void sendMessage(String msg) {
    Player p = Bukkit.getPlayerExact(maire);
    if (p != null)
      p.sendMessage(prefix + " " + msg); 
  }
  
  public static boolean listGardes() {
    for (String g : gardes)
      sendMessage("§2" + g + " §aest votre garde."); 
    return !gardes.isEmpty();
  }
  
  public static boolean isConnected() {
    Player p = Bukkit.getPlayerExact(maire);
    if (p != null)
      return true; 
    return false;
  }
  
  public static Player getMaire() {
    return Bukkit.getPlayerExact(maire);
  }
  
  public static void MaireDemission() {
    broadcastMaire("Le Maire vient de démissionner.");
    sendMessageGardes("Le Maire a démissionné. Vous venez de perdre votre poste.");
    for (String g : gardes) {
      if (!maire.equals(g)) {
        PlayerData data = PlayerData.getPlayerData(g);
        if (data != null)
          JobsEvents.changePlayerJob(data, Jobs.Job.CIVILE.getName(), g); 
      } 
    } 
    gardes.clear();
    laws.clear();
    maire = null;
  }
  
  public static boolean hasMandat(String gangName) {
    for (String gang : mandatGang) {
      if (gang.equals(gangName))
        return true; 
    } 
    return false;
  }
  
  public static void removeMandat(String gangName) {
    mandatGang.remove(gangName);
  }
  
  public static void saveJobData() {
    saveLaws();
    saveGardes();
    saveMandat();
  }
  
  public void loadData() {
    maire = getPlayer();
  }
  
  public List<String> jobs() {
    return Arrays.asList(Job.MAIREADJOINT.getName(), Job.CHEFGARDE.getName());
  }
}
