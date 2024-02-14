package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.grades.Grades;
import fr.karmaowner.jobs.grades.JobGrades;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.tresorerie.JobGradeTresorerie;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.RandomUtils;
import java.util.Arrays;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Medecin extends Jobs implements Legal, hasGrade, Radio {
  
  public static final int SERINGUE = 6558;
  
  public static final int SACMORTUAIRE = 351;
  
  public static Grades grades = new Grades("medecin");
  
  public static JobGradeTresorerie tresorerie = new JobGradeTresorerie("medecin");


  public JobGrades grade;

  public enum Action {
    CADAVRE("§cSe promener avec le cadavre", 397, (byte)3),
    DEPOSERCADAVREMORGUE("§aDéposer le cadavre à la morgue", SACMORTUAIRE, (byte)0);
    
    private String displayName;
    
    private Integer id;
    
    private Byte data;
    
    Action(String displayName, int id, byte data) {
      this.displayName = displayName;
      this.id = id;
      this.data = data;
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
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.id, this.data, 1, this.displayName, null);
    }
    
    public Action getAction(String displayName) {
      for (Action a : values()) {
        if (a.getDisplayName().equals(displayName))
          return a; 
      } 
      return null;
    }
  }
  
  public Medecin(String player) {
    super(player);
    this.grade = new JobGrades(grades, getPlayer(), true);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interaction2 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction2);

    ItemStack item2 = ItemUtils.getItem(Action.DEPOSERCADAVREMORGUE.getItem().getTypeId(), Action.DEPOSERCADAVREMORGUE.getItem().getData().getData(), 1, Action.DEPOSERCADAVREMORGUE
        .getItem().getItemMeta().getDisplayName(), Arrays.asList("§5coût: 1x Sac Mortuaire"));

    Interact interaction3 = InteractBuilder.build().interact(Jobs.TypeInteract.BLOCK).action(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK).block(ItemUtils.getItem(1134, (byte)14, 1, null, null)).priority(2).item(item2).create();
    setInActionInventory(interaction3);
  }

  
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
    Jobs.Job.MEDECIN.loadJobClothes();
  }

  public static void saveJobData() {
    tresorerie.saveData();
  }
  
  public JobGrades getGrade() {
    return this.grade;
  }
}
