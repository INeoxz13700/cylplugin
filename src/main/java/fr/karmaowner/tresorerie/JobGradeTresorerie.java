package fr.karmaowner.tresorerie;

import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.grades.hasGrade;
import java.util.List;
import org.bukkit.entity.Player;

public class JobGradeTresorerie extends Tresorerie {
  private List<String> grades;
  
  public JobGradeTresorerie(String tresorerieName) {
    super(tresorerieName);
  }
  
  public JobGradeTresorerie(String tresorerieName, List<String> grades) {
    super(tresorerieName);
    this.grades = grades;
  }
  
  public void setGrades(List<String> grades) {
    this.grades = grades;
  }
  
  public boolean hasPrivilege(Player p) {
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.selectedJob instanceof hasGrade) {
      hasGrade gradePl = (hasGrade)data.selectedJob;
      if (this.grades.contains(gradePl.getGrade().getGrade().getNom()))
        return true; 
    } 
    return false;
  }
  
  public String getNeededPrivilege() {
    return "Vous devez être un haut-gradé";
  }
}
