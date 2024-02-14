package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.grades.EnfoncerPorte;
import fr.karmaowner.jobs.grades.Grades;
import fr.karmaowner.jobs.grades.JobGrades;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.tresorerie.JobGradeTresorerie;
import fr.karmaowner.utils.ItemUtils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class Pompier extends Jobs implements Legal, hasGrade, Radio, Near, EnfoncerPorte {
  public static Grades grades = new Grades("pompier");
  
  public static JobGradeTresorerie tresorerie = new JobGradeTresorerie("pompier");
  
  public JobGrades grade;
  
  public static final int EXTINCTEUR = 6890;
  
  public static final int WATER = 5451;
  
  public static final double FIREDISPARITION = 240.0D;
  
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
  
  public Pompier(String player) {
    super(player);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    this.grade = new JobGrades(grades, getPlayer(), false);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public static final int FIREDISPARITIONMONEY = (int)Math.round(Math.random() * 10.0D + 10.0D);
  
  public void loadData() {
    super.loadData();
    this.grade.loadData();
  }
  
  public void saveData() {
    this.grade.stopTaskTimer();
    this.grade.saveData();
    super.saveData();
  }
  
  public static void loadJobData() {
    grades.loadData();
    tresorerie.setGrades(grades.getHg());
    tresorerie.loadData();
    Jobs.Job.POMPIER.loadJobClothes();
  }
  
  public static void saveJobData() {
    tresorerie.saveData();
  }
  
  public List<String> getGrades() {
    return Arrays.asList("Colonel", "ControleurGeneral", "ControleurGeneralDetatMajor");
  }
  
  public JobGrades getGrade() {
    return this.grade;
  }
  
  public int EnfoncerPorteOrder() {
    return 5;
  }
}
