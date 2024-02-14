package fr.karmaowner.jobs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.RegionUtils;
import java.awt.Point;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RebelleTerroriste extends Jobs {
  public static final int TIMEACTIONOTAGE = 15;
  
  public static final int TIMEACTIONASSAUT = 360;
  
  public static ArrayList<AttentatArea> zoneAttentat = new ArrayList<>();
  
  public static HashMap<String, String> otages = new HashMap<>();
  
  private int Prixnegociation;
  
  private boolean isNegociation;
  
  private Timestamp action;
  
  public static class AttentatArea {
    private Point p1;
    
    private Point p2;
    
    private ProtectedRegion rg;
    
    private boolean isRegion;
    
    private TaskCreator task;
    
    private AttentatArea instance;
    
    private static final int seconds = 180;
    
    public AttentatArea(Point p1, Point p2) {
      this.isRegion = false;
      this.p1 = p1;
      this.p2 = p2;
      this.instance = this;
      setTask();
    }
    
    public AttentatArea(ProtectedRegion rg) {
      this.isRegion = true;
      this.rg = rg;
      this.instance = this;
      setTask();
    }
    
    public boolean isRegion() {
      return this.isRegion;
    }
    
    public Point getP1() {
      return this.p1;
    }
    
    public Point getP2() {
      return this.p2;
    }
    
    public ProtectedRegion getRegion() {
      return this.rg;
    }
    
    public void setTask() {
      this.task = new TaskCreator(new CustomRunnable() {
            public void customRun() {
              RebelleTerroriste.zoneAttentat.remove(RebelleTerroriste.AttentatArea.this.instance);
            }
          },  false, 3600L);
    }
    
    public static boolean isInArea(ProtectedRegion r) {
      if (r == null)
        return false; 
      for (AttentatArea area : RebelleTerroriste.zoneAttentat) {
        if (area.isRegion && 
          r.getId().equals(r.getId()))
          return true; 
      } 
      return false;
    }
    
    public static AttentatArea getArea(ProtectedRegion r) {
      if (r == null)
        return null; 
      for (AttentatArea area : RebelleTerroriste.zoneAttentat) {
        if (area.isRegion && 
          r.getId().equals(r.getId()))
          return area; 
      } 
      return null;
    }
    
    public static AttentatArea getArea(Player p) {
      for (AttentatArea area : RebelleTerroriste.zoneAttentat) {
        if (!area.isRegion && 
          RegionUtils.isBetween2dots(area.getP1(), area.getP2(), p))
          return area; 
      } 
      return null;
    }
    
    public static boolean isInArea(Player p) {
      for (AttentatArea area : RebelleTerroriste.zoneAttentat) {
        if (!area.isRegion && 
          RegionUtils.isBetween2dots(area.getP1(), area.getP2(), p))
          return true; 
      } 
      return false;
    }
  }
  
  public enum Action {
    OTAGE(ChatColor.DARK_AQUA + "Prendre en otage", 420, (byte)0, null, null),
    FOUILLER(ChatColor.DARK_AQUA + "Fouiller le joueur", 5192, (byte)0, null, null),
    CADAVRE("§cSe promener avec le cadavre", 4281, (byte)0, null, null);
    
    private String displayName;
    
    private Integer id;
    
    private Byte data;
    
    private int idItem;
    
    private byte dataItem;
    
    Action(String displayName, int idItem, byte dataItem, Integer id, Byte data) {
      this.displayName = displayName;
      this.idItem = idItem;
      this.dataItem = dataItem;
      this.id = id;
      this.data = data;
    }
    
    public Integer getId() {
      return this.id;
    }
    
    public int getIdItem() {
      return this.idItem;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.idItem, this.dataItem, 1, this.displayName, null);
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
  
  public void setPrixNegociation(Player p, int Price) {
    if (Price >= 100 && Price <= 25000) {
      this.Prixnegociation = Price;
      p.sendMessage(ChatColor.GREEN + "Vous venez de fixer le prix de la négociation à !" + ChatColor.DARK_GREEN + this.Prixnegociation + "€");
    } else {
      p.sendMessage(ChatColor.RED + "Le prix de la négociation doit être compris entre 100€ et 25000€ !");
    } 
  }
  
  public void resetPrixNegociation() {
    this.Prixnegociation = 0;
  }
  
  public int getPrixNegociation() {
    return this.Prixnegociation;
  }
  
  public Timestamp getTimeAction() {
    return this.action;
  }
  
  public static void setOtage(Player p, Player otage) {
    if (!isOtage(otage)) {
      otages.put(p.getName(), otage.getName());
      p.sendMessage(ChatColor.GREEN + "Le joueur est désormais en otage !");
      otage.sendMessage(ChatColor.GREEN + "Vous êtes pris en otage ! Les gendarmes vont intervenir patientez...");
      int x = (int)p.getLocation().getX();
      int y = (int)p.getLocation().getY();
      int z = (int)p.getLocation().getZ();
      Job.GENDARME.sendMessageAll(ChatColor.BLUE + "Un otage a eu lieu aux coordonnées " + ChatColor.DARK_AQUA + "x=" + x + ";y=" + y + ";z=" + z + "");
      Jobs.Job.GENDARME.sendMessageAll(ChatColor.BLUE + "Le preneur d'otage est prêt à négocier ! Rendez vous dans les lieux au plus vite !");
    } else {
      p.sendMessage(ChatColor.RED + "Ce joueur est déjà en otage !");
    } 
  }
  
  public boolean hasOtage() {
    return (otages.get(getPlayer()) != null);
  }
  
  public static boolean isOtage(Player otage) {
    for (String o : otages.values()) {
      if (o.equals(otage.getName()))
        return true; 
    } 
    return false;
  }
  
  public static String getOtage(Player p) {
    for (Map.Entry<String, String> p1 : otages.entrySet()) {
      if (((String)p1.getKey()).equals(p.getName()))
        return p1.getValue(); 
    } 
    return null;
  }
  
  public static String getPreneurOtage(Player otage) {
    for (Map.Entry<String, String> p1 : otages.entrySet()) {
      if (((String)p1.getValue()).equals(otage.getName()))
        return p1.getKey(); 
    } 
    return null;
  }
  
  public static void removeOtage(Player p) {
    String otage = getOtage(p);
    if (otage != null) {
      otages.remove(p.getName());
      p.sendMessage(ChatColor.GOLD + "Otage libérée ! Prenez la fuite !");
      Player pOtage = Bukkit.getPlayerExact(otage);
      if (pOtage != null)
        pOtage.sendMessage(ChatColor.GOLD + "Vous n'êtes plus en otage !"); 
    } 
  }
  
  public void setTimeAction() {
    this.action = new Timestamp(System.currentTimeMillis());
  }
  
  public RebelleTerroriste(String player) {
    super(player);
    this.action = new Timestamp(0L);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact otage = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).item(ItemUtils.getItem(Action.OTAGE.getIdItem(), (byte)0, 1, Action.OTAGE.getDisplayName(), null)).item(ItemUtils.getItem(Action.FOUILLER.getIdItem(), (byte)0, 1, Action.FOUILLER.getDisplayName(), null)).create();
    setNegociation(false);
    setInActionInventory(otage);
    Interact interaction2 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction2);
  }
  
  public boolean isNegociation() {
    return this.isNegociation;
  }
  
  public void setNegociation(boolean isNegociation) {
    this.isNegociation = isNegociation;
  }
}
