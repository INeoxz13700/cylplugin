package fr.karmaowner.gps;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.RecordBuilder;
import java.awt.Point;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class GPS implements Listener {
  public static HashMap<Player, Location> gps = new HashMap<>();
  
  public static HashMap<Player, TaskCreator> gpse = new HashMap<>();
  
  public static HashMap<String, Point> coordinatesGPS = new HashMap<>();
  
  public static long timerRefreshGPSData = 0L;
  
  public static int refreshDelayInSecs = 60;
  
  public static void refreshGPSDataFromDB() throws SQLException {
    if (System.currentTimeMillis() - timerRefreshGPSData >= refreshDelayInSecs) {
      timerRefreshGPSData = System.currentTimeMillis();
      coordinatesGPS = new HashMap<>();
      SqlCollection result = Main.Database.select(RecordBuilder.build().selectAll("gps").toString());
      if (result.count() > 0)
        for (ResultSet s : result)
          coordinatesGPS.put(s.getString("place"), new Point(s.getInt("x"), s.getInt("z")));  
    } 
  }
  
  public static void stopGPS(Player p) {
    if (gps.containsKey(p)) {
      ((TaskCreator)gpse.get(p)).cancelTask();
      gps.remove(p);
      gpse.remove(p);
      p.sendMessage("§8[§6GPS§8] §aVous avez désactivé votre GPS en cours d'utilisation.");
    } else {
      p.sendMessage("§8[§6GPS§8] §cErreur, votre GPS n'est pas activé.");
    } 
  }
  
  public static void startGPS(final Player p, double x, double y, double z) {
    if (gps.get(p) != null) {
      p.sendMessage("§8[§6GPS§8] §cErreur, votre gps est déjà activé.");
      return;
    } 
    Location loc = new Location(p.getWorld(), x, y, z);
    p.sendMessage("§8[§6GPS§8] §aVous avez activé votre GPS sur les coordonnées X: " + x + " Y: " + y + " Z: " + z + ".");
    TaskCreator t = new TaskCreator(new CustomRunnable() {
          public void customRun() {
            if (!p.getWorld().getName().equals("cyl")) {
              cancel();
              return;
            }

            Location pLocation;
            if(p.getVehicle() != null)
            {
              pLocation = p.getVehicle().getLocation();
            }
            else
            {
              pLocation = p.getLocation();
            }

            if ((int)pLocation.distance((Location)GPS.gps.get(p)) < 10) {
              GPS.gps.remove(p);
              p.sendMessage("§8[§6GPS§8] §aVous êtes arrivé à destination");
              cancel();
            } else {

              p.setCompassTarget(GPS.gps.get(p));
              pLocation.setY(((Location)GPS.gps.get(p)).getY());
              ((Location)GPS.gps.get(p)).setY(((Location)GPS.gps.get(p)).getY());
              Vector d = pLocation.getDirection();
              Vector v = p.getCompassTarget().subtract(pLocation).toVector().normalize();
              double a = Math.toDegrees(Math.atan2(d.getX(), d.getZ()));
              a -= Math.toDegrees(Math.atan2(v.getX(), v.getZ()));
              a = ((int)(a + 22.5D) % 360);
              if (a < 0.0D)
                a += 360.0D;

              p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6GPS §e: §o" + "⬆↗➡↘⬇↙⬅↖".charAt((int)a / 45) + " §e(§7" + (int)pLocation.distance(GPS.gps.get(p)) + "m§e)"));
            }
          }
        }, false, 0L, 30L);
    gps.put(p, loc);
    gpse.put(p, t);
  }

  public static void correctGps() throws SQLException {
    ResultSet set = Main.Database.getConnection().prepareStatement("SELECT * FROM player_gps_data WHERE LEFT(NAMES,1) = ';'").executeQuery();
    while (set.next())
    {
      String username = set.getString("pseudo");
      String names = set.getString("names").substring(1);
      String x = set.getString("x").substring(1);
      String y = set.getString("y").substring(1);
      String z = set.getString("z").substring(1);

      String query = "update player_gps_data set names = ?, x = ?, y = ?, z = ? WHERE pseudo = ?";
      PreparedStatement preparedStmt = Main.Database.getConnection().prepareStatement(query);
      preparedStmt.setString   (1, names);
      preparedStmt.setString(2, x);
      preparedStmt.setString(3, y);
      preparedStmt.setString(4, z);
      preparedStmt.setString(5, username);

      preparedStmt.executeUpdate();
      preparedStmt.close();
    }
    set.close();
  }

}
