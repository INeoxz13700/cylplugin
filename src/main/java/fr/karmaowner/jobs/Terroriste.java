package fr.karmaowner.jobs;

public class Terroriste extends RebelleTerroriste {
  public Terroriste(String player) {
    super(player);
  }
  
  public static void loadJobData() {
    Jobs.Job.TERRORISTE.loadJobClothes();
  }
}
