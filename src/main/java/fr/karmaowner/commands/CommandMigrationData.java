package fr.karmaowner.commands;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.DataMigration;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandMigrationData implements CommandExecutor {
  public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
    if (arg0 instanceof Player && arg0.isOp()) {
      final Player p = (Player)arg0;
      if (arg1.getName().equalsIgnoreCase("migrate"))
        try {
          final DataMigration migration = new DataMigration(Main.Database);
          new TaskCreator(new CustomRunnable() {
                private Map<File, FileConfiguration> files = migration.getAllFile(DataMigration.TypeFile.PLAYER);
                
                private int max = this.files.size();
                
                private int progress = 0;
                
                public void customRun() {
                  for (Map.Entry<File, FileConfiguration> config : this.files.entrySet()) {
                    String pseudo = ((File)config.getKey()).getName().replaceAll("\\.yml", "");
                    HashMap<String, String> datas = new HashMap<>();
                    datas.put("pseudo", "'" + pseudo.replace("'", "''") + "'");
                    try {
                      migration.migrateFileDataToSql(DataMigration.TypeParser.PLAYERDATA, config.getValue(), datas);
                    } catch (SQLException | UnsupportedEncodingException e) {
                      e.printStackTrace();
                    }
                    this.progress++;
                    DecimalFormat df = new DecimalFormat("0.00");
                    double percent = this.progress * 1.0D / this.max * 100.0D;
                    percent = Double.parseDouble(df.format(percent).replaceFirst(",", "."));
                    p.sendMessage("§cProgression Migration Joueurs: " + percent + "%");
                    try {
                      Thread.sleep(100L);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    } 
                  }
                  p.sendMessage("§4Migration des joueurs terminées");
                }
              }, true, 0L);
          new TaskCreator(new CustomRunnable() {
                private Map<File, FileConfiguration> files = migration.getAllFile(DataMigration.TypeFile.GANG);
                
                private int max = this.files.size();
                
                private int progress = 0;
                
                public void customRun() {
                  for (Map.Entry<File, FileConfiguration> config : this.files.entrySet()) {
                    String gangname = ((File)config.getKey()).getName().replaceAll("\\.yml", "");
                    HashMap<String, String> datas = new HashMap<>();
                    datas.put("gangname", "'" + gangname.replace("'", "''") + "'");
                    try {
                      migration.migrateFileDataToSql(DataMigration.TypeParser.GANGDATA, config.getValue(), datas);
                    } catch (SQLException | UnsupportedEncodingException e) {
                      e.printStackTrace();
                    }
                    this.progress++;
                    DecimalFormat df = new DecimalFormat("0.00");
                    double percent = this.progress * 1.0D / this.max * 100.0D;
                    percent = Double.parseDouble(df.format(percent).replaceFirst(",", "."));
                    p.sendMessage("§cProgression Migration Gangs: " + percent + "%");
                    try {
                      Thread.sleep(200L);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    } 
                  } 
                  p.sendMessage("§4Migration des Gangs terminées");
                }
              }, true, 0L);
          new TaskCreator(new CustomRunnable() {
                private Map<File, FileConfiguration> files = migration.getAllFile(DataMigration.TypeFile.COMPANY);
                
                private int max = this.files.size();
                
                private int progress = 0;
                
                public void customRun() {
                  for (Map.Entry<File, FileConfiguration> config : this.files.entrySet()) {
                    try {
                      migration.migrateFileDataToSql(DataMigration.TypeParser.COMPANYDATA, config.getValue(), null);
                    } catch (SQLException | UnsupportedEncodingException e) {
                      e.printStackTrace();
                    }
                    this.progress++;
                    DecimalFormat df = new DecimalFormat("0.00");
                    double percent = this.progress * 1.0D / this.max * 100.0D;
                    percent = Double.parseDouble(df.format(percent).replaceFirst(",", "."));
                    p.sendMessage("§cProgression Migration Entreprises: " + percent + "%");
                    try {
                      Thread.sleep(200L);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    } 
                  } 
                  p.sendMessage("§4Migration des Entreprises terminées");
                }
              }, true, 0L);
          p.sendMessage("§4Lancement de la migration...");
        } catch (ClassNotFoundException | SQLException e) {
          e.printStackTrace();
        }
    } 
    return false;
  }
}
