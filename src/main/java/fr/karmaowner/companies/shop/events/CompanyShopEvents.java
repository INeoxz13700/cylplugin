package fr.karmaowner.companies.shop.events;

import fr.karmaowner.common.Main;
import fr.karmaowner.companies.shop.AbstractShop;
import fr.karmaowner.companies.shop.ActionShopType;
import fr.karmaowner.companies.shop.ItemShop;
import fr.karmaowner.companies.shop.Npc;
import fr.karmaowner.companies.shop.Shop;
import fr.karmaowner.companies.shop.TempActionShop;
import fr.karmaowner.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CompanyShopEvents implements Listener {
  @EventHandler
  public void onRightClickNpc(PlayerInteractEntityEvent e) {
    Entity entity = e.getRightClicked();
    Player p = e.getPlayer();
    if (entity instanceof org.bukkit.entity.Villager) {
      Npc pnj1 = Npc.getNearbyNpc(entity);
      if (pnj1 != null) {
        OpenShopEvent ev = new OpenShopEvent(p, pnj1);
        Main.INSTANCE.getServer().getPluginManager().callEvent(ev);
        if (!ev.isCancelled())
          p.openInventory(pnj1.getShop().getInventory()); 
      } 
    } 
  }
  
  @EventHandler
  public void onInteractInventoryShops(InventoryClickEvent e) {
    Inventory inv = e.getInventory();
    if (Shop.isShop(inv.getTitle()))
      e.setCancelled(true); 
  }
  
  @EventHandler
  public void onClickItemShop(InventoryClickEvent e) {
    ClickType action = e.getClick();
    Inventory inv = e.getClickedInventory();
    Player p = (Player)e.getWhoClicked();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    if (pData == null)
      return; 
    ItemStack item = e.getCurrentItem();
    if (item != null) {
      Npc pnj = Shop.getShop(inv.getName());
      if (pnj != null) {
        Shop shop = pnj.getShop();
        AbstractShop a = shop.getAbstractShop(new ItemShop(item.getTypeId(), item.getData().getData(), e.getSlot()));
        if (a != null) {
          if (inv.getTitle().equals(shop.getAbInventory().getTitle()))
            if (action == ClickType.LEFT && e.getSlot() == 19) {
              if (pnj.getType().equals("job")) {
                if (!pnj.getCategorie().equalsIgnoreCase(pData.selectedJob.getFeatures().getName())) {
                  p.sendMessage(ChatColor.RED + "Pour vendre un produit dans ce shop vous devez obligatoirement être " + ChatColor.DARK_RED + pnj.getCategorie());
                  return;
                } 
              } else if (pnj.getType().equals("company") && (
                pData.companyName == null || !pnj.getCategorie().equalsIgnoreCase(pData.companyCategory))) {
                p.sendMessage(ChatColor.RED + "Vous devez appartenir à une entreprise de " + ChatColor.DARK_RED + pnj
                    .getCategorie() + ChatColor.RED + " pour vendre !");
                return;
              } 
              p.closeInventory();
              TempActionShop.addAction(p, a.getItemShop(), shop, ActionShopType.SELL);
            } else if (action == ClickType.SHIFT_LEFT || action == ClickType.SHIFT_RIGHT) {
              if (e.getSlot() == 19)
                TempActionShop.addAction(p, a.getItemShop(), shop, ActionShopType.LOTBUY); 
            } else if (action == ClickType.RIGHT && 
              e.getSlot() == 19) {
              TempActionShop.addAction(p, a.getItemShop(), shop, ActionShopType.BUY);
            }  
          if (inv.getTitle().equals(shop.getInventory().getTitle()) && 
            action == ClickType.RIGHT) {
            shop.fillAbInventory(a);
            p.closeInventory();
            p.openInventory(shop.getAbInventory());
          } 
        } 
      } 
    } 
  }
}
