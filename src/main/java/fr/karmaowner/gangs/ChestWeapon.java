package fr.karmaowner.gangs;

import fr.karmaowner.utils.RandomItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class ChestWeapon extends Chest {
  public List<ItemStack> getContents() {
    List<ItemStack> items = Chest.loadChest("weapon");
    List<ItemStack> randoms = new ArrayList<>();
    RandomItem itms = new RandomItem();
    for (ItemStack i : items)
      itms.addItem(i); 
    itms.ExtractItemToList(randoms);
    return randoms;
  }
}
