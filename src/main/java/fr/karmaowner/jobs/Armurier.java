package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.grades.Grades;
import fr.karmaowner.jobs.grades.JobGrades;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class Armurier extends Jobs implements Legal, hasGrade {
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

  public static Grades grades = new Grades("armurier");
  public JobGrades grade;
  
  public Armurier(String player) {
    super(player);
    grade = new JobGrades(grades, getPlayer(), false);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public static void loadJobData() {
    Job.ARMURIER.loadJobClothes();
    grades.loadData();
  }

  public void loadData(){
    grade.loadData();
  }

  public void saveData() {
    grade.saveData();
  }

  @Override
  public JobGrades getGrade() {
    return grade;
  }
}
