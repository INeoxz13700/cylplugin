package fr.karmaowner.events.jobs;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyMenuiserie;
import fr.karmaowner.companies.CompanyMetallurgie;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Cuisinier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.karmaowner.utils.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CuisinierEvents implements Listener {
  private ItemStack recipe = null;
  
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.selectedJob instanceof Cuisinier && 
      item != null && 
      item.getType() != Material.AIR)
      if (inventory.getName().equals(Cuisinier.RECETTESINVENTORYNAME)) {
        if (data.selectedJob instanceof Cuisinier && 
          type == ClickType.RIGHT) {
          this.recipe = item;
          HashMap<Integer, HashMap<Integer, ItemStack>> craft = Cuisinier.getRecipe(item);
          Inventory step = Main.INSTANCE.getServer().createInventory(null, 9, ChatColor.RED + "Etapes");
          int i = 0;
          for (Map.Entry<Integer, HashMap<Integer, ItemStack>> s : craft.entrySet()) {
            ItemStack itemStep = new ItemStack(160, 1, (short)0, (byte) (i + 1));
            ItemMeta meta = itemStep.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_PURPLE + "Etape n°" + (i + 1));
            itemStep.setItemMeta(meta);
            step.addItem(itemStep);
            i++;
          } 
          p.openInventory(step);
        } 
        e.setCancelled(true);
      } else if (inventory.getName().equals(ChatColor.RED + "Etapes")) {
        String displayName = item.getItemMeta().getDisplayName();
        if ((displayName.split("n°")).length > 1) {
          int step = Integer.parseInt(displayName.split("n°")[1]);
          if (this.recipe != null) {
            InventoryType invtype = Cuisinier.getStepInventoryType(this.recipe, step, p);
            HashMap<Integer, ItemStack> ingredients = Cuisinier.getStep(this.recipe, step, p);
            Inventory inv = Main.INSTANCE.getServer().createInventory(null, invtype, this.recipe.getItemMeta().getDisplayName());
            for (Map.Entry<Integer, ItemStack> s : ingredients.entrySet())
              inv.setItem((Integer) s.getKey(), s.getValue());
            p.openInventory(inv);
          } 
        } 
        e.setCancelled(true);
      } else if (this.recipe != null && 
        inventory.getName().equals(this.recipe.getItemMeta().getDisplayName())) {
        e.setCancelled(true);
      }  
  }
  
  @EventHandler
  public void CraftingItems(CraftItemEvent e) {
    Player p = (Player)e.getWhoClicked();

    if(p.hasPermission("cylrp.admin")) return;

    PlayerData data = PlayerData.getPlayerData(p.getName());

    if(data.hasCompany()) {
      CompanyData companyData = CompanyData.getCompanyData(data.companyName);
      if (companyData.getCompany() instanceof CompanyMetallurgie) {
        CompanyMetallurgie cm = (CompanyMetallurgie) companyData.getCompany();
        Material material = e.getRecipe().getResult().getType();
        int id = e.getRecipe().getResult().getTypeId();
        byte metaData = e.getRecipe().getResult().getData().getData();
        Company.XP item = cm.toXP(material, metaData);
        item = (item == null) ? cm.toXP(id, metaData) : item;
        if (item != null) {
          return;
        }
      } else if (companyData.getCompany() instanceof CompanyMenuiserie) {
        CompanyMenuiserie cm = (CompanyMenuiserie) companyData.getCompany();
        Material material = e.getRecipe().getResult().getType();
        int id = e.getRecipe().getResult().getTypeId();
        byte metaData = e.getRecipe().getResult().getData().getData();
        Company.XP item = cm.toXP(material, metaData);
        item = (item == null) ? cm.toXP(id, metaData) : item;
        if (item != null) {
          return;
        }
      }
    }

    if (data.selectedJob instanceof Cuisinier) {
      if (e.getRecipe().getResult() != null && Cuisinier.CraftItems.isAbleToCraft(e.getRecipe().getResult())) {
        if (e.getAction().equals(InventoryAction.PICKUP_ALL) || e.getAction().equals(InventoryAction.PICKUP_HALF) || e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || e.getAction().equals(InventoryAction.HOTBAR_SWAP))
          if (Bukkit.getPlayerExact(data.getPlayerName()) != null)
            Bukkit.getPlayerExact(data.getPlayerName()).sendMessage(ChatColor.GREEN + "Objet fabriqué avec succès.");
      } else {
        Bukkit.getPlayerExact(data.getPlayerName()).sendMessage(ChatColor.GREEN + "Vous ne pouvez pas fabriquer cet objet.");
        e.setCancelled(true);
      }
    }
  }
}
