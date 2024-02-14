package fr.karmaowner.companies;

import fr.karmaowner.common.Main;
import fr.karmaowner.companies.armurerie.ArmureriePnj;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.events.JobsEvents;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.RecordBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Material;

public class CompanyArmurerie extends CompanyAchievements {
  private ArmureriePnj armurerie;
  
  private int TotalProduceWeapon;
  
  public enum XP_ARMURERIE implements Company.XP {
    ;

    private byte data;
    
    private int id;
    
    private int level;
    
    private Material s;
    
    private String name;
    
    private int delayInSecondsToUnlock;
    
    private double xp;
    
    private double price;
    
    XP_ARMURERIE(Material s, byte data, String name, double xp, int level, int delayInSecondsToUnlock, double price) {
      this(s, data, name, delayInSecondsToUnlock, price);
      this.xp = xp;
      this.level = level;
    }
    
    XP_ARMURERIE(Material s, byte data, String name, int delayInSecondsToUnlock, double price) {
      this.data = data;
      this.s = s;
      this.name = name;
      this.delayInSecondsToUnlock = delayInSecondsToUnlock;
      this.price = price;
    }
    
    XP_ARMURERIE(int id, byte data, String name, int delayInSecondsToUnlock, double price) {
      this.data = data;
      this.id = id;
      this.name = name;
      this.price = price;
      this.delayInSecondsToUnlock = delayInSecondsToUnlock;
    }
    
    XP_ARMURERIE(int id, byte data, double xp, int level, String name, int delayInSecondsToUnlock, double price) {
      this(id,data,name,delayInSecondsToUnlock,price);
      this.xp = xp;
      this.level = level;
    }
    
    public String getName() {
      return this.name;
    }
    
    public double getXp() {
      return this.xp;
    }
    
    public double getPrice() {
      return this.price;
    }
    
    public int getLevelToUnlock() {
      return this.level;
    }
    
    public Byte getData() {
      return this.data;
    }
    
    public int getId() {
      return this.id;
    }
    
    public Material getType() {
      return this.s;
    }
    
    public int getDelayInSecondsToUnlock() {
      return this.delayInSecondsToUnlock;
    }
  }

  public CompanyArmurerie(CompanyData data) {
    super(data, "Armes");
    this.armurerie = new ArmureriePnj(this);
    this.armurerie.loadData();
  }
  
  public Company.XP toXP(Material s, Byte data) {
    if (s == null)
      return null; 
    if (data == null)
      return null; 
    for (XP_ARMURERIE x : XP_ARMURERIE.values()) {
      if (s.equals(x.getType()) && data.equals(x.getData()))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(int id, Byte data) {
    if (id == 0)
      return null; 
    if (data == null)
      return null; 
    for (XP_ARMURERIE x : XP_ARMURERIE.values()) {
      if (id == x.getId() && data.equals(x.getData()))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_ARMURERIE x : XP_ARMURERIE.values()) {
      if (x.getType() == s)
        return x; 
    } 
    return null;
  }
  
  public ArmureriePnj getArmurerie() {
    return this.armurerie;
  }
  
  public void loadData() {
    super.loadData();
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("company_" + this.data
            .getCategoryName().toLowerCase(), new CustomEntry("CompanyName", this.data
              .getCompanyName())).toString());
      if (results.count() == 1 && !this.data.isCreation) {
        ResultSet rs = results.getActualResult();
        this.TotalProduceWeapon = rs.getInt("ProduceWeaponDay");
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void saveData() {
    super.saveData();
    this.armurerie.saveData();
    try {
      Main.Database.update(RecordBuilder.build().update(new CustomEntry("ProduceWeaponDay", this.TotalProduceWeapon), "company_" + this.data
            .getCategoryName().toLowerCase())
          .where(new CustomEntry("CompanyName", this.data.getCompanyName())).toString());
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public int getTotalProduceWeapon() {
    return this.TotalProduceWeapon;
  }
  
  public void setTotalProduceWeapon(int totalProduceWeapon) {
    this.TotalProduceWeapon = totalProduceWeapon;
  }
}
