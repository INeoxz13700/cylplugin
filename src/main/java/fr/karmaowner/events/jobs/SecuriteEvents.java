package fr.karmaowner.events.jobs;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.amende.Amende;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.events.JobsEvents;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Policier;
import fr.karmaowner.jobs.Prisons;
import fr.karmaowner.jobs.RebelleTerroriste;
import fr.karmaowner.jobs.missions.Missions;
import fr.karmaowner.jobs.missions.type.AmendeTask;
import fr.karmaowner.jobs.missions.type.FouilleTask;
import fr.karmaowner.jobs.missions.type.MenotteTask;
import fr.karmaowner.tresorerie.Tresorerie;
import fr.karmaowner.utils.*;
import fr.karmaowner.wantedlist.WantedList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SecuriteEvents implements Listener {
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    final Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    final PlayerData data = PlayerData.getPlayerData(p.getName());
    if (item != null && 
      item.getType() != Material.AIR)
      if (inventory.getName().equals(Jobs.NAMEACTIONINVENTORY)) {
        final Player cible = data.selectedJob.getTarget();
        boolean isActionAgree = true;
        PlayerData dataCible = null;
        if (cible != null) {
          dataCible = PlayerData.getPlayerData(cible.getName());
          if (cible instanceof RebelleTerroriste) {
            RebelleTerroriste rt = (RebelleTerroriste)cible;
            if (rt.hasOtage())
              isActionAgree = false; 
          } 
          if (data.selectedJob instanceof fr.karmaowner.jobs.Garde)
            if (dataCible.selectedJob instanceof fr.karmaowner.jobs.Maire) {
              p.sendMessage(ChatColor.RED + "Vous ne pouvez pas effectuer d'action au Maire.");
              return;
            }  
        } 
        if (data.selectedJob instanceof fr.karmaowner.jobs.Security || data.selectedJob instanceof fr.karmaowner.jobs.JobsMairie)
          if (isActionAgree) {
            Policier.Action menotter = Policier.Action.MENOTTER;
            Policier.Action demenotter = Policier.Action.DEMENOTTER;
            Policier.Action prison = Policier.Action.PRISON;
            Policier.Action amende = Policier.Action.AMENDE;
            Policier.Action wanted = Policier.Action.WANTED;
            Policier.Action rancon = Policier.Action.RANCON;
            if (item.getItemMeta().getDisplayName().equals(menotter.getDisplayName())) {
              if (!dataCible.getMenotte()) {
                if (data.selectedJob instanceof Missions) {
                  Missions m = (Missions)data.selectedJob;
                  if (m.getInProgress() != null && 
                    m.getInProgress().getType() instanceof MenotteTask) {
                    MenotteTask mt = (MenotteTask)m.getInProgress().getType();
                    mt.setTempCount(mt.getTempCount() - 1);
                  } 
                } 
                cible.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6000, 10));
                cible.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6000, 10));
                dataCible.setMenotte(true, p);
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "animations play " + cible.getName() + " 2");
                final PlayerData dCible = dataCible;
                new TaskCreator(new CustomRunnable() {
                      public void customRun() {
                        if (!p.isOnline()) {
                          dCible.setMenotte(false, null);
                          for (PotionEffect e : cible.getActivePotionEffects())
                            cible.removePotionEffect(e.getType()); 
                          cancel();
                          return;
                        } 
                        if (!dCible.getMenotte() || cible.isDead() || p.isDead()) {
                          cancel();
                          return;
                        } 
                        if (p.getLocation().distance(cible.getLocation()) > 2.0D)
                          cible.teleport(p.getLocation()); 
                      }
                    },false, 0L, 10L);
                p.sendMessage(ChatColor.GREEN + "Joueur menotté !");
              } else {
                p.sendMessage(ChatColor.RED + "Ce joueur est déjà menotté !");
              } 
              p.closeInventory();
            } 
            if (item.getItemMeta().getDisplayName().equals(rancon.getDisplayName())) {
              Player victim = data.selectedJob.getTarget();
              PlayerData dataVict = PlayerData.getPlayerData(victim.getName());
              if (dataVict.selectedJob instanceof RebelleTerroriste) {
                RebelleTerroriste r = (RebelleTerroriste)dataVict.selectedJob;
                if (r.hasOtage()) {
                  if (!RebelleTerroriste.getOtage(victim).equals(p.getName())) {
                    if (r.getPrixNegociation() > 0) {
                      Tresorerie t = Tresorerie.getTresorerie(data.selectedJob.getFeatures().getName().toLowerCase());
                      if (t.hasMoney(r.getPrixNegociation())) {
                        t.substMoney(r.getPrixNegociation());
                        dataVict.setMoney(dataVict.getMoney().add(BigDecimal.valueOf(r.getPrixNegociation())));
                        p.sendMessage(ChatColor.GREEN + "La rançon a été donné !");
                        victim.sendMessage(ChatColor.GREEN + "Vous venez de recevoir votre rançon !");
                        RebelleTerroriste.removeOtage(r.GetConnectedPlayer());
                        r.getTask().cancelTask();
                        r.setTask(null);
                        r.setTarget(null);
                        r.setPrixNegociation(r.GetConnectedPlayer(), 0);
                      } else {
                        MessageUtils.sendMessage((CommandSender)p, "La trésorerie ne dispose pas d'assez d'argent pour payer la rançon");
                      } 
                    } else {
                      p.sendMessage(ChatColor.RED + "Ce joueur n'a pas encore fixé de rançon !");
                    } 
                  } else {
                    p.sendMessage(ChatColor.RED + "Vous êtes l'otage, vous ne pouvez pas donner de rançon !");
                  } 
                } else {
                  p.sendMessage(ChatColor.RED + "Ce joueur n'a pas d'otage !");
                } 
              } 
              p.closeInventory();
            } else if (item.getItemMeta().getDisplayName().equals(demenotter.getDisplayName())) {
              if (dataCible.getMenotte()) {
                for (PotionEffect potion : cible.getActivePotionEffects())
                  cible.removePotionEffect(potion.getType()); 
                dataCible.setMenotte(false, null);
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "animations stop " + cible.getName());
                p.sendMessage(ChatColor.GREEN + "Joueur démenotté !");
              } else {
                p.sendMessage(ChatColor.RED + "Ce joueur n'est pas menotté !");
              } 
              p.closeInventory();
            } else if (item.getItemMeta().getDisplayName().equals(prison.getDisplayName())) {
              p.openInventory(Prisons.invPrison);
            } else if (item.getItemMeta().getDisplayName().equals(amende.getDisplayName())) {
              Amende.createAmende(p.getName(), cible.getName());
            } else if (item.getItemMeta().getDisplayName().equals(wanted.getDisplayName())) {
              if (WantedList.isWanted(cible.getName())) {
                cible.sendMessage("§4L'avis de recherche a été levé.");
                int stars = WantedList.getStars(cible.getName());
                int money = WantedList.WantedPrice(stars);
                data.setMoney(data.getMoney().add(BigDecimal.valueOf(money)));
                p.sendMessage("§2Félicitation vous avez retrouvé l'individu recherché de catégorie " + stars + ". §aVous remportez " + money + "€");
                WantedList.stopWanted(cible.getName());
              } else {
                p.sendMessage("§cIndividu non-recherché.");
              } 
              p.closeInventory();
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Ce joueur à un otage ! Action impossible ! Gardez vos distances !");
          }  
        if (data.selectedJob instanceof fr.karmaowner.jobs.Security || data.selectedJob instanceof fr.karmaowner.jobs.JobsMairie)
          if (isActionAgree) {
            Policier.Action fouiller = Policier.Action.FOUILLER;
            if (item.getItemMeta().getDisplayName().equals(fouiller.getDisplayName())) {
              if (data.selectedJob instanceof Missions) {
                Missions m = (Missions)data.selectedJob;
                if (m.getInProgress() != null && 
                  m.getInProgress().getType() instanceof FouilleTask) {
                  FouilleTask mt = (FouilleTask)m.getInProgress().getType();
                  mt.setTempCount(mt.getTempCount() - 1);
                } 
              } 
              dataCible.isFouille = true;
              cible.sendMessage("§aQuelqu'un est en train de vous fouiller...");
              final Inventory copy = Main.INSTANCE.getServer().createInventory(null, 36, ChatColor.DARK_AQUA + "Fouille");
              ItemStack[] copyinv = InventoryUtils.copiedInventoryContents((Inventory)cible.getInventory());
              PlayerUtils utils = new PlayerUtils();
              utils.setItem(ItemUtils.getItem(ServerUtils.PORTARME, (byte)0, 1, "§fPermis de port d'arme", null));
              utils.setInventory(copy);
              for (ItemStack i : copyinv) {
                if (i != null) {
                  ItemStack is = new ItemStack(i.getTypeId(), i.getAmount(), (short)0, i.getData().getData());
                  ItemMeta m = is.getItemMeta();
                  //boolean hasWeapon = (JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.M9Beretta, is) || JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.HKP2000, is) || JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.M1A1Carbine, is) || JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.M2Carbine, is));
                  boolean hasWeapon = false;
                  boolean LimitReachedTabacPlant = (is.getTypeId() == 6572 && utils.countOfItemIsInInventory(4, ItemUtils.getItem(6572, (byte)0, 1, null, null)));
                  boolean LimitReachedTabacSeche = (is.getTypeId() == 6573 && utils.countOfItemIsInInventory(4, ItemUtils.getItem(6573, (byte)0, 1, null, null)));
                  boolean LimitReachedCigarette = (is.getTypeId() == 6567 && utils.countOfItemIsInInventory(4, ItemUtils.getItem(6567, (byte)0, 1, null, null)));
                  boolean ItemsReached = (LimitReachedTabacPlant || LimitReachedTabacSeche || LimitReachedCigarette);
                  m.setDisplayName((JobsEvents.ILLEGAL_ITEMS_FOUILLE.contains(i) || hasWeapon || ItemsReached) ? ((hasWeapon && utils.isOnPlayerInventory()) ? "§aLe joueur à une licence de port d'armes" : "§4Objet illégal détecté") : "§aObjet légal");
                  i.setItemMeta(m);
                } 
              } 
              copy.setContents(copyinv);
              p.openInventory(copy);
              new TaskCreator(new CustomRunnable() {
                    public void customRun() {
                      if (!p.isOnline() || p.getOpenInventory() == null || !p.getOpenInventory().getTopInventory().getName().equals(ChatColor.DARK_AQUA + "Fouille") || cible == null || !cible.isOnline()) {
                        p.closeInventory();
                        cancel();
                        return;
                      } 
                      ItemStack[] copyinv = InventoryUtils.copiedInventoryContents((Inventory)cible.getInventory());
                      PlayerUtils utils = new PlayerUtils();
                      utils.setItem(ItemUtils.getItem(ServerUtils.PORTARME, (byte)0, 1, "§fPermis de port d'arme", null));
                      utils.setInventory(copy);
                      for (ItemStack i : copyinv) {
                        if (i != null) {
                          ItemStack is = new ItemStack(i.getTypeId(), i.getAmount(), (short)0, i.getData().getData());
                          ItemMeta m = is.getItemMeta();
                          //boolean hasWeapon = (JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.M9Beretta, is) || JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.HKP2000, is) || JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.M1A1Carbine, is) || JobsEvents.WEAPON.equalTo(JobsEvents.WEAPON.M2Carbine, is));
                          boolean hasWeapon = false;
                          boolean LimitReachedTabacPlant = (is.getTypeId() == 6572 && utils.countOfItemIsInInventory(4, ItemUtils.getItem(6572, (byte)0, 1, null, null)));
                          boolean LimitReachedTabacSeche = (is.getTypeId() == 6573 && utils.countOfItemIsInInventory(4, ItemUtils.getItem(6573, (byte)0, 1, null, null)));
                          boolean LimitReachedCigarette = (is.getTypeId() == 6567 && utils.countOfItemIsInInventory(4, ItemUtils.getItem(6567, (byte)0, 1, null, null)));
                          boolean ItemsReached = (LimitReachedTabacPlant || LimitReachedTabacSeche || LimitReachedCigarette);
                          m.setDisplayName((JobsEvents.ILLEGAL_ITEMS_FOUILLE.contains(i) || hasWeapon || ItemsReached) ? ((hasWeapon && utils.isOnPlayerInventory()) ? "§aLe joueur à une licence de port d'armes" : "§4Objet illégal détecté") : "§aObjet légal");
                          i.setItemMeta(m);
                        } 
                      } 
                      p.getOpenInventory().getTopInventory().setContents(copyinv);
                    }
                  },false, 20L, 10L);
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Ce joueur à un otage ! Action impossible ! Gardez vos distances !");
          }  
        e.setCancelled(true);
      } else if (inventory.getName().equals(ChatColor.DARK_AQUA + "Fouille") && (data.selectedJob instanceof fr.karmaowner.jobs.Security || data.selectedJob instanceof fr.karmaowner.jobs.JobsMairie)) {
        final Player cible = data.selectedJob.getTarget();
        PlayerData pCible = PlayerData.getPlayerData(cible.getName());
        if (e.isShiftClick()) {
          p.sendMessage("§cLe shift n'est pas autorisé");
          p.closeInventory();
        } 
        if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().contains("Objet illégal")) {
          if (!(pCible.selectedJob instanceof fr.karmaowner.jobs.Security) && !(pCible.selectedJob instanceof fr.karmaowner.jobs.JobsMairie)) {
            int slot = e.getSlot();
            ItemStack it = null;
            for (ItemStack i : cible.getInventory().getContents()) {
              if (i != null && e.getCurrentItem().getTypeId() == i.getTypeId() && e
                .getCurrentItem().getAmount() == i.getAmount() && e
                .getCurrentItem().getData().getData() == i.getData().getData()) {
                it = i;
                break;
              } 
            } 
            if (it != null) {
              p.getInventory().addItem(it);
              cible.getInventory().removeItem(it);
            } 
            e.setCancelled(true);
          } else {
            e.setCancelled(true);
            p.sendMessage("§cObjet légal: Vous ne pouvez pas saisir cet objet des Forces de l'ordre !");
          } 
        } else {
          e.setCancelled(true);
          p.sendMessage("§cObjet légal: Vous ne pouvez pas le saisir !");
        } 
      } else if (inventory.getName().equals(Prisons.PRISONINVNAME)) {
        final Player cible = data.selectedJob.getTarget();
        if (cible == null) {
          MessageUtils.sendMessage((CommandSender)p, "Impossible de mettre en prison. Aucune cible en vue.");
          e.setCancelled(true);
          return;
        } 
        final PlayerData dataCible = PlayerData.getPlayerData(cible.getName());
        if (dataCible.isMenotte) {
          for (PotionEffect potion : cible.getActivePotionEffects())
            cible.removePotionEffect(potion.getType()); 
          dataCible.setMenotte(false, null);
        } 
        int seconds = Prisons.getSecondsByItem(item);
        ProtectedRegion region = RegionUtils.getRegionByName(data.InteractingRegion, cible.getWorld().getName());
        if (region != null) {
          BlockVector blockVector1 = region.getMaximumPoint().toBlockPoint();
          BlockVector blockVector2 = region.getMinimumPoint().toBlockPoint();
          Vector half = blockVector1.subtract((Vector)blockVector2).divide(2);
          half = blockVector2.add(half);
          cible.teleport(new Location(p.getWorld(), half.getX(), half.getY() + 1.0D, half.getZ()));
          p.sendMessage(ChatColor.GREEN + "Joueur mit en prison !");
          cible.sendMessage(ChatColor.RED + "Vous venez d'être mit en prison par : §6" + p.getName());
          removeIllegalItem(p);
          dataCible.teleport = new Timestamp(System.currentTimeMillis());
          dataCible.waitTime = (seconds * 1000L);
          data.selectedJob.setTarget(null);
          new TaskCreator(new CustomRunnable() {
                public void customRun() {
                  if (Bukkit.getPlayerExact(cible.getName()) == null || dataCible.teleport == null) {
                    cancel();
                    return;
                  } 
                  Timestamp now = new Timestamp(System.currentTimeMillis());
                  if (dataCible.teleport != null && now.getTime() - dataCible.teleport.getTime() >= dataCible.waitTime) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "warp comico " + cible.getName());
                    for (PotionEffect potion : cible.getActivePotionEffects())
                      cible.removePotionEffect(potion.getType()); 
                    dataCible.waitTime = 0L;
                    dataCible.teleport = null;
                    cancel();
                  } else {
                    Timestamp timeLeft = new Timestamp(dataCible.waitTime - now.getTime() - dataCible.teleport.getTime());
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "hudmessage 500 " + cible.getName() + " 1 " + ChatColor.DARK_RED.toString() + timeLeft.getMinutes() + ":" + timeLeft.getSeconds());
                  } 
                }
              },false, 0L, 20L);
          e.setCancelled(true);
          p.closeInventory();
        } 
      } else if (inventory.getName().equals("§cCréation de l'amende")) {
        e.setCancelled(true);
        Amende am = Amende.getAmendeByExpediteur(p.getName());
        ItemStack augmenter = (ItemStack)(am.getExpediteur()).ACTIONS.get(Amende.ActionExpediteur.AUGMENTER);
        ItemStack diminuer = (ItemStack)(am.getExpediteur()).ACTIONS.get(Amende.ActionExpediteur.DIMINUER);
        ItemStack envoyer = (ItemStack)(am.getExpediteur()).ACTIONS.get(Amende.ActionExpediteur.ENVOYER);
        ItemStack annuler = (ItemStack)(am.getExpediteur()).ACTIONS.get(Amende.ActionExpediteur.ANNULER);
        if (item.isSimilar(augmenter)) {
          am.getExpediteur().increasePrice();
        } else if (item.isSimilar(diminuer)) {
          am.getExpediteur().decreasePrice();
        } else if (item.isSimilar(envoyer)) {
          if (am.getPrice() <= 0) {
            p.sendMessage("§cLe montant de l'amende doit être supérieur à 0€.");
          } else {
            if (data.selectedJob instanceof Missions) {
              Missions m = (Missions)data.selectedJob;
              if (m.getInProgress() != null && 
                m.getInProgress().getType() instanceof AmendeTask) {
                AmendeTask mt = (AmendeTask)m.getInProgress().getType();
                mt.setTempCount(mt.getTempCount() - 1);
              } 
            } 
            am.getExpediteur().setReady(true);
          } 
        } else if (item.isSimilar(annuler)) {
          am.getExpediteur().getTask().cancelTask();
          p.closeInventory();
          p.sendMessage("§cAmende annulée.");
          am.removeAmende();
        } 
      }  
  }
  
  public void onCloseInventory(InventoryCloseEvent e) {
    Inventory inv = e.getInventory();
    Player p = (Player)e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (inv.getName().equals(ChatColor.DARK_AQUA + "Fouille")) {
      Player cible = data.selectedJob.getTarget();
      if (cible != null) {
        PlayerData dataCible = PlayerData.getPlayerData(cible.getName());
        data.selectedJob.setTarget(null);
        dataCible.isFouille = false;
      } 
    } 
  }
  
  public static void removeIllegalItem(Player p) {
    for (ItemStack i : p.getInventory().getContents()) {
      if (i != null && 
        i.getTypeId() != 6616 && i.getTypeId() != 6615 && i.getTypeId() != 4255 && i
        .getTypeId() != 4256 && i.getTypeId() != 345 && JobsEvents.ILLEGAL_ITEMS.contains(i))
        p.getInventory().remove(i); 
    } 
  }
  
  @EventHandler
  public void onInteractWithTazer(PlayerInteractEntityEvent e) {
    Player p = e.getPlayer();
    ItemStack itemInHand = p.getItemInHand();
    final PlayerData data = PlayerData.getPlayerData(p.getName());
    Entity pEntity = e.getRightClicked();
    if (itemInHand != null && itemInHand.getTypeId() == 4694)
      if (data.selectedJob instanceof fr.karmaowner.jobs.Security) {
        if (data.selectedJob.getTask() == null) {
          if (pEntity instanceof Player) {
            data.selectedJob.setTask(new TaskCreator(new CustomRunnable() {
                    public void customRun() {
                      data.selectedJob.setTask(null);
                    }
                  },  false, 600L));
          } else {
            p.sendMessage(ChatColor.RED + "Aucune cible pour tirer !");
            e.setCancelled(true);
          } 
        } else {
          p.sendMessage(ChatColor.RED + "Vous devez attendre 30 secondes avant de retirer sur la cible !");
          e.setCancelled(true);
        } 
      } else {
        p.sendMessage(ChatColor.RED + "Vous ne pouvez pas utiliser cette objet car votre métier ne vous le permet pas.");
        e.setCancelled(true);
      }  
  }
  
  public void playerPrison(Player damager, int index) {
    final PlayerData dataDamager = PlayerData.getPlayerData(damager.getName());
    ProtectedRegion r = Prisons.getRandom();
    BlockVector blockVector1 = r.getMaximumPoint().toBlockPoint();
    BlockVector blockVector2 = r.getMinimumPoint().toBlockPoint();
    Vector half = blockVector1.subtract((Vector)blockVector2).divide(2);
    half = blockVector2.add(half);
    int seconds = Prisons.seconds[index];
    damager.teleport(new Location(damager.getWorld(), half.getX(), half.getY() + 1.0D, half.getZ()));
    damager.sendMessage(ChatColor.RED + "Vous venez d'être mit en prison pendant " + (seconds / 60) + " minutes");
    removeIllegalItem(damager);
    dataDamager.teleport = new Timestamp(System.currentTimeMillis());
    dataDamager.waitTime = (seconds * 1000L);
    final Player dam = damager;
    new TaskCreator(new CustomRunnable() {
          private long timer = System.currentTimeMillis();
          
          public void customRun() {
            if (Bukkit.getPlayerExact(dam.getName()) == null) {
              cancel();
              return;
            } 
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (dataDamager.teleport != null && now.getTime() - dataDamager.teleport.getTime() >= dataDamager.waitTime) {
              Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "warp comico " + dam.getName());
              for (PotionEffect potion : dam.getActivePotionEffects())
                dam.removePotionEffect(potion.getType()); 
              dataDamager.waitTime = 0L;
              dataDamager.teleport = null;
              cancel();
            } else if (dataDamager.teleport != null) {
              Timestamp timeLeft = new Timestamp(dataDamager.waitTime - now.getTime() - dataDamager.teleport.getTime());
              if (now.getTime() - this.timer >= 1500L) {
                this.timer = System.currentTimeMillis();
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "hudmessage 500 " + dam.getName() + " 1 " + ChatColor.DARK_RED.toString() + timeLeft.getMinutes() + ":" + timeLeft.getSeconds());
              } 
            } 
          }
        },false, 0L, 20L);
  }
}
