package fr.karmaowner.utils;

import fr.cylapi.core.IWeapon;
import fr.karmaowner.events.JobsEvents;
import org.bukkit.inventory.ItemStack;

public class WeaponUtils implements IWeapon {
  public boolean isWeapon(ItemStack arg0) {
    return JobsEvents.WEAPON.contains(arg0);
  }
  
  public ItemStack isWeaponGetItemStack(ItemStack arg0) {
    return isWeapon(arg0) ? arg0 : null;
  }
}
