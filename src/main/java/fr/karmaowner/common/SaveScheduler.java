package fr.karmaowner.common;

import fr.karmaowner.companies.shop.Npc;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.drogue.Drogue;
import fr.karmaowner.gangs.Capture;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Prisons;
import fr.karmaowner.jobs.chauffeur.Regions;
import fr.karmaowner.jobs.parcelle.FreeArea;
import fr.karmaowner.tresorerie.Tresorerie;
import fr.karmaowner.wantedlist.WantedList;
import java.sql.Timestamp;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class SaveScheduler {
  Timestamp lastSave = null;
  
  public static volatile boolean saveIsRunning = false;
  
  public void activateScheduler() {
    this.lastSave = new Timestamp(System.currentTimeMillis());
    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    scheduler.runTaskTimerAsynchronously((Plugin)Main.INSTANCE, new Runnable() {
          public void run() {
            if (System.currentTimeMillis() - SaveScheduler.this.lastSave.getTime() >= 900000L) {
              if (SaveScheduler.saveIsRunning)
                return; 
              SaveScheduler.saveIsRunning = true;

              Bukkit.broadcastMessage("§cServeur en cours de sauvegarde");

              WantedList.saveData();
              Prisons.saveData();
              Regions.saveData();
              PlayerData.SaveDatas();
              try {
                Jobs.Job.saveDatas();

              } catch (Exception e) {
                e.printStackTrace();
              } 
              CompanyData.saveDatas();

              Capture.saveData();

              Npc.saveData();

              GangData.saveDatas();

              Drogue.INSTANCE.saveData();

              Main.Log("Tresorerie Data saving...");
              Tresorerie.getTresorerie("staff").saveData();

              Tresorerie.getTresorerie("maire").saveData();

              Main.Log("Tresorerie Data saved");

              Main.INSTANCE.saveConfig();
              SaveScheduler.this.lastSave = new Timestamp(System.currentTimeMillis());
              SaveScheduler.saveIsRunning = false;
              Bukkit.broadcastMessage("§cSauvegarde terminée");
            } 
          }
        },  0L, 2400L);
  }
  
  public void forceSave() {
    new TaskCreator(new CustomRunnable() {
          public void customRun() {
            if (SaveScheduler.saveIsRunning)
              return; 
            SaveScheduler.saveIsRunning = true;
            Bukkit.broadcastMessage("§cServeur en cours de sauvegarde");
            WantedList.saveData();
            Prisons.saveData();
            Regions.saveData();
            PlayerData.SaveDatas();
            try {
              Jobs.Job.saveDatas();
            } catch (Exception e) {
              e.printStackTrace();
            } 
            CompanyData.saveDatas();
            Capture.saveData();
            Npc.saveData();
            GangData.saveDatas();
            Drogue.INSTANCE.saveData();

            Tresorerie tresorerie = Tresorerie.getTresorerie("staff");
            if(tresorerie != null) tresorerie.saveData();

            tresorerie = Tresorerie.getTresorerie("maire");
            if(tresorerie != null) tresorerie.saveData();

            Main.INSTANCE.saveConfig();
            SaveScheduler.saveIsRunning = false;
            Bukkit.broadcastMessage("§cSauvegarde terminée");
          }
        },  true, 0L);
  }
}
