package fr.karmaowner.companies;

import fr.karmaowner.common.Achievements;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;

public abstract class CompanyAchievements extends Company implements ConvertToXP {
  public CompanyAchievements(CompanyData data, String CompteurName) {
    super(data, CompteurName);
  }
  
  public void setCompanyAchievements(Achievements a) {
    if (!Achievements.hasAchievement(a, (DataAchievements)this.data)) {
      Company.XP x = toXP(a.getType(), a.getData());
      x = (x == null) ? toXP(a.getId(), a.getData()) : x;
      if (x != null && 
        isItemUnlocked(x))
        Achievements.setAchievement(a, (DataAchievements)this.data); 
    } 
  }
}
