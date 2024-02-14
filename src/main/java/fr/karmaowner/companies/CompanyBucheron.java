package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CompanyBucheron extends CompanyAchievements {
  public enum XP_BUCHERON implements Company.XP {
    WOOD01(Material.LOG, (byte)0, "Bois de chêne"),

    WOOD02(Material.LOG, (byte)2, "Bois de bouleau"),

    WOOD03(Material.LOG, (byte)1, "Bois de sapin"),

    WOOD04(Material.LOG, (byte)3, "Bois d'acajou"),
    WOOD05(162, (byte)1, "Bois de chêne noir"),
    WOOD06(162, (byte)0, "Bois d'acacia"),
    STONE_AXE(Material.STONE_AXE, (byte)0, "Hache en pierre"),
    WOOD_AXE(Material.WOOD_AXE, (byte)0, "Hache en bois"),
    IRON_AXE(Material.IRON_AXE, (byte)0, "Hache en fer");

    private byte data;
    
    private Material s;
    
    private int id;
    
    private String name;
    
    XP_BUCHERON(Material s, byte data, String name) {
      this.data = data;
      this.s = s;
      this.name = name;
    }
    
    XP_BUCHERON(int id, byte data, String name) {
      this.data = data;
      this.id = id;
      this.name = name;
    }
    
    public String getName() {
      return this.name;
    }
    
    public double getXp() {
      if (this == WOOD01)
        return 50.0D; 
      if (this == WOOD02)
        return 100.0D; 
      if (this == WOOD03)
        return 200.0D;
      if (this == WOOD04)
        return 350.0;
      if (this == WOOD05)
        return 500.0;
      if (this == WOOD06)
        return 750.0;
      return 0.0D;
    }
    
    public Byte getData() {
      return this.data;
    }
    
    public Material getType() {
      return this.s;
    }
    
    public int getLevelToUnlock() {
      switch (this) {
        case WOOD02:
          return 25;
        case WOOD03:
        case STONE_AXE:
        return 50;
        case WOOD04:
        case IRON_AXE:
        return 100;
        case WOOD05:
          return 150;
        case WOOD06:
          return 200;
      } 
      return 0;
    }
    
    public int getId() {
      return this.id;
    }
  }

  public static boolean isCompanyBlock(Block block)
  {
    for (CompanyBucheron.XP_BUCHERON x : CompanyBucheron.XP_BUCHERON.values()) {
      if (x.getType() == block.getType() || block.getTypeId() == x.getId())
        return true;
    }
    return false;
  }
  
  public CompanyBucheron(CompanyData data) {
    super(data, "Bois");
  }
  
  public Company.XP toXP(Material s, Byte data) {
    for (XP_BUCHERON x : XP_BUCHERON.values()) {
      if (x.getType() == s && x.getType() != null && x.getData().equals(data))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(int id, Byte data) {
    for (XP_BUCHERON x : XP_BUCHERON.values()) {
      if (x.getId() == id && x.getId() != -1 && x.getData().equals(data))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_BUCHERON x : XP_BUCHERON.values()) {
      if (x.getType() == s)
        return x; 
    } 
    return null;
  }
  
  public boolean isAxe(Material item) {
    return (item == Material.DIAMOND_AXE || item == Material.GOLD_AXE || item == Material.STONE_AXE || item == Material.IRON_AXE || item == Material.WOOD_AXE);
  }
  
  public void setCompanyAchievements() {
    for (Achievements a : Achievements.valuesBySameClass(getClass())) {
      if (!Achievements.hasAchievement(a, this.data)) {
        if (a.equals(Achievements.WOOD10)) {
          if (this.compteur >= 10)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        if (a.equals(Achievements.WOOD100)) {
          if (this.compteur >= 100)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        setCompanyAchievements(a);
      } 
    } 
  }
}
