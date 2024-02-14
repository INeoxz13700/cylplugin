package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ChefGarde extends Jobs implements Legal, Radio, JobsMairie {
  public enum Action {
    FOUILLER(ChatColor.DARK_AQUA + "Fouiller le joueur", null, null, 4190, (byte)0),
    PRISON(ChatColor.DARK_PURPLE + "Mettre en prison", null, null, 101, (byte)0),
    MENOTTER(ChatColor.YELLOW + "Menotter le joueur", null, null, 5194, (byte)0),
    CADAVRE("§cSe promener avec le cadavre", null, null, 4281, (byte)0),
    DEMENOTTER(ChatColor.RED + "Démenotter le joueur", null, null, 5194, (byte)0);
    
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
  
  public ChefGarde(String name) {
    super(name);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).priority(2).item(Action.MENOTTER.getItem()).item(Action.DEMENOTTER.getItem()).item(Action.FOUILLER.getItem()).create();
    Interact prison = InteractBuilder.build().interact(Jobs.TypeInteract.BLOCK).action(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK).priority(0).item(Action.PRISON.getItem()).create();
    for (String pr : Prisons.prisons)
      prison.addRegion(pr); 
    Interact interaction2 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction2);
    setInActionInventory(interaction);
    setInActionInventory(prison);
  }
  
  public static void loadJobData() {
    Jobs.Job.CHEFGARDE.loadJobClothes();
  }
}
