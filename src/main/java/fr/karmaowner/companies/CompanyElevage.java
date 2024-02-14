package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import fr.karmaowner.data.EggsData;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class CompanyElevage extends CompanyAchievements {
  private EggsData eggs;
  
  public enum XP_ELEVAGE implements Company.XP {
    PIG(383, (byte)0, "Oeuf de cochon non éclos", 1800, (byte)90, 2400.0D, 100, EntityType.PIG),
    SHEEP(383, (byte)0, "Oeuf de mouton non éclos", 1800, (byte)91, 1600.0D, 50, EntityType.SHEEP),
    COW(383, (byte)0, "Oeuf de vache non éclos", 1800, (byte)92, 4800.0D, 150, EntityType.COW),
    CHICKEN(383, (byte)0, "Oeuf de poule non éclos", 1800, (byte)93, 800.0D, 0, EntityType.CHICKEN);
    
    private byte data;
    
    private byte typeid;
    
    private int id;
    
    private int level;
    
    private int time;
    
    private Material s;
    
    private String name;
    
    private double xp;
    
    private XP_ELEVAGE associated;
    
    private EntityType type;
    
    XP_ELEVAGE(int id, byte data, String name, int timeBeforeHatch, byte typeId, double xp, int level, EntityType type) {
      this.data = data;
      this.id = id;
      this.name = name;
      this.xp = xp;
      this.level = level;
      this.time = timeBeforeHatch;
      this.typeid = typeId;
      this.type = type;
    }
    
    public EntityType getEntity() {
      return this.type;
    }
    
    public String getName() {
      return this.name;
    }
    
    public double getXp() {
      return this.xp;
    }
    
    public int getTime() {
      return this.time;
    }
    
    public byte getTypeId() {
      return this.typeid;
    }
    
    public int getLevelToUnlock() {
      return this.level;
    }
    
    public static XP_ELEVAGE getEnum(String name) {
      for (XP_ELEVAGE x : values()) {
        if (x.getName().equals(name))
          return x; 
      } 
      return null;
    }
    
    public static XP_ELEVAGE getEnumByEnumName(String name) {
      for (XP_ELEVAGE x : values()) {
        if (x.toString().equalsIgnoreCase(name))
          return x; 
      } 
      return null;
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
  }
  
  public CompanyElevage(CompanyData data) {
    super(data, "Animaux");
    this.eggs = new EggsData(data);
  }
  
  public EggsData getEggsData() {
    return this.eggs;
  }
  
  public Company.XP toXP(Material s, Byte data) {
    if (s == null)
      return null; 
    if (data == null)
      return null; 
    for (XP_ELEVAGE x : XP_ELEVAGE.values()) {
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
    for (XP_ELEVAGE x : XP_ELEVAGE.values()) {
      if (id == x.getId() && data.equals(x.getData()))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_ELEVAGE x : XP_ELEVAGE.values()) {
      if (x.getType() == s)
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(EntityType type) {
    for (XP_ELEVAGE x : XP_ELEVAGE.values()) {
      if (x.getEntity() == type)
        return x; 
    } 
    return null;
  }
  
  public void setCompanyAchievements() {
    for (Achievements a : Achievements.valuesBySameClass(getClass())) {
      if (!Achievements.hasAchievement(a, this.data)) {
        if (a.equals(Achievements.FABRICATION10)) {
          if (this.compteur >= 10)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        if (a.equals(Achievements.FABRICATION100)) {
          if (this.compteur >= 100)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        setCompanyAchievements(a);
      } 
    } 
  }
  
  public void saveData() {
    super.saveData();
    this.eggs.saveData();
  }
}
