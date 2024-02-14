package fr.karmaowner.jobs.chauffeur;

import org.bukkit.Location;

public class Traject {
  private String regionName;
  
  private Location departure;
  
  private Location arrived;
  
  private double price;
  
  public Traject(Location a, Location b, String regionName) {
    setDeparture(a);
    setArrived(b);
    setRegionName(regionName);
    this.price = 0.2D * (int)a.distance(b);
  }
  
  public Location getDeparture() {
    return this.departure;
  }
  
  public void setDeparture(Location departure) {
    this.departure = departure;
  }
  
  public Location getArrived() {
    return this.arrived;
  }
  
  public void setArrived(Location arrived) {
    this.arrived = arrived;
  }
  
  public double getPrice() {
    return this.price;
  }
  
  public void setPrice(int price) {
    this.price = price;
  }
  
  public String getRegionName() {
    return this.regionName;
  }
  
  public void setRegionName(String regionName) {
    this.regionName = regionName;
  }
  
  public String getCoord() {
    return (int)this.arrived.getX() + " " + (int)this.arrived.getY() + " " + (int)this.arrived.getZ();
  }
}
