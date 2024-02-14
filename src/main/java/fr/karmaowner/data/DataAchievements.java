package fr.karmaowner.data;

import fr.karmaowner.common.Achievements;
import java.util.List;

public interface DataAchievements extends Data {
  List<Achievements> getAchievements();
  
  void setAchievements(Achievements paramAchievements);
}
