package fr.karmaowner.commands;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.gps.GPS;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.Permissions;
import fr.karmaowner.utils.RecordBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGPSAdd implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    Player p = (Player)sender;
    if (args.length == 2) {
      if (args[0].equals("add")) {
        try {
          SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("player_gps_data")
                  .where(new CustomEntry("pseudo", p.getName())).toString());

          String name = args[1].startsWith(";") ? args[1].replaceFirst(";","") : args[1];
          if (results.count() == 0)
          {
            HashMap<String, Object> fields = new HashMap<>();

            fields.put("pseudo", p.getName());
            fields.put("x", String.valueOf(p.getLocation().getBlock().getX()));
            fields.put("y", String.valueOf(p.getLocation().getBlock().getY()));
            fields.put("z", String.valueOf(p.getLocation().getBlock().getZ()));
            fields.put("names", name);

            Main.Database.update(RecordBuilder.build().insert(fields, "player_gps_data").toString());

            p.sendMessage("§8[§6GPS§8] §aVous avez ajouté la destination §e" + name + " §aà vos coordonnées.");
          }
          else
          {
            List<String> xList = new ArrayList<>();
            List<String> yList = new ArrayList<>();
            List<String> zList = new ArrayList<>();
            List<String> names = new ArrayList<>();
            ResultSet data = results.getActualResult();

            if(!data.getString("x").isEmpty())
            {
              if ((data.getString("x").split(";")).length > 0) xList.addAll(Arrays.asList(data.getString("x").split(";")));
            }

            if(!data.getString("y").isEmpty())
            {
              if ((data.getString("y").split(";")).length > 0) yList.addAll(Arrays.asList(data.getString("y").split(";")));
            }

            if(!data.getString("z").isEmpty())
            {
              if ((data.getString("z").split(";")).length > 0) zList.addAll(Arrays.asList(data.getString("z").split(";")));
            }

            if(!data.getString("names").isEmpty())
            {
              if ((data.getString("names").split(";")).length > 0) names.addAll(Arrays.asList(data.getString("names").split(";")));
            }

            if (!names.contains(name)) {
              if (names.size() != 50) {
                xList.add(String.valueOf(p.getLocation().getBlock().getX()));
                yList.add(String.valueOf(p.getLocation().getBlock().getY()));
                zList.add(String.valueOf(p.getLocation().getBlock().getZ()));

                names.add(name);

                HashMap<String, Object> fields = new HashMap<>();

                fields.put("x", xList.size() > 1 ? StringUtils.join(xList, ';') : xList.get(0));
                fields.put("y", yList.size() > 1 ? StringUtils.join(yList, ';') : yList.get(0));
                fields.put("z", zList.size() > 1 ? StringUtils.join(zList, ';') : zList.get(0));
                fields.put("names", names.size() > 1 ? StringUtils.join(names, ';') : names.get(0));

                Main.Database.update(RecordBuilder.build().update(fields, "player_gps_data").where(new CustomEntry("pseudo", p.getName())).toString());
                p.sendMessage("§8[§6GPS§8] §aVous avez ajouté la destination §e" + name + " §aà vos coordonnées.");
              } else {
                p.sendMessage("§8[§6GPS§8] §cErreur, vous avez atteint le nombre maximum de destination personnalisé.");
              }
            } else {
              p.sendMessage("§8[§6GPS§8] §cErreur, cette destination existe déjà.");
            }
          }

        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      else if(args[0].equals("remove"))
      {
        if (!sender.hasPermission(Permissions.Admin)) {
          MessageUtils.sendMessage(sender, "§cVous n'avez pas la permission d'utiliser cette commande.");
          return false;
        }

        try {
          SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("gps")
                  .where(new CustomEntry("place", args[1])).toString());

          if (results.count() < 0) {
            sender.sendMessage("§cLe gps ne contient pas cette localisation");
            return false;
          }

          Main.Database.update(RecordBuilder.build().delete("gps")
                  .where(new CustomEntry("place", args[1])).toString());

          sender.sendMessage("§aLa localisation nommé : §b" + args[1] + " §aa bien été retiré du gps!");
        } catch (SQLException e) {
          e.printStackTrace();
        }

      }
    }
    else if (args.length == 4)
    {
        if(args[0].equals("start"))
        {
          double x = Double.parseDouble(args[1]);
          double y = Double.parseDouble(args[2]);
          double z = Double.parseDouble(args[3]);
          GPS.startGPS(p, x, y, z);
        }
        else if(args[0].equals("add")) {
          if (!sender.hasPermission(Permissions.Admin)) {
            MessageUtils.sendMessage(sender, "§cVous n'avez pas la permission d'utiliser cette commande.");
            return false;
          }
          String name = args[1];
          int x = Integer.parseInt(args[2]);
          int z = Integer.parseInt(args[3]);

          try {
            SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("gps")
                    .where(new CustomEntry("place", name)).toString());

            if (results.count() > 0) {
              sender.sendMessage("§cLe gps contient déjà cette localisation");
              return false;
            }

            HashMap<String, Object> fields = new HashMap<>();
            fields.put("place", name);
            fields.put("x", x);
            fields.put("z", z);

            Main.Database.update(RecordBuilder.build().insert(fields, "gps").toString());
            sender.sendMessage("§aLa localisation nommé : §b" + name + " §aa bien ajouté dans le gps!");

          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
    }

    return false;
  }
}
