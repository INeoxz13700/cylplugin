package fr.karmaowner.companies.armurerie;

import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyArmurerie;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.InventoryUtils;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.MessageUtils;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArmurerieEvents implements Listener {
  @EventHandler
  public void onRightClickNpc(PlayerInteractEntityEvent e) {
    Entity entity = e.getRightClicked();
    Player p = e.getPlayer();
    if (entity instanceof Villager) {
      Villager v = (Villager)entity;
      if (v.getCustomName().equals("Fabricateur")) {
        PlayerData data = PlayerData.getPlayerData(p.getName());
        if (data != null && data.companyName != null && !data.companyName.equals("") && data.companyCategory
          .equals(Company.TYPE.ARMURERIE.name().toLowerCase())) {
          CompanyArmurerie company = (CompanyArmurerie)CompanyData.getCompanyData(data.companyName).getCompany();
          company.getArmurerie().fillInventory();
          p.openInventory(company.getArmurerie().getInventory());
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Intéraction impossible: Vous n'avez pas d'entreprise d'armurerie");
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    ItemStack item = e.getCurrentItem();
    Inventory inv = e.getClickedInventory();
    if (inv != null && inv.getTitle().equals("§cUsine à fabrique d'armes")) {
      if (item != null && item.getType() != Material.AIR && 
        data != null && data.companyName != null) {
        CompanyData cd = CompanyData.getCompanyData(data.companyName);
        CompanyArmurerie company = (CompanyArmurerie)cd.getCompany();
        ArmureriePnj armurerie = company.getArmurerie();
        String weapon_statut = item.getItemMeta().getLore().get(0);
        CompanyArmurerie.XP_ARMURERIE w = (CompanyArmurerie.XP_ARMURERIE)company.toXP(item.getTypeId(), item.getData().getData());
        if (weapon_statut.equals("§2Fabrication de l'arme terminée")) {
          double cost = data.getMoney().subtract(BigDecimal.valueOf(w.getPrice())).doubleValue();
          if (cost >= 0.0D) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (cd.elapsedTimeQuota.getDay() != now.getDay()) {
              company.setTotalProduceWeapon(0);
              cd.elapsedTimeQuota = new Timestamp(System.currentTimeMillis());
            } 
            if (company.getTotalProduceWeapon() <= 50) {
              cd.elapsedTimeQuota = new Timestamp(System.currentTimeMillis());
              armurerie.getWeapons().remove(w);
              data.setMoney(BigDecimal.valueOf(cost));
              MessageUtils.sendMessage((CommandSender)p, "§aVous venez d'obtenir l'arme §2" + w.getName().toLowerCase() + " §adans votre inventaire");
              InventoryUtils.addItemInInventory((Inventory)p.getInventory(), 
                  ItemUtils.getItem(w.getId(), w.getData(), 1, null, null));
              company.addXp(p, (Company.XP)w);
              company.setTotalProduceWeapon(company.getTotalProduceWeapon() + 1);
              p.closeInventory();
            } else {
              MessageUtils.sendMessage((CommandSender)p, "Vous avez dépassé le quota de fabrication quotidienne (limite: 50 armes)");
            } 
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas assez d'argent pour fabriquer cette arme");
          } 
        } else if (weapon_statut.equals("§4Fabriquer cette arme")) {
          armurerie.getWeapons().put(w, System.currentTimeMillis());
          p.closeInventory();
          MessageUtils.sendMessage((CommandSender)p, "§aL'arme §2" + w.getName().toLowerCase() + "§a est en cours de fabrication...");
        } 
      } 
      e.setCancelled(true);
    } 
  }
}
