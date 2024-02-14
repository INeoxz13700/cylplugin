package fr.karmaowner.jobs.parcelle;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.utils.RegionUtils;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class FreeArea {
  public static BlockVector DOT;
  
  public static BlockVector MINDOT;
  
  public static final int DISTANCEX = 8;
  
  public static final int DISTANCEY = 16;
  
  private static double XMAX = Bukkit.getWorld("world1").getSpawnLocation().getX() + 400.0D;
  
  public static boolean isTaking = false;
  
  private static World world;

  public static void initDot(World w) {
    if (DOT == null) {
      Location location = w.getSpawnLocation();
      int x = location.getBlockX();
      int z = location.getBlockZ();
      setDot(new BlockVector(x, w.getHighestBlockYAt(x, z), z));
      MINDOT = new BlockVector(x, w.getHighestBlockYAt(x, z), z);
      world = w;
    }
  }
  
  public static BlockVector getDot(ProtectedRegion region) {
    double distanceX = region.getMaximumPoint().getX() - region.getMinimumPoint().getX();
    double distanceZ = region.getMaximumPoint().getZ() - region.getMinimumPoint().getZ();
    Location location = new Location(world, DOT.getX(), DOT.getY(), DOT.getZ());
    while (true) {
      while (location.getX() <= XMAX) {
        if (RegionUtils.getRegionManager(world.getName()).getApplicableRegions(location).size() > 0) {
          Set<ProtectedRegion> set = RegionUtils.getRegionManager(world.getName()).getApplicableRegions(location).getRegions();
          ProtectedRegion protectedRegion = null;
          Iterator<ProtectedRegion> iterator1 = set.iterator();
          if (iterator1.hasNext()) {
            ProtectedRegion r1 = iterator1.next();
            protectedRegion = r1;
          } 
          if (protectedRegion != null) {
            location = new Location(world, protectedRegion.getMaximumPoint().getX() + 8.0D, MINDOT.getY(), location.getZ());
            Location locMax = new Location(world, location.getX() + distanceX, location.getY(), location.getZ() + distanceZ);
            BlockVector min = new BlockVector(location.getX(), location.getY(), location.getZ());
            BlockVector max = new BlockVector(locMax.getX(), locMax.getY(), locMax.getZ());
            ProtectedRegion r2 = RegionUtils.contains(world, min, max);
            if (r2 != null)
              location = new Location(world, r2.getMinimumPoint().getX(), MINDOT.getY(), location.getZ()); 
          } 
          continue;
        } 
        setDot(new BlockVector(location.getX(), MINDOT.getY(), location.getZ()));
        return DOT;
      } 
      Set<ProtectedRegion> ls = RegionUtils.getRegionManager(world.getName()).getApplicableRegions(new Location(world, MINDOT.getX(), MINDOT.getY(), MINDOT.getZ())).getRegions();
      ProtectedRegion r = null;
      Iterator<ProtectedRegion> iterator = ls.iterator();
      if (iterator.hasNext()) {
        ProtectedRegion r1 = iterator.next();
        r = r1;
      } 
      if (r != null) {
        location = new Location(world, r.getMinimumPoint().getX(), MINDOT.getY(), r.getMaximumPoint().getZ() + 16.0D);
        MINDOT = new BlockVector(location.getX(), MINDOT.getY(), location.getZ());
      } 
    }
  }
  
  public static World getWorld() {
    return world;
  }
  
  private static void setDot(BlockVector b) {
    DOT = b;
  }
  
  public static void loadData() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String name = "freeArea";
    if (f.get(name) == null)
      return; 
    double x = f.getDouble(name + ".max.x");
    double y = f.getDouble(name + ".max.y");
    double z = f.getDouble(name + ".max.z");
    double x2 = f.getDouble(name + ".min.x");
    double y2 = f.getDouble(name + ".min.y");
    double z2 = f.getDouble(name + ".min.z");
    String worldName = f.getString(name + ".worldname");
    world = Main.INSTANCE.getServer().getWorld(worldName);
    setDot(new BlockVector(x, y, z));
    MINDOT = new BlockVector(x2, y2, z2);
  }
  
  public static void saveData() {
    Main.Log("Free Area Data saving...");
    FileConfiguration f = Main.INSTANCE.getConfig();
    String name = "freeArea";
    if (DOT != null) {
      f.set(name + ".max.x", DOT.getX());
      f.set(name + ".max.y", DOT.getY());
      f.set(name + ".max.z", DOT.getZ());
      f.set(name + ".min.x", MINDOT.getX());
      f.set(name + ".min.y", MINDOT.getY());
      f.set(name + ".min.z", MINDOT.getZ());
      if (world != null && world.getName() != null)
        f.set(name + ".worldname", world.getName()); 
    }
    Main.Log("Free Area Data saved");
  }
}
