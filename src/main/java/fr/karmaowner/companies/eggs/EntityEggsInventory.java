package fr.karmaowner.companies.eggs;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.EggsData;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EntityEggsInventory {
  private Inventory inv;
  
  private EggsData data;
  
  public EntityEggsInventory(EggsData data) {
    this.data = data;
    this.inv = Main.INSTANCE.getServer().createInventory(null, 27, ChatColor.RED + "Inventaire Oeufs");
    update();
  }
  
  public void update() {
    for (int i = 0; i < this.data.getEggs().size(); i++) {
      EggsHatching egg = this.data.getEggs().get(i);
      ItemStack item = new ItemStack(383, 1, (short)0, egg.getTypeId());
      ItemMeta meta = item.getItemMeta();
      ArrayList<String> liste = new ArrayList<>();
      liste.add(ChatColor.GOLD + egg.getName());
      if (egg.getState() != StateEggs.HATCH) {
        liste.add("Temps restant: " + egg.getTimeLeft().getHours() + ":" + egg.getTimeLeft().getMinutes() + ":" + egg.getTimeLeft().getSeconds());
      } else {
        liste.add("Oeuf éclos");
      } 
      meta.setLore(liste);
      item.setItemMeta(meta);
      this.inv.setItem(i, item);
    } 
  }
  
  public Inventory getInventory() {
    return this.inv;
  }
  
  public int getEmptySlot() {
    for (int i = 0; i < this.inv.getSize(); i++) {
      if (this.inv.getContents()[i] == null)
        return i; 
    } 
    return -1;
  }
}
