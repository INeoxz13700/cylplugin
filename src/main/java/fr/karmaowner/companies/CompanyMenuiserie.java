package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.Material;

public class CompanyMenuiserie extends CompanyAchievements {
  public enum XP_MENUISERIE implements Company.XP {
    PLANCHE_DE_CHENE("Planche de chêne", 5, 0, 50, 0),
    PLANCHE_DE_BOULEAU("Planche de bouleau", 5, 2, 50, 0),
    PLANCHE_DE_SAPIN("Planche de sapin", 5, 1, 50, 5),
    PLANCHE_D_ACAJOU("Planche d'acajou", 5, 3, 50, 5),
    PLANCHE_DE_CHENE_NOIR("Planche de chêne noir", 5, 5, 50, 10),
    PLANCHE_D_ACACIA("Planche d'acacia", 5, 4, 50, 10),
    ESCALIER_EN_CHENE("Escalier en chêne", 53, 0, 50, 15),
    ESCALIER_EN_BOULEAU("Escalier en bouleau", 135, 0, 75, 20),
    ESCALIER_EN_SAPIN("Escalier en sapin", 134, 0, 75, 25),
    ESCALIER_EN_ACAJOU("Escalier en acajou", 136, 0, 75, 30),
    ESCALIER_EN_CHENE_NOIR("Escalier en chêne noir", 164, 0, 80, 35),
    ESCALIER_EN_ACACIA("Escalier en acacia", 163, 0, 80, 40),
    DALLE_DE_CHENE("Dalle de chêne", 126, 0, 50, 0),
    DALLE_DE_BOULEAU("Dalle de bouleau", 126, 2, 50, 0),
    DALLE_DE_SAPIN("Dalle de sapin", 126, 1, 50, 5),
    DALLE_D_ACAJOU("Dalle d'acajou", 126, 3, 50, 5),
    DALLE_DE_CHENE_NOIR("Dalle de chêne noir", 126, 5, 50, 10),
    DALLE_D_ACACIA("Dalle d'acacia", 126, 4, 50, 10),
    BARRIERE_DE_CHENE("Barriere de chêne", 85, 0, 95, 45),
    BARRIERE_DE_BOULEAU("Barriere de bouleau", 189, 0, 100, 50),
    BARRIERE_DE_SAPIN("Barriere de sapin", 188, 0, 100, 55),
    BARRIERE_D_ACAJOU("Barriere d'acajou", 190, 0, 100, 60),
    BARRIERE_DE_CHENE_NOIR("Barriere de chêne noir", 191, 0, 100, 65),
    BARRIERE_D_ACACIA("Barriere d'acacia", 192, 0, 100, 70),
    PORTILLON_DE_CHENE("Portillon de chêne", 107, 0, 100, 45),
    PORTILLON_DE_BOULEAU("Portillon de bouleau", 184, 0, 100, 50),
    PORTILLON_DE_SAPIN("Portillon de sapin", 183, 0, 100, 55),
    PORTILLON_D_ACAJOU("Portillon d'acajou", 185, 0, 100, 60),
    PORTILLON_DE_CHENE_NOIR("Portillon de chêne noir", 186, 0, 100, 65),
    PORTILLON_D_ACACIA("Portillon d'acacia", 187, 0, 100, 70),
    PORTE_DE_CHENE("Porte de chêne", 324, 0, 100, 50),
    PORTE_DE_BOULEAU("Porte de bouleau", 428, 0, 100, 55),
    PORTE_DE_SAPIN("Porte de sapin", 427, 0, 100, 60),
    PORTE_D_ACAJOU("Porte d'acajou", 429, 0, 100, 65),
    PORTE_DE_CHENE_NOIR("Porte de chêne noir", 431, 0, 100, 70),
    PORTE_D_ACACIA("Porte d'acacia", 430, 0, 100, 75),
    BATEAU_EN_CHENE("Bateau en chêne", 333, 0, 150, 80),
    BATEAU_EN_BOULEAU("Bateau en bouleau", 445, 0, 150, 85),
    BATEAU_EN_SAPIN("Bateau en sapin", 444, 0, 150, 90),
    BATEAU_EN_ACAJOU("Bateau en acajou", 446, 0, 175, 95),
    BATEAU_EN_CHENE_NOIR("Bateau en chêne noir", 448, 0, 175, 100),
    BATEAU_EN_D_ACACIA("Bateau en d'acacia", 447, 0, 175, 105),
    TRAPPE_EN_BOIS("Trappe en bois", 96, 0, 100, 50),
    PANCARTE("Pancarte", 323, 0, 75, 25),
    COFFRE("Coffre", 54, 0, 50, 15),
    ECHELLE("Echelle", 65, 0, 80, 35),
    PLAQUE_DE_PRESSION_EN_BOIS("Plaque de pression en bois", 72, 0, 95, 45),
    BATON("Bâton", 280, 0, 50, 10),
    TABLE_EN_CHENE("Table en chêne", 4264, 0, 175, 110),
    TABLE_EN_BOULEAU("Table en bouleau", 4266, 0, 175, 115),
    TABLE_EN_SAPIN("Table en sapin", 4265, 0, 175, 120),
    TABLE_EN_ACAJOU("Table en acajou", 4267, 0, 175, 125),
    TABLE_EN_CHENE_NOIR("Table en chêne noir", 4269, 0, 200, 130),
    TABLE_EN_ACACIA("Table en acacia", 4268, 0, 200, 135),
    TABLE_BASSE_EN_CHENE("Table basse en chêne", 4254, 0, 200, 140),
    TABLE_BASSE_EN_BOULEAU("Table basse en bouleau", 4684, 0, 250, 145),
    TABLE_BASSE_EN_SAPIN("Table basse en sapin", 4445, 0, 250, 150),
    TABLE_BASSE_EN_ACAJOU("Table basse en acajou", 4464, 0, 250, 155),
    TABLE_BASSE_EN_CHENE_NOIR("Table basse en chêne noir", 4259, 0, 250, 160),
    TABLE_BASSE_EN_ACACIA("Table basse en acacia", 4258, 0, 250, 165),
    RIDEAUX_EN_CHENE("Rideaux en chêne", 4298, 0, 300, 170),
    RIDEAUX_EN_BOULEAU("Rideaux en bouleau", 4300, 0, 300, 175),
    RIDEAUX_EN_SAPIN("Rideaux en sapin", 4299, 0, 300, 180),
    RIDEAUX_EN_ACAJOU("Rideaux en acajou", 4307, 0, 300, 185),
    RIDEAUX_EN_CHENE_NOIR("Rideaux en chêne noir", 4303, 0, 300, 190),
    RIDEAUX_EN_ACACIA("Rideaux en acacia", 4302, 0, 400, 195),
    BANC_EN_CHENE("Banc en chêne", 4440, 0, 400, 200),
    BANC_EN_BOULEAU("Banc en bouleau", 4442, 0, 400, 210),
    BANS_EN_SAPIN("Banc en sapin", 4441, 0, 400, 220),
    BANC_EN_ACAJOU("Banc en acajou", 4655, 0, 400, 230),
    BANC_EN_CHENE_NOIR("Banc en chêne noir", 4674, 0, 500, 240),
    BANC_EN_ACACIA("Banc en acacia", 4444, 0, 500, 250),
    MIXING_BOWL("Mixing bowl", 6044, 0, 250, 150),
    CHAISE_EN_CHENE("Chaise en chêne", 4654, 0, 400, 200),
    CHAISE_EN_BOULEAU("Chaise en bouleau", 4276, 0, 400, 210),
    CHAISE_EN_SAPIN("Chaise en sapin", 4275, 0, 400, 220),
    CHAISE_EN_ACAJOU("Chaise en acajou", 4277, 0, 400, 230),
    CHAISE_EN_CHENE_NOIR("Chaise en chêne noir", 4279, 0, 500, 240),
    CHAISE_EN_ACACIA("Chaise en acacia", 4278, 0, 500, 250),
    CHOPPING_BOARD("Chopping Board", 4647, 0, 300, 175);

