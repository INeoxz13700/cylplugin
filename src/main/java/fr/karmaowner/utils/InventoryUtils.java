package fr.karmaowner.utils;

import fr.karmaowner.common.Main;
import net.minecraft.server.v1_12_R1.Container;
import net.minecraft.server.v1_12_R1.Slot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryUtils {
  public static int getFilledSlot(Inventory inv) {
    int i = 0;
    for (ItemStack item : inv.getContents()) {
      if (item != null && item.getType() != Material.AIR)
        i++; 
    } 
    return i;
  }
  
  public static void addItemInInventory(Inventory inv, ItemStack item) {
    PlayerUtils pu = new PlayerUtils();
    pu.setInventory(inv);
    if (inv instanceof PlayerInventory) {
      PlayerInventory pinv = (PlayerInventory)inv;
      if (item != null && item.getType() != Material.AIR) {
        Player p = (Player)pinv.getHolder();
        if (!ItemUtils.addItemToInventorySafe(item, p))
          p.getWorld().dropItem(p.getLocation(), item); 
      } 
    } 
  }
  
  public static void addAnInexistantItemInInventory(Inventory inv, ItemStack item) {
    PlayerUtils pu = new PlayerUtils();
    pu.setInventory(inv);
    if (!pu.isItemOnInventory(item))
      addItemInInventory(inv, item); 
  }
  
  public static void addEquippedClothesInInventory(Player p) {
    PlayerInventory playerInventory = p.getInventory();
    if (p.getInventory().getHelmet() != null) {
      ItemStack helmet = ItemUtils.getItem(p.getInventory().getHelmet().getTypeId(), p
          .getInventory().getHelmet().getData().getData(), p.getInventory().getHelmet().getAmount(), null, null);
      p.getInventory().setHelmet(new ItemStack(Material.AIR));
      addAnInexistantItemInInventory((Inventory)playerInventory, helmet);
    } 
    if (p.getInventory().getChestplate() != null) {
      ItemStack chestplate = ItemUtils.getItem(p.getInventory().getChestplate().getTypeId(), p
          .getInventory().getChestplate().getData().getData(), p.getInventory().getChestplate().getAmount(), null, null);
      p.getInventory().setChestplate(new ItemStack(Material.AIR));
      addAnInexistantItemInInventory((Inventory)playerInventory, chestplate);
    } 
    if (p.getInventory().getLeggings() != null) {
      ItemStack leggings = ItemUtils.getItem(p.getInventory().getLeggings().getTypeId(), p
          .getInventory().getLeggings().getData().getData(), p.getInventory().getLeggings().getAmount(), null, null);
      p.getInventory().setLeggings(new ItemStack(Material.AIR));
      addAnInexistantItemInInventory((Inventory)playerInventory, leggings);
    }

    if (p.getInventory().getBoots() != null) {
      ItemStack boots = ItemUtils.getItem(p.getInventory().getBoots().getTypeId(), p
          .getInventory().getBoots().getData().getData(), p.getInventory().getBoots().getAmount(), null, null);
      p.getInventory().setBoots(new ItemStack(Material.AIR));
      addAnInexistantItemInInventory((Inventory)playerInventory, boots);
    }

    if (InventoryUtils.getGpb(p).getType() != Material.AIR) {
      ItemStack gpb = ItemUtils.getItem(InventoryUtils.getGpb(p).getTypeId(), InventoryUtils.getGpb(p).getData().getData(), InventoryUtils.getGpb(p).getAmount(), null, null);
      InventoryUtils.setGpb(p, new ItemStack(Material.AIR), true);
      addAnInexistantItemInInventory((Inventory)playerInventory, gpb);
    }


  }
  
  public static void copyContentsInventory(Inventory from, Inventory to) {
    ItemStack[] contents = from.getContents();
    for (int i = 0; i < (from.getContents()).length; i++) {
      if (contents[i] != null)
        to.getContents()[i] = ItemUtils.getItem(contents[i].getTypeId(), contents[i].getData().getData(), contents[i]
            .getAmount(), contents[i].hasItemMeta() ? contents[i].getItemMeta().getDisplayName() : null, 
            contents[i].hasItemMeta() ? contents[i].getItemMeta().getLore() : null); 
    } 
  }
  
  public static ItemStack[] copiedInventoryContents(Inventory from) {
    ItemStack[] copy = new ItemStack[36];
    ItemStack[] contents = from.getStorageContents();
    for (int i = 0; i < (from.getStorageContents()).length; i++) {
      if (contents[i] != null)
        copy[i] = contents[i].clone(); 
    } 
    return copy;
  }

  public static void setGalon(Player player, ItemStack is, boolean onConnection)
  {
    if(onConnection)
    {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          CraftPlayer craftPlayer = (CraftPlayer) player;
          Container container = craftPlayer.getHandle().defaultContainer;
          container.setItem(46,  CraftItemStack.asNMSCopy(is));
        }
      };
      task.runTaskLater(Main.INSTANCE, 20);
    }
    else
    {
      CraftPlayer craftPlayer = (CraftPlayer) player;
      Container container = craftPlayer.getHandle().defaultContainer;
      container.setItem(46,  CraftItemStack.asNMSCopy(is));
    }
  }

  public static ItemStack getGalon(Player player)
  {
    CraftPlayer craftPlayer = (CraftPlayer) player;
    Container container = craftPlayer.getHandle().defaultContainer;
    return CraftItemStack.asBukkitCopy(container.getSlot(46).getItem());
  }

  public static void setGpb(Player player, ItemStack is, boolean onConnection)
  {
    if(onConnection)
    {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          CraftPlayer craftPlayer = (CraftPlayer) player;
          Container container = craftPlayer.getHandle().defaultContainer;
          container.setItem(47,  CraftItemStack.asNMSCopy(is));
        }
      };
      task.runTaskLater(Main.INSTANCE, 10);
    }
    else
    {
      CraftPlayer craftPlayer = (CraftPlayer) player;
      Container container = craftPlayer.getHandle().defaultContainer;
      container.setItem(47,  CraftItemStack.asNMSCopy(is));
    }

  }

  public static ItemStack getGpb(Player player)
  {
    CraftPlayer craftPlayer = (CraftPlayer) player;
    Container container = craftPlayer.getHandle().defaultContainer;
    return CraftItemStack.asBukkitCopy(container.getSlot(47).getItem());
  }

  public static int getFirstEmptySlot(Inventory inventory) {
    ItemStack[] contents = inventory.getStorageContents();

    for (int i = 0; i < contents.length; i++) {
      if (contents[i] == null) {
        return i;
      }
    }

    return -1;
  }

  public static void removeDuplicateItemStack(Player player, int itemId, byte subId)
  {
    int count = 0;
    for(int i = 0; i < player.getInventory().getStorageContents().length; i++)
    {
      ItemStack is = player.getInventory().getStorageContents()[i];
      if(is != null && is.getTypeId() == itemId && is.getData().getData() == subId)
      {
        count++;
        if(count >= 2)
        {
          player.getInventory().setItem(i, null);
          count--;
        }
      }
    }
  }
}
