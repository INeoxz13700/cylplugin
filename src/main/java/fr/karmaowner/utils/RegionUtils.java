package fr.karmaowner.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

public class RegionUtils {
  public static ProtectedRegion getRegionByName(String name, String world) {
    ProtectedRegion r = getRegionManager(world).matchRegion(name);
    if (r != null)
      return r; 
    return null;
  }
  
  public static RegionManager getRegionManager(String world) {
    return Main.WG.getRegionManager(Main.INSTANCE.getServer().getWorld(world));
  }
  
  public static List<Player> getPlayersInRegion(ProtectedRegion region) {
    List<Player> players = new ArrayList<>();
    RegionManager rm = getRegionManager("cyl");
    if (region != null)
      for (Player p : Bukkit.getOnlinePlayers()) {
        Set<ProtectedRegion> rgs = rm.getApplicableRegions(p.getLocation()).getRegions();
        for (ProtectedRegion rg : rgs) {
          if (rg.getId().contains(region.getId())) {
            players.add(p);
            break;
          } 
        } 
      }  
    return players;
  }

  public static List<Block> getAllBlocks(World world, ProtectedRegion r) {
    List<Block> blocks = new ArrayList<>();
    for (int x = r.getMinimumPoint().getBlockX(); x <= r.getMaximumPoint().getBlockX(); x++) {
      for (int z = r.getMinimumPoint().getBlockZ(); z <= r.getMaximumPoint().getBlockZ(); z++) {
        for (int y = r.getMinimumPoint().getBlockY(); y <= r.getMaximumPoint().getBlockY(); y++) {
          Block b = world.getBlockAt(x, y, z);
          blocks.add(b);
        } 
      } 
    } 
    return blocks;
  }
  
  public static List<Block> getBlocksByType(int id, byte data, World world, ProtectedRegion r) {
    List<Block> blocks = getAllBlocks(world, r);
    List<Block> specifiedBlocks = new ArrayList<>();
    for (Block b : blocks) {
      if (b.getTypeId() == id && b.getData() == data)
        specifiedBlocks.add(b); 
    } 
    return specifiedBlocks;
  }
  
  public static List<Block> getBlocksByType(int id, World world, ProtectedRegion r) {
    List<Block> blocks = getAllBlocks(world, r);
    List<Block> specifiedBlocks = new ArrayList<>();
    for (Block b : blocks) {
      if (b.getTypeId() == id)
        specifiedBlocks.add(b); 
    } 
    return specifiedBlocks;
  }
  
  public static List<String> getCopyBlocks(List<Block> blocks) {
    List<String> cpy = new ArrayList<>();
    for (Block b : blocks)
      cpy.add(b.getTypeId() + ":" + b.getData()); 
    return cpy;
  }


