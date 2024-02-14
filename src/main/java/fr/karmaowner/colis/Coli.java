package fr.karmaowner.colis;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.utils.ItemUtils;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Coli {
  public static final int ITEMTHROWCOLI = Material.SNOW_BALL.getId();
  
  public static final String COLIDISPLAYNAME = ChatColor.LIGHT_PURPLE + "Colis stratégique";
  
  public static ArrayList<ItemStack> items = new ArrayList<>();
  
  public static void giveColi(final Player p) {
    if (p.getInventory().firstEmpty() == -1) {
      new TaskCreator(new CustomRunnable() {
            public void customRun() {
              if (p.getInventory().firstEmpty() != -1) {
                p.getInventory().addItem(ItemUtils.getItem(Coli.ITEMTHROWCOLI, (byte)0, 1, Coli.COLIDISPLAYNAME, null));
                p.sendMessage(ChatColor.DARK_PURPLE + "Vous venez de recevoir un coli !");
                cancel();
              } else {
                p.sendMessage(ChatColor.RED + "Nous n'avons pas pu vous donner votre coli car votre inventaire est rempli ! Prochaine vérication dans 15 secondes !");
              } 
            }
          }, false, 0L, 300L);
    } else {
      p.getInventory().addItem(ItemUtils.getItem(ITEMTHROWCOLI, (byte)0, 1, COLIDISPLAYNAME, null));
      p.sendMessage(ChatColor.DARK_PURPLE + "Vous venez de recevoir un colis !");
    } 
  }
  
  public static void spawnColi(Location loc, Player p) {
    double y = loc.getY() + 20.0D;
    final Location l = new Location(loc.getWorld(), loc.getX(), y, loc.getZ());
    final FallingBlock chest = p.getWorld().spawnFallingBlock(l, Material.CHEST, (byte)0);
    chest.setMetadata("coli", (MetadataValue)new FixedMetadataValue((Plugin)Main.INSTANCE, "coli"));
    (new BukkitRunnable() {
        public void run() {
          if (!l.getChunk().isLoaded())
            l.getChunk().load(); 
          if (chest.isOnGround()) {
            chest.remove();
            cancel();
          } 
        }
      }).runTaskTimer((Plugin)Main.INSTANCE, 0L, 20L);
  }
  
  public static void depopColi(final Block b) {
    new TaskCreator(new CustomRunnable() {
          private Block bloc = b;
          
          public void customRun() {
            b.getLocation().getBlock().setType(Material.AIR);
            cancel();
          }
        },  false, 1200L);
  }
  
  public static ItemStack[] getRandomItems(int n) {
    ArrayList<ItemStack> copy = new ArrayList<>();
    ArrayList<ItemStack> its = new ArrayList<>();
    copy.addAll(items);
    int i = 0;
    int j = copy.size();
    while (i < n && !copy.isEmpty()) {
      int random = (int)(Math.random() * j);
      its.add(copy.get(random));
      copy.remove(random);
      j--;
      i++;
    } 
    return its.<ItemStack>toArray(new ItemStack[0]);
  }
  
  public static void loadData() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String section = "Colis";
    if (f.getConfigurationSection(section) != null)
      for (String key : f.getConfigurationSection(section).getKeys(false)) {
        int id = f.getInt(section + "." + key + ".id");
        byte data = Byte.parseByte(f.getString(section + "." + key + ".byte"));
        int amount = f.getInt(section + "." + key + ".amount");
        items.add(ItemUtils.getItem(id, data, amount, "", null));
      }  
  }
}
