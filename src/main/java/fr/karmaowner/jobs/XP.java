package fr.karmaowner.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;

public class XP {
  private int xp;
  
  private String name;
  
  public XP(String name) {
    this.name = name;
  }
  
  public void setXp(int xp) {
    this.xp = xp;
  }
  
  public int getXp() {
    return this.xp;
  }
  
  public void loadData(ResultSet data, String fieldname) {
    try {
      this.xp = data.getInt(fieldname);
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
}
