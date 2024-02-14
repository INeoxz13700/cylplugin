package fr.karmaowner.gps;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.InventoryUtils;
import fr.karmaowner.utils.RecordBuilder;
import java.awt.Point;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GPSInventory implements Listener {


  @EventHandler
  public void clickGPSItem(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if (e.getItem() != null) {
      ItemStack a = e.getItem();
      if (a != null && a.getType().getId() == 345 && a.hasItemMeta() && a.getItemMeta().hasDisplayName() && a.getItemMeta().getDisplayName().equals("§cGPS")) {
        try {
          GPS.refreshGPSDataFromDB();


        } catch (SQLException e2) {
          e2.printStackTrace();
        }
        displayGps(1,p);
      } 
    } 
  }

  public void displayGps(int page, Player p)
  {
    Inventory menu = Bukkit.createInventory(null, 54, "§cGPS (Page : " + page + ")");

    int elementsInPage = menu.getSize() - 4;
    int index = 0;
    List<String> keyList = new ArrayList<>();
    keyList.addAll(GPS.coordinatesGPS.keySet());


    for (int i = 0 ; i < elementsInPage; i++)
    {
      index = i + ((page-1)*elementsInPage);
      ItemStack gps = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);
      ItemMeta gpsM = gps.getItemMeta();

      if(index >= keyList.size()-1) break;

      String key = keyList.get(index);

      gpsM.setDisplayName("§a" + key);
      gpsM.setLore(Arrays.asList("", String.valueOf(GPS.coordinatesGPS.get(key).getX()), "65", String.valueOf(GPS.coordinatesGPS.get(key).getY()), "§eActiver votre gps sur la destination: §a" + key, "§eDestination automatique"));
      gps.setItemMeta(gpsM);
      menu.addItem(gps);
    }

    int slots = InventoryUtils.getFilledSlot(menu);

    ItemStack gps_next_page = new ItemStack(Material.PAPER, 1);
    ItemMeta gps_next_page_meta = gps_next_page.getItemMeta();
    gps_next_page_meta.setDisplayName("§6Page suivante");
    gps_next_page.setItemMeta(gps_next_page_meta);

    ItemStack gps_previous_page = new ItemStack(Material.PAPER, 1);
    ItemMeta gps_previous_page_meta = gps_previous_page.getItemMeta();
    gps_previous_page_meta.setDisplayName("§6Page précédente");
    gps_previous_page.setItemMeta(gps_previous_page_meta);

    ItemStack gps9 = new ItemStack(Material.GLASS, 1);
    ItemMeta gps9M = gps9.getItemMeta();
    gps9M.setDisplayName("§aAjouter une destination");
    gps9M.setLore(Arrays.asList("", "§e/destination add (Nom)"));
    ItemStack gps11 = new ItemStack(Material.GLASS, 1);
    ItemMeta gps11M = gps11.getItemMeta();
    gps11M.setDisplayName("§aArrêter le GPS");
    gps11M.setLore(Arrays.asList("", "§eDésactiver le GPS s'il est en cours d'utilisation"));
    gps9.setItemMeta(gps9M);
    gps11.setItemMeta(gps11M);
    menu.setItem(menu.getSize() - 4, gps_previous_page);
    menu.setItem(menu.getSize() - 3, gps_next_page);
    menu.setItem(menu.getSize() - 2, gps9);
    menu.setItem(menu.getSize() - 1, gps11);
    PlayerData pData = PlayerData.getPlayerData(p.getPlayer().getName());
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("player_gps_data")
              .where(new CustomEntry("pseudo", p.getName())).toString());
      if (results.count() == 1) {
        ResultSet data = results.getActualResult();
        List<String> xList = new ArrayList<>();
        if (!data.getString("x").isEmpty())
          xList.addAll(Arrays.asList(data.getString("x").split(";")));
        List<String> yList = new ArrayList<>();
        if (!data.getString("y").isEmpty())
          yList.addAll(Arrays.asList(data.getString("y").split(";")));
        List<String> zList = new ArrayList<>();
        if (!data.getString("z").isEmpty())
          zList.addAll(Arrays.asList(data.getString("z").split(";")));
        List<String> names = new ArrayList<>();
        if (!data.getString("names").isEmpty())
          names.addAll(Arrays.asList(data.getString("names").split(";")));

        int leftSlot = elementsInPage - slots;

        for (index = 0; index < xList.size(); index++) {
          if (!(xList.get(index)).isEmpty() && !(yList.get(index)).isEmpty() && !(zList.get(index)).isEmpty() && !(names.get(index)).isEmpty()) {

            if(leftSlot == 0) break;

            ItemStack gps10 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)5);
            ItemMeta gps10M = gps10.getItemMeta();
            gps10M.setDisplayName("§a" + names.get(index));
            gps10M.setLore(Arrays.asList("", "" + Double.parseDouble(xList.get(index)), "" +
                    Double.parseDouble(yList.get(index)), "" + Double.parseDouble(zList.get(index)), "§eActiver votre gps sur la destination: §a" + (String)names.get(index), "§eClic-droit pour retirer cette destination"));
            gps10.setItemMeta(gps10M);
            menu.setItem(index + slots, gps10);
            leftSlot--;


          }
        }
      }
    } catch (SQLException e1) {
      e1.printStackTrace();
    }
    p.openInventory(menu);
  }
  
  @EventHandler
  public void InventoryClick(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    Inventory inventory = e.getInventory();
    ItemStack item = e.getCurrentItem();
    if (inventory != null && inventory.getName().startsWith("§cGPS")) {
      if (item == null || item.getType() == null)
        return; 
      e.setCancelled(true);
      if (item.getType() == Material.STAINED_GLASS_PANE){
        if (item.hasItemMeta()) {
          PlayerData pData = PlayerData.getPlayerData(p.getName());
          Location loc = new Location(p.getWorld(), Double.parseDouble(item.getItemMeta().getLore().get(1)), Double.parseDouble(item.getItemMeta().getLore().get(2)), Double.parseDouble(item.getItemMeta().getLore().get(3)));
          if (item.getItemMeta().getLore().contains("§eClic-droit pour retirer cette destination")) {
            if (e.isRightClick() && item.getDurability() == 5) {
              try {
                SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("player_gps_data")
                    .where(new CustomEntry("pseudo", p.getName())).toString());
                if (results.count() == 1) {
                  ResultSet data = results.getActualResult();
                  List<String> xList = new ArrayList<>();
                  if (!data.getString("x").isEmpty())
                    xList.addAll(Arrays.asList(data.getString("x").split(";"))); 
                  List<String> yList = new ArrayList<>();
                  if (!data.getString("y").isEmpty())
                    yList.addAll(Arrays.asList(data.getString("y").split(";"))); 
                  List<String> zList = new ArrayList<>();
                  if (!data.getString("z").isEmpty())
                    zList.addAll(Arrays.asList(data.getString("z").split(";"))); 
                  List<String> names = new ArrayList<>();
                  if (!data.getString("names").isEmpty())
                    names.addAll(Arrays.asList(data.getString("names").split(";"))); 
                  xList.remove(String.valueOf(loc.getBlock().getX()));
                  yList.remove(String.valueOf(loc.getBlock().getY()));
                  zList.remove(String.valueOf(loc.getBlock().getZ()));
                  names.remove(item.getItemMeta().getDisplayName().replaceAll("§a", ""));
                  HashMap<String, Object> fields = new HashMap<>();
                  fields.put("x", StringUtils.join(xList, ';'));
                  fields.put("y", StringUtils.join(yList, ';'));
                  fields.put("z", StringUtils.join(zList, ';'));
                  fields.put("names", StringUtils.join(names, ';'));
                  Main.Database.update(RecordBuilder.build().update(fields, "player_gps_data").where(new CustomEntry("pseudo", p.getName())).toString());
                  p.sendMessage("§8[§6GPS§8] §aVous avez supprimé la destination §e" + item.getItemMeta().getDisplayName());


                }


              } catch (SQLException e1) {
                e1.printStackTrace();
              }

              p.getOpenInventory().close();
            } else {
              GPS.startGPS(p, loc.getX(), loc.getY(), loc.getZ());
              p.closeInventory();
            } 
          } else if (item.getItemMeta().getLore().contains("§eDestination automatique")) {
            GPS.startGPS(p, loc.getX(), loc.getY(), loc.getZ());
            p.closeInventory();
          } 
        } 
      } else if (item.getType() == Material.GLASS) {
        if (item.hasItemMeta())
          if (item.getItemMeta().getLore().contains("§eDésactiver le GPS s'il est en cours d'utilisation")) {
            GPS.stopGPS(p);
            p.closeInventory();
          }  
      } else if (item.getType() == Material.PAPER) {
        if(item.hasItemMeta()){
          int currentPage = Integer.parseInt(inventory.getName().substring(14).replaceAll("\\)", ""));
          if(item.getItemMeta().getDisplayName().equals("§6Page suivante"))
          {
            displayGps(currentPage+1,p);
          }
          else if(item.getItemMeta().getDisplayName().equals("§6Page précédente"))
          {
            displayGps(Math.max(currentPage-1,1),p);
          }
        }
      }
    } 
  }


}
