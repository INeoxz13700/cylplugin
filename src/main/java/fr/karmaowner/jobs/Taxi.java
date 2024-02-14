package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.chauffeur.Bus;
import fr.karmaowner.jobs.chauffeur.Request;
import fr.karmaowner.jobs.chauffeur.Traject;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Taxi extends Jobs implements Legal {
  private static Request request = new Request();
  
  public static final double PRICEPERBLOCK = 0.2D;
  
  private Traject traject;

  public final List<String> busVehiclesEntityId = Arrays.asList(new String[]{"vehicle_irizari6"});


  public Bus bus;
  
  public enum Action {
    ENMENER(ChatColor.BLUE + "Emmener un joueur", null, null, 4877, (byte)0),
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
    
    public int getId() {
      return this.id;
    }
    
    public byte getData() {
      return this.data;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.idItem, this.dataItem, 1, this.displayName, null);
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

  public boolean isBusVehicle(String vehicleName)
  {
    for(String bus : busVehiclesEntityId)
    {
      if(vehicleName.contains(bus)) return true;
    }
    return false;
  }

  public void setTraject(Traject t) {
    this.traject = t;
  }
  
  public Traject getTraject() {
    return this.traject;
  }
  
  public static Request getRequests() {
    return request;
  }
  
  public Taxi(String player) {
    super(player);
    this.bus = new Bus(Bukkit.getPlayerExact(player));
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interact = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).priority(0).item(Action.ENMENER.getItem()).create();
    setInActionInventory(interact);
    Interact interact2 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("vehicle_irizari6:7").priority(0).create();
    setInActionInventory(interact2);
    Interact interact3 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("IkarusZ60").priority(0).create();
    setInActionInventory(interact3);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public static void loadJobData() {
    Jobs.Job.TAXI.loadJobClothes();
  }
  
  public static void saveJobData() {}
}
