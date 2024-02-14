package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.hacker.AtmHacking;
import fr.karmaowner.jobs.hacker.CircuitBanque;
import fr.karmaowner.jobs.hacker.MakeHackingGame;
import fr.karmaowner.jobs.hacker.WantedRemoveGame;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.ItemUtils;
import java.sql.Timestamp;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hacker extends Jobs {
  public static HashMap<Player, Player> checkAction = new HashMap<>();
  
  private MakeHackingGame game;
  
  public Timestamp timer;
  
  public enum Action {
    HACKERATM(ChatColor.RED + "Hacker un ATM", 6565, (byte)0, null, null),
    HACKERBANQUE("§aDigicode hacking", 6590, (byte)0, null, null),
    CADAVRE("§cSe promener avec le cadavre", 4281, (byte)0, null, null),
    HACKERBDDCOMICO("§aPirater la base de donnée", 1187, (byte)0, null, null);
    
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
  
  public Hacker(String player) {
    super(player);
    this.game = null;
    this.timer = new Timestamp(0L);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);

    Interact interaction = InteractBuilder.build().action(org.bukkit.event.block.Action.LEFT_CLICK_BLOCK).interact(Jobs.TypeInteract.BLOCK).create();
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)13, 1, null, null));
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)3, 1, null, null));
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)14, 1, null, null));
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)4, 1, null, null));
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)12, 1, null, null));
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)2, 1, null, null));
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)15, 1, null, null));
    interaction.setBlockToInteract(ItemUtils.getItem(1133, (byte)5, 1, null, null));

    setInActionInventory(interaction);

    interaction.setItem(Action.HACKERATM.getItem());

    /*Interact interaction2 = InteractBuilder.build().item(Action.HACKERATM.getItem()).interact(Jobs.TypeInteract.BLOCK).create();
    setInActionInventory(interaction2);

    Interact interaction4 = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction4);*/
  }
  
  public MakeHackingGame startHacking(Action a, Player p) {
    if (this.game == null)
      if (a == Action.HACKERATM) {
        this.game = (MakeHackingGame)new AtmHacking(27, p, this);
        this.game.start();
      } else if (a == Action.HACKERBANQUE) {
        PlayerData data = PlayerData.getPlayerData(p.getName());
        ItemStack CardLevel = null;
        if (data.InteractingRegion != null && data.InteractingRegion.contains("bqdigicode1")) {
          CardLevel = ItemUtils.getItem(6590, (byte)0, 1, null, null);
        } else if (data.InteractingRegion != null && data.InteractingRegion.contains("bqdigicode2")) {
          CardLevel = ItemUtils.getItem(6590, (byte)1, 1, null, null);
        } else if (data.InteractingRegion != null && data.InteractingRegion.contains("bqdigicode3")) {
          CardLevel = ItemUtils.getItem(6590, (byte)2, 1, null, null);
        } else if (data.InteractingRegion != null && data.InteractingRegion.contains("bqdigicode4")) {
          CardLevel = ItemUtils.getItem(6590, (byte)4, 1, null, null);
        } else if (data.InteractingRegion != null && data.InteractingRegion.contains("bqdigicode5")) {
          CardLevel = ItemUtils.getItem(6590, (byte)5, 1, null, null);
        } 
        this.game = (MakeHackingGame)new CircuitBanque(54, p, this, CardLevel);
        this.game.start();
      } else if (a == Action.HACKERBDDCOMICO) {
        this.game = (MakeHackingGame)new WantedRemoveGame(p, this);
        this.game.start();
      }  
    return this.game;
  }
  
  public MakeHackingGame getGame() {
    return this.game;
  }
  
  public void setGame(MakeHackingGame m) {
    this.game = m;
  }
  
  public static void loadJobData() {
    Jobs.Job.HACKER.loadJobClothes();
  }
  
  public static void saveJobData() {}
}
