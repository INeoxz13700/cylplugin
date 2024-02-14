package fr.karmaowner.events.jobs;

import fr.karmaowner.chat.events.ChatFormatEvent;
import fr.karmaowner.common.Main;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.events.JobsEvents;
import fr.karmaowner.jobs.BAC;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Maire;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import fr.karmaowner.utils.TimerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BacEvents implements Listener {
  @EventHandler
  public void DetectorTool(PlayerInteractEntityEvent e) {
    Entity target = e.getRightClicked();
    PlayerData data = PlayerData.getPlayerData(e.getPlayer().getName());
    ItemStack item = e.getPlayer().getItemInHand();
    if (target instanceof Player) {
      Player pTarget = (Player)target;
      PlayerData Tdata = PlayerData.getPlayerData(pTarget.getName());
      if (data != null && data.selectedJob instanceof BAC) {
        BAC b = (BAC)data.selectedJob;
        if (item.getTypeId() == 4190)
          if (System.currentTimeMillis() - b.timerDetectDrugs >= 60000L) {
            b.timerDetectDrugs = System.currentTimeMillis();
            PlayerUtils utils = new PlayerUtils();
            utils.setInventory((Inventory)pTarget.getInventory());
            boolean hasDrugs = utils.isOneOfItemsOnInv(JobsEvents.DRUGS.getItems());
            if (hasDrugs) {
              MessageUtils.sendMessage((CommandSender)e.getPlayer(), "§aDrogue interceptée. Vous pouvez procéder à la fouille");
            } else {
              MessageUtils.sendMessage((CommandSender)e.getPlayer(), "Cet individu n'a pas de drogue en sa possession");
            } 
          } else {
            MessageUtils.sendMessage((CommandSender)e.getPlayer(), "Cette fonctionnalité est disponible dans " + TimerUtils.formatString(60L));
          }  
      } 
    } 
  }
  
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (item != null && item.getType() != Material.AIR && inventory.getName().equals(Jobs.NAMEACTIONINVENTORY) && data.selectedJob instanceof BAC)
    {
      hasGrade grade = (hasGrade)data.selectedJob;
      if (item.getItemMeta().getDisplayName().equals(BAC.Action.DEMANTELER.getDisplayName()))
        if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
          if (data.selectedJob.getTarget() != null) {
            Player target = data.selectedJob.getTarget();
            PlayerData pdata = PlayerData.getPlayerData(target.getName());
            if (Maire.hasMandat(pdata.gangName)) {
              if (pdata.gangName != null && !pdata.gangName.equals("")) {
                GangData gData = GangData.getGangData(pdata.gangName);
                if (gData.getChef() != null) {
                  try {
                    gData.destroy();
                    MessageUtils.sendMessageFromConfig((CommandSender)p, "gang-disbanded");
                  } catch (Exception e1) {
                    e1.printStackTrace();
                  } 
                } else {
                  p.sendMessage(ChatColor.RED + "Il n'y a aucun chef dans ce gang. Il se pourrait que ce gang soit buggé");
                } 
              } else {
                Maire.removeMandat(pdata.gangName);
                MessageUtils.sendMessage((CommandSender)p, "Ce gang n'existe pas. Mandat supprimé");
              } 
            } else {
              Maire.removeMandat(pdata.gangName);
              MessageUtils.sendMessage((CommandSender)p, "Ce gang n'a pas de mandat");
            } 
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Aucune cible trouvée");
          } 
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Cette action est réservée aux " + 
              StringUtils.join(grade.getGrade().getGrades().getHg(), ", ") + " de la BAC");
        }  
    } 
  }
  
  @EventHandler
  public void spyChat(ChatFormatEvent e) {
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.selectedJob instanceof BAC && !Main.chat.getPlayerPrefix(p).contains("[STAFF]"))
      if (data.selectedJob.getEquipedClothes() != null) {
        String[] clothesName = data.selectedJob.getEquipedClothes().split(" ");
        if (clothesName.length == 2) {
          String metier = clothesName[1].toLowerCase();
          Jobs.Job j = Jobs.Job.getJobByName(metier);
          if (j != null)
            e.setNewMsgFormat(e.getMsgFormat().replaceAll("%job", j.getDisplayName())); 
        } 
      }  
  }
}
