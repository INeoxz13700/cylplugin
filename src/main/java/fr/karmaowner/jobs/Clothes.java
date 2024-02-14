package fr.karmaowner.jobs;

import java.util.List;

public class Clothes {
  private String id;
  
  private int Helmets;
  
  private int Chestplates;
  
  private int leggings;
  
  private int boots;

  private int gpb;



  private boolean defaultClothes;
  
  private boolean equipGrade;


  private List<String> grades = null;
  
  public Clothes(String id, int Helmets, int Chestplates, int leggings, int boots, int gpb, boolean isDefault, boolean equipGrade) {
    this.id = id;
    setHelmets(Helmets);
    setChestplates(Chestplates);
    setLeggings(leggings);
    setBoots(boots);
    setGpb(gpb);
    this.defaultClothes = isDefault;
    this.equipGrade = equipGrade;
  }
  
  public Clothes(String id, int Helmets, int Chestplates, int leggings, int boots, int gpb, boolean isDefault, boolean equipGrade, List<String> grades) {
    this(id, Helmets, Chestplates, leggings, boots,gpb, isDefault, equipGrade);
    this.grades = grades;
  }
  
  public int getHelmets() {
    return this.Helmets;
  }
  
  public void setHelmets(int helmets) {
    this.Helmets = helmets;
  }
  
  public int getChestplates() {
    return this.Chestplates;
  }
  
  public String getId() {
    return this.id;
  }
  
  public void setChestplates(int chestplates) {
    this.Chestplates = chestplates;
  }
  
  public int getLeggings() {
    return this.leggings;
  }
  
  public void setLeggings(int leggings) {
    this.leggings = leggings;
  }
  
  public int getBoots() {
    return this.boots;
  }
  
  public void setBoots(int boots) {
    this.boots = boots;
  }

  public int getGpb() {
    return this.gpb;
  }

  public void setGpb(int gpbId) {
    this.gpb = gpbId;
  }
  
  public boolean isClothes(int id) {
    if (id == 0)
      return false;

    return (id == getHelmets() || id == getChestplates() || id == getLeggings() || id == getBoots() || id == getGpb());
  }
  
  public String toString() {
    return "id=" + getId() + ";helmet=" + getHelmets() + ";chestplate=" + getChestplates() + ";legging=" + getLeggings() + ";boots=" + getBoots() + ";gpb=" + getGpb();
  }
  
  public boolean hasGrade() {
    return (this.grades != null);
  }
  
  public boolean hasGrade(String grade) {
    if (hasGrade()) {
      for (String grd : this.grades) {
        if (grd.equals(grade))
          return true; 
      } 
      return false;
    } 
    return true;
  }
  
  public List<String> getGrades() {
    return this.grades;
  }
  
  public boolean isDefaultClothes() {
    return this.defaultClothes;
  }
  
  public boolean isEquipGrade() {
    return this.equipGrade;
  }
}
