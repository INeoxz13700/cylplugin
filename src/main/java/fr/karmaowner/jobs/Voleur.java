package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.RandomItem;
import java.sql.Timestamp;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Voleur extends Jobs implements WithoutTeamAction {
  private Timestamp time;
  
  private RandomItem random;
  
  private RandomItem itemsChest;
  
  private ArrayList<ItemStack> interactableBlock = new ArrayList<>();
  
  public static final int ACTIONDURATION = 120;
  
  private Location lastLocation = null;
  
  public enum Action {
    VOLER1(ChatColor.DARK_AQUA + "Voler le joueur", null, null, 131, (byte)0),
    VOLER2(ChatColor.BLUE + "dérober des objets", null, null, 4202, (byte)0),
    VOLER3(ChatColor.RED + "Enfoncer la porte", null, null, 4202, (byte)0),
    VOLER4(ChatColor.GREEN + "Faire demi-tour", null, null, 131, (byte)0),
    VOLER5("§eVider la caisse enregistreuse", null, null, 1186, (byte)0),
    CADAVRE("§cSe promener avec le cadavre", null, null, 4281, (byte)0);
    
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
    
    public byte getData() {
      return this.data;
    }
    
    public String getDisplayName() {
      return this.displayName;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.idItem, this.dataItem, 1, this.displayName, null);
    }
    
    public Action getAction(String displayName) {
      for (Action a : values()) {
        if (a.getDisplayName().equals(displayName))
          return a; 
      } 
      return null;
    }
  }
  
  public enum Dooor {
    WOODEN(64, (byte)0, 50),
    IRON(71, (byte)0, 5),
    CARPENTER(632, (byte)0, 50),
    ACACIA(901, (byte)0, 50),
    BIRCH(902, (byte)0, 50),
    DARKOAK(903, (byte)0, 50),
    JUNGLE(904, (byte)0, 50),
    SPRUCE(905, (byte)0, 50),
    GLASS(906, (byte)0, 35),
    IRONGLASS(907, (byte)0, 2),
    LABORATORY(909, (byte)0, 1),
    FACTORY(910, (byte)0, 1),
    SHOJI(911, (byte)0, 80);
    
    private int id;
    
    private int proba;
    
    private byte data;
    
    Dooor(int id, byte data, int proba) {
      this.id = id;
      this.data = data;
      this.proba = proba;
    }
    
    public int getProbability() {
      return this.proba;
    }
    
    public int getId() {
      return this.id;
    }
    
    public byte getData() {
      return this.data;
    }
    
    public static Dooor getDoor(int id) {
      for (Dooor d : values()) {
        if (id == d.getId())
          return d; 
      } 
      return null;
    }
  }
  
  public Location getLastLocation() {
    return this.lastLocation;
  }
  
  public void setLastLocation(Location l) {
    this.lastLocation = l;
  }
  
  public RandomItem getRandomize() {
    return this.random;
  }
  
  public int DoorOpeningProbability(int id) {
    Dooor d = Dooor.getDoor(id);
    if (d != null)
      return d.getProbability(); 
    return -1;
  }
  
  public Voleur(String player) {
    super(player);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    setInActionInventory(InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER)
        .priority(0).item(Action.VOLER1.getItem()).create());
    Interact chest = InteractBuilder.build().interact(Jobs.TypeInteract.BLOCK).item(Action.VOLER2.getItem()).priority(0).block(ItemUtils.getItem(54, (byte)2, 1, null, null)).block(ItemUtils.getItem(54, (byte)3, 1, null, null)).block(ItemUtils.getItem(54, (byte)4, 1, null, null)).block(ItemUtils.getItem(54, (byte)5, 1, null, null)).block(ItemUtils.getItem(236, (byte)0, 1, null, null)).block(ItemUtils.getItem(693, (byte)0, 1, null, null)).block(ItemUtils.getItem(693, (byte)1, 1, null, null)).block(ItemUtils.getItem(693, (byte)2, 1, null, null)).block(ItemUtils.getItem(693, (byte)3, 1, null, null)).block(ItemUtils.getItem(693, (byte)4, 1, null, null)).block(ItemUtils.getItem(693, (byte)5, 1, null, null)).block(ItemUtils.getItem(685, (byte)0, 1, null, null)).block(ItemUtils.getItem(685, (byte)1, 1, null, null)).block(ItemUtils.getItem(685, (byte)2, 1, null, null)).block(ItemUtils.getItem(685, (byte)3, 1, null, null)).block(ItemUtils.getItem(685, (byte)4, 1, null, null)).block(ItemUtils.getItem(685, (byte)5, 1, null, null)).action(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK).create();
    setInActionInventory(chest);
    Interact doors = InteractBuilder.build().interact(Jobs.TypeInteract.BLOCK).item(Action.VOLER3.getItem()).item(Action.VOLER4.getItem()).priority(0).action(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK).create();
    for (Dooor d : Dooor.values()) {
      for (int j = 0; j < 9; j++)
        doors.setBlockToInteract(ItemUtils.getItem(d.getId(), (byte)j, 1, null, null)); 
    } 
    setInActionInventory(doors);
    this.time = new Timestamp(0L);
    this.random = new RandomItem();
    this.itemsChest = new RandomItem();
    for (int i = 5477; i <= 5483; i++) {
      this.random.addItem(new ItemStack(5477));
      setItemDroppableFromChest(new ItemStack(5477));
    } 
    int[] idItems = { 
        4362, 5438, 4367, 5437, 4368, 5436, 4362, 5469, 5475, 5463, 
        5473, 5457, 4170, 4165 };
    for (int id : idItems) {
      this.random.addItem(new ItemStack(id));
      setItemDroppableFromChest(new ItemStack(id));
    } 
    Interact caisse = InteractBuilder.build().interact(Jobs.TypeInteract.BLOCK).item(Action.VOLER5.getItem()).priority(0).action(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK).create();
    caisse.setBlockToInteract(ItemUtils.getItem(1186, (byte)107, 1, null, null));
    setInActionInventory(caisse);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public void setTimestamp() {
    this.time = new Timestamp(System.currentTimeMillis());
  }
  
  public Timestamp getTimestamp() {
    return this.time;
  }
  
  public void setItemDroppableFromChest(ItemStack item) {
    this.itemsChest.addItem(item);
  }
  
  public ItemStack isItemDroppableFromChest(ItemStack item) {
    for (ItemStack i : this.itemsChest.getItems()) {
      if (ItemUtils.compareById(i, item))
        return i; 
    } 
    return null;
  }
  
  public static void loadJobData() {
    Jobs.Job.VOLEUR.loadJobClothes();
  }
  
  public static void saveJobData() {}
}
