package fr.karmaowner.amende;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.ItemUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Amende {
  public static ArrayList<Amende> AMENDES = new ArrayList<>();
  
  private Amende instance;
  
  private Expediteur e;
  
  private Receveur r;
  
  private int price;
  
  private String reason;
  
  public static final String EXPEDITEURINVNAME = "§cCréation de l'amende";
  
  public static final String RECEVEURINVNAME = "§2Traitement de l'amende";
  
  public enum ActionExpediteur {
    AUGMENTER, DIMINUER, ENVOYER, ANNULER;
  }
  
  public enum ActionReceveur {
    PAYER, REFUSER;
  }
  
  private abstract class Request {
    private Inventory inv;
    
    private String name;
    
    private boolean ready = false;
    
    public Request(String name, int InventorySize, String InvName) {
      this.name = name;
      this.inv = Main.INSTANCE.getServer().createInventory(null, InventorySize, InvName);
    }
    
    public String getName() {
      return this.name;
    }
    
    public void setReady(boolean b) {
      this.ready = b;
    }
    
    public boolean getReady() {
      return this.ready;
    }
    
    public Inventory getInventory() {
      return this.inv;
    }
  }
  
  public class Expediteur extends Request {
    private TaskCreator task;
    
    public HashMap<Amende.ActionExpediteur, ItemStack> ACTIONS = new HashMap<>();
    
    public Expediteur(String name) {
      super(name, 45, "§cCréation de l'amende");
      fillInventory();
      startTask();
    }
    
    public void increasePrice() {
      if (Amende.this.getPrice() <= 10000) {
        if (Amende.this.getPrice() == 0) {
          Amende.this.setPrice(250);
        } else if (Amende.this.getPrice() == 250) {
          Amende.this.setPrice(500);
        } else if (Amende.this.getPrice() == 500) {
          Amende.this.setPrice(750);
        } else if (Amende.this.getPrice() == 750) {
          Amende.this.setPrice(1000);
        } else if (Amende.this.getPrice() == 1000) {
          Amende.this.setPrice(1500);
        } else if (Amende.this.getPrice() >= 1500 && Amende.this.getPrice() < 10000) {
          Amende.this.setPrice(Amende.this.getPrice() + 500);
        } 
        fillInventory();
      } else {
        List<String> Lores = ((ItemStack)this.ACTIONS.get(Amende.ActionExpediteur.AUGMENTER)).getItemMeta().getLore();
        if (Lores.size() == 1)
          ((ItemStack)this.ACTIONS.get(Amende.ActionExpediteur.AUGMENTER)).getItemMeta().getLore().add("§4Limite amende = 10000€"); 
      } 
    }
    
    public void decreasePrice() {
      if (Amende.this.getPrice() > 0) {
        if (Amende.this.getPrice() == 250) {
          Amende.this.setPrice(0);
        } else if (Amende.this.getPrice() == 500) {
          Amende.this.setPrice(250);
        } else if (Amende.this.getPrice() == 750) {
          Amende.this.setPrice(500);
        } else if (Amende.this.getPrice() == 1000) {
          Amende.this.setPrice(750);
        } else if (Amende.this.getPrice() == 1500) {
          Amende.this.setPrice(1000);
        } else if (Amende.this.getPrice() > 1500 && Amende.this.getPrice() <= 10000) {
          Amende.this.setPrice(Amende.this.getPrice() - 500);
        } 
        fillInventory();
      } 
      if (Amende.this.getPrice() < 10000) {
        List<String> Lores = ((ItemStack)this.ACTIONS.get(Amende.ActionExpediteur.AUGMENTER)).getItemMeta().getLore();
        if (Lores.size() > 1)
          ((ItemStack)this.ACTIONS.get(Amende.ActionExpediteur.AUGMENTER)).getItemMeta().getLore().remove(1); 
      } 
    }
    
    public void startTask() {
      this.task = new TaskCreator(new CustomRunnable() {
            public void customRun() {
              Player p = Bukkit.getPlayerExact(Amende.Expediteur.this.getName());
              if (p == null || !p.isOnline()) {
                Amende.this.removeAmende();
                cancel();
                return;
              } 
              if (!Amende.Expediteur.this.getReady()) {
                if (p.getOpenInventory() == null || 
                  !p.getOpenInventory().getTopInventory().getName().equals("§cCréation de l'amende"))
                  p.openInventory(Amende.Expediteur.this.getInventory()); 
              } else {
                p.closeInventory();
                Amende.Expediteur.this.setReady(false);
                Amende.Expediteur.this.startReasonTask();
                cancel();
              } 
            }
          },  false, 0L, 20L);
    }
    
    public void startReasonTask() {
      new TaskCreator(new CustomRunnable() {
            private int delaySc = 15;
            
            private long elapsed = 0L;
            
            public void customRun() {
              Player p = Bukkit.getPlayerExact(Amende.Expediteur.this.getName());
              if (p == null || !p.isOnline()) {
                Amende.this.removeAmende();
                cancel();
                return;
              } 
              long now = System.currentTimeMillis();
              if (Amende.Expediteur.this.getReady()) {
                p.sendMessage("§aL'amende a été envoyé à l'individu");
                PlayerData data = PlayerData.getPlayerData(p.getName());
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "penalty add " + Amende.this.getReceveur().getName() + " " + p.getName() + " " + Amende.this.getPrice() + " " + data.selectedJob.getFeatures().getName().toLowerCase() + " " + Amende.this.getReason().replace(" ", "_"));
                Amende.Expediteur.this.setReady(false);
                Amende.this.removeAmende();
                cancel();
              } else if (now - this.elapsed >= (this.delaySc * 1000)) {
                this.elapsed = System.currentTimeMillis();
                p.sendMessage("§6[Amende] §eEn attente du motif de l'amende. Veuillez donner le motif en tapant la commande §6/amende <motif>");
              } 
            }
          }, false, 0L, 20L);
    }
    
    public TaskCreator getTask() {
      return this.task;
    }
    
    private void fillInventory() {
      List<String> lores = Arrays.asList("§aPrix: " + Amende.this.getPrice() + "€");
      ItemStack augmenter = ItemUtils.getItem(160, (byte)1, 1, "§4Clic-droit pour augmenter le montant de l'amende", lores);
      ItemStack diminuer = ItemUtils.getItem(160, (byte)4, 1, "§2Clic-droit pour diminuer le montant de l'amende", lores);
      ItemStack envoyer = ItemUtils.getItem(160, (byte)5, 1, "§3Clic-droit pour envoyer", lores);
      ItemStack annuler = ItemUtils.getItem(160, (byte)14, 1, "§dClic-droit pour annuler", lores);
      this.ACTIONS.put(Amende.ActionExpediteur.AUGMENTER, augmenter);
      this.ACTIONS.put(Amende.ActionExpediteur.DIMINUER, diminuer);
      this.ACTIONS.put(Amende.ActionExpediteur.ENVOYER, envoyer);
      this.ACTIONS.put(Amende.ActionExpediteur.ANNULER, annuler);
      getInventory().setItem(19, augmenter);
      getInventory().setItem(22, diminuer);
      getInventory().setItem(16, envoyer);
      getInventory().setItem(34, annuler);
    }
  }
  
  public void removeAmende() {
    AMENDES.remove(this.instance);
  }
  
  public class Receveur extends Request {
    private TaskCreator task;
    
    public HashMap<Amende.ActionReceveur, ItemStack> ACTIONS = new HashMap<>();
    
    public Receveur(String name) {
      super(name, 9, "§2Traitement de l'amende");
    }
    
    public void deleteAmende() {
      PlayerData receveur = PlayerData.getPlayerData(getName());
      BigDecimal price = BigDecimal.valueOf(Amende.this.getPrice());
      Player pExp = Bukkit.getPlayerExact(Amende.this.getExpediteur().getName());
      Player pRec = Bukkit.getPlayerExact(getName());
      if (receveur != null && receveur.getMoney().doubleValue() >= price.doubleValue()) {
        receveur.setMoney(receveur.getMoney().subtract(price));
        PlayerData expediteur = PlayerData.getPlayerData(Amende.this.getExpediteur().getName());
        if (expediteur != null)
          expediteur.setMoney(expediteur.getMoney().add(price)); 
        if (pExp != null)
          pExp.sendMessage("§aL'individu vient de vous payer l'amende de §2" + Amende.this.getPrice() + "€"); 
        if (pRec != null)
          pRec.sendMessage("§dVous venez de payer l'amende de §5" + Amende.this.getPrice() + "€"); 
      } else {
        if (pExp != null)
          pExp.sendMessage("§aL'individu n'a pas assez d'argent pour vous payer"); 
        if (pRec != null)
          pRec.sendMessage("§dVous n'avez pas assez d'argent. l'amende n'a pas été payé"); 
      } 
      Amende.this.removeAmende();
    }
  }
  
  public Amende(String expediteur, String receveur) {
    this.e = new Expediteur(expediteur);
    this.r = new Receveur(receveur);
    this.instance = this;
  }
  
  public void setPrice(int price) {
    this.price = price;
  }
  
  public int getPrice() {
    return this.price;
  }
  
  public static Amende getAmendeByExpediteur(String name) {
    for (Amende a : AMENDES) {
      if (a.getExpediteur() != null && a.getExpediteur().getName().equals(name))
        return a; 
    } 
    return null;
  }
  
  public static Amende getAmendeByReceveur(String name) {
    for (Amende a : AMENDES) {
      if (a.getReceveur() != null && a.getReceveur().getName().equals(name))
        return a; 
    } 
    return null;
  }
  
  public Expediteur getExpediteur() {
    return this.e;
  }
  
  public Receveur getReceveur() {
    return this.r;
  }
  
  public static Amende createAmende(String expediteur, String receveur) {
    Amende a = new Amende(expediteur, receveur);
    AMENDES.add(a);
    return a;
  }
  
  public String getReason() {
    return this.reason;
  }
  
  public void setReason(String reason) {
    this.reason = reason;
  }
}
