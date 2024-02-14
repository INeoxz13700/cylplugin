package fr.karmaowner.jobs;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.InventoryUtils;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Psychopathe extends Jobs {
  private long TortureTimer = 0L;
  
  public static final int TortureDelaySeconds = 300;
  
  private TaskCreator tortureTask;
  
  public enum Action {
    TORTURERTETE(ChatColor.BLUE + "Torturer la tête", null, null, 397, (byte)3),
    TORTURERCOEUR(ChatColor.BLUE + "Torturer le coeur", null, null, 5473, (byte)0),
    TORTUREROREILLE(ChatColor.BLUE + "Torturer les oreilles", null, null, 5446, (byte)0),
    TORTURERCRANE(ChatColor.BLUE + "Torturer le crâne", null, null, 5460, (byte)0),
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
  
  public String getTortureType(Action a) {
    switch (a) {
      case TORTUREROREILLE:
        return "torture des oreilles";
      case TORTURERTETE:
        return "torture de la tête";
      case TORTURERCRANE:
        return "torture du crâne";
      case TORTURERCOEUR:
        return "torture du coeur";
    } 
    return null;
  }
  
  public ItemStack getItemByTortureType(Action a) {
    switch (a) {
      case TORTUREROREILLE:
        return ItemUtils.getItem(5446, (byte)0, 2, "§cOreille", null);
      case TORTURERTETE:
        return ItemUtils.getItem(397, (byte)3, 2, "§cTête", null);
      case TORTURERCRANE:
        return ItemUtils.getItem(5460, (byte)0, 2, "§cCrâne", null);
      case TORTURERCOEUR:
        return ItemUtils.getItem(5473, (byte)0, 2, "§cCoeur", null);
    } 
    return null;
  }
  
  public Psychopathe(String player) {
    super(player);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    Interact interact = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity(EntityType.PLAYER).priority(0).item(Action.TORTUREROREILLE.getItem()).item(Action.TORTURERTETE.getItem()).item(Action.TORTURERCRANE.getItem()).item(Action.TORTURERTETE.getItem()).item(Action.TORTURERCOEUR.getItem()).create();
    setInActionInventory(interact);
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public boolean torturer(final Player target, final Action a) {
    final Player p = Bukkit.getPlayerExact(getPlayer());
    if (this.tortureTask == null && 
      getTarget() != null) {
      this.tortureTask = new TaskCreator(new CustomRunnable() {
            private Location TargetLoc = target.getLocation().clone();
            
            private int progress = 0;
            
            private long elapsed = 0L;
            
            public void customRun() {
              long now = System.currentTimeMillis();
              if (p == null) {
                if (target != null)
                  MessageUtils.sendMessage((CommandSender)target, "Le psychopathe s'est déconnecté. Torture annulée"); 
                Psychopathe.this.tortureTask = null;
                cancel();
                return;
              } 
              if (target == null) {
                this.progress = 100;
                MessageUtils.sendMessage((CommandSender)p, "La cible s'est déconnecté");
              } 
              if (p.getLocation().distance(this.TargetLoc) >= 2.0D) {
                MessageUtils.sendMessage((CommandSender)p, "Vous vous êtes éloigné de la cible. La torture a été annulé");
                Psychopathe.this.tortureTask = null;
                cancel();
                return;
              } 
              if (target != null && target.getLocation().distance(this.TargetLoc) > 1.0D) {
                target.teleport(this.TargetLoc);
                MessageUtils.sendMessage((CommandSender)target, "Vous êtes en train de vous faire torturer. Impossible de bouger.");
              } 
              if (this.progress >= 100) {
                InventoryUtils.addItemInInventory((Inventory)p.getInventory(), Psychopathe.this.getItemByTortureType(a));
                MessageUtils.sendMessage((CommandSender)p, "§2Torture terminée");
                Psychopathe.this.tortureTask = null;
                Psychopathe.this.setTortureTimer(System.currentTimeMillis());
                if (target != null) {
                  MessageUtils.sendMessage((CommandSender)target, "§2Torture terminée");
                  Player player = target;
                  player.setHealth(15);
                } 
                cancel();
              } else if (now - this.elapsed >= 3000L) {
                this.elapsed = System.currentTimeMillis();
                int percent = (int)(Math.random() * 5.0D) + 5;
                this.progress += percent;
                this.progress = (100 - this.progress < 0) ? 100 : this.progress;
                MessageUtils.sendMessage((CommandSender)p, "La §4" + Psychopathe.this.getTortureType(a) + " §cest à §4" + this.progress + "%");
              } 
            }
          }, false, 0L, 20L);
      return true;
    } 
    return false;
  }
  
  public static void loadJobData() {
    Jobs.Job.PSYCHOPATHE.loadJobClothes();
  }
  
  public static void saveJobData() {}
  
  public long getTortureTimer() {
    return this.TortureTimer;
  }
  
  public void setTortureTimer(long tortureTimer) {
    this.TortureTimer = tortureTimer;
  }
}
