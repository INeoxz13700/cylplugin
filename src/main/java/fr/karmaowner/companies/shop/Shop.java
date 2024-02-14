package fr.karmaowner.companies.shop;

import fr.karmaowner.common.Main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop {
  private String name;
  
  private Inventory inv;
  
  private Inventory abstractShopInv;
  
  private Npc npc;
  
  private ArrayList<AbstractShop> abstractShop = new ArrayList<>();
  
  public static HashMap<String, Shop> shops = new HashMap<>();
  
  public Shop(String name, Npc npc) {
    this.name = name;
    this.inv = Main.INSTANCE.getServer().createInventory(null, 54, ChatColor.GOLD + name);
    this.abstractShopInv = Main.INSTANCE.getServer().createInventory(null, 54, ChatColor.GOLD + name);
    shops.put(ChatColor.GOLD + this.name, this);
    this.npc = npc;
    loadData(this.name);
  }
  
  public boolean isExist(ItemShop item) {
    for (AbstractShop a : this.abstractShop) {
      if (a.getItemShop().equals(item))
        return true; 
    } 
    return false;
  }
  
  public Npc getNpc() {
    return this.npc;
  }
  
  public String getName() {
    return this.name;
  }
  
  public static Npc getShop(String inventoryName) {
    for (Shop s : shops.values()) {
      String name = ChatColor.GOLD + s.getName();
      if (name.equals(inventoryName))
        return s.getNpc(); 
    } 
    return null;
  }
  
  public static void addItem(Player p, ItemShop item) {
    Npc pnj = Npc.getNearbyNpc(p, 1);
    if (pnj != null) {
      Shop s = pnj.getShop();
      if (item.isRightSlot(s.getInventory().getSize())) {
        if (!s.isExist(item)) {
          if (s.getInventory().getItem(item.getSlot()) == null) {
            ItemStack obj = item.getItem();
            ItemMeta meta = obj.getItemMeta();
            List<String> list = new ArrayList<>();
            list.add(ChatColor.BLUE + "Prix d'achat du marché: " + item.getBuyPrice());
            list.add(ChatColor.DARK_PURPLE + "Prix de vente du marché: " + item.getSellPrice());
            list.add(ChatColor.RED + "Clic droit pour ouvrir le shop");
            meta.setLore(list);
            obj.setItemMeta(meta);
            s.getInventory().setItem(item.getSlot(), obj);
            s.abstractShop.add(new AbstractShop(item));
            p.sendMessage(ChatColor.GREEN + "Objet ajouté avec succès !");
          } else {
            p.sendMessage(ChatColor.RED + "Il y a déjà un objet à cet endroit du shop !");
          } 
        } else {
          p.sendMessage(ChatColor.RED + "L'objet existe déjà dans le shop !");
        } 
      } else {
        p.sendMessage(ChatColor.RED + "Le slot que vous avez saisie est incorrect !");
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Aucun shop à proximité");
    } 
  }
  
  public Inventory getInventory() {
    return this.inv;
  }
  
  public static boolean isShop(String title) {
    for (String title1 : shops.keySet()) {
      if (title1.equals(title))
        return true; 
    } 
    return false;
  }
  
  public Inventory getAbInventory() {
    return this.abstractShopInv;
  }
  
  public void fillAbInventory(AbstractShop a) {
    this.abstractShopInv.clear();
    for (int i = 3; i <= 48; ) {
      this.abstractShopInv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
      i += 9;
    } 
    ItemStack item = a.getItemShop().getItem();
    ItemMeta meta = item.getItemMeta();
    List<String> list = new ArrayList<>();
    list.add(ChatColor.BLUE + "Prix d'achat du marché: " + a.getItemShop().getBuyPrice() + "€");
    list.add(ChatColor.DARK_PURPLE + "Prix de vente du marché: " + a.getItemShop().getSellPrice() + "€");
    list.add(ChatColor.RED + "Clic droit pour acheter");
    list.add(ChatColor.LIGHT_PURPLE + "Maintenir shift + Clic Droit pour acheter par stack");
    list.add(ChatColor.DARK_RED + "Clic gauche pour vendre");
    meta.setLore(list);
    item.setItemMeta(meta);
    this.abstractShopInv.setItem(19, item);
    for (int j = 4; j <= 49; j += 9) {
      for (int k = j; k <= j + 4; k++)
        this.abstractShopInv.setItem(k, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)0, (byte) 15));
    } 
  }
  
  public AbstractShop getAbstractShop(ItemShop item) {
    for (AbstractShop a : this.abstractShop) {
      if (a.getItemShop().equals(item))
        return a; 
    } 
    return null;
  }
  
  public AbstractShop getAbstractShopByItemStack(ItemStack item) {
    for (AbstractShop a : this.abstractShop) {
      if (a.getItemShop().getId() == item.getTypeId() && a.getItemShop().getData() == item.getData().getData())
        return a; 
    } 
    return null;
  }
  
  public static void removeItem(Player p, int slot) {
    Npc pnj = Npc.getNearbyNpc(p, 1);
    if (pnj != null) {
      Shop s = pnj.getShop();
      if (slot >= 0 && slot <= 53) {
        ItemStack inInventory = s.getInventory().getItem(slot);
        if (inInventory != null) {
          s.abstractShop.remove(s.getAbstractShop(new ItemShop(inInventory.getTypeId(), inInventory.getData().getData(), slot)));
          s.getInventory().remove(inInventory);
          p.sendMessage(ChatColor.GREEN + "Objet supprimé avec succès !");
        } else {
          p.sendMessage(ChatColor.RED + "Aucun objet trouvé à cette endroit de l'inventaire !");
        } 
      } else {
        p.sendMessage(ChatColor.RED + "Le slot que vous avez saisie est incorrect !");
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Aucun shop à proximité");
    } 
  }
  
  public void loadData(String key) {
    String section = "npc";
    if (Npc.f.getFileConfiguration() != null)
      if (Npc.f.getFileConfiguration().getConfigurationSection(section) != null && 
        Npc.f.getFileConfiguration().getConfigurationSection(section + "." + key + ".abstractShop") != null)
        for (String key2 : Npc.f.getFileConfiguration().getConfigurationSection(section + "." + key + ".abstractShop").getKeys(false)) {
          AbstractShop a = new AbstractShop(new ItemShop(Npc.f.getFileConfiguration().getInt(section + "." + key + ".abstractShop." + key2 + ".itemBasis.id"), Byte.parseByte(Npc.f.getFileConfiguration().getString(section + "." + key + ".abstractShop." + key2 + ".itemBasis.data")), Npc.f.getFileConfiguration().getDouble(section + "." + key + ".abstractShop." + key2 + ".itemBasis.buyPrice"), Npc.f.getFileConfiguration().getDouble(section + "." + key + ".abstractShop." + key2 + ".itemBasis.sellPrice"), Npc.f.getFileConfiguration().getInt(section + "." + key + ".abstractShop." + key2 + ".itemBasis.slot"), Npc.f.getFileConfiguration().getInt(section + "." + key + ".abstractShop." + key2 + ".itemBasis.amount")));
          ItemStack obj = a.getItemShop().getItem();
          if (obj != null && obj.getType() != null) {
            ItemMeta meta = obj.getItemMeta();
            if (meta != null) {
              List<String> list = new ArrayList<>();
              list.add(ChatColor.BLUE + "Prix d'achat du marché: " + a.getItemShop().getBuyPrice());
              list.add(ChatColor.DARK_PURPLE + "Prix de vente du marché: " + a.getItemShop().getSellPrice());
              list.add(ChatColor.RED + "Clic droit pour ouvrir le shop");
              meta.setLore(list);
              obj.setItemMeta(meta);
              getInventory().setItem(a.getItemShop().getSlot(), obj);
              this.abstractShop.add(a);
            } 
          } 
        }   
  }
  
  public void saveData(String key) {
    String section = "npc";
    if (Npc.f.getFileConfiguration() != null) {
      int i = 0;
      for (AbstractShop a : this.abstractShop) {
        Npc.f.getFileConfiguration().set(section + "." + key + ".abstractShop." + i + ".itemBasis.id", a.getItemShop().getId());
        Npc.f.getFileConfiguration().set(section + "." + key + ".abstractShop." + i + ".itemBasis.data", a.getItemShop().getData());
        Npc.f.getFileConfiguration().set(section + "." + key + ".abstractShop." + i + ".itemBasis.buyPrice", a.getItemShop().getBuyPrice());
        Npc.f.getFileConfiguration().set(section + "." + key + ".abstractShop." + i + ".itemBasis.sellPrice", a.getItemShop().getSellPrice());
        Npc.f.getFileConfiguration().set(section + "." + key + ".abstractShop." + i + ".itemBasis.slot", a.getItemShop().getSlot());
        Npc.f.getFileConfiguration().set(section + "." + key + ".abstractShop." + i + ".itemBasis.amount", a.getItemShop().getAmount());
        i++;
      } 
    } 
  }
}
