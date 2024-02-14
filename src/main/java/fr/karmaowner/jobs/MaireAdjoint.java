package fr.karmaowner.jobs;

public class MaireAdjoint extends Jobs implements Legal, Radio, JobsMairie {
  public MaireAdjoint(String name) {
    super(name);
  }
  
  public static void loadJobData() {
    Jobs.Job.MAIREADJOINT.loadJobClothes();
  }
}
