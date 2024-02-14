package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CompanyAgriculture extends CompanyAchievements {
  public enum XP_AGRICULTURE implements Company.XP {

    CROPS(59, (byte)7, "Blé", 50,0,false),

    CAROT(141, (byte)3, "Carrote",50,5, true),
    POTATO(142, (byte)3, "Patate",50,15, true),
    RYE(778, (byte)3, "Seigle",50,25, true),
    BARLEY(776, (byte)3, "Orge",50,30, true),
    ONION(786, (byte)3, "Oignon",50,45, true),
    RUTABAGA(790, (byte)3, "Rutabaga",100,50, true),
    BROCCOLI(801, (byte)3, "Brocoli",100,60, true),
    CAULIFLOWER(995, (byte)3, "Chou-Fleur",120,70, true),
    LEEK(803, (byte)3, "Poireau",125,80, true),

    LETTUCE(804, (byte)3, "Laitue",150,90, true),
    BELLPEPPER(813, (byte)3, "Poivron",150,100, true),
    TOMATO(823, (byte)3, "Tomate",200,110, true),
    PINEAPPLE(820, (byte)3, "Ananas",200,120, true),
    QUINOA(847, (byte)3, "Quinoa", 200,130,true),
    EGGPLANT(815, (byte)3, "Aubergine",250,140, true),
    JUNIPERBERRY(851, (byte)3, "Génévier",250,150, true),
    BEET(1001, (byte)3, "Radis",300,160, true),
    AGAVE(831, (byte)3, "Agave",350,170, true),
    ARTICHOKE(806, (byte)3, "Artichaut",350,180, true),
    CACTUS_FRUIT(774, (byte)3, "Fruit du Cactus",400,190, true),
    CORN(779, (byte)3, "Maïs", 450,200,true),
    PARSNIP(787, (byte)3, "Panais",450,225, true),
    TOMATILLO(850, (byte)3, "Petites tomates",500,250, true),

    WOODHOE(Material.WOOD_HOE, (byte)0, "Houe en bois", 0, 0, null, false),
    STONEHOE(Material.STONE_HOE, (byte)0, "Houe en pierre", 0, 0, null, false),
    IRONHOE(Material.IRON_HOE, (byte)0, "Houe en fer", 0, 0, null, false),
    GOLDHOE(Material.GOLD_HOE, (byte)0, "Houe en or", 0, 0, null, false),
    DIAMONDHOE(Material.DIAMOND_HOE, (byte)0, "Houe en diamant", 0, 0, null, false);

    private byte data;
    
    private int id;
    
    private int level;
    
    private Material s;
    
    private String name;
    
    private double xp;
    
    private XP_AGRICULTURE associe;
    
    private boolean isCrop;
    
    XP_AGRICULTURE(Material s, byte data, String name, int xp, int level, XP_AGRICULTURE associe, boolean isCrop) {
      this(s, data, name,xp,level,isCrop);
      this.associe = associe;
      this.xp = xp;
      this.level = level;
    }
    
    XP_AGRICULTURE(Material s, byte data, String name, int xp, int level, boolean isCrop) {
      this.data = data;
      this.s = s;
      this.name = name;
      this.isCrop = isCrop;
      this.xp = xp;
      this.level = level;
    }
    
    XP_AGRICULTURE(int id, byte data, String name,int xp, int level, boolean isCrop) {
      this.data = data;
      this.id = id;
      this.name = name;
      this.isCrop = isCrop;
      this.xp = xp;
      this.level = level;
    }
    
    XP_AGRICULTURE(int id, byte data, String name, int xp, int level, XP_AGRICULTURE associe, boolean isCrop) {
      this(id, data, name,xp,level, isCrop);
      this.xp = xp;
      this.level = level;
      this.associe = associe;
    }
    
    public String getName() {
      return this.name;
    }
    
    public XP_AGRICULTURE getAssociation() {
      return this.associe;
    }
    
    public boolean isCrop() {
      return this.isCrop;
    }
    
    public double getXp() {
      return this.xp;
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
  
  public CompanyAgriculture(CompanyData data) {
    super(data, "Plantations");
  }
  
  public boolean isHoe(Material m) {
    return (m == Material.GOLD_HOE || m == Material.WOOD_HOE || m == Material.DIAMOND_HOE || m == Material.IRON_HOE || m == Material.STONE_HOE);
  }


  public static boolean isCompanyBlock(Block block) {
    for (CompanyAgriculture.XP_AGRICULTURE x : CompanyAgriculture.XP_AGRICULTURE.values()) {
      if (x.getType() == block.getType()  || (block.getTypeId() == x.getId()))
        return true;
    }
    return false;
  }

  public Company.XP toXP(Material s, Byte data) {
    if (s == null)
    {
      return null;

    }
    if (data == null)
    {
      return null;
    }

    for (XP_AGRICULTURE x : XP_AGRICULTURE.values()) {
      if (s.equals(x.getType()) && data.equals(x.getData()))
        return x; 
    } 
    return null;
  }



  @Override
  public XP toXP(int paramInt, Byte paramByte) {
    return null;
  }

  public Company.XP toXP(int id) {
    if (id == 0)
      return null; 
    if (data == null)
      return null; 
    for (XP_AGRICULTURE x : XP_AGRICULTURE.values()) {
      if (id == x.getId())
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_AGRICULTURE x : XP_AGRICULTURE.values()) {
      if (x.getType() == s)
        return x; 
    } 
    return null;
  }
  
  public void setCompanyAchievements() {
    for (Achievements a : Achievements.valuesBySameClass(getClass())) {
      if (!Achievements.hasAchievement(a, (DataAchievements)this.data)) {
        if (a.equals(Achievements.PLANTATION10)) {
          if (this.compteur >= 10)
            Achievements.setAchievement(a, (DataAchievements)this.data); 
          continue;
        } 
        if (a.equals(Achievements.PLANTATION100)) {
          if (this.compteur >= 100)
            Achievements.setAchievement(a, (DataAchievements)this.data); 
          continue;
        } 
        setCompanyAchievements(a);
      } 
    } 
  }
}