    private int id;
    
    private Material s;
    
    private String name;

    private byte subId;

    private int xp;

    private int lvl;
    

    XP_MENUISERIE(String name, int id, int subId, int xp, int lvl) {
      this.subId = (byte)subId;
      this.id = id;
      this.name = name;
      this.xp = xp;
      this.lvl = lvl;
    }
    
    public String getName() {
      return this.name;
    }
    
    public double getXp() {
      return xp;
    }
    
    public int getLevelToUnlock() {
      return lvl;
    }
    
    public Byte getData() {
      return this.subId;
    }
    
    public int getId() {
      return this.id;
    }
    
    public Material getType() {
      return this.s;
    }
  }
  
  public CompanyMenuiserie(CompanyData data) {
    super(data, "Objets");
  }
  
  public Company.XP toXP(Material s, Byte data) {
    if (s == null)
      return null; 
    if (data == null)
      return null; 
    for (XP_MENUISERIE x : XP_MENUISERIE.values()) {
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
    for (XP_MENUISERIE x : XP_MENUISERIE.values()) {
      if (id == x.getId() && data.equals(x.getData()))
        return x; 
    } 
    return null;
  }
  
  public Company.XP toXP(Material s) {
    for (XP_MENUISERIE x : XP_MENUISERIE.values()) {
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
