package fr.karmaowner.restore.regions;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.utils.RegionUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RegionState {
  private Timestamp timeLeft;
  
  private int seconds;
  
  private String rgName;
  
  private ProtectedRegion rg;
  
  private List<String> contents;
  
  private World world;
  
  private TaskCreator task;

  private boolean isRegionInitialized = false;
  
  public RegionState(Player p, String rgName, final int seconds) {
    this.rgName = rgName;
    this.seconds = seconds;
    setTimeLeft();
    this.world = Main.INSTANCE.getServer().getWorld("cyl");
    this.rg = Main.WG.getRegionManager(this.world).getRegion(rgName);


    this.contents = new ArrayList<>();

    new TaskCreator(new CustomRunnable() {

      private int x = rg.getMinimumPoint().getBlockX();
      private int z = rg.getMinimumPoint().getBlockZ();

      private int blockCount = RegionUtils.getBlockCount(rg);

      @Override
      public void customRun() {

        if(x <= rg.getMaximumPoint().getBlockX())
        {
          if(z <= rg.getMaximumPoint().getBlockZ())
          {
            for(int i = rg.getMinimumPoint().getBlockY(); i <= rg.getMaximumPoint().getBlockY(); i++)
            {
              contents.add(RegionUtils.getCopyBlockWithTileEntity(world, world.getBlockAt(x,i,z)));
            }
            z++;
          }
          else
          {
            z = rg.getMinimumPoint().getBlockZ();
            x++;
          }
        }
        else
        {
          isRegionInitialized = true;
          cancel();
        }

        if(p.getWorld().getTime() % 20 == 0)
        {
          float percent = (float) contents.size() / blockCount * 100F;
          p.sendMessage("§aCréation du restorerg en cours : §b" + percent + "%");
        }
      }
    }, false, 0L, 1L);

    setTask(new TaskCreator(new CustomRunnable() {
            public void customRun() {

              if(!isRegionInitialized) return;

              Timestamp now = new Timestamp(System.currentTimeMillis());
              if (now.getTime() - RegionState.this.timeLeft.getTime() >= (seconds * 1000L)) {
                RegionState.this.setTimeLeft();
                RegionState.this.restore();
              }
            }
    },  false, 0L, 20L));
  }
  
  public RegionState(String rgName, final int seconds, List<String> contents) {
    this.rgName = rgName;
    this.seconds = seconds;
    setTimeLeft();
    this.world = Main.INSTANCE.getServer().getWorld("cyl");
    this.rg = Main.WG.getRegionManager(this.world).getRegion(rgName);
    this.contents = contents;
    isRegionInitialized = true;
    setTask(new TaskCreator(new CustomRunnable() {
            public void customRun() {
              Timestamp now = new Timestamp(System.currentTimeMillis());
              if (now.getTime() - RegionState.this.timeLeft.getTime() >= (seconds * 1000L)) {
                RegionState.this.setTimeLeft();
                RegionState.this.restore();
              }
            }
    },  false, 0L, 20L));
  }
  
  private void restore() {

    new TaskCreator(new CustomRunnable() {

      private int x = rg.getMinimumPoint().getBlockX();

      private int z = rg.getMinimumPoint().getBlockZ();

      private int k = 0;

      @Override
      public void customRun() {

        if(x <= rg.getMaximumPoint().getBlockX())
        {
          if(z <= rg.getMaximumPoint().getBlockZ())
          {
            for(int i = rg.getMinimumPoint().getBlockY(); i <= rg.getMaximumPoint().getBlockY(); i++)
            {
              RegionUtils.putBlockWithTileEntity(world, x, i, z, contents.get(k));
              k++;
            }
            z++;
          }
          else
          {
            z = rg.getMinimumPoint().getBlockZ();
            x++;
          }
        }
        else
        {
          cancel();
        }
      }
    }, false, 0L, 1L);
  }
  
  public void setTask(TaskCreator t) {
    this.task = t;
  }
  
  public TaskCreator getTask() {
    return this.task;
  }
  
  public int getSeconds() {
    return this.seconds;
  }
  
  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }
  
  public Timestamp getTimeLeft() {
    return this.timeLeft;
  }
  
  public void setTimeLeft() {
    this.timeLeft = new Timestamp(System.currentTimeMillis());
  }
  
  public String getRgName() {
    return this.rgName;
  }
  
  public void setRgName(String rgName) {
    this.rgName = rgName;
  }

  public boolean isRegionInitialized()
  {
    return this.isRegionInitialized;
  }
  
  public ProtectedRegion getRg() {
    return this.rg;
  }
  
  public void setRg(ProtectedRegion rg) {
    this.rg = rg;
  }
  
  public List<String> getContents() {
    return this.contents;
  }
  
  public void setContents(List<String> contents) {
    this.contents = contents;
  }
  
  public World getWorld() {
    return this.world;
  }
  
  public void setWorld(World world) {
    this.world = world;
  }
}
