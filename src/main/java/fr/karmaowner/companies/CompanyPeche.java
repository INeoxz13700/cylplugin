package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.Material;

public class CompanyPeche extends CompanyAchievements {
  public enum XP_PECHE implements Company.XP {

    Clownfish(6086, (byte) 0, "Poisson Clown", 0, 150),
    Pufferfish(6087, (byte) 0, "Poisson Ballon", 0, 150),
    RawFish(6082, (byte) 0, "Poisson Brut", 0, 150),
    RawSalmon(6084, (byte) 0, "Saumon Brut", 0, 150),
    RawAnchovy(6046, (byte) 0, "Anchois Brut", 0, 150),
    RawBass(6047, (byte) 0, "Bar Brut", 0, 150),
    RawCarp(6048, (byte) 0, "Carpe Brute", 0, 150),
    RawCatfish(6049, (byte) 0, "Silure Brut", 0, 150),
    RawCharr(6050, (byte) 0, "Omble Chevalier Brut", 0, 150),
    RawClam(6051, (byte) 0, "Palourde Brut", 0, 150),
    RawCrab(6052, (byte) 0, "Crabe Brut", 0, 150),
    RawCrayfish(6053, (byte) 0, "Écrevisse Brut", 0, 150),
    RawEel(6054, (byte) 0, "Anguille Brut", 0, 150),
    RawFrog(6055, (byte) 0, "Grenouille Brut", 0, 150),
    RawGrouper(6056, (byte) 0, "Mérou Brut", 0, 150),
    RawHerring(6057, (byte) 0, "Hareng Brut", 0, 150),
    RawJellyfish(6058, (byte) 0, "Méduse Brut", 0, 150),
    RawMudfish(6059, (byte) 0, "Poisson Boue Brut", 0, 150),
    RawOctopus(6060, (byte) 0, "Poulpe Brut", 0, 150),
    RawPerch(6061, (byte) 0, "Perche Brut", 0, 150),
    RawScallop(6062, (byte) 0, "Coquille Saint-Jacques Brut", 0, 150),
    RawShrimp(6063, (byte) 0, "Crevette Brut", 0, 150),
    RawSnail(6064, (byte) 0, "Escargot Brut", 0, 150),
    RawSnapper(6065, (byte) 0, "Vivaneau Brut", 0, 150),
    RawTilapia(6066, (byte) 0, "Tilapia Brut", 0, 150),
    RawTrout(6067, (byte) 0, "Truite Brut", 0, 150),
    RawTuna(6068, (byte) 0, "Thon Brut", 0, 150),
    RawTurtle(6069, (byte) 0, "Tortue Brut", 0, 150),
    RawWalleye(6070, (byte) 0, "Sandre Brut", 0, 150),
    GreenHeartFish(6071, (byte) 0, "Poisson Cœur Vert", 0, 150),
    RawSardine(6072, (byte) 0, "Sardine Brut", 0, 150),
    RawMussel(6073, (byte) 0, "Moule Brut", 0, 150),
    RawToFish(6074, (byte) 0, "Poisson À Frire Brut", 0, 150),
    RawOyster(6075, (byte) 0, "Huître Brut", 0, 150);


    private byte data;
    
    private int id;
    
    private Material s;
    
    private String name;
    
    private int level;
    
    private double xp;
    
    XP_PECHE(Material s, byte data, String name, int level, double xp) {
      this.data = data;
      this.s = s;
      this.name = name;
      this.level = level;
      this.xp = xp;
    }
    
    XP_PECHE(int id, byte data, String name, int level, double xp) {
      this.data = data;
      this.id = id;
      this.name = name;
      this.level = level;
      this.xp = xp;
    }
    
    public double getXp() {
      return this.xp;
    }
    
    public String getName() {
      return this.name;
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
  }
  
  public CompanyPeche(CompanyData data) {
    super(data, "Poissons");
  }
  
  public Company.XP toXP(Material s, Byte data) {
    for (XP_PECHE x : XP_PECHE.values()) {
      if (x.getType() == s && x.getType() != null && x.getData().equals(data))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(int id, Byte data) {
    for (XP_PECHE x : XP_PECHE.values()) {
      if (x.getId() == id && x.getId() != -1 && x.getData().equals(data))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_PECHE x : XP_PECHE.values()) {
      if (x.getType() == s)
        return x; 
    } 
    return null;
  }
  
  public void setCompanyAchievements() {
    for (Achievements a : Achievements.valuesBySameClass(getClass())) {
      if (!Achievements.hasAchievement(a, this.data)) {
        if (a.equals(Achievements.PECHE10)) {
          if (this.compteur >= 10)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        if (a.equals(Achievements.PECHE100)) {
          if (this.compteur >= 100)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        setCompanyAchievements(a);
      } 
    } 
  }
}
