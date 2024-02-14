package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class CompanyForgeron extends CompanyAchievements {
  public enum XP_FORGERON implements Company.XP {
    COAL(Material.COAL, Material.COAL_ORE, (byte)0, (byte)0,50,0, "Charbon"),
    IRON(Material.IRON_INGOT, Material.IRON_ORE, (byte)0, (byte)0, 75, 25, "Fer"),

    GOLD(Material.GOLD_INGOT, Material.GOLD_ORE, (byte)0, (byte)0, 250,150, "Or"),
    DIAMOND(264, 56, (byte)0, (byte)0,400,175, "Diamant"),

    EMERALD(Material.EMERALD, Material.EMERALD_ORE, (byte)0, (byte)0,300,200, "Emeraude"),
    LAPIS(Material.INK_SACK, Material.LAPIS_ORE, (byte)0, (byte)4,200,100, "Lapis"),
    REDSTONE(Material.REDSTONE, Material.REDSTONE_ORE, (byte)0, (byte)0, 150,85,"Redstone"),

    GLOWSTONE(Material.GLOWSTONE_DUST, Material.GLOWSTONE, (byte)4, (byte)0,125,65, "Glowstone"),

    QUARTZ(Material.QUARTZ, Material.QUARTZ_ORE, (byte)0, (byte)0,100,50, "Quartz"),

    VERRE(Material.GLASS, Material.SAND, (byte)0, (byte)0, 75,15,"Verre");
    
    private byte data;
    
    private int id;
    
    private Material s;
    
    private Material ore;
    
    private int idOre;
    
    private String name;
    
    private byte dataOre;

    private  int xp;

    private int level;
    
    XP_FORGERON(Material s, Material ore, byte dataOre, byte data, int xp, int level, String name) {
      this.data = data;
      this.s = s;
      this.ore = ore;
      this.dataOre = dataOre;
      this.name = name;
      this.xp = xp;
      this.level = level;
    }
    
    XP_FORGERON(int id, int idOre, byte dataOre, byte data, int xp, int level, String name) {
      this.data = data;
      this.id = id;
      this.dataOre = dataOre;
      this.idOre = idOre;
      this.name = name;
      this.xp = xp;
      this.level = level;
    }
    
    public byte getDataOre() {
      return this.dataOre;
    }
    
    public String getName() {
      return this.name;
    }
    
    public int getIdOre() {
      return this.idOre;
    }
    
    public Material getMaterialOre() {
      return this.ore;
    }
    
    public double getXp() {
      return xp;
    }
    
    public int getLevelToUnlock() {
      return level;
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


  public CompanyForgeron(CompanyData data) {
    super(data, "Transformations");
  }


  public static boolean isCompanyBlock(ItemStack block) {
    for (CompanyForgeron.XP_FORGERON x : CompanyForgeron.XP_FORGERON.values()) {
      if ((block.getType().equals(x.getMaterialOre()) && block.getData().getData() == x.getDataOre()) || (block.equals(x.getType()) && block.getData().getData() == x.getData()) || (block.getTypeId() == x.getIdOre() && block.getData().getData() == x.getDataOre())  || (block.getTypeId() == x.getId() && block.getData().getData() == x.getData()))
        return true;
    }
    return false;
  }

  public Company.XP toXP(Material s, Byte data) {
    if (s == null)
      return null; 
    if (data == null)
      return null; 
    for (XP_FORGERON x : XP_FORGERON.values()) {
      if ((s.equals(x.getMaterialOre()) && data.equals(x.getDataOre())) || (s.equals(x.getType()) && data.equals(x.getData())))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(int id, Byte data) {
    if (id == 0)
      return null; 
    if (data == null)
      return null; 
    for (XP_FORGERON x : XP_FORGERON.values()) {
      if ((id == x.getIdOre() && data.equals(x.getDataOre()))  || (id == x.getId() && data.equals(x.getData())))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_FORGERON x : XP_FORGERON.values()) {
      if (x.getType() == s)
        return x; 
    } 
    return null;
  }
  
  public void setCompanyAchievements() {
    for (Achievements a : Achievements.valuesBySameClass(getClass())) {
      if (!Achievements.hasAchievement(a, this.data)) {
        if (a.equals(Achievements.TRANSFORMATION10)) {
          if (this.compteur >= 10)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        if (a.equals(Achievements.TRANSFORMATION100)) {
          if (this.compteur >= 100)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        setCompanyAchievements(a);
      } 
    } 
  }
}
