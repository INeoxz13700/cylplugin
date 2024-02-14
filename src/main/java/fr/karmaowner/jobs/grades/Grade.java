package fr.karmaowner.jobs.grades;

import fr.cylapi.core.IGrade;
import org.bukkit.ChatColor;

public class Grade implements IGrade {
  private String nom;
  
  private ChatColor color;
  
  private int points;
  
  private boolean gettable;
  
  private boolean hg;
  
  public Grade(String nom, ChatColor color, int points, boolean gettable, boolean hg) {
    this.nom = nom;
    this.color = color;
    this.points = points;
    this.gettable = gettable;
    this.hg = hg;
  }
  
  public boolean getGettable() {
    return this.gettable;
  }
  
  public static int ConvertToPoints(int nbMissions, int xp, int malus, int timer) {
    return nbMissions + xp + timer - malus;
  }
  
  public int getPoints() {
    return this.points;
  }
  
  public String getNom() {
    return this.nom;
  }
  
  public String getPrefix() {
    return this.color + "[" + this.nom + "]";
  }
  
  public boolean isHg() {
    return this.hg;
  }
  
  public void setHg(boolean hg) {
    this.hg = hg;
  }
  
  public String getName() {
    return this.nom;
  }
  
  public boolean isGettable() {
    return this.gettable;
  }
}
