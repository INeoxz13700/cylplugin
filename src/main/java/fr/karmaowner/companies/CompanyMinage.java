package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CompanyMinage extends CompanyAchievements {
  public enum XP_MINAGE implements Company.XP {
    ROCHE(1, (byte) 0, 1, 50, 0, "Roche"),
    MINERAI_CHARBON(16, (byte) 0, 16, 50, 10, "Minerai charbon"),
    MINERAI_REDSTONE(73, (byte) 0, 73, 50, 15, "Minerai redstone"),
    MINERAI_REDSTONE2(74, (byte) 0, 73, 50, 15, "Minerai redstone"),
    MINERAI_LAPIS(21, (byte) 0, 21, 50, 15, "Minerai lapis"),
    MINERAI_FER(15, (byte) 0, 15, 50, 20, "Minerai fer"),
    MINERAI_D_OR(14, (byte) 0, 14, 75, 30, "Minerai d'or"),
    MINERAI_DIAMANT(56, (byte) 0, 56,75, 40, "Minerai diamant"),
    MINERAI_D_EMERAUDE(129, (byte) 0,129, 75, 50, "Minerai d'emeraude"),
    SULFUR_ORE(738, (byte) 0,4547, 100, 60, "Sulfur ore"),
    PURPLE_QUARTZ_ORE(760, (byte) 0,4709, 100, 70, "Purple quartz ore"),
    OBSIDIAN_ORE(692, (byte) 0,4691, 100, 80, "Obsidian ore"),
    AMETHYST_ORE(751, (byte) 0, 4551, 100, 90, "Amethyst ore"),
    JADE_ORE(667, (byte) 0,4132, 100, 100, "Jade ore"),
    RUBY_ORE(802, (byte) 0,4517, 125, 110, "Ruby ore"),
    OPAL_ORE(911, (byte) 0,4521, 125, 120, "Opal ore"),
    SILVER_ORE(767, (byte) 0, 4554, 125, 130, "Silver ore"),
    TOPAR_ORE(701, (byte) 0,4251, 150, 140, "Topar ore"),
    COPPER_ORE(690, (byte) 0,4575, 150, 150, "Copper ore"),
    RED_JADE_ORE(785, (byte) 0, 4742, 175, 160, "Red jade ore"),
    SAPPHIRE_ORE(659, (byte) 0,5464, 175, 170, "Sapphire ore"),
    ERYTHRITE_ORE(765, (byte) 0,4625, 200, 180, "Erythrite ore"),
    PLATINIUM_ORE(1000, (byte) 0,4561, 200, 190, "Platinium ore"),
    EPIDOTE_ORE(740, (byte) 0,4765, 250, 200, "Epidote ore"),
    FLUORITE_ORE(666, (byte) 0, 4484, 300, 210, "Fluorite ore"),
    MAGNETITE_ORE(655, (byte) 0,4481, 350, 220, "Magnetite ore"),
    MALACHITE_ORE(999, (byte) 0,4522, 400, 230, "Malachite ore"),
    AMAZONITE_ORE(702, (byte) 0, 4694, 500, 240, "Amazonite ore"),
    BLACK_DIAMOND_ORE(762, (byte) 0, 4711, 500, 250, "Black diamond ore"),
    DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, (byte)0, "Pioche en diamant"),
    GOLD_PICKAXE(Material.GOLD_PICKAXE, (byte)0, "Pioche en or"),
    IRON_PICKAXE(Material.IRON_PICKAXE, (byte)0, "Pioche en fer"),
    STONE_PICKAXE(Material.STONE_PICKAXE, (byte)0, "Pioche en pierre");
    
    private byte data;
    
    private int id;
    
    private Material s;
    
    private String name;

    private int xp;

    private int level;

    private int itemBlockId;
    
    XP_MINAGE(Material s, byte data, String name) {
      this.data = data;
      this.s = s;
      this.name = name;
    }
    
    XP_MINAGE(int id, byte data, int itemBlockId, int xp, int level, String name) {
      this.data = data;
      this.id = id;
      this.name = name;
      this.xp = xp;
      this.itemBlockId = itemBlockId;
      this.level = level;
    }
    
    public double getXp() {
      return xp;
    }
    
    public String getName() {
      return this.name;
    }

    public int getItemBlockId()
    {
      return  itemBlockId;
    }
    
    public int getLevelToUnlock() {
      switch (this) {
        case STONE_PICKAXE:
          return 0;
        case IRON_PICKAXE:
          return 50;
        case GOLD_PICKAXE:
          return 100;
        case DIAMOND_PICKAXE:
          return 150;
      }

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
  
  public CompanyMinage(CompanyData data) {
    super(data, "Minerais");

  }

  public static boolean isCompanyBlock(Block block) {
    for (XP_MINAGE x : XP_MINAGE.values()) {
      if (x.getType() == block.getType() || (block.getTypeId() == x.getId() && block.getData() == x.getData()))
        return true;
    }
    return false;
  }

  public Company.XP toXP(Material s, Byte data) {
    for (XP_MINAGE x : XP_MINAGE.values()) {
      if (x.getType() == s && x.getType() != null && x.getData().equals(data))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(int id, Byte data) {
    for (XP_MINAGE x : XP_MINAGE.values()) {
      if (x.getId() == id && x.getId() != -1 && x.getData().equals(data))
        return x; 
    } 
    return null;
  }
  
  public boolean isPickaxe(Material item) {
    return item == Material.DIAMOND_PICKAXE || item == Material.WOOD_PICKAXE || item == Material.GOLD_PICKAXE || item == Material.IRON_PICKAXE || item == Material.STONE_PICKAXE;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_MINAGE x : XP_MINAGE.values()) {
      if (x.getType() == s)
        return x; 
    } 
    return null;
  }
  
  public void setCompanyAchievements() {
    for (Achievements a : Achievements.valuesBySameClass(getClass())) {
      if (!Achievements.hasAchievement(a, this.data)) {
        if (a.equals(Achievements.MINAGE10)) {
          if (this.compteur >= 10)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        if (a.equals(Achievements.MINAGE100)) {
          if (this.compteur >= 100)
            Achievements.setAchievement(a, this.data);
          continue;
        } 
        setCompanyAchievements(a);
      } 
    } 
  }
}
