package fr.karmaowner.companies.shop;

import fr.karmaowner.common.Main;
import fr.karmaowner.utils.FileUtils;
import java.util.ArrayList;
import java.util.UUID;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Npc {
  private String categorie;
  
  private String type;
  
  private NPC npc;
  
  private Shop shop;
  
  public static FileUtils f = new FileUtils("PnjShop", "");
  
  public static ArrayList<Npc> npcList = new ArrayList<>();
  
  private Npc(String categorie, String type, String nameShop, String namePnj, String uuid) {
    NPC n = Main.npclib.getNPCRegistry().getByUniqueId(UUID.fromString(uuid));
    if (n != null) {
      this.npc = n;
      this.type = type;
      this.categorie = categorie;
      if (getExistingShop(nameShop) == null) {
        this.shop = new Shop(nameShop, this);
      } else {
        this.shop = getExistingShop(nameShop);
      } 
      npcList.add(this);
    } 
  }
  
  public Npc(Player player, String nameNpc, String nameShop, String categorie, String type) {
    this.npc = Main.npclib.getNPCRegistry().createNPC(EntityType.VILLAGER, nameNpc);
    this.npc.spawn(player.getLocation());
    this.type = type;
    this.categorie = categorie;
    npcList.add(this);
    if (getExistingShop(nameShop) == null) {
      this.shop = new Shop(nameShop, this);
    } else {
      this.shop = getExistingShop(nameShop);
    } 
    player.sendMessage(ChatColor.GREEN + "Pnj crée avec succès !");
  }
  
  public static Shop getExistingShop(String nameShop) {
    for (Npc n : npcList) {
      if (n != null && n.getShop() != null && n.getShop().getName() != null && n.getShop().getName().equals(nameShop))
        return n.getShop(); 
    } 
    return null;
  }
  
  public String getType() {
    return this.type;
  }
  
  public String getCategorie() {
    return this.categorie;
  }
  
  public NPC getNpc() {
    return this.npc;
  }
  
  public Shop getShop() {
    return this.shop;
  }
  
  public static void deleteNpc(Player p, String name) {
    for (Npc n : npcList) {
      if (n.getNpc().getName().equals(name)) {
        n.getNpc().destroy();
        npcList.remove(n);
        p.sendMessage(ChatColor.GREEN + "Pnj supprimé !");
        return;
      } 
    } 
    p.sendMessage(ChatColor.DARK_RED + "Ce pnj n'existe pas !");
  }
  
  public static void printPnjList(int page, Player p) {
    int nbperpage = 5;
    int maxpage = (npcList.size() / nbperpage <= 0) ? 1 : (npcList.size() / nbperpage);
    if (page < 1 || page > maxpage) {
      p.sendMessage(ChatColor.RED + "Page incorrect !");
      return;
    } 
    p.sendMessage(ChatColor.GOLD + "------" + ChatColor.YELLOW + "Liste des pnjs vendeurs page-" + page + ChatColor.GOLD + "------");
    int i = 0;
    for (Npc n : npcList) {
      if (i >= (page - 1) * nbperpage && i < page * nbperpage) {
        int x = n.getNpc().getEntity().getLocation().getBlockX();
        int y = n.getNpc().getEntity().getLocation().getBlockY();
        int z = n.getNpc().getEntity().getLocation().getBlockZ();
        p.sendMessage(ChatColor.GREEN.toString() + i + "- nom=" + ChatColor.DARK_GREEN + n.npc.getName() + " est situé x=" + x + ";y=" + y + ";z=" + z);
      } 
      i++;
    } 
    p.sendMessage(ChatColor.GOLD + "------ " + ChatColor.RED + "page " + page + ChatColor.GOLD + "/" + ChatColor.RED + maxpage + ChatColor.GOLD + " ------");
  }
  
  public static void loadData() {
    if (!f.directoryExist())
      f.createFile(); 
    f.loadFileConfiguration();
    if (f.getFileConfiguration() != null) {
      String section = "pnj";
      if (f.getFileConfiguration().getConfigurationSection(section) != null)
        for (String key : f.getFileConfiguration().getConfigurationSection(section).getKeys(false)) {
          if (!isNpcExist(f.getFileConfiguration().getString(section + "." + key + ".uuid")))
            new Npc(f.getFileConfiguration().getString(section + "." + key + ".categorie"), f
                .getFileConfiguration().getString(section + "." + key + ".type"), f
                .getFileConfiguration().getString(section + "." + key + ".nameShop"), f
                .getFileConfiguration().getString(section + "." + key + ".namePnj"), f
                .getFileConfiguration().getString(section + "." + key + ".uuid")); 
        }  
    } 
  }
  
  public static void saveData() {
    String section = "pnj", section1 = "npc";
    if (f.getFileConfiguration() != null) {
      Main.Log("Npc Data saving...");
      f.getFileConfiguration().set(section, null);
      f.getFileConfiguration().set(section1, null);
      int i = 0;
      for (Npc n : npcList) {
        if (n.categorie != null)
          f.getFileConfiguration().set(section + "." + i + ".categorie", n.categorie); 
        if (n.type != null)
          f.getFileConfiguration().set(section + "." + i + ".type", n.type); 
        if (n.shop != null && n.shop.getName() != null)
          f.getFileConfiguration().set(section + "." + i + ".nameShop", n.shop.getName()); 
        if (n.getNpc() != null)
          f.getFileConfiguration().set(section + "." + i + ".uuid", n.getNpc().getUniqueId().toString()); 
        if (n.shop != null)
          n.shop.saveData(n.getShop().getName()); 
        i++;
      } 
      f.saveConfig();
      Main.Log("Npc Data saved");
    }
  }
  
  public static void addNpc(Player pSender, String npcName, String npcShop, String type, String categorie) {
    if (type.equalsIgnoreCase("job") || type.equalsIgnoreCase("company")) {
      new Npc(pSender, npcName, npcShop, categorie, type);
    } else {
      pSender.sendMessage(ChatColor.RED + "Type incorrect: Soit Job ou Company");
    } 
  }
  
  public static Npc getNearbyNpc(Player p, int rayon) {
    for (Entity e : p.getNearbyEntities(rayon, rayon, rayon)) {
      for (Npc n : npcList) {
        if (Main.npclib.getNPCRegistry().getNPC(e) != null && n.getNpc().getUniqueId().toString().equals(Main.npclib.getNPCRegistry().getNPC(e).getUniqueId().toString()))
          return n; 
      } 
    } 
    return null;
  }
  
  public static boolean isNpcExist(String uuid) {
    for (Npc n : npcList) {
      if (n.getNpc().getUniqueId().toString().equals(uuid))
        return true; 
    } 
    return false;
  }
  
  public static Npc getNearbyNpc(Entity e) {
    for (Npc n : npcList) {
      if (n.getNpc().getUniqueId().toString().equals(Main.npclib.getNPCRegistry().getNPC(e).getUniqueId().toString()))
        return n; 
    } 
    return null;
  }
}
