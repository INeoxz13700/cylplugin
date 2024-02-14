package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import org.bukkit.entity.EntityType;

public class CompanyChasse extends Company {
  public enum XP_CHASSE implements Company.XP {

    POULE(EntityType.CHICKEN, 50.0D, 0),
    COCHON(EntityType.PIG, 50, 10),
    MOUTON(EntityType.SHEEP, 75, 25),
    VACHE(EntityType.COW, 75, 40),
    OCELOT(EntityType.OCELOT, 100, 75),
    CHEVAL(EntityType.HORSE, 125, 100),
    ANE(EntityType.DONKEY, 150, 120),
    MULET(EntityType.MULE, 200, 150),
    LAMA(EntityType.LLAMA, 250, 180),
    PERROQUET(EntityType.PARROT, 300, 200),
    LAPIN(EntityType.RABBIT, 350, 225),
    LOUP(EntityType.WOLF, 400, 240),
    OURS_POLAIRE(EntityType.POLAR_BEAR, 500.0, 250);

    private EntityType type;
    
    private double xp;
    
    private int level;
    
    XP_CHASSE(EntityType type, double xp, int level) {
      this.type = type;
      this.xp = xp;
      this.level = level;
    }
    
    public EntityType getType() {
      return this.type;
    }
    
    public double getXp() {
      return this.xp;
    }
    
    public int getLevelToUnlock() {
      return this.level;
    }
    
    public String getName() {
      return this.type.toString();
    }
  }
  
  public CompanyChasse(CompanyData data) {
    super(data, "Animaux");
  }
  
  public Company.XP toXP(EntityType type) {
    if (type == null)
      return null; 
    for (XP_CHASSE x : XP_CHASSE.values()) {
      if (x.getType() == type)
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
        if (a.equals(Achievements.TRANSFORMATION100) && 
          this.compteur >= 100)
          Achievements.setAchievement(a, this.data);
      } 
    } 
  }
}
