package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
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

public class AssembleeNationale extends Jobs implements hasGrade, CanChangeJob, Radio {
  public static Grades grades = new Grades("assemblee");
  
  public static JobGradeTresorerie tresorerie = new JobGradeTresorerie("assemblee");
  
  public JobGrades grade;
  
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
  
  public AssembleeNationale(String name) {
    super(name);
    this.grade = new JobGrades(grades, getPlayer(), false);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public static void loadJobData() {
    grades.loadData();
    tresorerie.setGrades(grades.getHg());
    tresorerie.loadData();
    Jobs.Job.ASSEMBLEE.loadJobClothes();
  }
  
  public static void saveJobData() {
    tresorerie.saveData();
  }
  
  public void saveData() {
    this.grade.saveData();
    super.saveData();
  }
  
  public void loadData() {
    super.loadData();
    this.grade.loadData();
  }
  
  public List<String> getGrades() {
    return Arrays.asList("VicePrésident", "Président", "ChefGarde");
  }
  
  public JobGrades getGrade() {
    return this.grade;
  }
  
  public List<String> jobs() {
    return Arrays.asList("assemblee");
  }
}
