package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.grades.EnfoncerPorte;
import fr.karmaowner.jobs.grades.Grades;
import fr.karmaowner.jobs.grades.JobGrades;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.jobs.missions.Missions;
import fr.karmaowner.tresorerie.JobGradeTresorerie;
import fr.karmaowner.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Militaire extends Missions implements Legal, Security, Radio, Near, EnfoncerPorte {
  public static Grades grades = new Grades("militaire");
  
  public static JobGradeTresorerie tresorerie = new JobGradeTresorerie("militaire");
  
  public enum Action {
    RANCON(ChatColor.GOLD + "Donner la rançon", null, null, 4684, (byte)0),
    FOUILLER(ChatColor.DARK_AQUA + "Fouiller le joueur", null, null, 4190, (byte)0),
    PRISON(ChatColor.DARK_PURPLE + "Mettre en prison", null, null, 101, (byte)0),
    MENOTTER(ChatColor.YELLOW + "Menotter le joueur", null, null, 5194, (byte)0),
    DEMENOTTER(ChatColor.RED + "Démenotter le joueur", null, null, 5194, (byte)0),
    CADAVRE("§cSe promener avec le cadavre", null, null, 4281, (byte)0),
    AMENDE(ChatColor.LIGHT_PURPLE + "Mettre une amende", null, null, 4258, (byte)0),
    WANTED("§dIndividu trouvé (avis de recherche)", null, null, 397, (byte)3);
    
    private String displayName;
    
    private Integer id;
    
    private Byte data;
    
    private int idItem;
    
    private byte dataItem;
    
    Action(String displayName, Integer id, Byte data, int idItem, byte dataItem) {
      this.displayName = displayName;
      this.id = id;
      this.data = data;
      this.idItem = idItem;
      this.dataItem = dataItem;
    }
    
    public int getId() {
      return this.id;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.idItem, this.dataItem, 1, this.displayName, null);
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
  
  public Militaire(String player) {
    super(player);
    this.grade = new JobGrades(grades, getPlayer(), false);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).priority(0).item(Action.MENOTTER.getItem()).item(Action.DEMENOTTER.getItem()).item(Action.FOUILLER.getItem()).item(Action.AMENDE.getItem()).item(Action.WANTED.getItem()).create();
    setInActionInventory(interaction);
    Interact interaction4 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).job(Jobs.Job.REBELLE).priority(3).item(Action.RANCON.getItem()).item(Action.MENOTTER.getItem()).item(Action.DEMENOTTER.getItem()).item(Action.FOUILLER.getItem()).item(Action.AMENDE.getItem()).item(Action.WANTED.getItem()).create();
    Interact interaction5 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).job(Jobs.Job.TERRORISTE).priority(3).item(Action.RANCON.getItem()).item(Action.MENOTTER.getItem()).item(Action.DEMENOTTER.getItem()).item(Action.FOUILLER.getItem()).item(Action.AMENDE.getItem()).item(Action.WANTED.getItem()).create();
    setInActionInventory(interaction4);
    setInActionInventory(interaction5);
    Interact interaction2 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    Interact prison = InteractBuilder.build().interact(Jobs.TypeInteract.BLOCK).action(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK).priority(0).item(Action.PRISON.getItem()).create();
    for (String pr : Prisons.prisons)
      prison.addRegion(pr); 
    setInActionInventory(interaction2);
    setInActionInventory(prison);
  }
  
  public void loadData() {
    super.loadData();
    this.grade.loadData();
  }
  
  public static void loadJobData() {
    grades.loadData();
    tresorerie.setGrades(grades.getHg());
    tresorerie.loadData();
    Jobs.Job.MILITAIRE.loadJobClothes();
  }
  
  public static void saveJobData() {
    tresorerie.saveData();
  }
  
  public void saveData() {
    this.grade.stopTaskTimer();
    this.grade.saveData();
    super.saveData();
  }
  
  public int EnfoncerPorteOrder() {
    return 3;
  }
}
