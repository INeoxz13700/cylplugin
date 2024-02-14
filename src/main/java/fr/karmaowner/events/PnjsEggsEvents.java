package fr.karmaowner.events;

import fr.karmaowner.common.Main;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyElevage;
import fr.karmaowner.companies.eggs.EggsHatching;
import fr.karmaowner.companies.eggs.EntityEggsInventory;
import fr.karmaowner.companies.eggs.EntityEggsPnj;
import fr.karmaowner.companies.eggs.StateEggs;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PnjsEggsEvents implements Listener {
  private Inventory inv;
  
  private EntityEggsInventory eei;
  
  @EventHandler
  public void onClickPnjEggs(PlayerInteractEntityEvent e) {
    Player p = e.getPlayer();
    PlayerData Player = PlayerData.getPlayerData(p.getName());
    CompanyData company = CompanyData.Companies.get(Player.companyName);
    Entity entity = e.getRightClicked();
    if (entity instanceof org.bukkit.entity.Villager) {
      if (company != null && 
        company.getCompany() instanceof CompanyElevage) {
        CompanyElevage ce = (CompanyElevage)company.getCompany();
        this.eei = new EntityEggsInventory(ce.getEggsData());
        this.inv = this.eei.getInventory();
        NPC npc = Main.npclib.getNPCRegistry().getNPC(entity);
        if (npc.getName().equalsIgnoreCase(EntityEggsPnj.PNJNAME))
          p.openInventory(this.inv); 
      } 
      e.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    Inventory inv2 = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    PlayerData Player = PlayerData.getPlayerData(p.getName());
    CompanyData company = CompanyData.Companies.get(Player.companyName);
    if (company != null && 
      company.getCompany() instanceof CompanyElevage && 
      this.inv != null && 
      inv2.getName().equals(this.inv.getName())) {
      CompanyElevage ce = (CompanyElevage)company.getCompany();
      int index = e.getSlot();
      if (this.inv.getContents()[index] != null) {
        EggsHatching egg = ce.getEggsData().getEggs().get(index);
        if (egg != null) {
          CompanyElevage.XP_ELEVAGE x = CompanyElevage.XP_ELEVAGE.getEnum(egg.getName());
          if (ce.isItemUnlocked(x)) {
            if (egg.getState() == StateEggs.HATCH) {
              if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(new ItemStack(383, 1, (short)0, Byte.valueOf(egg.getTypeId())));
                ce.getEggsData().getEggs().remove(egg);
                p.sendMessage(ChatColor.GREEN + "Vous venez de récupérer " + egg.getName());
                p.closeInventory();
              } else {
                p.sendMessage(ChatColor.DARK_RED + "Votre inventaire est plein");
              } 
            } else {
              p.sendMessage(ChatColor.DARK_RED + "L'oeuf n'a pas encore éclos");
            } 
          } else {
            ce.locked_Message(p, x);
          } 
        } 
      } 
      e.setCancelled(true);
    } 
  }
}
