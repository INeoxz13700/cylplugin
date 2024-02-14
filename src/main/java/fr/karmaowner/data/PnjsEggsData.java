package fr.karmaowner.data;

import fr.karmaowner.common.Main;
import fr.karmaowner.companies.eggs.EntityEggsPnj;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PnjsEggsData {
  public static ArrayList<NPC> pnjs = new ArrayList<>();
  
  public PnjsEggsData() {
    loadData();
  }
  
  public static NPC getPnjLooking(Player p) {
    for (NPC v : pnjs) {
      for (Entity e : p.getNearbyEntities(1.0D, 1.0D, 1.0D)) {
        if (v.getUniqueId().toString().equals(Main.npclib.getNPCRegistry().getNPC(e).getUniqueId().toString()))
          return v; 
      } 
    } 
    return null;
  }
  
  public static void printPnjList(int page) {
    int nbperpage = 5;
    int maxpage = (pnjs.size() / nbperpage <= 0) ? 1 : (pnjs.size() / nbperpage);
    if (page < 1 || page > maxpage) {
      Bukkit.broadcastMessage(ChatColor.RED + "Page incorrect !");
      return;
    } 
    Bukkit.broadcastMessage(ChatColor.GOLD + "------" + ChatColor.YELLOW + "Liste des pnjs page-" + page + ChatColor.GOLD + "------");
    int i = 0;
    for (NPC v : pnjs) {
      if (i >= (page - 1) * nbperpage && i < page * nbperpage) {
        int x = (int)v.getEntity().getLocation().getX();
        int y = (int)v.getEntity().getLocation().getY();
        int z = (int)v.getEntity().getLocation().getZ();
        Bukkit.broadcastMessage(ChatColor.GREEN.toString() + i + "- id=" + ChatColor.DARK_GREEN + v.getId() + " est situé x=" + x + ";y=" + y + ";z=" + z);
      } 
      i++;
    } 
    Bukkit.broadcastMessage(ChatColor.GOLD + "------ " + ChatColor.RED + "page " + page + ChatColor.GOLD + "/" + ChatColor.RED + maxpage + ChatColor.GOLD + " ------");
  }
  
  public static Entity getEntities(int idEntity) {
    List<Entity> e = Main.INSTANCE.getServer().getWorld("cyl").getEntities();
    for (Entity e1 : e) {
      if (e1.getEntityId() == idEntity)
        return e1; 
    } 
    return null;
  }
  
  public static void removePnjLooking(Player p) {
    if (getPnjLooking(p) != null) {
      NPC npc = getPnjLooking(p);
      pnjs.remove(npc);
      npc.despawn();
      npc.destroy();
      p.sendMessage(ChatColor.GREEN + "Pnj supprimé avec succés !");
    } else {
      p.sendMessage(ChatColor.DARK_RED + "Aucun Pnj Eggs à proximité");
    } 
  }
  
  public void loadData() {
    Iterator<NPC> it = Main.npclib.getNPCRegistry().iterator();
    while (it.hasNext()) {
      NPC npc = it.next();
      if (npc.getEntity() != null && npc.getName().equalsIgnoreCase(EntityEggsPnj.PNJNAME))
        pnjs.add(npc); 
    } 
  }
}
