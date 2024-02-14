package fr.karmaowner.jobs;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.grades.Grade;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.tresorerie.Tresorerie;
import fr.karmaowner.utils.MessageUtils;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Salary {
  public static final int vipSalaryBonusMinutes = 250;
  
  public static final int vipPlusSalaryBonusMinutes = 500;
  
  public static final int supervipSalaryBonusMinutes = 1000;
  
  public static final int TauxImpots = 5;
  
  public static final int ImpotsMax = 15000;
  
  public static final long duration = 3600L;
  
  public Salary() {
    new TaskCreator(new CustomRunnable() {
          private Timestamp timer = new Timestamp(System.currentTimeMillis());
          
          public void customRun() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - this.timer.getTime() >= 3600000L) {
              this.timer = new Timestamp(System.currentTimeMillis());
              for (Player p : Bukkit.getOnlinePlayers())
                Salary.distributeSalary(p); 
            } 
          }
        },  true, 0L, 20L);
  }
  
  public static void distributeSalary(Player p) {
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    if (pData.salaryTimeStamp == null)
      return; 
    double money = 0.0D;
    if (p.hasPermission("cylrp.rank.vip")) {
      money = 250.0D;
    } else if (p.hasPermission("cylrp.rank.vipplus")) {
      money = 500.0D;
    } else if (p.hasPermission("cylrp.rank.supervip")) {
      money = 1000.0D;
    } 
    if (pData.selectedJob instanceof hasGrade) {
      int salary = 0;
      if (pData.selectedJob instanceof Policier) {
        salary = 1500;
      } else if (pData.selectedJob instanceof Gendarme) {
        salary = 1500;
      } else if (pData.selectedJob instanceof Pompier) {
        salary = 1400;
      } else if (pData.selectedJob instanceof Militaire) {
        salary = 1700;
      } else if (pData.selectedJob instanceof Gign) {
        salary = 1600;
      } else if (pData.selectedJob instanceof BAC) {
        salary = 1550;
      } else if (pData.selectedJob instanceof Medecin) {
        salary = 1400;
      } else if (pData.selectedJob instanceof Douanier) {
        salary = 1500;
      } 
      hasGrade grade = (hasGrade)pData.selectedJob;
      if (pData.selectedJob instanceof AssembleeNationale) {
        if (grade.getGrade().getGrade().getNom().equalsIgnoreCase("Président")) {
          money += 800.0D;
        } else if (grade.getGrade().getGrade().getNom().equalsIgnoreCase("VicePrésident")) {
          money += 750.0D;
        } else if (grade.getGrade().getGrade().getNom().equalsIgnoreCase("ChefGarde")) {
          money += 550.0D;
        } else if (grade.getGrade().getGrade().getNom().equalsIgnoreCase("Député")) {
          money += 650.0D;
        } 
      } else {
        int i = 0;
        for (Grade g : (grade.getGrade().getGrades()).grades) {
          if (pData.selectedJob instanceof Pompier) {
            if (g.getNom().equalsIgnoreCase(grade.getGrade().getGrade().getNom())) {
              money += (salary + i * 80);
              break;
            } 
            i++;
            continue;
          } 
          if (g.getNom().equalsIgnoreCase(grade.getGrade().getGrade().getNom())) {
            money += (salary + i * 300);
            break;
          } 
          i++;
        } 
      } 
    } else if (pData.selectedJob instanceof Cuisinier) {
      money += 250.0D;
    } else if (pData.selectedJob instanceof Civile) {
      money += 150.0D;
    } else if (pData.selectedJob instanceof Armurier) {
      money += 250.0D;
    } else if (pData.selectedJob instanceof Taxi) {
      money += 250.0D;
    } else if (pData.selectedJob instanceof Chimiste) {
      money += 250.0D;
    } else if (pData.selectedJob instanceof Rebelle) {
      money += 150.0D;
    } else if (pData.selectedJob instanceof Terroriste) {
      money += 150.0D;
    } else if (pData.selectedJob instanceof Maire) {
      money += 800.0D;
    } else if (pData.selectedJob instanceof MaireAdjoint) {
      money += 750.0D;
    } else if (pData.selectedJob instanceof ChefGarde) {
      money += 600.0D;
    } else if (pData.selectedJob instanceof Garde) {
      money += 1200.0D;
    } else if (pData.selectedJob instanceof Voleur) {
      money += 150.0D;
    } else if (pData.selectedJob instanceof Hacker) {
      money += 150.0D;
    } else if (pData.selectedJob instanceof Psychopathe) {
      money += 150.0D;
    } 
    if (money == 0.0D)
      return; 
    double subImpots = money * 0.05D;
    subImpots = Math.min(subImpots, 15000.0D);
    pData.setMoney(pData.getMoney().add(BigDecimal.valueOf(money)));
    MessageUtils.sendMessage((CommandSender)p, "§aVous avez reçu votre salaire : §6" + money + "€");
    pData.setMoney(pData.getMoney().subtract(BigDecimal.valueOf(subImpots)));
    Tresorerie t = Tresorerie.getTresorerie("maire");
    if (t != null)
      t.addMoney(subImpots); 
    MessageUtils.sendMessage((CommandSender)p, "§dLes Impôts ont été prélevés de votre compte ( 5% ): §5" + subImpots + "€");
    pData.salaryTimeStamp = new Timestamp(System.currentTimeMillis());
  }
}
