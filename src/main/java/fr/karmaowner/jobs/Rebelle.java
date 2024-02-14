package fr.karmaowner.jobs;

public class Rebelle extends RebelleTerroriste {
  public Rebelle(String player) {
    super(player);
  }
  
  public static void loadJobData() {
    Jobs.Job.REBELLE.loadJobClothes();
  }
  
  public static void saveJobData() {}
}
