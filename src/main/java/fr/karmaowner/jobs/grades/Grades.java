package fr.karmaowner.jobs.grades;

import fr.cylapi.core.IGrade;
import fr.cylapi.core.IGrades;
import fr.karmaowner.common.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Grades implements IGrades {
  public ArrayList<Grade> grades = new ArrayList<>();
  
  public static final int MALUSPERKILL = 500;
  
  private String job;
  
  public Grades(String job) {
    this.job = job;
  }
  
  public String getJob() {
    return this.job;
  }
  
  public Grade getGrade(String nom) {
    for (Grade g : this.grades) {
      if (g.getNom().equals(nom))
        return g; 
    } 
    return null;
  }
  
  public int gradeOrder(String nom) {
    int order = 1;
    for (Grade g : this.grades) {
      if (g.getNom().equals(nom))
        return order; 
      order++;
    } 
    return -1;
  }
  
  public Grade nextGrade(int points) {
    for (Grade g : this.grades) {
      if (g.getPoints() > points)
        return g; 
    } 
    return null;
  }
  
  public Grade previousGrade(int points) {
    for (int i = this.grades.size() - 1; i >= 0; i--) {
      if (((Grade)this.grades.get(i)).getPoints() < points)
        return this.grades.get(i); 
    } 
    return null;
  }
  
  public Grade getGrade(int points) {
    for (int i = this.grades.size() - 1; i >= 0; i--) {
      if (((Grade)this.grades.get(i)).getPoints() <= points)
        return this.grades.get(i); 
    } 
    return null;
  }
  
  public void setGrade(Grade g) {
    if (getGrade(g.getNom()) == null) {
      this.grades.add(g);
      this.grades.sort(new Comparator<Grade>() {
            public int compare(Grade o1, Grade o2) {
              return Integer.compare(o1.getPoints(), o2.getPoints());
            }
          });
    } 
  }
  
  public List<String> getHg() {
    List<String> hgs = new ArrayList<>();
    for (Grade g : this.grades) {
      if (g.isHg())
        hgs.add(g.getNom()); 
    } 
    return hgs;
  }
  
  public void loadData() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String nameSection = "grades";
    for (String key : f.getConfigurationSection("jobs." + this.job + "." + nameSection).getKeys(false)) {
      String name = key;
      boolean gettable = f.get("jobs." + this.job + "." + nameSection + "." + key + ".gettable") == null || f.getBoolean("jobs." + this.job + "." + nameSection + "." + key + ".gettable");
      boolean hg = f.get("jobs." + this.job + "." + nameSection + "." + key + ".gettable") != null && f.getBoolean("jobs." + this.job + "." + nameSection + "." + key + ".hg");
      ChatColor color = ChatColor.valueOf(f.getString("jobs." + this.job + "." + nameSection + "." + key + ".color"));
      int points = f.getInt("jobs." + this.job + "." + nameSection + "." + key + ".points");
      setGrade(new Grade(name, color, points, gettable, hg));
    } 
  }
  
  public List<IGrade> getGrades() {
    return Arrays.asList(this.grades.toArray(new IGrade[0]));
  }
}
