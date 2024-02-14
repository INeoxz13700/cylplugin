package fr.karmaowner.tresorerie;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.Data;
import java.util.ArrayList;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Tresorerie implements Data {
  private FileConfiguration fileConfig;
  
  private String tresorerieName;
  
  private double money = 500000.0D;
  
  public static ArrayList<Tresorerie> TresorerieList = new ArrayList<>();
  
  public abstract boolean hasPrivilege(Player paramPlayer);
  
  public abstract String getNeededPrivilege();
  
  public Tresorerie(String tresorerieName) {
    this.fileConfig = Main.INSTANCE.getConfig();
    setTresorerie(this);
    this.tresorerieName = tresorerieName;
  }
  
  public void loadData() {
    if (this.fileConfig != null && 
      this.fileConfig.get("tresorerie." + this.tresorerieName) != null)
      this.money = this.fileConfig.getDouble("tresorerie." + this.tresorerieName); 
  }
  
  public void saveData() {
    if (this.fileConfig != null)
      this.fileConfig.set("tresorerie." + this.tresorerieName, this.money);
  }
  
  public double getMoney() {
    return this.money;
  }
  
  public String getTresorerieName() {
    return this.tresorerieName;
  }
  
  public boolean hasMoney(double money) {
    return (this.money >= money);
  }
  
  public void setMoney(double money) {
    this.money = money;
  }
  
  public void addMoney(double money) {
    this.money += money;
  }
  
  public void substMoney(double money) {
    this.money -= money;
  }
  
  public static void setTresorerie(Tresorerie t) {
    TresorerieList.add(t);
  }
  
  public static Tresorerie getTresorerie(String tresorerieName) {
    for (Tresorerie t : TresorerieList) {
      if (t.getTresorerieName().equalsIgnoreCase(tresorerieName))
        return t; 
    } 
    return null;
  }
}
