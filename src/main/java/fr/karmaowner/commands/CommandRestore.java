package fr.karmaowner.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.casino.Casino;
import fr.karmaowner.casino.Jackpot;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.restore.regions.RegionState;
import fr.karmaowner.restore.regions.Restore;
import fr.karmaowner.utils.FileUtils;
import fr.karmaowner.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CommandRestore implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("restorerg")) {
      Player p = (Player)sender;
      if (!sender.hasPermission("cylrp.restore")) {
        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
        return false;
      } 
      if (args.length == 0) {
        displayHelp(sender, 1);
        return false;
      } 
      if (args[0].equalsIgnoreCase("add")) {
        if (args[1].equalsIgnoreCase("region")) {
          String rgName = args[2];
          int seconds = Integer.parseInt(args[3]);
          Restore.INSTANCE.addRegion(p, rgName, seconds);
        }
      } else if (args[0].equalsIgnoreCase("delete")) {
        if (args[1].equalsIgnoreCase("region")) {
          String rgName = args[2];
          Restore.INSTANCE.deleteRegion(p, rgName);
        } 
      } else if (args[0].equalsIgnoreCase("help")) {
        int page = Integer.parseInt(args[1]);
        displayHelp(sender, page);
      }
      else if (args[0].equalsIgnoreCase("convert"))
      {

        File file = new File("plugins/CYLRP-CORE/restores");
        if(!file.exists())
        {
          file.mkdir();
        }

        sender.sendMessage(Restore.REGIONS.size() + " regions restore");
        new TaskCreator(new CustomRunnable() {
          public void customRun() {

            for (int i = 0; i < Restore.REGIONS.size(); i++)
            {
              float percent = ((i+1) / (float)Restore.REGIONS.size()) * 100;
              sender.sendMessage("§aConversion region en cours : §b" + percent + " §a%");

              RegionState state = Restore.REGIONS.get(i);

              File rgFile = new File(file.getPath() + "/" + state.getRgName() + ".yml");
              if(!rgFile.exists()) {
                try {
                  rgFile.createNewFile();
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }

              FileConfiguration fileConfiguration = (FileConfiguration)YamlConfiguration.loadConfiguration(rgFile);
              if(fileConfiguration != null)
              {
                fileConfiguration.set(state.getRgName() + ".state.seconds", state.getSeconds());
                int j = 0;
                for (String s : state.getContents()) {
                  int id = Integer.parseInt(s.split(":")[0]);
                  int data = Byte.parseByte(s.split(":")[1]);
                  int direction = -1;
                  if ((s.split(":")).length == 3)
                    direction = Integer.parseInt(s.split(":")[2]);
                  fileConfiguration.set(state.getRgName() + ".contents." + j + ".id", id);
                  fileConfiguration.set(state.getRgName() + ".contents." + j + ".data", data);
                  if (direction != -1)
                    fileConfiguration.set(state.getRgName() + ".contents." + j + ".direction", direction);
                  j++;
                }

                try {
                  fileConfiguration.save(rgFile);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }

            }
          }

        },  true, 0L);

      }
      else if(args[0].equalsIgnoreCase("debug"))
      {
        sender.sendMessage("Restorerg debug :");
        sender.sendMessage("regions count : " + Restore.REGIONS.size());
        for(int i = 0; i < Restore.REGIONS.size(); i++)
        {
          RegionState state = Restore.REGIONS.get(i);
          sender.sendMessage(state.getRgName() + " " + state.getSeconds());
        }
      }
    } 
    return false;
  }
  
  public void displayHelp(CommandSender sender, int page) {
    if (page == 1) {
      sender.sendMessage("§b【Restoreg commands 1/3 】");
      sender.sendMessage("");
      sender.sendMessage("● §c/restorerg add region <rg name> <restore interval secondes> §7- §aAjoute une region régénératrice");
      sender.sendMessage("● §c/restorerg delete region <rg name> §7- §aRetire une region régénératrice");
      sender.sendMessage("● §c/restorerg help <page> §7- §aAffiche la liste des commandes");
    } 
  }
}