  public static List<String> getCopyBlocksWithTileEntity(World w, List<Block> blocks) {
    List<String> cpy = new ArrayList<>();
    CraftWorld cw = (CraftWorld)w;
    for (Block b : blocks) {
      TileEntity tileEntity = cw.getHandle().getTileEntity(new BlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
      boolean hasDirection = false;
      if (tileEntity != null) {
        NBTTagCompound compound = new NBTTagCompound();
        tileEntity.load(compound);
        if (compound.hasKey("Direction"))
          hasDirection = true;
        cpy.add(b.getTypeId() + ":" + b.getData() + (hasDirection ? (":" + compound.getInt("Direction")) : ""));
        continue;
      }
      cpy.add(b.getTypeId() + ":" + b.getData());
    }
    return cpy;
  }

  public static String getCopyBlockWithTileEntity(World w, Block b)
  {
    CraftWorld cw = (CraftWorld)w;
    TileEntity tileEntity = cw.getHandle().getTileEntity(new BlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
    boolean hasDirection = false;
    if (tileEntity != null) {
      NBTTagCompound compound = new NBTTagCompound();
      tileEntity.load(compound);
      if (compound.hasKey("Direction"))
        hasDirection = true;
      return b.getTypeId() + ":" + b.getData() + (hasDirection ? (":" + compound.getInt("Direction")) : "");
    }
    return b.getTypeId() + ":" + b.getData();
  }


  public static void replaceBlock(Block b1, Block b2) {
    b1.setTypeId(b2.getTypeId());
    b1.setData(b2.getData());
  }
  
  public static void replaceBlockByType(Block b1, int id, byte data) {
    b1.setTypeId(id);
    b1.setData(data);
  }
  
  public static void putBlocksInRegion(World worldFrom, World worldTo, ProtectedRegion from, ProtectedRegion to) {
    List<Block> blocks = getAllBlocks(worldFrom, from);
    int k = 0;
    for (int x = to.getMinimumPoint().getBlockX(); x <= to.getMaximumPoint().getBlockX(); x++) {
      for (int z = to.getMinimumPoint().getBlockZ(); z <= to.getMaximumPoint().getBlockZ(); z++) {
        for (int y = to.getMinimumPoint().getBlockY(); y <= to.getMaximumPoint().getBlockY(); y++) {
          int distZ = to.getMaximumPoint().getBlockZ() - to.getMinimumPoint().getBlockZ();
          int distY = to.getMaximumPoint().getBlockY() - to.getMinimumPoint().getBlockY();
          if (k < blocks.size())
            replaceBlock(worldTo
                .getBlockAt(x, y, z), blocks.get(k)); 
          k++;
        } 
      } 
    } 
  }
  
  public static void putBlocks(World world, ProtectedRegion rg, List<String> blocks) {
    int k = 0;
    for (int x = rg.getMinimumPoint().getBlockX(); x <= rg.getMaximumPoint().getBlockX(); x++) {
      for (int z = rg.getMinimumPoint().getBlockZ(); z <= rg.getMaximumPoint().getBlockZ(); z++) {
        for (int y = rg.getMinimumPoint().getBlockY(); y <= rg.getMaximumPoint().getBlockY(); y++) {
          if (k < blocks.size()) {
            int id = Integer.parseInt(((String)blocks.get(k)).split(":")[0]);
            byte data = Byte.parseByte(((String)blocks.get(k)).split(":")[1]);
            replaceBlockByType(world.getBlockAt(x, y, z), id, data);
          } 
          k++;
        } 
      } 
    } 
  }
  
  public static void putBlocksWithTileEntity(World world, ProtectedRegion rg, List<String> blocks) {
    int k = 0;
    for (int x = rg.getMinimumPoint().getBlockX(); x <= rg.getMaximumPoint().getBlockX(); x++) {
      for (int z = rg.getMinimumPoint().getBlockZ(); z <= rg.getMaximumPoint().getBlockZ(); z++) {
        for (int y = rg.getMinimumPoint().getBlockY(); y <= rg.getMaximumPoint().getBlockY(); y++) {
          if (k < blocks.size() && blocks.get(k) != null) {
            int id = Integer.parseInt(((String)blocks.get(k)).split(":")[0]);
            byte data = Byte.parseByte(((String)blocks.get(k)).split(":")[1]);
            int direction = -1;
            if ((((String)blocks.get(k)).split(":")).length == 3)
              direction = Integer.parseInt(((String)blocks.get(k)).split(":")[2]); 
            Block b = world.getBlockAt(x, y, z);
            replaceBlockByType(b, id, data);
            if (direction != -1) {
              CraftWorld cw = (CraftWorld)world;
              TileEntity tileEntity = cw.getHandle().getTileEntity(new BlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
              if (tileEntity != null) {
                NBTTagCompound compound = new NBTTagCompound();
                tileEntity.load(compound);
                compound.setInt("Direction", direction);
                tileEntity.save(compound);
              } 
            } 
          } 
          k++;
        } 
      } 
    } 
  }

  public static void putBlockWithTileEntity(World world, int x, int y, int z, String content) {
    String[] itemData = content.split(":");

    int id = Integer.parseInt(itemData[0]);
    byte data = Byte.parseByte(itemData[1]);
    int direction = -1;
    if (itemData.length == 3) direction = Integer.parseInt(itemData[2]);

    Block b = world.getBlockAt(x, y, z);
    replaceBlockByType(b, id, data);
    if (direction != -1) {
      CraftWorld cw = (CraftWorld)world;
      TileEntity tileEntity = cw.getHandle().getTileEntity(new BlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
      if (tileEntity != null) {
        NBTTagCompound compound = new NBTTagCompound();
        tileEntity.load(compound);
        compound.setInt("Direction", direction);
        tileEntity.save(compound);
      }
    }
  }
  
  public static BlockVector getMiddleXYZ(ProtectedRegion r) {
    double x = (r.getMaximumPoint().getX() - r.getMinimumPoint().getX()) / 2.0D;
    double y = (r.getMaximumPoint().getY() - r.getMinimumPoint().getY()) / 2.0D;
    double z = (r.getMaximumPoint().getZ() - r.getMinimumPoint().getZ()) / 2.0D;
    return new BlockVector(r.getMinimumPoint().getX() + x, r.getMinimumPoint().getY() + y, r.getMinimumPoint().getZ() + z);
  }
  
  public static boolean isBetween2dots(Point P1, Point P2, Player p) {
    int x1 = Math.min(Math.abs(P1.x), Math.abs(P2.x));
    int x2 = Math.max(Math.abs(P1.x), Math.abs(P2.x));
    int y1 = Math.min(Math.abs(P1.y), Math.abs(P2.y));
    int y2 = Math.max(Math.abs(P1.y), Math.abs(P2.y));
    Point bottomLeft = new Point(x1, y1);
    Point topRight = new Point(x2, y2);
    Point bottomRight = new Point(topRight.x, bottomLeft.y);
    Point topLeft = new Point(bottomLeft.x, topRight.y);
    Point pp = new Point((int)Math.abs(p.getLocation().getX()), (int)Math.abs(p.getLocation().getZ()));
    boolean Xaxe = (pp.x >= bottomLeft.x && pp.x <= bottomRight.x);
    boolean Yaxe = (pp.y >= bottomLeft.y && pp.y <= topLeft.y);
    return (Xaxe && Yaxe);
  }
  
  public static Block getBlockAirAtY(Location l, World w) {
    Block b = w.getBlockAt(l);
    while (b.getType() != Material.AIR) {
      Location l2 = b.getLocation();
      b = w.getBlockAt(l2.getBlockX(), l2.getBlockY() + 1, l2.getBlockZ());
    } 
    return b;
  }
  
  public static int getTopBlockAtXZ(World w, double x, double z) {
    Location check = new Location(w, x, 255.0D, z);
    while (w.getBlockAt(check).getType() == Material.AIR)
      check = new Location(w, check.getX(), check.getY() - 1.0D, check.getZ()); 
    return check.getBlockY();
  }
  
  public static BlockVector getMiddleXZ(World w, ProtectedRegion r) {
    double x = (r.getMaximumPoint().getX() - r.getMinimumPoint().getX()) / 2.0D;
    double y = getBlockAirAtY(new Location(w, r.getMinimumPoint().getX(), r.getMinimumPoint().getY(), r.getMinimumPoint().getZ()), w).getLocation().getY();
    double z = (r.getMaximumPoint().getZ() - r.getMinimumPoint().getZ()) / 2.0D;
    return new BlockVector(r.getMinimumPoint().getX() + x, r.getMinimumPoint().getY() + y, r.getMinimumPoint().getZ() + z);
  }
  
  public static Location getLocation(World world, BlockVector bv) {
    return new Location(world, bv.getX(), bv.getY(), bv.getZ());
  }
  
  public static Location getLocationAboveRegion(World world, ProtectedRegion r, Location middle, int countOfBlockAbove) {
    return new Location(world, middle.getBlockX(), world.getHighestBlockYAt(middle), r.getMaximumPoint().getZ() + countOfBlockAbove);
  }
  
  public static ProtectedRegion contains(World w, BlockVector min, BlockVector max) {
    int y = min.getBlockY();
    for (int i = min.getBlockZ(); i <= max.getBlockZ(); i++) {
      for (int j = min.getBlockX(); j <= max.getBlockX(); j++) {
        Vector v = new Vector(j, y, i);
        ApplicableRegionSet ars = getRegionManager(w.getName()).getApplicableRegions(v);
        Iterator<ProtectedRegion> iterator = ars.iterator();
        if (iterator.hasNext()) {
          ProtectedRegion r = iterator.next();
          return r;
        } 
      } 
    } 
    return null;
  }

  public static int getBlockCount(ProtectedRegion rg)
  {
    CuboidRegion region = new CuboidRegion(rg.getMinimumPoint(), rg.getMaximumPoint());

    return region.getArea();
  }
}
