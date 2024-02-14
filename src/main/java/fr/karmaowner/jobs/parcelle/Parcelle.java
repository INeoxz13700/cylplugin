package fr.karmaowner.jobs.parcelle;

public class Parcelle {
  private double price = 0.0D;
  
  private String name;
  
  public Parcelle(String name) {
    setName(name);
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public double getPrice() {
    return this.price;
  }
  
  public void setPrice(double price) {
    this.price = price;
  }
}
