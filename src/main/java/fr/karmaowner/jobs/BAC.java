package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.GangData;
import fr.karmaowner.jobs.grades.Grades;
import fr.karmaowner.jobs.grades.JobGrades;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.tresorerie.JobGradeTresorerie;
import fr.karmaowner.utils.ItemUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BAC extends Jobs implements Near, Radio, Security, Legal, hasGrade {
  public static Grades grades = new Grades("bac");
  
  public static JobGradeTresorerie tresorerie = new JobGradeTresorerie("bac");
  
  public JobGrades grade;
  
  public static final int DETECTEUR = 4190;
  
  public static final int delaySecs = 60;

  public String permission;
  
  public long timerDetectDrugs = 0L;
  
  public enum Action {
    FOUILLER(ChatColor.DARK_AQUA + "Fouiller le joueur", null, null, 4190, (byte)0),
    PRISON(ChatColor.DARK_PURPLE + "Mettre en prison", null, null, 101, (byte)0),
    MENOTTER(ChatColor.YELLOW + "Menotter le joueur", null, null, 5194, (byte)0),
    DEMENOTTER(ChatColor.RED + "Démenotter le joueur", null, null, 5194, (byte)0),
    AMENDE(ChatColor.LIGHT_PURPLE + "Mettre une amende", null, null, 4258, (byte)0),
    WANTED("§dIndividu trouvé (avis de recherche)", null, null, 397, (byte)3),
    DEMANTELER("§dDémanteler le gang", null, null, 397, (byte)3),
    CADAVRE("§cSe promener avec le cadavre", null, null, 4281, (byte)0);
    
    private String displayName;
    
    private Integer id;
    
    private Byte data;
    
    private byte dataItem;
    
    private int idItem;
    
    Action(String displayName, Integer id, Byte data, int idItem, byte dataItem) {
      this.displayName = displayName;
      this.id = id;
      this.data = data;
      this.idItem = idItem;
      this.dataItem = dataItem;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.idItem, this.dataItem, 1, this.displayName, null);
    }
    
    public int getId() {
      return this.id;
    }
    
    public byte getData() {
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
  
  public BAC(String pseudo) {
    super(pseudo);
    this.grade = new JobGrades(grades, getPlayer(), true);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).priority(0).item(Action.MENOTTER.getItem()).item(Action.DEMENOTTER.getItem()).item(Action.FOUILLER.getItem()).item(Action.AMENDE.getItem()).item(Action.WANTED.getItem()).create();
    Interact prison = InteractBuilder.build().interact(Jobs.TypeInteract.BLOCK).action(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK).priority(0).item(Action.PRISON.getItem()).create();
    for (String pr : Prisons.prisons)
      prison.addRegion(pr); 
    Interact interaction2 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    Interact interaction3 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).gang(GangData.RANKS.CHEF).priority(2).item(Action.DEMANTELER.getItem()).item(Action.MENOTTER.getItem()).item(Action.DEMENOTTER.getItem()).item(Action.FOUILLER.getItem()).item(Action.AMENDE.getItem()).item(Action.WANTED.getItem()).create();
    setInActionInventory(interaction2);
    setInActionInventory(interaction3);
    setInActionInventory(interaction);
    setInActionInventory(prison);
  }
  
  public static void loadJobData() {
    grades.loadData();
    tresorerie.setGrades(grades.getHg());
    tresorerie.loadData();
    Job.BAC.loadJobClothes();
  }
  
  public static void saveJobData() {
    tresorerie.saveData();
  }
  
  public void saveData() {
    this.grade.stopTaskTimer();
    this.grade.saveData();
    super.saveData();
  }
  
  public void loadData() {
    super.loadData();
    this.grade.loadData();
  }
  
  public List<String> getGrades() {
    return Collections.singletonList("Major");
  }
  
  public JobGrades getGrade() {
    return this.grade;
  }
}
