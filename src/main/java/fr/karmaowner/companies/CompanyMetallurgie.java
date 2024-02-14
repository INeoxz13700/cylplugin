package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.Material;

public class CompanyMetallurgie extends CompanyAchievements {
  public enum XP_METALLURGIE implements Company.XP {
    SEAU(325, 0, "Seau", 50, 0),
    CEILING_FAN(4394, 0, "Ceiling Fan", 150, 95),
    MODERN_LIGHT(4426, 0, "Modern light", 200, 100),
    COOKIE_JAR(4360, 0, "Cookie jar", 200, 105),
    BLENDER(4658, 0, "Blender", 200, 110),
    MIRROR(4370, 0, "Mirror", 200, 115),
    SHOWER_HEAD(4512, 0, "Shower Head", 200, 120),
    TV(4453, 0, "TV", 200, 125),
    SPATULE(4468, 0, "Spatule", 200, 130),
    TV_REMOTE(4660, 0, "TV Remote", 200, 135),
    CROW_BAR(4473, 0, "Crow bar", 200, 140),
    COOLING_PACK(4450, 0, "Cooling pack", 250, 145),
    CUP(4460, 0, "Cup", 250, 150),
    KNIFE(4459, 0, "Knife", 250, 155),
    BLOC_DE_FER(42, 0, "Bloc de fer", 75, 50),
    BLOC_D_OR(41, 0, "Bloc d'or", 75, 55),
    BLOCK_DE_DIAMANT(57, 0, "Block de diamant", 100, 60),
    HOUE_EN_PIERRE(291, 0, "Houe en pierre", 75, 25),
    PELLE_EN_PIERRE(273, 0, "Pelle en pierre", 75, 30),
    HACHE_EN_PIERRE(275, 0, "Hache en pierre", 75, 35),
    PIOCHE_EN_PIERRE(274, 0, "Pioche en pierre", 75, 40),
    PLAQUE_EN_PIERRE(70, 0, "Plaque en pierre", 75, 45),
    BOUTON_EN_PIERRE(77, 0, "Bouton en pierre", 50, 15),
    ESCALIER_EN_PIERRE(67, 0, "Escalier en pierre", 50, 10),
    DALLE_EN_PIERRE(44, 0, "Dalle en pierre", 50, 5),
    BLOCK_DE_QUARTZ(155, 0, "Block de quartz", 100, 75),
    ESCALIER_EN_QUARTZ(156, 0, "Escalier en quartz", 150, 80),
    DALLE_EN_QUARTZ(44, 7, "Dalle en quartz", 150, 85),
    TRAPPE_EN_FER(167, 0, "Trappe en fer", 100, 60),
    PORTE_EN_FER(330, 0, "Porte en fer", 100, 75),
    BARREAUX_EN_FER(101, 0, "Barreaux en fer", 150, 80),
    OVEN(4315, 0, "Oven", 250, 160),
    FRIDGE(4284, 0, "Fridge", 500, 250),
    TAP(4327, 0, "Tap", 500, 240),
    TOILET(4348, 0, "Toilet", 500, 230),
    BASIN(4349, 0, "Basin", 400, 220),
    BATH(4351, 0, "Bath", 400, 210),
    SHOWNER(4352, 0, "Showner", 400, 200),
    BIN(4354, 0, "Bin", 300, 195),
    PLATE(4362, 0, "Plate", 300, 180),
    WASHING_MACHINE(4359, 0, "Washing machine", 300, 175),
    MICROWAVE(4358, 0, "Microwave", 250, 170),
    STONE_COFFEE_TABLE(4260, 0, "Stone coffee table", 250, 160),
    STONE_TABLE(4270, 0, "Stone table", 250, 155),
    STONE_CHAIR(4280, 0, "Stone chair", 250, 150),
    PLAQUE_EN_OR(147, 0, "Plaque en or", 150, 85),
    RAILS(66, 0, "Rails", 150, 90),
    WAGONNET(328, 0, "Wagonnet", 75, 45),
    CISAILLES(359, 0, "Cisailles", 75, 20),
    HACHE_EN_FER(258, 0, "Hache en fer", 75, 40),
    PIOCHE_EN_FER(257, 0, "Pioche en fer", 75, 45),
    HOUE_EN_FER(256, 0, "Houe en fer", 75, 50),
    PELLE_EN_FER(292, 0, "Pelle en fer", 75, 55),
    PLAQUE_EN_FER(148, 0, "Plaque en fer", 75, 25);
    
    private byte data;
    
    private int id;
    
    private int level;
    
    private Material s;
    
    private String name;
    
    private double xp;

    XP_METALLURGIE(int id, int data, String name, double xp, int level) {
      this.id = id;
      this.data =(byte) data;
      this.name = name;
      this.xp = xp;
      this.level = level;
    }
    
    public String getName() {
      return this.name;
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
  
  public CompanyMetallurgie(CompanyData data) {
    super(data, "Objets");
  }
  
  public Company.XP toXP(Material s, Byte data) {
    if (s == null)
      return null; 
    if (data == null)
      return null; 
    for (XP_METALLURGIE x : XP_METALLURGIE.values()) {
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
    for (XP_METALLURGIE x : XP_METALLURGIE.values()) {
      if (id == x.getId() && data.equals(x.getData()))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_METALLURGIE x : XP_METALLURGIE.values()) {
      if (x.getType() == s)
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
}
