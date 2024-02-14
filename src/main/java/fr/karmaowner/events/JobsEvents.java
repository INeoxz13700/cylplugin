package fr.karmaowner.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.chat.Chat;
import fr.karmaowner.chat.ChatGroup;
import fr.karmaowner.common.Main;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.gangs.events.DamageGangPlayerEvent;
import fr.karmaowner.jobs.*;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.utils.*;
import fr.karmaowner.utils.events.AttentatAreaStayEvent;
import fr.karmaowner.wantedlist.WantedList;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JobsEvents implements Listener {
  public enum FORBIDCLICKBLOCK {
    COMPUTER(954, true),
    BENCH(676, true),
    RADAR(1035, false),
    CRAFTTABLE1(249, true),
    CRAFTTABLE2(663, true),
    CRAFTTABLE3(669, true),
    CRAFTTABLE4(670, true),
    CRAFTTABLE5(667, true),
    CRAFTTABLE6(671, true),
    CRAFTTABLE7(673, true),
    CRAFTTABLE8(674, true),
    CRAFTTABLE9(665, true),
    CRAFTTABLE10(672, true),
    CRAFTTABLE11(668, true);
    
    private int id;
    
    private boolean remove;
    
    FORBIDCLICKBLOCK(int id, boolean remove) {
      this.id = id;
      this.remove = remove;
    }
    
    public static boolean contains(Block b) {
      for (FORBIDCLICKBLOCK fc : values()) {
        if (fc != null && 
          b != null && fc.id == b.getTypeId())
          return true; 
      } 
      return false;
    }
    
    public int getId() {
      return this.id;
    }
    
    public static FORBIDCLICKBLOCK getBloc(int id) {
      for (FORBIDCLICKBLOCK f : values()) {
        if (f.getId() == id)
          return f; 
      } 
      return null;
    }
    
    public boolean getRemove() {
      return this.remove;
    }
  }
  
  public enum FORBIDCLICKITEM {
    PAPER(339, (byte)0);
    private byte data;
    
    private int id;
    
    FORBIDCLICKITEM(int id, byte data) {
      this.id = id;
      this.data = data;
    }
    
    public static boolean contains(ItemStack i) {
      for (FORBIDCLICKITEM fc : values()) {
        if (fc != null && 
          i != null && fc.id == i.getTypeId())
          return true; 
      } 
      return false;
    }
    
    public int getId() {
      return this.id;
    }
  }
  
  public enum WEAPON {
    DEAGLE(6445, (byte)0),
    S1897(6435, (byte)0),
    AK74(6479, (byte)0),
    M4A4(6493, (byte)0),
    M24A2(6432, (byte)0),
    MG42(6447, (byte)0),
    HKG36(6528, (byte)0),
    AMD65(6577, (byte)0),
    MP40(6417, (byte)0),
    MP5(6508, (byte)0),
    P88(6444, (byte)0),
    L115A3(6441, (byte)0),
    TEC9(6443, (byte)0),
    MP5SD(6421, (byte)0),
    SPAS12(6455, (byte)0),
    BTAPC308(6579, (byte)0),
    HKMP7(6526, (byte)0),
    AKM(6586, (byte)0),
    VSSVINTOREZ(6153, (byte)0),
    SIGSG552(6578, (byte)0),
    A545(6582, (byte)0),
    BenelliM4(6529, (byte)0),
    AK47(6407, (byte)0),
    SKS(6201, (byte)0),
    FNX45(6431, (byte)0),
    TAURUSRAGGING(6499, (byte)0),
    FNSCARH(6527, (byte)0),
    M14(6177, (byte)0),
    AK742(6583, (byte)0),
    M16A4(6585, (byte)0),
    MINI_UZI(6433, (byte)0),
    GLOCK_17(6580, (byte)0),
    MOSING(6477, (byte)0),
    VZ68(6459, (byte)0),
    HK416(6480, (byte)0),
    AK74U(6448, (byte)0),
    G36C(6530, (byte)0),
    M4A1(6453, (byte)0),
    HK_USP(6531, (byte)0),
    M16A1(6581, (byte)0),
    KNIFE(4459, (byte)0),
    HIKING_PACK(4459, (byte)0),
    FRAG_GRENADE(6470, (byte)0),
    SMOKE_GRENADE(6427, (byte)0),
    STUN_GRENADE(6491, (byte)0);





    private int id;
    
    private byte data;
    
    WEAPON(int id, byte data) {
      this.id = id;
      this.data = data;
    }
    
    public int getId() {
      return this.id;
    }
    
    public byte getData() {
      return this.data;
    }
    
    public ItemStack item() {
      return ItemUtils.getItem(this.id, this.data, 1, null, null);
    }
    
    public static List<ItemStack> items() {
      List<ItemStack> items = new ArrayList<>();
      for (WEAPON w : values())
        items.add(w.item()); 
      return items;
    }
    
    public static boolean contains(ItemStack item) {
      for (WEAPON w : values()) {
        if (w.id == item.getTypeId() && w.data == item.getData().getData())
          return true; 
      } 
      return false;
    }

    public static boolean equalTo(WEAPON w, ItemStack item) {
      return (item != null && w.getId() == item.getTypeId() && w.getData() == item.getData().getData());
    }
  }
  
  public enum ILLEGAL_ITEMS {
    Feuilledecanabis(6571),
    Canabis(6576),
    Jointdecanabis(6566),
    Feuilledecocaine(6575),
    Poudredecocaine(6569),
    Seringuedecocaine(6558),
    Pendentif(6116),
    Amethyse(6564),
    Necklace(6589),
    Locket(6588);

    
    private int id;
    
    ILLEGAL_ITEMS(int id) {
      this.id = id;
    }
    
    public static boolean contains(ItemStack item) {
      for (ILLEGAL_ITEMS ill : values()) {
        if (ill.id == item.getTypeId())
          return true; 
      } 
      return false;
    }
    
    public int getId() {
      return this.id;
    }
  }
  
  public enum DRUGS {
    FeuilleCanabis(JobsEvents.ILLEGAL_ITEMS.Feuilledecanabis.getId(), (byte)0),
    JointDeCanabis(JobsEvents.ILLEGAL_ITEMS.Jointdecanabis.getId(), (byte)0),
    Feuilledecocaine(JobsEvents.ILLEGAL_ITEMS.Feuilledecocaine.getId(), (byte)0),
    Poudredecocaine(JobsEvents.ILLEGAL_ITEMS.Poudredecocaine.getId(), (byte)0),
    Seringuedecocaine(JobsEvents.ILLEGAL_ITEMS.Seringuedecocaine.getId(), (byte)0),
    Canabis(JobsEvents.ILLEGAL_ITEMS.Canabis.getId(), (byte)0);
    
    private int id;
    
    private byte data;
    
    DRUGS(int id, byte data) {
      this.id = id;
      this.data = data;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.id, this.data, 1, null, null);
    }
    
    public static ArrayList<ItemStack> getItems() {
      ArrayList<ItemStack> items = new ArrayList<>();
      for (DRUGS d : values())
        items.add(d.getItem()); 
      return items;
    }
    
    public static boolean compareTo(ItemStack item) {
      if (item != null)
        for (DRUGS d : values()) {
          if (d.getItem().getTypeId() == item.getTypeId() && d
            .getItem().getData().getData() == item.getData().getData())
            return true; 
        }  
      return false;
    }
    
    public static boolean hasDrugs(ItemStack item) {
      return compareTo(item);
    }
  }
  
  public enum ILLEGAL_ITEMS_FOUILLE {
    MEDAILLON(6588),
    AMETHYSTE(6564),
    JOINT(6566),
    CANNABIS_SECHE(6576),
    FEUILLE_CANNABIS(6571),
    POUDRE_COCAINE(6569),
    FEUILLE_COCAINE(6575),
    TABLEAU(6560),
    LINGOT_OR(6514),

    SAPHIR(6587),
    RUBIS(6525),
    ANNEAU(6590),
    PENDENTIF(6116),
    COLLIER(6589);
    private int id;
    
    ILLEGAL_ITEMS_FOUILLE(int id) {
      this.id = id;
    }
    
    public static boolean contains(ItemStack item) {
      if (JobsEvents.ILLEGAL_ITEMS.contains(item))
        return true; 
      for (ILLEGAL_ITEMS_FOUILLE ill : values()) {
        if (ill.id == item.getTypeId())
          return true; 
      } 
      return false;
    }
  }
  
  @EventHandler
  public void JobClothesInventoryOpen(InventoryClickEvent e) {
    Inventory inv = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data != null && inv.getTitle() != null && inv
      .getTitle().contains(data.selectedJob.getFeatures().getClothesInventory().getTitle())) {
      ItemStack item = e.getCurrentItem();
      if (item.getItemMeta() != null)
        if (!data.selectedJob.isOutOfService()) {
          String displayName = item.getItemMeta().getDisplayName();
          Clothes c = data.selectedJob.getFeatures().getClothes(displayName);
          if (c != null) {
            if (!data.selectedJob.isEquippedClothes(c.getId())) {
              if (data.selectedJob instanceof hasGrade) {
                hasGrade grade = (hasGrade)data.selectedJob;
                if (c.hasGrade(grade.getGrade().getGrade().getNom())) {
                  data.selectedJob.equipClothes(c);
                  PlayerUtils.sendMessagePlayer(p.getName(), "§aLa tenue a été équipée");
                } else {
                  PlayerUtils.sendMessagePlayer(p.getName(), "§cVous ne pouvez pas équiper cette tenue car elle ne correspond pas à votre rang");
                } 
              } else {
                data.selectedJob.equipClothes(c);
                PlayerUtils.sendMessagePlayer(p.getName(), "§aLa tenue a été équipée");
              } 
            } else {
              PlayerUtils.sendMessagePlayer(p.getName(), "§cVous avez déjà cette tenue sur vous");
            } 
          } else {
            PlayerUtils.sendMessagePlayer(p.getName(), "§cCette tenue n'existe pas");
          } 
        } else {
          PlayerUtils.sendMessagePlayer(p.getName(), "§cVous n'êtes pas en service pour changer de tenue");
        }  
      e.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void openJobsInventory(PlayerInteractEntityEvent e) {
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Entity entity = e.getRightClicked();
    if (entity instanceof Villager) {
      Villager v = (Villager)entity;
      if (v.getCustomName().toLowerCase().equals("Pôle Emploi".toLowerCase()) && (data.selectedJob instanceof fr.karmaowner.jobs.Douanier || data.selectedJob instanceof Medecin)) {
        Jobs.fillInventory();
        p.openInventory(Jobs.POLEEMPLOIINVENTORY);
        return;
      } 
      if (v.getCustomName().toLowerCase().equals("Métiers illégaux".toLowerCase()) && (data.selectedJob instanceof fr.karmaowner.jobs.Douanier || data.selectedJob instanceof Medecin)) {
        Jobs.fillInventory();
        p.openInventory(Jobs.ILLEGALJOBSINVENTORY);
        return;
      } 
      if (v.getCustomName().toLowerCase().equals("Pôle Emploi".toLowerCase()) && (!data.selectedJob.isOutOfService() || data.selectedJob.getFeatures().isIllegal())) {
        Jobs.fillInventory();
        p.openInventory(Jobs.POLEEMPLOIINVENTORY);
      } else {
        if (v.getCustomName().toLowerCase().equals("Métiers illégaux".toLowerCase()) && !data.selectedJob.isOutOfService()) {
          Jobs.fillInventory();
          p.openInventory(Jobs.ILLEGALJOBSINVENTORY);
          return;
        } 
        if (v.getCustomName().toLowerCase().equals("Pôle Emploi".toLowerCase()) || v.getCustomName().toLowerCase().equals("Métiers illégaux".toLowerCase())) {
          if (data.selectedJob.isOutOfService())
            p.sendMessage(ChatColor.RED + "Vous ne pouvez pas effectuer cette action car vous êtes en hors fonction tapez la commande /jobs out service off");
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onAttentatArea(AttentatAreaStayEvent e) {
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (!(data.selectedJob instanceof fr.karmaowner.jobs.Security) && !(data.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste))
      e.getPlayer().sendMessage(ChatColor.RED + "Vous êtes dans une zone d'attentat potentiellement dangereuse. Eloignez-vous au plus vite !"); 
  }
  
  @EventHandler
  public void onDropItems(PlayerDropItemEvent event) {
    if (event.getPlayer().hasPermission("cylrp.jobs.change"))
      return;

    if(ServerUtils.notMoveableItems.contains(event.getItemDrop().getItemStack().getTypeId()))
    {
      event.setCancelled(true);
      event.getPlayer().sendMessage("§cVous ne pouvez pas jeter cet d'objet");
    }
    else
    {
      PlayerData data = PlayerData.getPlayerData(event.getPlayer().getName());
      for(ItemStack is : data.predefinedJobItems)
      {
        if(event.getItemDrop().getItemStack().getTypeId() == is.getTypeId())
        {
          event.setCancelled(true);
          event.getPlayer().sendMessage("§cVous ne pouvez pas jeter cet d'objet");
          return;
        }

      }
    }



  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClient(InventoryClickEvent event) {
    /*Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage("title : " + event.getInventory().getTitle());
    Bukkit.broadcastMessage("name : " + event.getInventory().getName());
    Bukkit.broadcastMessage("cursor : " + event.getCursor());
    Bukkit.broadcastMessage("currentItem : " + event.getCurrentItem());

    Bukkit.broadcastMessage("clicked inv : " + event.getClickedInventory());
    Bukkit.broadcastMessage("shiftclick : " + event.isShiftClick());
    Bukkit.broadcastMessage("clickType : " + event.getClick());
    Bukkit.broadcastMessage("action : " + event.getAction());
    Bukkit.broadcastMessage("slot : " + event.getSlot());
    Bukkit.broadcastMessage("slot item : " + event.getInventory().getItem(event.getSlot()));*/

    PlayerData data = PlayerData.getPlayerData(event.getWhoClicked().getName());

    if (event.getWhoClicked().hasPermission("cylrp.jobs.change"))
      return;

    if(event.getCursor() != null && ServerUtils.notMoveableItems.contains(event.getCursor().getTypeId()))
    {
      if(event.getClickedInventory() != event.getWhoClicked().getInventory() && event.getClickedInventory() != null && !event.getClickedInventory().toString().contains("CraftInventoryCustom"))
      {
        event.getWhoClicked().sendMessage("§cVous ne pouvez pas sortir cette item de votre inventaire");
        event.setCancelled(true);
      }
    }
    else if(event.getClickedInventory() != event.getWhoClicked().getInventory())
    {
      if(event.getCursor() != null)
      {
        for(ItemStack is : data.predefinedJobItems)
        {
          if(is.getTypeId() == event.getCursor().getTypeId())
          {
            event.getWhoClicked().sendMessage("§cVous ne pouvez pas sortir cette item de votre inventaire");
            event.setCancelled(true);
            break;
          }
        }
      }
    }

    if(event.getCurrentItem() != null && ServerUtils.notMoveableItems.contains(event.getCurrentItem().getTypeId()))
    {
      if(event.getClickedInventory() == event.getWhoClicked().getInventory())
      {
        if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
        {
          event.getWhoClicked().sendMessage("§cVous ne pouvez pas sortir cette item de votre inventaire");
          event.setCancelled(true);
        }
      }
    }
    else if(event.getCurrentItem() != null)
    {
      if(event.getClickedInventory() == event.getWhoClicked().getInventory())
      {
        if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
        {
          for(ItemStack is : data.predefinedJobItems)
          {
            if(event.getCurrentItem().getTypeId() == is.getTypeId())
            {
              event.getWhoClicked().sendMessage("§cVous ne pouvez pas sortir cette item de votre inventaire");
              event.setCancelled(true);
              break;
            }
          }

        }
      }
    }

    if(event.getClickedInventory() != event.getWhoClicked().getInventory())
    {
      if(event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
      {
        event.setCancelled(true);
      }
    }

  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClient(InventoryDragEvent event) {

    if (event.getWhoClicked().hasPermission("cylrp.jobs.change"))
      return;

    /*Bukkit.broadcastMessage("drag");
    Bukkit.broadcastMessage("old cursor : " + event.getOldCursor());
    Bukkit.broadcastMessage("cursor : " + event.getCursor());
    Bukkit.broadcastMessage("inventory : " + event.getInventory().getTitle());
    Bukkit.broadcastMessage("inventory : " + event.getInventory().getName());
    */
    if(event.getOldCursor() != null && ServerUtils.notMoveableItems.contains(event.getOldCursor().getTypeId()))
    {
      if(event.getInventory() != event.getWhoClicked().getInventory() && !event.getInventory().toString().contains("CraftInventoryCustom"))
      {
        event.getWhoClicked().sendMessage("§cVous ne pouvez pas sortir cette item de votre inventaire");
        event.setCancelled(true);
      }
    }
    else if(event.getInventory() != event.getWhoClicked().getInventory())
    {
        if(event.getOldCursor() != null)
        {
          PlayerData data = PlayerData.getPlayerData(event.getWhoClicked().getName());
          for(ItemStack is : data.predefinedJobItems)
          {
            if(is.getTypeId() == event.getOldCursor().getTypeId())
            {
              event.getWhoClicked().sendMessage("§cVous ne pouvez pas sortir cette item de votre inventaire");
              event.setCancelled(true);
              return;
            }
          }
        }
    }
  }


  
  /*@EventHandler
  public void onMoveItems(InventoryClickEvent event) {
    Player p = (Player)event.getWhoClicked();
    if (p.hasPermission("cylrp.jobs.change"))
      return;

    if(event.isShiftClick())
    {
      if(ServerUtils.notMoveableItems.contains(event.getCurrentItem().getTypeId()))
      {
        event.setCancelled(true);
        p.sendMessage("§cVous ne pouvez pas déplacer cet objet");
      }

    }



    if(event.getInventory() != null && event.getInventory().getType() != InventoryType.CRAFTING)
    {
      if(ServerUtils.notMoveableItems.contains(event.getCursor().getTypeId()))
      {
        event.setCancelled(true);
        p.sendMessage("§cVous ne pouvez pas déplacer cet objet");
      }
      else
      {

      }
    }

  }*/
  
  /*@EventHandler
  public void onMoveItems2(CraftItemEvent event) {
    Player p = (Player)event.getWhoClicked();
    if (p.hasPermission("cylrp.jobs.change")) return;

    if (event.isShiftClick()) {
      if(ServerUtils.notMoveableItems.contains(event.getCurrentItem().getTypeId()))
      {
        event.setCancelled(true);
        p.sendMessage("§cVous ne pouvez pas déplacer cet objet");
      }
      else
      {

      }
    }



    if (event.getInventory() != null && event.getInventory().getType() != InventoryType.CRAFTING)
    {
      if( ServerUtils.notMoveableItems.contains(event.getCursor().getTypeId())) {
        event.setCancelled(true);
        p.sendMessage("§cVous ne pouvez pas déplacer cet objet");
      }
      else
      {

      }
    }



  }*/

  
  @EventHandler
  public void onPlayerInteractVehicle(VehicleEvent e) {
    Player p = e.getPlayer();
    String vehicle = e.getVehicleName();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    ArrayList<Interact> interacts = data.selectedJob.getInteraction(Jobs.TypeInteract.ENTITY);
    for (Interact t : interacts) {
      if (data.selectedJob.getActionJobInventory() != null && ((t.IsOutService() != null && data.selectedJob.isOutOfService() == t.IsOutService()) || t.IsOutService() == null) &&
        t.isEntityType(vehicle) && (
        t.getRegions().isEmpty() || (!t.getRegions().isEmpty() && t
        .getRegions().contains((data.ActuallyRegion != null) ? data.ActuallyRegion.getId() : "")))) {
        if (!data.selectedJob.isOutOfService() && 
          t.PlayerInteractingContainsClothes() != null && 
          !data.selectedJob.getEquipedClothes().toLowerCase().contains(t.PlayerInteractingContainsClothes().toLowerCase()))
          continue; 
        data.selectedJob.fillActionJobInventory(t);
        e.getPlayer().openInventory(data.selectedJob.getActionJobInventory());
      } 
    } 
  }
  
  @EventHandler
  public void onClickPlayer(PlayerInteractEntityEvent e) {
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Entity entity = e.getRightClicked();
    PlayerUtils utils = new PlayerUtils();
    utils.setPlayer(p);
    Set<ProtectedRegion> rgs_interact = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(entity.getLocation()).getRegions();
    if (rgs_interact.size() == 0) {
      data.InteractingRegion = null;
    } else {
      data.InteractingRegion = "";
      StringJoiner joiner = new StringJoiner("_");
      for (ProtectedRegion rg : rgs_interact)
        joiner.add(rg.getId()); 
      data.InteractingRegion = joiner.toString();
    } 
    ArrayList<Interact> interacts = data.selectedJob.getInteraction(Jobs.TypeInteract.ENTITY);
    for (Interact t : interacts) {
      if (data.selectedJob.getActionJobInventory() != null && ((t.IsOutService() != null && data.selectedJob.isOutOfService() == t.IsOutService()) || t.IsOutService() == null) && (
        t.isEntityType(entity.getType()) || t.isEntityType(entity.getType().getName())) && 
        utils.isClose(entity, 3) && (
        t.getRegions().isEmpty() || (!t.getRegions().isEmpty() && t
        .getRegions().contains((data.ActuallyRegion != null) ? data.ActuallyRegion.getId() : "")))) {
        if (entity instanceof Player) {
          Player pEntity = (Player)entity;
          PlayerData dataEntity = PlayerData.getPlayerData(pEntity.getName());
          if (!data.selectedJob.isOutOfService() && 
            t.PlayerInteractingContainsClothes() != null && 
            !data.selectedJob.getEquipedClothes().toLowerCase().contains(t.PlayerInteractingContainsClothes().toLowerCase()))
            continue; 
          if (t.isGang())
            if (dataEntity.gangName != null && !dataEntity.gangName.isEmpty()) {
              GangData Gdata = GangData.getGangData(dataEntity.gangName);
              if (Gdata != null) {
                String rankPlayer = Gdata.rankNameUser(pEntity.getName());
                if (rankPlayer != null && t.getGangRank() != null && rankPlayer
                  .equals(t.getGangRank())) {
                  data.selectedJob.fillActionJobInventory(t);
                  data.selectedJob.setTarget(pEntity);
                  e.getPlayer().openInventory(data.selectedJob.getActionJobInventory());
                  return;
                } 
              } 
            } else {
              continue;
            }  
          if (t.isCompany())
            if (dataEntity.companyName != null && !dataEntity.companyName.isEmpty()) {
              CompanyData Cdata = CompanyData.getCompanyData(dataEntity.companyName);
              if (Cdata != null) {
                String rankPlayer = Cdata.getRankName(pEntity.getName());
                if (rankPlayer != null && t.getCompanyRank() != null && rankPlayer
                  .equals(t.getCompanyRank())) {
                  data.selectedJob.fillActionJobInventory(t);
                  data.selectedJob.setTarget(pEntity);
                  e.getPlayer().openInventory(data.selectedJob.getActionJobInventory());
                  return;
                } 
              } 
            } else {
              continue;
            }

          if (t.isJob(dataEntity.selectedJob.getFeatures()) && (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR)
                  &&
                  (dataEntity.selectedJob instanceof Medecin || !dataEntity.selectedJob.getFeatures().getName().equals(data.selectedJob.getFeatures().getName()) || (!(data.selectedJob instanceof fr.karmaowner.jobs.Legal) && !(data.selectedJob instanceof fr.karmaowner.jobs.WithoutTeamAction)) || dataEntity.selectedJob.isOutOfService()))
          {

            if (t.getJobRank().size() != 0) {
              if (data.selectedJob instanceof hasGrade) {
                hasGrade grade = (hasGrade)data.selectedJob;
                if (t.getJobRank().contains(grade.getGrade().getGrade().getNom())) {
                  data.selectedJob.fillActionJobInventory(t);
                  data.selectedJob.setTarget(pEntity);
                  e.getPlayer().openInventory(data.selectedJob.getActionJobInventory());
                } 
              } 
            } else {
              data.selectedJob.fillActionJobInventory(t);
              data.selectedJob.setTarget(pEntity);
              e.getPlayer().openInventory(data.selectedJob.getActionJobInventory());
            } 
            return;
          } 
          continue;
        } 
        data.selectedJob.fillActionJobInventory(t);
        data.selectedJob.setEntityTarget(entity);
        e.getPlayer().openInventory(data.selectedJob.getActionJobInventory());
        return;
      } 
    } 
  }
  
  @EventHandler
  public void onInteractWithBin(PlayerInteractEvent e) {
    Action a = e.getAction();
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Block b = e.getClickedBlock();
    if (b != null && b.getType() != Material.AIR && b.getTypeId() == 1157 && (
      a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK))
      p.openInventory(Bukkit.createInventory(null, 54)); 
  }
  
  @EventHandler
  public void onInteractWithBlock(PlayerInteractEvent e) {
    Action a = e.getAction();
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Block b = e.getClickedBlock();
    if (b != null) {
      Set<ProtectedRegion> rgs_interact = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(b.getLocation()).getRegions();
      if (rgs_interact.size() == 0) {
        data.InteractingRegion = null;
      } else {
        data.InteractingRegion = "";
        StringJoiner joiner = new StringJoiner("_");
        for (ProtectedRegion rg : rgs_interact)
          joiner.add(rg.getId()); 
        data.InteractingRegion = joiner.toString();
      } 
    }

    if (data.selectedJob.getInteraction(Jobs.TypeInteract.BLOCK) != null) {

      Interact i = data.selectedJob.getInteractionForBlock(b);


      if (i != null && ((i.IsOutService() != null && data.selectedJob.isOutOfService() == i.IsOutService()) || i.IsOutService() == null) &&
        i.getAction() == a) {
        PlayerUtils utils = new PlayerUtils();
        utils.setPlayer(p);
        if (utils.isClose(b, 2.0D)) {
          if (!data.selectedJob.isOutOfService() && 
            i.PlayerInteractingContainsClothes() != null && 
            !data.selectedJob.getEquipedClothes().toLowerCase().contains(i.PlayerInteractingContainsClothes().toLowerCase()))
            return;

          if (i.getRegions().isEmpty()) {
            data.selectedJob.fillActionJobInventory(i);
            data.selectedJob.setBlockClicked(b);
            e.setCancelled(true);
            p.openInventory(data.selectedJob.getActionJobInventory());
          } else if (data.InteractingRegion != null) {
            for (String rg : i.getRegions()) {
              if (data.InteractingRegion.contains(rg)) {
                data.selectedJob.fillActionJobInventory(i);
                data.selectedJob.setBlockClicked(b);
                e.setCancelled(true);
                p.closeInventory();
                p.openInventory(data.selectedJob.getActionJobInventory());
                return;
              } 
            } 
          } 
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onInteractWithAir(PlayerInteractEvent e) {
    Action a = e.getAction();
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Block b = e.getClickedBlock();
    ArrayList<Interact> interacts = data.selectedJob.getInteraction(Jobs.TypeInteract.CLICK_AIR);
    if (interacts.size() > 0) {
      Interact t = interacts.get(0);
      if (a == t.getAction() && !data.selectedJob.isOutOfService() && 
        b == null) {
        data.selectedJob.fillActionJobInventory(t);
        p.openInventory(data.selectedJob.getActionJobInventory());
      } 
    } 
  }
  
  @EventHandler
  public void onInteractWithBlockBlocked(PlayerInteractEvent e) {
    Action a = e.getAction();
    Player p = e.getPlayer();
    Block b = e.getClickedBlock();
    if (p.hasPermission("cylrp.interact"))
      return; 
    if (FORBIDCLICKBLOCK.contains(b) && (
      a == Action.RIGHT_CLICK_BLOCK || a == Action.RIGHT_CLICK_AIR)) {
      FORBIDCLICKBLOCK f = FORBIDCLICKBLOCK.getBloc(b.getTypeId());
      e.setCancelled(true);
      if (f != null && f.getRemove()) {
        b.setType(Material.AIR);
      } else {
        p.closeInventory();
      } 
      p.sendMessage(ChatColor.RED + "Ce bloc est interdit et vient d'être supprimé");
    } 
  }
  
  @EventHandler
  public void onInteractWithItemsBlocked(PlayerInteractEvent e) {
    Action a = e.getAction();
    Player p = e.getPlayer();
    ItemStack i = e.getItem();
    if (p.hasPermission("cylrp.interact"))
      return; 
    if (FORBIDCLICKITEM.contains(i)) {
      e.setCancelled(true);
      p.sendMessage(ChatColor.RED + "Vous ne pouvez pas intéragir avec cet objet");
    } 
  }

  
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) throws Exception {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    FileConfiguration f = Main.INSTANCE.getConfig();
    if (item != null && 
      item.getType() != Material.AIR && (
      inventory.getName().equals(Jobs.POLEEMPLOIINVENTORY.getName()) || inventory.getName().equals(Jobs.ILLEGALJOBSINVENTORY.getName()))) {
      if (Jobs.Job.isJob(item.getItemMeta().getDisplayName())) {
        Jobs Job = Jobs.Job.getJob(item.getItemMeta().getDisplayName(), p.getName());
        Jobs.Job j = Job.getFeatures();
        List<String> permissions = j.getPermissions();
        boolean flag = false;
        for(String permission : permissions)
        {
            if(p.hasPermission(permission))
            {
                flag = true;
                break;
            }
        }

        if (!flag) {
          MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas la permission pour utiliser ce grade");
          e.setCancelled(true);
          return;
        }

        long beforeChangeTime = (f.getInt("timeBeforeChangeJob") * 1000L);
        Long now = (new Timestamp(System.currentTimeMillis())).getTime();
        Long lastJob = (data.lastJob == null) ? 0L : data.lastJob.getTime();
        Timestamp timeLeft = new Timestamp(now - lastJob);
        if (timeLeft.getTime() >= beforeChangeTime || p.hasPermission("cylrp.jobs.change")) {
          if (!data.isForbidJob(j.getName())) {
            Jobs.Job oldJ = data.selectedJob.getFeatures();
            changePlayerJob(data, item.getItemMeta().getDisplayName(), p.getName());
            if (oldJ.isJob(Jobs.Job.DOUANIER) || oldJ.isJob(Jobs.Job.MEDECIN)) {
              data.selectedJob.setOutOfService(false);
            } 
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "warp " + data.selectedJob.getFeatures().getName() + " " + p.getName());
          } else {
            long seconds = (long)(((Long) data.delayProhibition.get(j.getName()) * 1000L - now - ((Timestamp)data.startProhibition.get(j.getName())).getTime()) / 1000.0D);
            p.sendMessage(ChatColor.DARK_RED + "Vous avez une interdiction d'exercer ce métier pendant " + TimerUtils.formatString(seconds));
          } 
          p.closeInventory();
        } else {
          int seconds = (int)(f.getInt("timeBeforeChangeJob") - timeLeft.getTime() / 1000.0D);
          p.sendMessage(ChatColor.GOLD + "Vous devez attendre " + ChatColor.YELLOW + TimerUtils.formatString(seconds) + " " + ChatColor.GOLD + " avant de changer de métier !");
        } 
      } 
      e.setCancelled(true);
    } 
  }
  
  public static void changePlayerJob(PlayerData data, String JobName, String playername) {
    if (WantedList.isWanted(playername) && !(data.selectedJob instanceof fr.karmaowner.jobs.Security) && !(data.selectedJob instanceof fr.karmaowner.jobs.Pompier) && !(data.selectedJob instanceof Medecin) && !(data.selectedJob instanceof fr.karmaowner.jobs.JobsMairie)) {
      PlayerUtils.sendMessagePlayer(playername, ChatColor.RED + "Impossible de changer de métier: Vous avez un avis de recherche. Pour vous en débarasser vous pouvez vous rendre au commissariat et discuter avec la secrétaire à l'accueil");
      return;
    } 
    data.selectedJob.setOutOfService(false);
    ChatGroup group = Chat.getPlayerChatGroup(playername);
    if (group != null && 
      Chat.getDefaultGroup().addPlayer(playername))
      group.deletePlayer(playername); 
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      if (data.selectedJob.getFeatures().isIllegal() || data.selectedJob.getFeatures().isJob(Jobs.Job.CIVILE))
        InventoryUtils.addEquippedClothesInInventory(p); 
      if (Jobs.Job.getJobByName(JobName).isIllegal() || Jobs.Job.getJobByName(JobName).isJob(Jobs.Job.CIVILE))
        if (WantedList.isWanted(playername)) {
          int stars = WantedList.getStars(playername);
          WantedList.TaskSemer(playername);
          WantedList.wantedMessage(playername, stars);
        }
    }
    data.selectedJob.saveData();
    data.saveData();
    data.selectedJob.unequipClothes(data, true, false);
    if (data.selectedJob instanceof Maire)
      Maire.MaireDemission(); 
    data.lastJob = new Timestamp(System.currentTimeMillis());
    try {
      data.selectedJob = Jobs.Job.getJob(JobName, playername);


    } catch (Exception e) {
      e.printStackTrace();
    } 
    data.selectedJob.loadData();

    if(!data.selectedJob.isLegalJob())
    {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " defender set 1000");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " illegal set 1600");
    }
    else if(data.selectedJob instanceof Security) //si fdl
    {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " defender set 1600");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " illegal set 0");
    }
    else
    {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " defender set 600");
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " illegal set 600");
    }

    PlayerUtils.sendMessagePlayer(playername, ChatColor.DARK_AQUA + "Vous êtes maintenant " + JobName);
  }
  
  @EventHandler
  public void distanceTarget(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Player target = data.selectedJob.getTarget();

    if (RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(p.getLocation()).size() <= 0) {
      data.setActuallyRegion(null);
    } else {
      Iterator<ProtectedRegion> iterator = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(p.getLocation()).iterator();
      if (iterator.hasNext()) {
        ProtectedRegion r = iterator.next();
        data.setActuallyRegion(r);
      } 
    } 
  }

  @EventHandler
  public void onEquipHeldWithInteract(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    ItemStack item = e.getItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (item != null && ItemUtils.IsEquipable(item) && !data.selectedJob.isOutOfService() && 
      data.selectedJob.getFeatures().hasServiceFunction() && !(data.selectedJob instanceof fr.karmaowner.jobs.OnServiceCanEquipClothes)) {
      e.setCancelled(true);
      MessageUtils.sendMessage((CommandSender)p, "Impossible: Vous êtes en service");
    } 
  }
  
  @EventHandler
  public void onEquipHeldWithClick(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());

    if (item != null && (e.getClickedInventory().getName().equals(ServerUtils.newInventoryName) || e.getClickedInventory().getName().equals(p.getInventory().getName())) && ItemUtils.IsEquipable(item) && !data.selectedJob.isOutOfService() &&
            data.selectedJob.getFeatures().hasServiceFunction() && !(data.selectedJob instanceof fr.karmaowner.jobs.OnServiceCanEquipClothes)) {
      e.setCancelled(true);
      MessageUtils.sendMessage((CommandSender)p, "Impossible: Vous êtes en service");
    }
  }

}
