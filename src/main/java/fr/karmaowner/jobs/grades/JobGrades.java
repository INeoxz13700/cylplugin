package fr.karmaowner.jobs.grades;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.jobs.Militaire;
import fr.karmaowner.jobs.XP;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.InventoryUtils;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.RecordBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.swing.plaf.ButtonUI;

public class JobGrades {
  private XP xp;
  
  private XP malus;
  
  private XP nbMissions;
  
  private XP timer;
  
  private int points;
  
  private int pointSuppl;
  
  private Grade grade;
  
  private Grades grades;
  
  private String player;
  
  private TaskCreator timeTask;
  
  public JobGrades(Grades g, String playername, boolean TimerTask) {
    this.grades = g;
    this.player = playername;
    this.xp = new XP("xp");
    this.malus = new XP("malus");
    this.nbMissions = new XP("missions");
    this.timer = new XP("timer");
    this.points = 0;
    this.pointSuppl = 0;
    if (TimerTask)
      this.timeTask = startTaskTimer(); 
    setGrade(this.grades.getGrade(this.points));
  }
  
  public TaskCreator startTaskTimer() {
    return new TaskCreator(new CustomRunnable() {
          public void customRun() {
            if (Bukkit.getPlayerExact(JobGrades.this.player) == null) {
              cancel();
              return;
            } 
            PlayerData data = PlayerData.getPlayerData(JobGrades.this.player);
            if (!data.selectedJob.isOutOfService())
              JobGrades.this.setTimer(10); 
          }
        },  false, 1200L, 1200L);
  }
  
  public void stopTaskTimer() {
    if (this.timeTask != null)
      this.timeTask.cancelTask(); 
  }
  
  public Grades getGrades() {
    return this.grades;
  }
  
  public Grade getGrade() {
    return this.grade;
  }
  
  public int getPointSuppl() {
    return this.pointSuppl;
  }
  
  public void setPointSuppl(int point) {
    this.pointSuppl = point;
    setPoints(Grade.ConvertToPoints(getNbMissions(), this.xp.getXp(), this.malus.getXp(), this.timer.getXp()));
  }
  
  public void addPointSuppl(int point) {
    setPointSuppl(getPointSuppl() + point);
  }
  
  public void setGrade(Grade grade) {
    this.grade = grade;
  }
  
  public void checkRankUP() {
    if (this.player == null)
      return; 
    PlayerData data = PlayerData.getPlayerData(this.player);
    if (this.grades.getGrade(this.points) != null && 
      !this.grades.getGrade(this.points).getNom().equals(getGrade().getNom())) {
      setGrade(this.grades.getGrade(this.points));
      if (Bukkit.getPlayerExact(this.player) != null)
        Bukkit.getPlayerExact(this.player).sendMessage(ChatColor.DARK_AQUA + "Félicitation vous venez d'être promu " + ChatColor.AQUA + this.grade.getNom()); 
      if (data != null)
        data.selectedJob.equipClothes(); 
    } 
    if (data != null && data.selectedJob != null) {
      ArrayList<Interact> interacts = data.selectedJob.getActionInventoryContent();
      for (Interact i : interacts) {
        if (i.getItems().contains(Militaire.Action.AMENDE.getItem())) {
          if (this.grades.gradeOrder(this.grade.getNom()) <= 2) {
            i.getItems().remove(Militaire.Action.AMENDE.getItem());
            break;
          } 
          continue;
        } 
        if (i.getItems().contains(Militaire.Action.MENOTTER.getItem())) {
          i.setItem(Militaire.Action.AMENDE.getItem());
          break;
        } 
      } 
    } 
  }
  
  public void CheckRankUPByGettable() {
    if (this.grades.getGrade(this.points) != null && this.grades.getGrade(this.points).getGettable())
      checkRankUP(); 
  }
  
  public int getXp() {
    return this.xp.getXp();
  }
  
  public void setXp(int xp) {
    this.xp.setXp(this.xp.getXp() + xp);
    setPoints(Grade.ConvertToPoints(getNbMissions(), this.xp.getXp(), this.malus.getXp(), this.timer.getXp()));
  }
  
  public int getTimer() {
    return this.timer.getXp();
  }
  
  public void setTimer(int xp) {
    this.timer.setXp(this.timer.getXp() + xp);
    setPoints(Grade.ConvertToPoints(getNbMissions(), this.xp.getXp(), this.malus.getXp(), this.timer.getXp()));
  }
  
  public int getMalus() {
    return this.malus.getXp();
  }
  
  public void setMalus(int xp) throws Exception {
    this.malus.setXp(this.malus.getXp() + xp);
    setPoints(Grade.ConvertToPoints(getNbMissions(), this.xp.getXp(), this.malus.getXp(), this.timer.getXp()));
    if (this.points < 0) {
      PlayerData data = PlayerData.getPlayerData(this.player);
      this.malus.setXp(0);
      data.selectedJob.ban(86400L);
    } 
  }
  
  public int getPoints() {
    return this.points;
  }
  
  public void setPoints(int points) {
    this.points = this.pointSuppl + points;
    CheckRankUPByGettable();
  }
  
  public int getNbMissions() {
    return this.nbMissions.getXp();
  }
  
  public void setNbMissions(int missions) {
    this.nbMissions.setXp(this.nbMissions.getXp() + missions);
    setPoints(Grade.ConvertToPoints(getNbMissions(), this.xp.getXp(), this.malus.getXp(), this.timer.getXp()));
  }
  
