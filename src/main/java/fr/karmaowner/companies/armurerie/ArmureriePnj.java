package fr.karmaowner.companies.armurerie;

import fr.karmaowner.common.Main;
import fr.karmaowner.companies.CompanyArmurerie;
import fr.karmaowner.data.Data;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.utils.CustomConcurrentHashMap;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.RecordBuilder;
import fr.karmaowner.utils.TimerUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArmureriePnj implements Data {
  private Inventory inv;
  
  private CompanyArmurerie company;
  
  public static final String PNJNAME = "Fabricateur";
  
  public static final String INVENTORYNAME = "§cUsine à fabrique d'armes";
  
  public static final String MESSAGETOPRODUCE = "§4Fabriquer cette arme";
  
  public static final String MESSAGEPRODUCEDWEAPON = "§2Fabrication de l'arme terminée";
  
  private CustomConcurrentHashMap<CompanyArmurerie.XP_ARMURERIE, Long> weapons = new CustomConcurrentHashMap();
  
  public ArmureriePnj(CompanyArmurerie company) {
    this.inv = Bukkit.createInventory(null, 54, "§cUsine à fabrique d'armes");
    this.company = company;
  }
  
  public void fillInventory() {
    this.inv.clear();
    int i = 0;
    for (CompanyArmurerie.XP_ARMURERIE w : getUnlockedWeapons()) {
      Long elapsedTime = (Long)this.weapons.get(w);
      long now = System.currentTimeMillis();
      ItemStack item = null;
      if (elapsedTime != null) {
        if (now - elapsedTime.longValue() >= (w.getDelayInSecondsToUnlock() * 1000L)) {
          item = ItemUtils.getItem(w.getId(), w.getData(), 1, "§c" + w.getName(),
              Arrays.asList("§2Fabrication de l'arme terminée"));
        } else {
          item = ItemUtils.getItem(w.getId(), w.getData().byteValue(), 1, "§c" + w.getName(), 
              Arrays.asList("§6Arme en cours de fabrication...", "§4Temps Restant: " + TimerUtils.formatString((int)(w.getDelayInSecondsToUnlock() - (now - elapsedTime.longValue()) / 1000.0D))));
        } 
      } else {
        item = ItemUtils.getItem(w.getId(), w.getData().byteValue(), 1, "§c" + w.getName(), 
            Arrays.asList("§4Fabriquer cette arme", "§6Clic dessus pour la fabriquer", "§6Quantité: 1", "§eNiveau: " + w.getLevelToUnlock(), "§bCoût de fabrication: " + w
                .getPrice() + "€"));
      } 
      this.inv.setItem(i, item);
      i++;
    } 
  }
  
  public ArrayList<CompanyArmurerie.XP_ARMURERIE> getUnlockedWeapons() {
    ArrayList<CompanyArmurerie.XP_ARMURERIE> weapons = new ArrayList<>();
    for (CompanyArmurerie.XP_ARMURERIE w : CompanyArmurerie.XP_ARMURERIE.values()) {
      if (this.company != null && this.company.data != null && this.company.data.getLevelReached() >= w.getLevelToUnlock()) {
        weapons.add(w);
      }
    } 
    return weapons;
  }
  
  public void loadData() {
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("armurerie_data", new CustomEntry("CompanyName", this.company.data
              .getCompanyName())).toString());
      if (results.count() > 0) {
        for (ResultSet result : results) {
          String item = result.getString("weapon");
          long elapsed = result.getLong("elapsedTime");
          String[] itemData = item.split(":");
          int id = Integer.parseInt(itemData[0]);
          byte data = Byte.parseByte(itemData[1]);
          CompanyArmurerie.XP_ARMURERIE w = (CompanyArmurerie.XP_ARMURERIE)this.company.toXP(id, data);
          this.weapons.put(w, elapsed);
        } 
        Main.Database.update(RecordBuilder.build().delete("armurerie_data")
            .where(new CustomEntry("CompanyName", this.company.data.getCompanyName())).toString());
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void saveData() {
    for (Map.Entry<CompanyArmurerie.XP_ARMURERIE, Long> w : (Iterable<Map.Entry<CompanyArmurerie.XP_ARMURERIE, Long>>)this.weapons.entrySet()) {
      HashMap<String, Object> fields = new HashMap<>();
      fields.put("CompanyName", this.company.data.getCompanyName());
      fields.put("weapon", ((CompanyArmurerie.XP_ARMURERIE)w.getKey()).getId() + ":" + ((CompanyArmurerie.XP_ARMURERIE)w.getKey()).getData());
      fields.put("elapsedTime", w.getValue());
      try {
        Main.Database.update(RecordBuilder.build().insert(fields, "armurerie_data").toString());
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public Inventory getInventory() {
    return this.inv;
  }
  
  public CustomConcurrentHashMap<CompanyArmurerie.XP_ARMURERIE, Long> getWeapons() {
    return this.weapons;
  }
}