  public void loadData() {
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build()
          .selectAll("jobs_grade_data").where(new CustomEntry("pseudo", this.player))
          .where(new CustomEntry("jobname", this.grades.getJob().toLowerCase()), RecordBuilder.LINK.AND).toString());
      if (results.count() == 1) {
        ResultSet data = results.getActualResult();
        this.xp.loadData(data, "xp");
        this.malus.loadData(data, "malus");
        this.nbMissions.loadData(data, "missions");
        this.timer.loadData(data, "timer");
        this.pointSuppl = data.getInt("pointSuppl");
        setPoints(Grade.ConvertToPoints(getNbMissions(), this.xp.getXp(), this.malus.getXp(), this.timer.getXp()));
        setGrade(this.grades.getGrade(this.points));
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void saveData() {
    try {
      HashMap<String, Object> fields = new HashMap<>();
      fields.put("xp", this.xp.getXp());
      fields.put("malus", this.malus.getXp());
      fields.put("timer", this.timer.getXp());
      fields.put("missions", this.nbMissions.getXp());
      fields.put("pointSuppl", this.pointSuppl);
      SqlCollection results = Main.Database.select(RecordBuilder.build()
          .selectAll("jobs_grade_data").where(new CustomEntry("pseudo", this.player))
          .where(new CustomEntry("jobname", this.grades.getJob().toLowerCase()), RecordBuilder.LINK.AND).toString());
      if (results.count() == 1) {
        Main.Database.update(RecordBuilder.build().update(fields, "jobs_grade_data")
            .where(new CustomEntry("pseudo", this.player))
            .where(new CustomEntry("jobname", this.grades.getJob().toLowerCase()), RecordBuilder.LINK.AND).toString());
      } else {
        HashMap<String, Object> dataSup = new HashMap<>();
        dataSup.put("pseudo", this.player);
        dataSup.put("jobname", this.grades.getJob().toLowerCase());
        dataSup.putAll(fields);
        Main.Database.update(RecordBuilder.build().insert(dataSup, "jobs_grade_data").toString());
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void equipGrade() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String nameSection = "grades";
    ItemStack item = null;
    if (f.get("jobs." + this.grades.getJob() + "." + nameSection + "." + this.grade.getNom() + ".item-rank") != null) {
      String gradeId = f.getString("jobs." + this.grades.getJob() + "." + nameSection + "." + this.grade.getNom() + ".item-rank");
      int id = 0;
      byte subId = 0;
      if(gradeId.contains(":"))
      {
          String[] datas = gradeId.split(":");
          id = Integer.parseInt(datas[0]);
          subId = Byte.parseByte(datas[1]);
      }
      else
      {
        id = Integer.parseInt(gradeId);
      }

      item = ItemUtils.getItem(id, subId, 1, null, null);
    } 
    Player p = Bukkit.getPlayerExact(this.player);
    PlayerData data = PlayerData.getPlayerData(this.player);
    if (p != null && item != null && data.selectedJob.isGradeEquipable(data.selectedJob.getEquipedClothes()))
    {
      InventoryUtils.setGalon(p, item,true);
    }
  }

  public void equipInventory() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String nameSection = "grades";
    List<ItemStack> inventorySet = null;
    if (f.get("jobs." + this.grades.getJob() + "." + nameSection + "." + this.grade.getNom() + ".inventory") != null) {
      List<String> inventoryString =  f.getStringList("jobs." + this.grades.getJob() + "." + nameSection + "." + this.grade.getNom() + ".inventory");

      inventorySet = new ArrayList<>();
      for(String str : inventoryString)
      {
        String[] data = str.split(",");
        String[] idData = data[0].split(":");
        int id = Integer.parseInt(idData[1]);
        byte subId = 0;
        if(idData.length == 3) subId = Byte.parseByte(idData[2]);
        int quantity = Integer.parseInt(data[1].split(":")[1]);

        ItemStack is = new ItemStack(id, quantity, subId);

        inventorySet.add(is);
      }
    }


    Player p = Bukkit.getPlayerExact(this.player);
    PlayerData data = PlayerData.getPlayerData(this.player);

    data.predefinedJobItems.clear();

    if (p != null && inventorySet != null)
    {
      List<ItemStack> toRemove = new ArrayList<>();
      ItemStack[] inventory = new ItemStack[p.getInventory().getStorageContents().length];


      for(int i = 0; i < inventory.length; i++)
      {
          ItemStack inventoryIs = p.getInventory().getStorageContents()[i];
          for(ItemStack is : inventorySet) {

            if(inventoryIs == null) break;

            if(is.getTypeId() == inventoryIs.getTypeId() && is.getAmount() == inventoryIs.getAmount()) {
              toRemove.add(is);
              data.predefinedJobItems.add(is);
              break;
            }
          }
      }

      inventorySet.removeAll(toRemove);

      int count = inventorySet.size();

      for(int i = 0; i < inventorySet.size(); i++)
      {
        ItemStack itemStack = inventorySet.get(i);
        int slotIndex = InventoryUtils.getFirstEmptySlot(p.getInventory());
        if(slotIndex != -1)
        {
          count--;
          p.getInventory().setItem(slotIndex,itemStack);
          data.predefinedJobItems.add(itemStack);
        }
      }

      if(count > 0)
      {
        p.sendMessage("§bCertains armes prédéfinies n'ont pas pu être ajouté dans votre inventaire car votre inventaire est plein. Faites de la place et changez de tenue pour obtenir les armes manquantes.");
      }
    }


  }
}

